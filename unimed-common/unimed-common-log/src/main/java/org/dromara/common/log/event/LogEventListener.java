package org.dromara.common.log.event;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ip.AddressUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.api.RemoteClientService;
import org.dromara.system.api.RemoteLogService;
import org.dromara.system.api.domain.bo.RemoteLogininforBo;
import org.dromara.system.api.domain.bo.RemoteOperLogBo;
import org.dromara.system.api.domain.vo.RemoteClientVo;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 异步调用日志服务
 *
 * @author unimed
 */
@Component
@Slf4j
public class LogEventListener {

    @DubboReference
    private RemoteLogService remoteLogService;
    @DubboReference
    private RemoteClientService remoteClientService;

    /**
     * 保存系统日志记录
     */
    @EventListener
    public void saveLog(OperLogEvent operLogEvent) {
        try {
            RemoteOperLogBo sysOperLog = BeanUtil.toBean(operLogEvent, RemoteOperLogBo.class);
            remoteLogService.saveLog(sysOperLog);
            log.debug("操作日志保存成功 - 操作者: {}, 操作: {}", 
                operLogEvent.getOperName(), operLogEvent.getTitle());
        } catch (Exception e) {
            log.warn("操作日志保存失败，远程日志服务不可用 - 操作者: {}, 操作: {}, 错误: {}", 
                operLogEvent.getOperName(), operLogEvent.getTitle(), e.getMessage());
            // 可以考虑将日志保存到本地文件或其他存储
        }
    }

    /**
     * 保存系统访问记录
     */
    @EventListener
    public void saveLogininfor(LogininforEvent logininforEvent) {
        HttpServletRequest request = ServletUtils.getRequest();
        final UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
        final String ip = ServletUtils.getClientIP(request);
        // 客户端信息
        String clientId = request.getHeader(LoginHelper.CLIENT_KEY);
        RemoteClientVo clientVo = null;
        if (StringUtils.isNotBlank(clientId)) {
            clientVo = remoteClientService.queryByClientId(clientId);
        }

        String address = AddressUtils.getRealAddressByIP(ip);
        StringBuilder s = new StringBuilder();
        s.append(getBlock(ip));
        s.append(address);
        s.append(getBlock(logininforEvent.getUsername()));
        s.append(getBlock(logininforEvent.getStatus()));
        s.append(getBlock(logininforEvent.getMessage()));
        // 打印信息到日志
        log.info(s.toString(), logininforEvent.getArgs());
        // 获取客户端操作系统
        String os = userAgent.getOs().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        // 封装对象
        RemoteLogininforBo logininfor = new RemoteLogininforBo();
        logininfor.setTenantId(logininforEvent.getTenantId());
        logininfor.setUserName(logininforEvent.getUsername());
        if (ObjectUtil.isNotNull(clientVo)) {
            logininfor.setClientKey(clientVo.getClientKey());
            logininfor.setDeviceType(clientVo.getDeviceType());
        }
        logininfor.setIpaddr(ip);
        logininfor.setLoginLocation(address);
        logininfor.setBrowser(browser);
        logininfor.setOs(os);
        logininfor.setMsg(logininforEvent.getMessage());
        // 日志状态
        if (StringUtils.equalsAny(logininforEvent.getStatus(), Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER)) {
            logininfor.setStatus(Constants.SUCCESS);
        } else if (Constants.LOGIN_FAIL.equals(logininforEvent.getStatus())) {
            logininfor.setStatus(Constants.FAIL);
        }
        try {
            remoteLogService.saveLogininfor(logininfor);
            log.debug("登录日志保存成功 - 用户: {}, IP: {}", logininfor.getUserName(), logininfor.getIpaddr());
        } catch (Exception e) {
            log.warn("登录日志保存失败，远程日志服务不可用 - 用户: {}, IP: {}, 错误: {}", 
                logininfor.getUserName(), logininfor.getIpaddr(), e.getMessage());
        }
    }

    private String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg + "]";
    }

}
