package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChPatientCloseApplyBo;
import org.dromara.chronic.domain.vo.ChPatientCloseApplyVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 患者结案申请服务接口
 *
 * @author unimed
 */
public interface IChPatientCloseApplyService {

    /**
     * 发起结案申请
     *
     * @param bo 申请业务对象
     * @return 申请ID
     */
    Long applyClose(ChPatientCloseApplyBo bo);

    /**
     * 审核结案申请（通过/驳回/撤回）
     *
     * @param bo 审核业务对象（applyId、auditStatus 必填）
     */
    void auditClose(ChPatientCloseApplyBo bo);

    /**
     * 分页查询结案申请
     */
    TableDataInfo<ChPatientCloseApplyVo> queryPageList(ChPatientCloseApplyBo bo, PageQuery pageQuery);

    /**
     * 查询申请详情
     */
    ChPatientCloseApplyVo queryById(Long applyId);

    /**
     * 查询患者最新一条结案申请（用于列表回显状态）
     */
    ChPatientCloseApplyVo queryLatestByPatient(Long patientId);
}
