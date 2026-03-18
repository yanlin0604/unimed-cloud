package org.dromara.dhcore.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.TenantConstants;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.dhcore.domain.DhUserProfile;
import org.dromara.dhcore.domain.bo.portal.PasswordUpdateBo;
import org.dromara.dhcore.domain.bo.portal.PasswordLoginBo;
import org.dromara.dhcore.domain.bo.portal.PortalRegisterBo;
import org.dromara.dhcore.domain.bo.portal.SmsCodeBo;
import org.dromara.dhcore.domain.bo.portal.SmsLoginBo;
import org.dromara.dhcore.domain.vo.portal.PortalLoginVo;
import org.dromara.dhcore.mapper.DhUserProfileMapper;
import org.dromara.dhcore.service.IPortalAuthService;
import org.dromara.system.api.model.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Date;
import java.util.Set;

/**
 * C端门户认证服务实现
 *
 * @author unimed
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PortalAuthServiceImpl implements IPortalAuthService {

    private static final String SMS_CODE_KEY_PREFIX = "portal:sms:code:";
    private static final String TEST_CODE = "123456";
    private static final int CODE_EXPIRE_MINUTES = 5;
    private static final String USER_TYPE_PORTAL = "portal";
    /**
     * C端门户客户端ID（复用app客户端配置）
     */
    private static final String PORTAL_CLIENT_ID = "428a8310cd442757ae699df5d894f051";

    private final DhUserProfileMapper userProfileMapper;

    @Override
    public String sendSmsCode(SmsCodeBo bo) {
        String phone = bo.getPhone();
        String cacheKey = SMS_CODE_KEY_PREFIX + phone;

        // 开发环境返回测试验证码
        String code = TEST_CODE;
        RedisUtils.setCacheObject(cacheKey, code, Duration.ofMinutes(CODE_EXPIRE_MINUTES));
        log.info("发送短信验证码到手机: {}, 验证码: {}", phone, code);

        // 返回测试验证码供前端使用（生产环境应返回 null）
        return code;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PortalLoginVo smsLogin(SmsLoginBo bo) {
        String phone = bo.getPhone();
        String code = bo.getCode();

        // 验证码校验
        String cacheKey = SMS_CODE_KEY_PREFIX + phone;
        String cachedCode = RedisUtils.getCacheObject(cacheKey);
        if (cachedCode == null) {
            throw new ServiceException("验证码已过期，请重新获取");
        }
        if (!cachedCode.equals(code)) {
            throw new ServiceException("验证码错误");
        }

        // 删除验证码
        RedisUtils.deleteObject(cacheKey);

        // 查询或创建用户
        DhUserProfile user = getOrCreateUser(phone);

        // 检查用户状态
        if ("1".equals(user.getStatus())) {
            throw new ServiceException("账号已被禁用");
        }

        // 登录
        return doLogin(user);
    }

    @Override
    public PortalLoginVo passwordLogin(PasswordLoginBo bo) {
        String phone = bo.getPhone();
        String password = bo.getPassword();

        // 查询用户
        DhUserProfile user = userProfileMapper.selectOne(
            new LambdaQueryWrapper<DhUserProfile>()
                .eq(DhUserProfile::getPhone, phone)
        );

        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        // 密码校验
        if (user.getPassword() == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new ServiceException("密码错误");
        }

        // 检查用户状态
        if ("1".equals(user.getStatus())) {
            throw new ServiceException("账号已被禁用");
        }

        // 登录
        return doLogin(user);
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(PasswordUpdateBo bo) {
        Long userId = LoginHelper.getUserId();

        // 查询用户
        DhUserProfile user = userProfileMapper.selectById(userId);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        // 验证旧密码
        if (user.getPassword() == null) {
            // 用户未设置密码（可能是短信登录创建的），允许直接设置密码
            log.info("用户未设置密码，直接设置新密码: userId={}", userId);
        } else if (!BCrypt.checkpw(bo.getOldPassword(), user.getPassword())) {
            throw new ServiceException("旧密码错误");
        }

        // 更新密码
        user.setPassword(BCrypt.hashpw(bo.getNewPassword()));
        userProfileMapper.updateById(user);

        log.info("用户修改密码成功: userId={}", userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PortalLoginVo register(PortalRegisterBo bo) {
        String phone = bo.getUsername();
        String password = bo.getPassword();

        // 检查用户是否已存在
        DhUserProfile existUser = userProfileMapper.selectOne(
            new LambdaQueryWrapper<DhUserProfile>()
                .eq(DhUserProfile::getPhone, phone)
        );

        if (existUser != null) {
            throw new ServiceException("该手机号已注册");
        }

        // 创建新用户
        DhUserProfile user = new DhUserProfile();
        user.setPhone(phone);
        user.setUserName("用户" + phone.substring(phone.length() - 4));
        user.setPassword(BCrypt.hashpw(password));
        user.setMemberLevel("NORMAL");
        user.setWalletBalance(BigDecimal.ZERO);
        user.setTotalTopup(BigDecimal.ZERO);
        user.setTotalConsume(BigDecimal.ZERO);
        user.setOrderCount(0);
        user.setStatus("0");
        user.setRegisterTime(new Date());
        userProfileMapper.insert(user);

        log.info("用户注册成功: phone={}, userId={}", phone, user.getUserId());

        // 自动登录
        return doLogin(user);
    }

    /**
     * 查询或创建用户（短信登录时自动注册）
     */
    private DhUserProfile getOrCreateUser(String phone) {
        DhUserProfile user = userProfileMapper.selectOne(
            new LambdaQueryWrapper<DhUserProfile>()
                .eq(DhUserProfile::getPhone, phone)
        );

        if (user == null) {
            // 自动注册
            user = new DhUserProfile();
            user.setPhone(phone);
            user.setUserName("用户" + phone.substring(phone.length() - 4));
            user.setMemberLevel("NORMAL");
            user.setWalletBalance(BigDecimal.ZERO);
            user.setTotalTopup(BigDecimal.ZERO);
            user.setTotalConsume(BigDecimal.ZERO);
            user.setOrderCount(0);
            user.setStatus("0");
            user.setRegisterTime(new Date());
            userProfileMapper.insert(user);
            log.info("短信登录自动注册用户: phone={}, userId={}", phone, user.getUserId());
        }

        return user;
    }

    /**
     * 执行登录
     */
    private PortalLoginVo doLogin(DhUserProfile user) {
        // 构建 LoginUser
        LoginUser loginUser = new LoginUser();
        loginUser.setTenantId(TenantConstants.DEFAULT_TENANT_ID);
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickname(user.getUserName());
        loginUser.setUserType(USER_TYPE_PORTAL);
        // C端用户基础权限：文件上传、文件下载
        loginUser.setMenuPermission(Set.of("system:oss:upload", "system:oss:download"));
        loginUser.setRolePermission(Set.of());

        // 使用 LoginHelper 登录（正确设置扩展信息）
        SaLoginParameter loginParameter = new SaLoginParameter();
        loginParameter.setDevice("portal-web");
        // 设置客户端ID，用于网关验证
        loginParameter.setExtra(LoginHelper.CLIENT_KEY, PORTAL_CLIENT_ID);
        LoginHelper.login(loginUser, loginParameter);

        // 返回登录信息
        PortalLoginVo vo = new PortalLoginVo();
        vo.setAccessToken(StpUtil.getTokenValue());
        vo.setUserId(user.getUserId());
        vo.setUserName(user.getUserName());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setMemberLevel(user.getMemberLevel());

        return vo;
    }
}
