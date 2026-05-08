package org.dromara.chronic.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChPatientAccountBo;
import org.dromara.chronic.domain.bo.WxLoginCodeBo;
import org.dromara.chronic.domain.entity.ChPatientAccount;
import org.dromara.chronic.domain.vo.ChPatientAccountVo;
import org.dromara.chronic.domain.vo.WxLoginVo;
import org.dromara.chronic.mapper.ChPatientAccountMapper;
import org.dromara.chronic.service.IChPatientAccountService;
import org.dromara.common.core.constant.TenantConstants;
import org.dromara.common.core.enums.UserType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.api.model.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.dromara.common.redis.utils.RedisUtils;

/**
 * 患者账号服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChPatientAccountServiceImpl implements IChPatientAccountService {

    private static final String SMS_CODE_KEY = "chronic:sms:code:";

    private final ChPatientAccountMapper patientAccountMapper;
    private final WxMaService wxMaService;

    @Override
    public WxLoginVo register(ChPatientAccountBo bo) {
        // 校验短信验证码
        if (StringUtils.isBlank(bo.getPhone()) || StringUtils.isBlank(bo.getSmsCode())) {
            throw new ServiceException("手机号和验证码不能为空");
        }
        String cachedCode = RedisUtils.getCacheObject(SMS_CODE_KEY + bo.getPhone());
        if (cachedCode == null) {
            throw new ServiceException("验证码已过期，请重新获取");
        }
        if (!cachedCode.equals(bo.getSmsCode())) {
            throw new ServiceException("验证码错误");
        }
        RedisUtils.deleteObject(SMS_CODE_KEY + bo.getPhone());

        ChPatientAccountVo existing = queryByPhone(bo.getPhone());
        if (ObjectUtil.isNotNull(existing)) {
            // 已注册，直接登录
            return doLogin(existing);
        }
        ChPatientAccount entity = MapstructUtils.convert(bo, ChPatientAccount.class);
        if (entity.getIsFamilyProxy() == null) {
            entity.setIsFamilyProxy(false);
        }
        // 如果BO中没有设置tenantId，使用默认租户ID
        if (StringUtils.isBlank(entity.getTenantId())) {
            entity.setTenantId(TenantConstants.DEFAULT_TENANT_ID);
        }
        patientAccountMapper.insert(entity);
        ChPatientAccountVo newAccount = patientAccountMapper.selectVoOne(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getAccountId, entity.getAccountId())
        );
        return doLogin(newAccount);
    }

    @Override
    public ChPatientAccountVo queryByPhone(String phone) {
        return patientAccountMapper.selectVoOne(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getPhone, phone)
        );
    }

    @Override
    public ChPatientAccountVo queryByOpenid(String openid) {
        return patientAccountMapper.selectVoOne(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getOpenid, openid)
        );
    }

    @Override
    public ChPatientAccountVo queryByPatientId(Long patientId) {
        return patientAccountMapper.selectVoOne(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getPatientId, patientId)
                .eq(ChPatientAccount::getIsFamilyProxy, false)
                .last("LIMIT 1")
        );
    }

    @Override
    public List<ChPatientAccountVo> queryFamilyProxies(Long masterAccountId) {
        return patientAccountMapper.selectVoList(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getMasterAccountId, masterAccountId)
                .eq(ChPatientAccount::getIsFamilyProxy, true)
        );
    }

    @Override
    public Boolean bindFamilyProxy(ChPatientAccountBo bo) {
        bo.setIsFamilyProxy(true);
        if (bo.getMasterAccountId() == null) {
            throw new ServiceException("家属代管必须指定主账号");
        }
        ChPatientAccount master = patientAccountMapper.selectById(bo.getMasterAccountId());
        if (ObjectUtil.isNull(master)) {
            throw new ServiceException("主账号不存在");
        }
        if (bo.getAuthExpireTime() != null && bo.getAuthExpireTime().before(new Date())) {
            throw new ServiceException("授权过期时间不能早于当前时间");
        }
        ChPatientAccount entity = MapstructUtils.convert(bo, ChPatientAccount.class);
        // 如果BO中没有设置tenantId，使用默认租户ID
        if (StringUtils.isBlank(entity.getTenantId())) {
            entity.setTenantId(TenantConstants.DEFAULT_TENANT_ID);
        }
        patientAccountMapper.insert(entity);
        return true;
    }

    @Override
    public Boolean unbindFamilyProxy(Long accountId) {
        ChPatientAccount entity = patientAccountMapper.selectById(accountId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("账号不存在");
        }
        if (!Boolean.TRUE.equals(entity.getIsFamilyProxy())) {
            throw new ServiceException("非家属代管账号不能解绑");
        }
        patientAccountMapper.deleteById(accountId);
        return true;
    }

    @Override
    public Boolean updateAuthScope(Long accountId, String authScope) {
        ChPatientAccount entity = patientAccountMapper.selectById(accountId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("账号不存在");
        }
        entity.setAuthScope(authScope);
        patientAccountMapper.updateById(entity);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxLoginVo loginByWxCode(WxLoginCodeBo bo) {
        WxMaJscode2SessionResult wxResult;
        try {
            wxResult = wxMaService.getUserService().getSessionInfo(bo.getCode());
        } catch (Exception e) {
            throw new ServiceException("微信登录失败: " + e.getMessage());
        }
        String openid = wxResult.getOpenid();
        String unionid = wxResult.getUnionid();

        // 查询 openid 对应的主账号（is_family_proxy=false 或 NULL）
        List<ChPatientAccountVo> accounts = patientAccountMapper.selectVoList(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getOpenid, openid)
                .and(w -> w.isNull(ChPatientAccount::getIsFamilyProxy)
                    .or().eq(ChPatientAccount::getIsFamilyProxy, false))
                .orderByAsc(ChPatientAccount::getAccountId)
        );

        if (accounts.isEmpty()) {
            // openid 未绑定任何主账号
            return handleNotBound(bo, openid, unionid);
        }

        if (accounts.size() > 1) {
            log.warn("multiple-account-on-openid: openidPrefix={}, hits={}",
                maskOpenid(openid), accounts.size());
        }

        ChPatientAccountVo account = accounts.get(0);
        return doLogin(account);
    }

    private WxLoginVo handleNotBound(WxLoginCodeBo bo, String openid, String unionid) {
        String phone = bo.getPhone();

        // 优先使用微信授权手机号（getPhoneNumber）
        if (StringUtils.isNotBlank(bo.getPhoneCode())) {
            try {
                cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo phoneInfo =
                    wxMaService.getUserService().getPhoneNoInfo(bo.getPhoneCode());
                phone = phoneInfo.getPhoneNumber();
                log.info("微信授权获取手机号成功: {}****", phone != null && phone.length() > 4 ? phone.substring(0, phone.length() - 4) : "***");
            } catch (Exception e) {
                log.warn("微信授权手机号失败: {}，尝试手动输入", e.getMessage());
            }
        }

        if (StringUtils.isBlank(phone)) {
            // 需要前端补充手机号
            WxLoginVo vo = new WxLoginVo();
            vo.setNeedBind(true);
            return vo;
        }

        // 短信验证码校验（微信授权手机号时跳过）
        if (StringUtils.isBlank(bo.getPhoneCode()) && StringUtils.isNotBlank(bo.getSmsCode())) {
            String cachedCode = RedisUtils.getCacheObject(SMS_CODE_KEY + phone);
            if (cachedCode == null) {
                throw new ServiceException("验证码已过期，请重新获取");
            }
            if (!cachedCode.equals(bo.getSmsCode())) {
                throw new ServiceException("验证码错误");
            }
            RedisUtils.deleteObject(SMS_CODE_KEY + phone);
        }

        // 校验 openid 是否已绑定其他手机号
        List<ChPatientAccountVo> existingWithOpenid = patientAccountMapper.selectVoList(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getOpenid, openid)
        );
        if (!existingWithOpenid.isEmpty()) {
            ChPatientAccountVo existing = existingWithOpenid.get(0);
            if (existing.getPhone() != null && !existing.getPhone().equals(phone)) {
                throw new ServiceException("该微信号已绑定其他患者账号");
            }
        }

        // 查手机号是否已有账号
        ChPatientAccountVo phoneAccount = queryByPhone(phone);
        if (phoneAccount != null) {
            // 手机号已有账号，绑定 openid
            return bindOpenidToExistingAccount(phoneAccount, openid, unionid, phone);
        }

        // 新建账号
        Long accountId = createNewAccountWithWechat(openid, unionid, phone);
        ChPatientAccountVo newAccount = patientAccountMapper.selectVoOne(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getAccountId, accountId)
        );
        return doLogin(newAccount);
    }

    private WxLoginVo bindOpenidToExistingAccount(ChPatientAccountVo account, String openid, String unionid, String phone) {
        ChPatientAccount entity = patientAccountMapper.selectById(account.getAccountId());
        entity.setOpenid(openid);
        entity.setUnionid(unionid);
        patientAccountMapper.updateById(entity);
        account.setOpenid(openid);
        account.setUnionid(unionid);
        return doLogin(account);
    }

    private Long createNewAccountWithWechat(String openid, String unionid, String phone) {
        ChPatientAccount entity = new ChPatientAccount();
        entity.setPhone(phone);
        entity.setOpenid(openid);
        entity.setUnionid(unionid);
        entity.setIsFamilyProxy(false);
        // 设置默认租户ID
        entity.setTenantId(TenantConstants.DEFAULT_TENANT_ID);
        patientAccountMapper.insert(entity);
        return entity.getAccountId();
    }

    private WxLoginVo doLogin(ChPatientAccountVo account) {
        // 构建 LoginUser
        LoginUser loginUser = new LoginUser();
        loginUser.setTenantId(account.getTenantId() != null ? account.getTenantId() : TenantConstants.DEFAULT_TENANT_ID);
        loginUser.setUserId(account.getAccountId());
        loginUser.setUsername(account.getPhone());
        loginUser.setNickname(account.getNickname());
        loginUser.setUserType(UserType.PORTAL_USER.getUserType());
        // C端用户基础权限：文件上传、文件下载
        loginUser.setMenuPermission(Set.of("system:oss:upload", "system:oss:download"));
        loginUser.setRolePermission(Set.of());

        // 使用 LoginHelper 登录（正确设置扩展信息）
        SaLoginParameter loginParameter = new SaLoginParameter();
        loginParameter.setDevice("mp-weixin");
        // 从 HTTP 请求头中获取 clientid（前端传递），用于网关验证
        String clientId = getClientIdFromRequest();
        if (StringUtils.isNotBlank(clientId)) {
            loginParameter.setExtra(LoginHelper.CLIENT_KEY, clientId);
        }
        log.info("患者登录: loginId={}, phone={}****, clientId={}", loginUser.getLoginId(),
            account.getPhone() != null && account.getPhone().length() > 4 ? account.getPhone().substring(0, account.getPhone().length() - 4) : "***",
            clientId);
        LoginHelper.login(loginUser, loginParameter);

        String token = StpUtil.getTokenValue();
        long expireIn = StpUtil.getTokenTimeout();

        // 组装脱敏 VO
        WxLoginVo vo = new WxLoginVo();
        vo.setToken(token);
        vo.setExpireIn(expireIn);
        vo.setNeedBind(false);

        // 脱敏：phone 中间 4 位替换为 ****，openid 不返回
        if (account.getPhone() != null && account.getPhone().length() == 11) {
            account.setPhone(account.getPhone().substring(0, 3) + "****" + account.getPhone().substring(7));
        }
        account.setIsBoundWechat(StringUtils.isNotBlank(account.getOpenid()));
        account.setOpenid(null);
        account.setUnionid(null);
        vo.setAccount(account);

        return vo;
    }

    private String maskOpenid(String openid) {
        if (openid == null || openid.length() <= 6) {
            return "***";
        }
        return openid.substring(0, 6) + "***";
    }

    @Override
    public Boolean bindWechat(Long accountId, String code) {
        ChPatientAccount entity = patientAccountMapper.selectById(accountId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("账号不存在");
        }
        if (StringUtils.isNotBlank(entity.getOpenid())) {
            throw new ServiceException("该账号已绑定微信");
        }
        WxMaJscode2SessionResult wxResult;
        try {
            wxResult = wxMaService.getUserService().getSessionInfo(code);
        } catch (Exception e) {
            throw new ServiceException("微信授权失败: " + e.getMessage());
        }
        String openid = wxResult.getOpenid();
        // 检查 openid 是否已被其他账号绑定
        ChPatientAccountVo existing = queryByOpenid(openid);
        if (ObjectUtil.isNotNull(existing) && !existing.getAccountId().equals(accountId)) {
            throw new ServiceException("该微信号已绑定其他账号");
        }
        entity.setOpenid(openid);
        entity.setUnionid(wxResult.getUnionid());
        patientAccountMapper.updateById(entity);
        return true;
    }

    @Override
    public ChPatientAccountVo getAccountById(Long accountId) {
        return patientAccountMapper.selectVoOne(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getAccountId, accountId)
        );
    }

    @Override
    public Boolean updateAccountInfo(Long accountId, String nickname, String avatarOssId) {
        ChPatientAccount entity = patientAccountMapper.selectById(accountId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("账号不存在");
        }
        if (StringUtils.isNotBlank(nickname)) {
            entity.setNickname(nickname);
        }
        if (StringUtils.isNotBlank(avatarOssId)) {
            entity.setAvatarOssId(avatarOssId);
        }
        patientAccountMapper.updateById(entity);
        return true;
    }

    /**
     * 从当前 HTTP 请求头中获取 clientid
     */
    private String getClientIdFromRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String clientId = request.getHeader(LoginHelper.CLIENT_KEY);
                if (StringUtils.isNotBlank(clientId)) {
                    return clientId;
                }
                // 尝试从 query param 中获取
                clientId = request.getParameter(LoginHelper.CLIENT_KEY);
                if (StringUtils.isNotBlank(clientId)) {
                    return clientId;
                }
            }
        } catch (Exception e) {
            log.debug("获取clientid失败: {}", e.getMessage());
        }
        return null;
    }
}
