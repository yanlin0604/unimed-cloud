package org.dromara.dh.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 数字人服务请求 DTO
 *
 * <p>专门用于调用数字人服务的请求格式，字段命名符合数字人服务的要求</p>
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
public class DhServiceRequest {

    private ConfigInfo configs;

    /**
     * 数字人服务配置信息
     */
    @Data
    public static class ConfigInfo {

        @JsonProperty("REF_FILE")
        private String refFile;

        @JsonProperty("avatar_id")
        private String avatarId;
    }

    /**
     * 将业务请求转换为数字人服务所需的请求格式
     * 
     * @param businessRequest 业务请求对象
     * @return 数字人服务请求对象
     */
    public static DhServiceRequest from(DhConfigRequest businessRequest) {
        var serviceRequest = new DhServiceRequest();
        var serviceConfigs = new ConfigInfo();
        
        serviceConfigs.setRefFile(businessRequest.getConfigs().getRefFile());
        serviceConfigs.setAvatarId(businessRequest.getConfigs().getAvatarId());
        
        serviceRequest.setConfigs(serviceConfigs);
        return serviceRequest;
    }
}