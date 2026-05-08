package org.dromara.chronic.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序 WxJava 配置
 *
 * @author unimed
 */
@Slf4j
@Configuration
public class WxMaConfig {

    @Bean
    @ConditionalOnProperty(prefix = "chronic.wx.patient", name = "appid")
    public WxMaService wxMaService(ChronicWxProperties properties) {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(properties.getAppid());
        config.setSecret(properties.getSecret());
        config.setMsgDataFormat("json");

        WxMaServiceImpl service = new WxMaServiceImpl();
        service.setWxMaConfig(config);
        log.info("微信小程序 WxJava 初始化完成, appid={}", properties.getAppid());
        return service;
    }
}
