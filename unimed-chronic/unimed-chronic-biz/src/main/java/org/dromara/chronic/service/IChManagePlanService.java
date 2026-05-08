package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChManagePlanBo;
import org.dromara.chronic.domain.vo.ChManagePlanVo;

import java.util.List;

/**
 * 管理方案服务
 *
 * @author unimed
 */
public interface IChManagePlanService {

    Long createPlan(ChManagePlanBo bo);

    Boolean updatePlan(ChManagePlanBo bo);

    Boolean enablePlan(Long planId);

    Boolean disablePlan(Long planId);

    List<ChManagePlanVo> queryByPatientId(Long patientId);

    /**
     * 查询当前生效管理方案（planStatus=ACTIVE）
     *
     * @param patientId 患者ID
     * @return 当前生效方案（含子项），无则返回 null
     */
    ChManagePlanVo queryCurrentPlan(Long patientId);
}
