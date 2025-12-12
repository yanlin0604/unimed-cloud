package org.dromara.dh.service;

import org.dromara.dh.domain.dto.DhConfigRequest;
import org.dromara.dh.domain.dto.DhConfigResponse;
import org.dromara.dh.domain.dto.DigitalHumanListRequest;
import org.dromara.dh.domain.dto.DigitalHumanListResponse;
import org.dromara.dh.domain.dto.DigitalHumanDeleteResponse;
import reactor.core.publisher.Mono;

/**
 * 数字人服务接口
 *
 * <p>负责与数字人服务进行交互</p>
 *
 * @author unimed
 * @since 2.5.1
 */
public interface IDigitalHumanApiService {

    /**
     * 保存数字人配置
     *
     * @param request 配置请求（Java 驼峰格式）
     * @return 配置响应
     */
    Mono<DhConfigResponse> saveDigitalHumanConfig(DhConfigRequest request);

    /**
     * 查询数字人列表
     *
     * @param request 查询请求
     * @return 数字人列表响应
     */
    Mono<DigitalHumanListResponse> getDigitalHumanList(DigitalHumanListRequest request);

    /**
     * 删除数字人
     *
     * @param digitalHumanId 数字人ID
     * @return 删除响应
     */
    Mono<DigitalHumanDeleteResponse> deleteDigitalHuman(String digitalHumanId);
}