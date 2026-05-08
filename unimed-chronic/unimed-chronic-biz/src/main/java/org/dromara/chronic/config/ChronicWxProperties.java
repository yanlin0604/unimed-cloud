package org.dromara.chronic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 微信小程序登录配置
 *
 * @author unimed
 */
@Data
@ConfigurationProperties(prefix = "chronic.wx.patient")
public class ChronicWxProperties {

    /**
     * 微信小程序 appId
     */
    private String appid;

    /**
     * 微信小程序 secret
     */
    private String secret;
}
