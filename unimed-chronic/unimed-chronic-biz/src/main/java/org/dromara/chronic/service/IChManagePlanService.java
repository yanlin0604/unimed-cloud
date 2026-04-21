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
}
