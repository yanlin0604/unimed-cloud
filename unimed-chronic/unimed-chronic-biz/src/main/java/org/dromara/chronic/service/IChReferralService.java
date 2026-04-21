package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChArchiveShareApplyBo;
import org.dromara.chronic.domain.bo.ChReferralRecordBo;
import org.dromara.chronic.domain.vo.ChArchiveShareApplyVo;
import org.dromara.chronic.domain.vo.ChReferralRecordVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 转诊服务
 *
 * @author unimed
 */
public interface IChReferralService {

    Long createReferral(ChReferralRecordBo bo);

    ChReferralRecordVo queryById(Long referralId);

    TableDataInfo<ChReferralRecordVo> queryPageList(ChReferralRecordBo bo, PageQuery pageQuery);

    List<ChReferralRecordVo> queryByPatientId(Long patientId);

    Void updateStatus(Long referralId, String newStatus);

    Long applyArchiveShare(ChArchiveShareApplyBo bo);

    ChArchiveShareApplyVo queryApplyById(Long id);

    TableDataInfo<ChArchiveShareApplyVo> queryApplyPageList(ChArchiveShareApplyBo bo, PageQuery pageQuery);

    Void approveArchiveShare(Long id, String approvalStatus);

    void logSync(String syncType, String syncDirection, String externalSystem, String syncStatus, String syncDetail);
}
