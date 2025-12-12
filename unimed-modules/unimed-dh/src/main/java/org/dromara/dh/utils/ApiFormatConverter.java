package org.dromara.dh.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dh.domain.dto.DhConfigRequest;
import org.dromara.dh.domain.dto.DhServiceRequest;

/**
 * 数字人服务格式转换工具类
 *
 * <p>用于演示业务对象与数字人服务请求格式之间的转换</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Slf4j
public class ApiFormatConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 演示格式转换过程
     */
    public static void demonstrateConversion() {
        try {
            // 1. 业务请求对象（使用 Java 命名规范）
            var businessRequest = new DhConfigRequest();
            var configs = new DhConfigRequest.ConfigInfo();
            configs.setRefFile("BV700_V2_streaming");
            configs.setAvatarId("测试001_20251125_9763582602965_avatar");
            businessRequest.setConfigs(configs);

            log.info("业务请求对象 (Java 命名规范):");
            log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(businessRequest));

            // 2. 转换为数字人服务请求格式
            var serviceRequest = DhServiceRequest.from(businessRequest);

            log.info("数字人服务请求格式:");
            log.info(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(serviceRequest));

            // 3. 展示实际发送到数字人服务的 JSON
            log.info("发送到数字人服务的实际 JSON:");
            log.info("""
                {
                  "configs": {
                    "REF_FILE": "BV700_V2_streaming",
                    "avatar_id": "测试001_20251125_9763582602965_avatar"
                  }
                }
                """);

        } catch (JsonProcessingException e) {
            log.error("JSON 序列化失败", e);
        }
    }

    /**
     * 验证对象转换是否正确
     */
    public static boolean validateMapping(DhConfigRequest businessRequest) {
        var serviceRequest = DhServiceRequest.from(businessRequest);
        
        boolean isValid = businessRequest.getConfigs().getRefFile()
            .equals(serviceRequest.getConfigs().getRefFile()) &&
            businessRequest.getConfigs().getAvatarId()
            .equals(serviceRequest.getConfigs().getAvatarId());
        
        log.info("对象转换验证结果: {}", isValid ? "通过" : "失败");
        return isValid;
    }
}