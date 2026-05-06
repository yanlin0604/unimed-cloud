package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.MedicalDocumentOcrTaskBo;
import org.dromara.chronic.domain.entity.ChMedicalDocumentOcrArchiveDraft;
import org.dromara.chronic.domain.entity.ChMedicalDocumentOcrMetricItem;
import org.dromara.chronic.domain.entity.ChMedicalDocumentOcrReportItem;
import org.dromara.chronic.domain.entity.ChMedicalDocumentOcrTask;
import org.dromara.chronic.domain.vo.MedicalDocumentOcrArchiveDraftVo;
import org.dromara.chronic.domain.vo.MedicalDocumentOcrMetricItemVo;
import org.dromara.chronic.domain.vo.MedicalDocumentOcrReportItemVo;
import org.dromara.chronic.domain.vo.MedicalDocumentOcrTaskVo;
import org.dromara.chronic.mapper.ChMedicalDocumentOcrArchiveDraftMapper;
import org.dromara.chronic.mapper.ChMedicalDocumentOcrMetricItemMapper;
import org.dromara.chronic.mapper.ChMedicalDocumentOcrReportItemMapper;
import org.dromara.chronic.mapper.ChMedicalDocumentOcrTaskMapper;
import org.dromara.chronic.service.IMedicalDocumentOcrService;
import org.dromara.chronic.support.ocr.domain.MedicalDocumentOcrParseResult;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 医疗文档OCR任务服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class MedicalDocumentOcrServiceImpl implements IMedicalDocumentOcrService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_RECOGNIZING = "RECOGNIZING";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_DISCARDED = "DISCARDED";

    private final ChMedicalDocumentOcrTaskMapper taskMapper;
    private final ChMedicalDocumentOcrArchiveDraftMapper archiveDraftMapper;
    private final ChMedicalDocumentOcrMetricItemMapper metricItemMapper;
    private final ChMedicalDocumentOcrReportItemMapper reportItemMapper;

    @Override
    public Long createTask(MedicalDocumentOcrTaskBo bo) {
        ChMedicalDocumentOcrTask entity = MapstructUtils.convert(bo, ChMedicalDocumentOcrTask.class);
        entity.setStatus(STATUS_PENDING);
        taskMapper.insert(entity);
        return entity.getTaskId();
    }

    @Override
    public MedicalDocumentOcrTaskVo querySuccessByFileMd5(Long patientId, String fileMd5) {
        if (StringUtils.isBlank(fileMd5)) {
            return null;
        }
        LambdaQueryWrapper<ChMedicalDocumentOcrTask> lqw = Wrappers.<ChMedicalDocumentOcrTask>lambdaQuery()
            .eq(ChMedicalDocumentOcrTask::getFileMd5, fileMd5)
            .eq(ChMedicalDocumentOcrTask::getStatus, STATUS_SUCCESS)
            .eq(ObjectUtil.isNotNull(patientId), ChMedicalDocumentOcrTask::getPatientId, patientId)
            .orderByDesc(ChMedicalDocumentOcrTask::getCreateTime)
            .last("limit 1");
        return taskMapper.selectVoOne(lqw);
    }

    @Override
    public MedicalDocumentOcrTaskVo queryById(Long taskId) {
        MedicalDocumentOcrTaskVo vo = taskMapper.selectVoById(taskId);
        if (vo == null) {
            return null;
        }
        vo.setArchiveDraft(archiveDraftMapper.selectVoOne(
            Wrappers.<ChMedicalDocumentOcrArchiveDraft>lambdaQuery().eq(ChMedicalDocumentOcrArchiveDraft::getTaskId, taskId).last("limit 1")
        ));
        vo.setMetricItems(metricItemMapper.selectVoList(
            Wrappers.<ChMedicalDocumentOcrMetricItem>lambdaQuery().eq(ChMedicalDocumentOcrMetricItem::getTaskId, taskId).orderByAsc(ChMedicalDocumentOcrMetricItem::getId)
        ));
        vo.setReportItems(reportItemMapper.selectVoList(
            Wrappers.<ChMedicalDocumentOcrReportItem>lambdaQuery().eq(ChMedicalDocumentOcrReportItem::getTaskId, taskId).orderByAsc(ChMedicalDocumentOcrReportItem::getId)
        ));
        return vo;
    }

    @Override
    public TableDataInfo<MedicalDocumentOcrTaskVo> queryPageList(MedicalDocumentOcrTaskBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChMedicalDocumentOcrTask> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChMedicalDocumentOcrTask::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getDocumentType()), ChMedicalDocumentOcrTask::getDocumentType, bo.getDocumentType());
        lqw.eq(StringUtils.isNotBlank(bo.getSourceType()), ChMedicalDocumentOcrTask::getSourceType, bo.getSourceType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ChMedicalDocumentOcrTask::getStatus, bo.getStatus());
        lqw.ge(ObjectUtil.isNotNull(bo.getBeginCreateTime()), ChMedicalDocumentOcrTask::getCreateTime, bo.getBeginCreateTime());
        lqw.le(ObjectUtil.isNotNull(bo.getEndCreateTime()), ChMedicalDocumentOcrTask::getCreateTime, bo.getEndCreateTime());
        lqw.orderByDesc(ChMedicalDocumentOcrTask::getCreateTime);
        Page<MedicalDocumentOcrTaskVo> page = taskMapper.selectVoPage(pageQuery.build(), lqw);
        page.getRecords().forEach(item -> item.setRawOcrJson(null));
        return TableDataInfo.build(page);
    }

    @Override
    public Void markRecognizing(Long taskId) {
        updateStatus(taskId, STATUS_RECOGNIZING, null, null, null);
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void markSuccess(Long taskId, MedicalDocumentOcrParseResult result) {
        ChMedicalDocumentOcrTask task = requireTask(taskId);
        task.setStatus(STATUS_SUCCESS);
        task.setRawOcrJson(result.getRawOcrJson());
        task.setErrorCode(null);
        task.setErrorMsg(null);
        taskMapper.updateById(task);
        if (result.getArchiveDraft() != null) {
            result.getArchiveDraft().setTaskId(taskId);
            archiveDraftMapper.insert(result.getArchiveDraft());
        }
        if (CollUtil.isNotEmpty(result.getMetricItems())) {
            for (ChMedicalDocumentOcrMetricItem item : result.getMetricItems()) {
                item.setTaskId(taskId);
                metricItemMapper.insert(item);
            }
        }
        if (CollUtil.isNotEmpty(result.getReportItems())) {
            for (ChMedicalDocumentOcrReportItem item : result.getReportItems()) {
                item.setTaskId(taskId);
                reportItemMapper.insert(item);
            }
        }
        return null;
    }

    @Override
    public Void markFailed(Long taskId, String errorCode, String errorMsg, String rawOcrJson) {
        updateStatus(taskId, STATUS_FAILED, errorCode, errorMsg, rawOcrJson);
        return null;
    }

    @Override
    public Void markConfirmed(Long taskId, Long patientId, Integer metricCount, Long examId) {
        ChMedicalDocumentOcrTask task = requireTask(taskId);
        task.setStatus(STATUS_CONFIRMED);
        task.setConfirmedPatientId(patientId);
        task.setConfirmedMetricCount(metricCount == null ? 0 : metricCount);
        task.setConfirmedExamId(examId);
        task.setConfirmedTime(new Date());
        taskMapper.updateById(task);
        return null;
    }

    @Override
    public Void discard(Long taskId) {
        ChMedicalDocumentOcrTask task = requireTask(taskId);
        if (STATUS_CONFIRMED.equals(task.getStatus())) {
            throw new ServiceException("已确认的OCR任务不能废弃");
        }
        task.setStatus(STATUS_DISCARDED);
        taskMapper.updateById(task);
        return null;
    }

    private void updateStatus(Long taskId, String status, String errorCode, String errorMsg, String rawOcrJson) {
        ChMedicalDocumentOcrTask task = requireTask(taskId);
        task.setStatus(status);
        task.setErrorCode(errorCode);
        task.setErrorMsg(errorMsg);
        task.setRawOcrJson(rawOcrJson);
        taskMapper.updateById(task);
    }

    private ChMedicalDocumentOcrTask requireTask(Long taskId) {
        ChMedicalDocumentOcrTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("OCR任务不存在");
        }
        return task;
    }
}
