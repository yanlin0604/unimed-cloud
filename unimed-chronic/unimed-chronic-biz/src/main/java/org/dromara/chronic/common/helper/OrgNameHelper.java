package org.dromara.chronic.common.helper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.system.api.RemoteDeptService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 组织/机构名称批量查询工具
 * <p>
 * 本系统中 orgId 对应系统部门体系，通过 RemoteDeptService 查询名称
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrgNameHelper {

    @DubboReference
    private RemoteDeptService remoteDeptService;

    public Map<Long, String> batchGetOrgName(List<Long> orgIds) {
        if (orgIds == null || orgIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> ids = orgIds.stream().distinct().collect(java.util.stream.Collectors.toList());
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        try {
            return remoteDeptService.selectDeptNamesByIds(ids);
        } catch (Exception e) {
            log.warn("批量查询组织名称失败: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}
