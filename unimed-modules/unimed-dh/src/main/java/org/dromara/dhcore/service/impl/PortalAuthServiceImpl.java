package org.dromara.dhcore.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.digest.BCrypt;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.TenantConstants;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.dhcore.domain.DhUserProfile;
import org.dromara.dhcore.domain.bo.portal.PasswordLoginBo;
import org.dromara.dhcore.domain.bo.portal.SmsCodeBo;
import org.dromara.dhcore.domain.bo.portal.SmsLoginBo;
import org.dromara.dhcore.domain.vo.portal.PortalLoginVo;
import org.dromara.dhcore.mapper.DhUserProfileMapper;
import org.dromara.dhcore.service.IPortalAuthService;
import org.dromara.system.api.model.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Date;
import java.util.Set;

/**
 * C端门户认证服务实�? *
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

    private final DhUserProfileMapper userProfileMapper;

    @Override
    public String sendSmsCode(SmsCodeBo bo) {
        String phone = bo.getPhone();
        String cacheKey = SMS_CODE_KEY_PREFIX + phone;

        // 开发环境返回测试验证码
        String code = TEST_CODE;
        RedisUtils.setCacheObject(cacheKey, code, Duration.ofMinutes(CODE_EXPIRE_MINUTES));
        log.info("发送短信验证码到手�? {}, 验证�? {}", phone, code);

        // 返回测试验证码供前端使用（生产环境应返回 null�?        return code;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PortalLoginVo smsLogin(SmsLoginBo bo) {
        String phone = bo.getPhone();
        String code = bo.getCode();

        // 验证码校�?        String cacheKey = SMS_CODE_KEY_PREFIX + phone;
        String cachedCode = RedisUtils.getCacheObject(cacheKey);
        if (cachedCode == null) {
            throw new ServiceException("验证码已过期，请重新获取");
        }
        if (!cachedCode.equals(code)) {
            throw new ServiceException("验证码错�?);
        }

        // 删除验证�?        RedisUtils.deleteObject(cacheKey);

        // 查询或创建用�?        DhUserProfile user = getOrCreateUser(phone);

        // 检查用户状�?        if ("1".equals(user.getStatus())) {
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
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DhUserProfile>()
                .eq(DhUserProfile::getPhone, phone)
        );

        if (user == null) {
            throw new ServiceException("用户不存�?);
        }

        // 密码校验
        if (user.getPassword() == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new ServiceException("密码错误");
        }

        // 检查用户状�?        if ("1".equals(user.getStatus())) {
            throw new ServiceException("账号已被禁用");
        }

        // 登录
        return doLogin(user);
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    /**
     * 查询或创建用户（短信登录时自动注册）
     */
    private DhUserProfile getOrCreateUser(String phone) {
        DhUserProfile user = userProfileMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<DhUserProfile>()
                .eq(DhUserProfile::getPhone, phone)
        );

        if (user == null) {
            // 自动注册
            user = new DhUserProfile();
            user.setPhone(phone);
            user.setUserName("用户" + phone.substring(phone.length() - 4));
            user.setMemberLevel("NORMAL");
            user.setWalletBalance(java.math.BigDecimal.ZERO);
            user.setTotalTopup(java.math.BigDecimal.ZERO);
            user.setTotalConsume(java.math.BigDecimal.ZERO);
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
        loginUser.setMenuPermission(Set.of());
        loginUser.setRolePermission(Set.of());

        // Sa-Token 登录
        SaLoginParameter loginParameter = new SaLoginParameter();
        loginParameter.setDevice("portal-web");
        StpUtil.login(loginUser.getLoginId(), loginParameter);
        StpUtil.getTokenSession().set("loginUser", loginUser);

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
