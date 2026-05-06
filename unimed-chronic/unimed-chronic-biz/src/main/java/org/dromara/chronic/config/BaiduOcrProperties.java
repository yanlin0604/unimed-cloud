package org.dromara.chronic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 百度OCR配置
 *
 * @author unimed
 */
@Data
@Component
@ConfigurationProperties(prefix = "chronic.ocr.baidu")
public class BaiduOcrProperties {

    private Boolean enabled = false;

    /**
     * 百度AI应用 APP_ID
     */
    private String appId;

    /**
     * 百度AI应用 API_KEY
     */
    private String apiKey;

    /**
     * 百度AI应用 SECRET_KEY
     */
    private String secretKey;

    private Integer connectTimeout = 5000;

    private Integer readTimeout = 30000;

    private Long maxFileSize = 10485760L;
}
