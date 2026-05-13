package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.OcrTaskBo;
import org.dromara.chronic.domain.entity.ChOcrArchiveDraft;
import org.dromara.chronic.domain.entity.ChOcrMetricItem;
import org.dromara.chronic.domain.entity.ChOcrReportItem;
import org.dromara.chronic.domain.entity.ChOcrTask;
import org.dromara.chronic.domain.vo.OcrArchiveDraftVo;
import org.dromara.chronic.domain.vo.OcrMetricItemVo;
import org.dromara.chronic.domain.vo.OcrReportItemVo;
import org.dromara.chronic.domain.vo.OcrTaskVo;
import org.dromara.chronic.mapper.ChOcrArchiveDraftMapper;
import org.dromara.chronic.mapper.ChOcrMetricItemMapper;
import org.dromara.chronic.mapper.ChOcrReportItemMapper;
import org.dromara.chronic.mapper.ChOcrTaskMapper;
import org.dromara.chronic.service.IOcrService;
import org.dromara.chronic.support.ocr.OcrDraftDataConverter;
import org.dromara.chronic.support.ocr.domain.OcrParseResult;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 医疗文档OCR任务服务实现
 * <p>
 * 表名重定向后：3 个 Draft Entity 物理上共用 ch_ocr_draft 表，
 * 所有查询必须显式带上 draft_category 过滤，防止跨类型脏读。
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class OcrServiceImpl implements IOcrService {

    private static final String STATUS_PENDING = "PENDING";
    /** 设计书规范状态：原 RECOGNIZING → PROCESSING */
    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String STATUS_FAILED = "FAILED";
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_DISCARDED = "DISCARDED";

    private static final String DRAFT_CATEGORY_PROFILE = "PROFILE";
    private static final String DRAFT_CATEGORY_METRIC = "METRIC";
    private static final String DRAFT_CATEGORY_REPORT = "REPORT";

    private final ChOcrTaskMapper taskMapper;
    private final ChOcrArchiveDraftMapper archiveDraftMapper;
    private final ChOcrMetricItemMapper metricItemMapper;
    private final ChOcrReportItemMapper reportItemMapper;
    private final OcrDraftDataConverter draftDataConverter;

    @Override
    public Long createTask(OcrTaskBo bo) {
        ChOcrTask entity = MapstructUtils.convert(bo, ChOcrTask.class);
        entity.setStatus(STATUS_PENDING);
        taskMapper.insert(entity);
        return entity.getTaskId();
    }

    @Override
    public OcrTaskVo querySuccessByFileMd5(Long patientId, String fileMd5) {
        if (StringUtils.isBlank(fileMd5)) {
            return null;
        }
        LambdaQueryWrapper<ChOcrTask> lqw = Wrappers.<ChOcrTask>lambdaQuery()
            .eq(ChOcrTask::getFileMd5, fileMd5)
            .eq(ChOcrTask::getStatus, STATUS_SUCCESS)
            .eq(ObjectUtil.isNotNull(patientId), ChOcrTask::getPatientId, patientId)
            .orderByDesc(ChOcrTask::getCreateTime)
            .last("limit 1");
        return taskMapper.selectVoOne(lqw);
    }

    @Override
    public OcrTaskVo queryById(Long taskId) {
        OcrTaskVo vo = taskMapper.selectVoById(taskId);
        if (vo == null) {
            return null;
        }
        // 三个 Draft Entity 共表查询，必须带 draft_category 过滤
        OcrArchiveDraftVo archiveVo = archiveDraftMapper.selectVoOne(
            Wrappers.<ChOcrArchiveDraft>lambdaQuery()
                .eq(ChOcrArchiveDraft::getTaskId, taskId)
                .eq(ChOcrArchiveDraft::getDraftCategory, DRAFT_CATEGORY_PROFILE)
                .last("limit 1")
        );
        // 解包 draftData → profileDraftJson/diseaseDraftJson/rawItemJson 兼容业务字段
        draftDataConverter.unpackArchive(archiveVo);
        vo.setArchiveDraft(archiveVo);
        vo.setMetricItems(metricItemMapper.selectVoList(
            Wrappers.<ChOcrMetricItem>lambdaQuery()
                .eq(ChOcrMetricItem::getTaskId, taskId)
                .eq(ChOcrMetricItem::getDraftCategory, DRAFT_CATEGORY_METRIC)
                .orderByAsc(ChOcrMetricItem::getId)
        ));
        vo.setReportItems(reportItemMapper.selectVoList(
            Wrappers.<ChOcrReportItem>lambdaQuery()
                .eq(ChOcrReportItem::getTaskId, taskId)
                .eq(ChOcrReportItem::getDraftCategory, DRAFT_CATEGORY_REPORT)
                .orderByAsc(ChOcrReportItem::getId)
        ));
        return vo;
    }

    @Override
    public TableDataInfo<OcrTaskVo> queryPageList(OcrTaskBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChOcrTask> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChOcrTask::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getDocumentType()), ChOcrTask::getDocumentType, bo.getDocumentType());
        lqw.eq(StringUtils.isNotBlank(bo.getSourceType()), ChOcrTask::getSourceType, bo.getSourceType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ChOcrTask::getStatus, bo.getStatus());
        lqw.ge(ObjectUtil.isNotNull(bo.getBeginCreateTime()), ChOcrTask::getCreateTime, bo.getBeginCreateTime());
        lqw.le(ObjectUtil.isNotNull(bo.getEndCreateTime()), ChOcrTask::getCreateTime, bo.getEndCreateTime());
        lqw.orderByDesc(ChOcrTask::getCreateTime);
        Page<OcrTaskVo> page = taskMapper.selectVoPage(pageQuery.build(), lqw);
        page.getRecords().forEach(item -> item.setRawOcrJson(null));
        return TableDataInfo.build(page);
    }

    @Override
    public Void markRecognizing(Long taskId) {
        updateStatus(taskId, STATUS_PROCESSING, null, null, null);
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void markSuccess(Long taskId, OcrParseResult result) {
        ChOcrTask task = requireTask(taskId);
        task.setStatus(STATUS_SUCCESS);
        task.setRawOcrJson(result.getRawOcrJson());
        task.setErrorCode(null);
        task.setErrorMsg(null);
        taskMapper.updateById(task);
        if (result.getArchiveDraft() != null) {
            result.getArchiveDraft().setTaskId(taskId);
            // 写入前把 profileDraftJson/diseaseDraftJson/rawItemJson 打包到 draftData
            draftDataConverter.packArchive(result.getArchiveDraft());
            archiveDraftMapper.insert(result.getArchiveDraft());
        }
        if (CollUtil.isNotEmpty(result.getMetricItems())) {
            for (ChOcrMetricItem item : result.getMetricItems()) {
                item.setTaskId(taskId);
                metricItemMapper.insert(item);
            }
        }
        if (CollUtil.isNotEmpty(result.getReportItems())) {
            for (ChOcrReportItem item : result.getReportItems()) {
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
        ChOcrTask task = requireTask(taskId);
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
        ChOcrTask task = requireTask(taskId);
        if (STATUS_CONFIRMED.equals(task.getStatus())) {
            throw new ServiceException("已确认的OCR任务不能废弃");
        }
        task.setStatus(STATUS_DISCARDED);
        taskMapper.updateById(task);
        return null;
    }

    private void updateStatus(Long taskId, String status, String errorCode, String errorMsg, String rawOcrJson) {
        ChOcrTask task = requireTask(taskId);
        task.setStatus(status);
        task.setErrorCode(errorCode);
        task.setErrorMsg(errorMsg);
        task.setRawOcrJson(rawOcrJson);
        taskMapper.updateById(task);
    }

    private ChOcrTask requireTask(Long taskId) {
        ChOcrTask task = taskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("OCR任务不存在");
        }
        return task;
    }
}
