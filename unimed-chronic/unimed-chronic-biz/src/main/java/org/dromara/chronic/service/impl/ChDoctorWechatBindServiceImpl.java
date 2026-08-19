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
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.chronic.domain.bo.ChDoctorWechatBindBo;
import org.dromara.chronic.domain.entity.ChDoctorWechatBind;
import org.dromara.chronic.domain.vo.ChDoctorWechatBindVo;
import org.dromara.chronic.domain.vo.DoctorLoginVo;
import org.dromara.chronic.mapper.ChDoctorWechatBindMapper;
import org.dromara.chronic.service.IChDoctorWechatBindService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.model.LoginUser;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

/**
 * 医生微信绑定服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChDoctorWechatBindServiceImpl implements IChDoctorWechatBindService {

    private final ChDoctorWechatBindMapper wechatBindMapper;
    private final WxMaService wxMaService;
    @DubboReference
    private RemoteUserService remoteUserService;

    @Override
    public DoctorLoginVo loginByWxCode(String code) {
        if (StringUtils.isBlank(code)) {
            throw new ServiceException("微信 code 不能为空");
        }
        WxMaJscode2SessionResult wxResult;
        try {
            wxResult = wxMaService.getUserService().getSessionInfo(code);
        } catch (Exception e) {
            throw new ServiceException("微信登录失败: " + e.getMessage());
        }
        String openid = wxResult.getOpenid();
        ChDoctorWechatBindVo bind = queryByOpenid(openid);
        DoctorLoginVo vo = new DoctorLoginVo();
        if (bind == null || bind.getUserId() == null) {
            // 未绑定：前端引导先用账号密码登录再绑定微信
            vo.setNeedBind(true);
            return vo;
        }

        // 取系统用户（含菜单/角色权限），签发 Sa-Token
        LoginUser loginUser;
        try {
            loginUser = remoteUserService.getUserInfo(bind.getUserId(), TenantHelper.getTenantId());
        } catch (Exception e) {
            throw new ServiceException("获取医生账号信息失败: " + e.getMessage());
        }
        if (loginUser == null) {
            throw new ServiceException("微信绑定的医生账号不存在或已停用");
        }

        SaLoginParameter loginParameter = new SaLoginParameter();
        loginParameter.setDevice("mp-weixin");
        String clientId = getClientIdFromRequest();
        if (StringUtils.isNotBlank(clientId)) {
            loginParameter.setExtra(LoginHelper.CLIENT_KEY, clientId);
        }
        LoginHelper.login(loginUser, loginParameter);
        log.info("医生微信登录: userId={}, clientId={}", loginUser.getUserId(), clientId);

        vo.setNeedBind(false);
        vo.setAccess_token(StpUtil.getTokenValue());
        vo.setExpire_in(StpUtil.getTokenTimeout());
        vo.setUserId(loginUser.getUserId());
        vo.setNickName(loginUser.getNickname());
        return vo;
    }

    /**
     * 从当前 HTTP 请求头/参数中获取 clientid（网关校验需要）
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
                return request.getParameter(LoginHelper.CLIENT_KEY);
            }
        } catch (Exception e) {
            log.warn("获取 clientid 失败: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public Long bind(ChDoctorWechatBindBo bo) {
        // 绑定对象只能是当前登录医生本人，userId 一律从登录态取，忽略请求体传入值。
        //
        // 原实现直接使用 bo.getUserId()，且 openid 已被占用时走「更新绑定」覆盖 existing.userId，
        // 构成账号完全接管：任一持有合法 token 的医生
        //   ① POST /chronic/doctor/auth/wechat/bind  {userId: 目标医生, openid: 自己的openid}
        //   ② POST /chronic/doctor/auth/wechat/code  {code: 自己的微信code}   ← 该端点无 @SaCheckLogin
        // 即可拿到目标医生的 access_token。因为 ① 是覆盖语义，目标是否已绑微信都不影响。
        Long loginUserId = LoginHelper.getUserId();
        if (loginUserId == null) {
            throw new ServiceException("未登录");
        }

        ChDoctorWechatBind existing = wechatBindMapper.selectOne(
            Wrappers.<ChDoctorWechatBind>lambdaQuery()
                .eq(ChDoctorWechatBind::getOpenid, bo.getOpenid())
        );
        if (ObjectUtil.isNotNull(existing)) {
            // openid 已绑定他人时必须拒绝，不能覆盖——覆盖等于把他人账号的微信登录入口交给当前用户
            if (!loginUserId.equals(existing.getUserId())) {
                throw new ServiceException("该微信已绑定其他医生账号");
            }
            // 已绑定本人：仅刷新 unionid，幂等返回
            existing.setUnionid(bo.getUnionid());
            wechatBindMapper.updateById(existing);
            return existing.getId();
        }

        ChDoctorWechatBind entity = MapstructUtils.convert(bo, ChDoctorWechatBind.class);
        entity.setUserId(loginUserId);
        entity.setBindTime(new Date());
        wechatBindMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public ChDoctorWechatBindVo queryByOpenid(String openid) {
        return wechatBindMapper.selectVoOne(
            Wrappers.<ChDoctorWechatBind>lambdaQuery()
                .eq(ChDoctorWechatBind::getOpenid, openid)
        );
    }

    @Override
    public ChDoctorWechatBindVo queryByUserId(Long userId) {
        return wechatBindMapper.selectVoOne(
            Wrappers.<ChDoctorWechatBind>lambdaQuery()
                .eq(ChDoctorWechatBind::getUserId, userId)
                .last("LIMIT 1")
        );
    }

    @Override
    public Boolean unbind(Long id) {
        ChDoctorWechatBind entity = wechatBindMapper.selectById(id);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("绑定记录不存在");
        }
        // 归属校验：绑定ID是自增整数，不校验则任意医生可枚举 id 解绑他人微信
        Long loginUserId = LoginHelper.getUserId();
        if (loginUserId == null || !loginUserId.equals(entity.getUserId())) {
            throw new ServiceException("无权解绑该微信");
        }
        wechatBindMapper.deleteById(id);
        return true;
    }
}
