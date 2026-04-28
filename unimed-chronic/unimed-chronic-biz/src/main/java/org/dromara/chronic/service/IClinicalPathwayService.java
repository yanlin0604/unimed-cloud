package org.dromara.chronic.service;

import org.dromara.chronic.domain.vo.PathwayProgressVo;

/**
 * 临床管理路径进度 Service 接口
 *
 * @author unimed
 */
public interface IClinicalPathwayService {

    /**
     * 获取患者的临床路径进度聚合数据
     *
     * @param patientId   患者ID
     * @param diseaseCode 病种编码（可选，不传则查主病种）
     * @return 路径进度聚合视图
     */
    PathwayProgressVo getPathwayProgress(Long patientId, String diseaseCode);
}
