package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.chronic.common.helper.DiseaseNameHelper;
import org.dromara.chronic.domain.bo.*;
import org.dromara.chronic.domain.dto.FollowupContentJson;
import org.dromara.chronic.domain.entity.*;
import org.dromara.chronic.domain.vo.*;
import org.dromara.chronic.manager.HealthMetricManager;
import org.dromara.chronic.mapper.*;
import org.dromara.chronic.service.IChFollowupService;
import org.dromara.chronic.service.IChMessageSessionService;
import org.dromara.chronic.service.IChNotificationTemplateService;
import org.dromara.chronic.support.FollowupOverdueRefresher;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.resource.api.RemoteMessageService;
import org.dromara.resource.api.RemoteSmsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 随访服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChFollowupServiceImpl implements IChFollowupService {

    private final ChFollowupPlanMapper followupPlanMapper;
    private final ChFollowupPlanItemMapper followupPlanItemMapper;
    private final ChFollowupTaskMapper followupTaskMapper;
    private final ChFollowupRecordMapper followupRecordMapper;
    private final ChFollowupQuestionnaireMapper questionnaireMapper;
    private final ChFollowupAnswerMapper answerMapper;
    private final ChPatientProfileMapper patientProfileMapper;
    private final ChPatientTimelineMapper patientTimelineMapper;
    private final ChHealthMetricRecordMapper healthMetricRecordMapper;
    private final ChMedicationRecordMapper medicationRecordMapper;
    private final IChNotificationTemplateService notificationTemplateService;
    private final HealthMetricManager healthMetricManager;
    private final DiseaseNameHelper diseaseNameHelper;
    private final FollowupOverdueRefresher overdueRefresher;
    private final IChMessageSessionService messageSessionService;
    private final org.dromara.chronic.support.rule.FollowupDynamicAdjuster dynamicAdjuster;
    private final ObjectMapper objectMapper;

    @DubboReference(mock = "org.dromara.resource.api.RemoteMessageServiceStub")
    private RemoteMessageService remoteMessageService;

    @DubboReference(mock = "true")
    private RemoteSmsService remoteSmsService;

    /** 支持的三种标准随访类型：线上、线下、电话 */
    private static final Set<String> VALID_VISIT_TYPES = Set.of("ONLINE", "OFFLINE", "PHONE");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPlan(ChFollowupPlanBo bo) {
        validatePlan(bo);
        ChFollowupPlan plan = MapstructUtils.convert(bo, ChFollowupPlan.class);
        if (plan.getPlanStatus() == null) {
            plan.setPlanStatus("ACTIVE");
        }
        followupPlanMapper.insert(plan);
        savePlanItems(plan.getPlanId(), bo.getItemList());
        generateTasks(plan, bo.getItemList());
        return plan.getPlanId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<Long> createBatchPlans(ChFollowupPlanBatchBo batchBo) {
        if (CollUtil.isEmpty(batchBo.getPatientIds())) {
            throw new ServiceException("患者列表不能为空");
        }
        List<Long> planIds = new ArrayList<>();
        for (Long patientId : batchBo.getPatientIds()) {
            ChFollowupPlanBo bo = new ChFollowupPlanBo();
            bo.setPatientId(patientId);
            bo.setDiseaseCode(batchBo.getDiseaseCode());
            bo.setCycleDays(batchBo.getCycleDays());
            bo.setTotalRounds(batchBo.getTotalRounds());
            bo.setPlanStatus(batchBo.getPlanStatus());
            bo.setAssigneeUserId(batchBo.getAssigneeUserId());
            bo.setItemList(batchBo.getItemList());
            planIds.add(createPlan(bo));
        }
        return planIds;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlan(ChFollowupPlanBo bo) {
        if (bo.getPlanId() == null) {
            throw new ServiceException("计划ID不能为空");
        }
        validatePlan(bo);
        ChFollowupPlan current = followupPlanMapper.selectById(bo.getPlanId());
        if (current == null) {
            throw new ServiceException("随访计划不存在");
        }
        if (Set.of("COMPLETED", "HISTORY").contains(current.getPlanStatus())) {
            throw new ServiceException("已完成或历史计划不能修改");
        }
        if (ObjectUtil.defaultIfNull(current.getCurrentRound(), 0) > bo.getTotalRounds()) {
            throw new ServiceException("总轮次不能小于当前已完成轮次");
        }
        ChFollowupPlan plan = MapstructUtils.convert(bo, ChFollowupPlan.class);
        if (plan.getPlanStatus() == null) {
            plan.setPlanStatus(current.getPlanStatus());
        }
        plan.setCurrentRound(current.getCurrentRound());
        followupPlanMapper.updateById(plan);
        followupPlanItemMapper.delete(
            Wrappers.<ChFollowupPlanItem>lambdaQuery().eq(ChFollowupPlanItem::getPlanId, bo.getPlanId()));
        savePlanItems(bo.getPlanId(), bo.getItemList());
        syncUnfinishedTasks(plan, bo.getItemList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatchPlans(List<ChFollowupPlanBo> planList) {
        if (CollUtil.isEmpty(planList)) {
            throw new ServiceException("随访计划列表不能为空");
        }
        planList.forEach(this::updatePlan);
    }

    @Override
    public TableDataInfo<ChFollowupPlanVo> queryPlanPage(Long patientId, String diseaseCode,
                                                           Long assigneeUserId,
                                                           String planStatus, PageQuery pageQuery) {
        Page<ChFollowupPlanVo> page = followupPlanMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.<ChFollowupPlan>lambdaQuery()
                .eq(ObjectUtil.isNotNull(patientId), ChFollowupPlan::getPatientId, patientId)
                .eq(StringUtils.isNotBlank(diseaseCode), ChFollowupPlan::getDiseaseCode, diseaseCode)
                .eq(ObjectUtil.isNotNull(assigneeUserId), ChFollowupPlan::getAssigneeUserId, assigneeUserId)
                .eq(StringUtils.isNotBlank(planStatus), ChFollowupPlan::getPlanStatus, planStatus)
                .orderByDesc(ChFollowupPlan::getCreateTime));
        fillFollowupPlanNames(page.getRecords());
        fillPlanPatientNames(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlanStatus(Long planId, String planStatus) {
        if (!Set.of("ACTIVE", "DISABLED").contains(planStatus)) {
            throw new ServiceException("仅支持启用或停用随访计划");
        }
        ChFollowupPlan plan = followupPlanMapper.selectById(planId);
        if (plan == null) {
            throw new ServiceException("随访计划不存在");
        }
        if (Set.of("COMPLETED", "HISTORY").contains(plan.getPlanStatus())) {
            throw new ServiceException("已完成或历史计划不能变更状态");
        }
        plan.setPlanStatus(planStatus);
        followupPlanMapper.updateById(plan);
        if ("DISABLED".equals(planStatus)) {
            // 停用计划时级联取消未完成任务
            followupTaskMapper.update(null,
                Wrappers.<ChFollowupTask>lambdaUpdate()
                    .set(ChFollowupTask::getTaskStatus, "CANCELLED")
                    .eq(ChFollowupTask::getPlanId, planId)
                    .notIn(ChFollowupTask::getTaskStatus, List.of("DONE", "CANCELLED")));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatchPlanStatus(List<Long> planIds, String planStatus) {
        if (CollUtil.isEmpty(planIds)) {
            throw new ServiceException("随访计划ID列表不能为空");
        }
        for (Long planId : planIds) {
            updatePlanStatus(planId, planStatus);
        }
    }

    @Override
    public TableDataInfo<ChFollowupTaskVo> queryTaskPage(Long patientId, Long assigneeUserId,
                                                           String taskStatus, String visitType,
                                                           Date beginDate, Date endDate,
                                                           PageQuery pageQuery) {
        overdueRefresher.refreshIfNeeded();
        Page<ChFollowupTaskVo> page = followupTaskMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ObjectUtil.isNotNull(patientId), ChFollowupTask::getPatientId, patientId)
                .eq(ObjectUtil.isNotNull(assigneeUserId), ChFollowupTask::getAssigneeUserId, assigneeUserId)
                .eq(StringUtils.isNotBlank(taskStatus), ChFollowupTask::getTaskStatus, taskStatus)
                .eq(StringUtils.isNotBlank(visitType), ChFollowupTask::getVisitType, visitType)
                .ge(ObjectUtil.isNotNull(beginDate), ChFollowupTask::getPlanDueDate, beginDate)
                .le(ObjectUtil.isNotNull(endDate), ChFollowupTask::getPlanDueDate, endDate)
                .orderByAsc(ChFollowupTask::getPlanDueDate));
        fillTaskMetadata(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<ChFollowupTaskVo> queryTaskPoolPage(String diseaseCode, String visitType, PageQuery pageQuery) {
        overdueRefresher.refreshIfNeeded();
        // 随访任务池：assignee_user_id 为空，且状态为 PENDING / REMINDING / OVERDUE
        var wrapper = Wrappers.<ChFollowupTask>lambdaQuery()
            .isNull(ChFollowupTask::getAssigneeUserId)
            .in(ChFollowupTask::getTaskStatus, List.of("PENDING", "REMINDING", "OVERDUE"))
            .eq(StringUtils.isNotBlank(visitType), ChFollowupTask::getVisitType, visitType);

        if (StringUtils.isNotBlank(diseaseCode)) {
            List<ChFollowupPlan> plans = followupPlanMapper.selectList(
                Wrappers.<ChFollowupPlan>lambdaQuery().eq(ChFollowupPlan::getDiseaseCode, diseaseCode));
            if (plans.isEmpty()) {
                return TableDataInfo.build(new ArrayList<>());
            }
            List<Long> planIds = plans.stream().map(ChFollowupPlan::getPlanId).toList();
            wrapper.in(ChFollowupTask::getPlanId, planIds);
        }

        wrapper.orderByAsc(ChFollowupTask::getPlanDueDate);
        Page<ChFollowupTaskVo> page = followupTaskMapper.selectVoPage(pageQuery.build(), wrapper);
        fillTaskMetadata(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claimTask(Long taskId, Long userId) {
        if (userId == null) {
            throw new ServiceException("未获取到当前认领人信息");
        }
        ChFollowupTask task = followupTaskMapper.selectOne(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getTaskId, taskId)
                .last("for update"));
        if (task == null) {
            throw new ServiceException("随访任务不存在");
        }
        if (Set.of("DONE", "CANCELLED").contains(task.getTaskStatus())) {
            throw new ServiceException("该任务已结束，无法认领");
        }
        if (task.getAssigneeUserId() != null) {
            throw new ServiceException("该任务已被其他执行人认领或指派");
        }
        task.setAssigneeUserId(userId);
        followupTaskMapper.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchClaimTasks(List<Long> taskIds, Long userId) {
        if (CollUtil.isEmpty(taskIds)) {
            throw new ServiceException("认领任务列表不能为空");
        }
        if (userId == null) {
            throw new ServiceException("未获取到当前认领人信息");
        }
        for (Long taskId : taskIds) {
            claimTask(taskId, userId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTask(Long taskId, Long assigneeUserId) {
        if (assigneeUserId == null) {
            throw new ServiceException("指派执行人不能为空");
        }
        ChFollowupTask task = followupTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("随访任务不存在");
        }
        if (Set.of("DONE", "CANCELLED").contains(task.getTaskStatus())) {
            throw new ServiceException("已结束的任务不能重新指派");
        }
        task.setAssigneeUserId(assigneeUserId);
        followupTaskMapper.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAssignTasks(List<Long> taskIds, Long assigneeUserId) {
        if (CollUtil.isEmpty(taskIds)) {
            throw new ServiceException("指派任务列表不能为空");
        }
        if (assigneeUserId == null) {
            throw new ServiceException("指派执行人不能为空");
        }
        for (Long taskId : taskIds) {
            assignTask(taskId, assigneeUserId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseTask(Long taskId, Long userId) {
        ChFollowupTask task = followupTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("随访任务不存在");
        }
        if (Set.of("DONE", "CANCELLED").contains(task.getTaskStatus())) {
            throw new ServiceException("已结束的任务无法释放");
        }
        task.setAssigneeUserId(null);
        followupTaskMapper.updateById(task);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTask(Long taskId) {
        cancelTask(taskId, null, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelTask(Long taskId, String cancelReasonCode, String cancelReasonDesc) {
        ChFollowupTask task = followupTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("随访任务不存在");
        }
        if ("DONE".equals(task.getTaskStatus())) {
            throw new ServiceException("已完成的任务不能取消");
        }
        if ("CANCELLED".equals(task.getTaskStatus())) {
            return;
        }
        task.setTaskStatus("CANCELLED");
        if (StringUtils.isNotBlank(cancelReasonCode)) {
            task.setCancelReasonCode(cancelReasonCode);
        }
        if (StringUtils.isNotBlank(cancelReasonDesc)) {
            task.setCancelReasonDesc(cancelReasonDesc);
        }
        followupTaskMapper.updateById(task);
        refreshPlanCompletion(task.getPlanId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long completeTask(Long taskId, ChFollowupSubmitBo bo, Long expectedPatientId,
                             Long expectedAssigneeUserId, Long visitorUserId, String forcedVisitType) {
        ChFollowupTask task = followupTaskMapper.selectOne(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getTaskId, taskId)
                .last("for update"));
        if (task == null) {
            throw new ServiceException("随访任务不存在");
        }
        if (expectedPatientId != null && !expectedPatientId.equals(task.getPatientId())) {
            throw new ServiceException("无权操作该患者随访任务");
        }
        if (expectedAssigneeUserId != null && !expectedAssigneeUserId.equals(task.getAssigneeUserId())) {
            throw new ServiceException("该任务未指派给当前执行人");
        }
        if (Set.of("DONE", "CANCELLED").contains(task.getTaskStatus())) {
            throw new ServiceException("随访任务已结束，不能重复提交");
        }

        ChFollowupRecord existing = followupRecordMapper.selectOne(
            Wrappers.<ChFollowupRecord>lambdaQuery()
                .eq(ChFollowupRecord::getTaskId, taskId)
                .last("limit 1 for update"));
        if (existing != null) {
            throw new ServiceException("该随访任务已经提交过记录");
        }

        ChFollowupQuestionnaire questionnaire = resolveQuestionnaire(task);
        // 医生/管理端评估完成时, 合并患者自填(PATIENT_FILLED)的体征/问卷/小结
        Map<String, Object> patientVital = null;
        List<ChFollowupAnswerInputBo> patientAnswers = null;
        String patientSummary = null;
        if ("PATIENT_FILLED".equals(task.getTaskStatus()) && StringUtils.isNotBlank(task.getPatientFillContent())) {
            try {
                JsonNode fillNode = objectMapper.readTree(task.getPatientFillContent());
                if (fillNode != null && fillNode.isObject()) {
                    if (fillNode.has("vitalSigns")) {
                        patientVital = objectMapper.convertValue(fillNode.get("vitalSigns"), Map.class);
                    }
                    if (fillNode.has("answers") && fillNode.get("answers").isArray()) {
                        List<ChFollowupAnswerInputBo> collectedAnswers = new ArrayList<>();
                        fillNode.get("answers").forEach(en -> {
                            if (en != null && en.isObject() && en.hasNonNull("questionId")) {
                                ChFollowupAnswerInputBo ans = new ChFollowupAnswerInputBo();
                                ans.setQuestionId(en.get("questionId").asText());
                                ans.setAnswerValue(en.path("answerValue").asText(""));
                                collectedAnswers.add(ans);
                            }
                        });
                        patientAnswers = collectedAnswers;
                    }
                    if (fillNode.has("summary")) {
                        patientSummary = fillNode.get("summary").asText();
                    }
                }
            } catch (Exception e) {
                log.warn("解析患者自填内容失败 taskId={}, err={}", taskId, e.getMessage());
            }
        }

        validateAnswers(bo, questionnaire);

        // 1. 结构化随访内容组装
        FollowupContentJson content = new FollowupContentJson();
        // 医生评估若无独立小结, 则回退采用患者自填小结; 若有则以医生为准
        content.setSummary(StringUtils.isNotBlank(bo.getVisitContent()) ? bo.getVisitContent() : patientSummary);
        // 体征: 医生评估体征优先, 缺失项用患者自填体征补齐
        Map<String, Object> mergedVital = mergeVitalSigns(patientVital, bo.getVitalSigns());
        content.setVitalSigns(mergedVital);
        content.setMedicationStatus(bo.getMedicationStatus());
        content.setAdherence(bo.getAdherence());
        content.setLifestyle(bo.getLifestyle());
        content.setRehabilitationStatus(bo.getRehabilitationStatus());
        content.setFollowupResult(bo.getFollowupResult());
        content.setRehabLevel(bo.getRehabLevel());
        content.setAdvice(bo.getAdvice());
        content.setFeedbackAdvice(bo.getFeedbackAdvice());
        content.setNextFollowupDate(bo.getNextFollowupDate());

        // 2. 插入随访记录
        ChFollowupRecord record = new ChFollowupRecord();
        record.setTaskId(taskId);
        record.setPatientId(task.getPatientId());
        record.setVisitType(StringUtils.isNotBlank(forcedVisitType) ? forcedVisitType : task.getVisitType());
        record.setVisitorUserId(visitorUserId != null ? visitorUserId : task.getAssigneeUserId());
        record.setVisitDate(new Date());
        record.setFollowupResult(bo.getFollowupResult());
        record.setRehabLevel(bo.getRehabLevel());
        record.setFeedbackAdvice(bo.getFeedbackAdvice());
        record.setUnsatisfiedReason(bo.getUnsatisfiedReason());
        record.setAdrDescription(bo.getAdrDescription());
        record.setIsReferralSuggested(bo.getIsReferralSuggested());
        try {
            record.setVisitContent(objectMapper.writeValueAsString(content));
        } catch (Exception e) {
            throw new ServiceException("随访内容格式化失败");
        }
        followupRecordMapper.insert(record);

        // 3. 问卷答案保存(合并患者自填答案与医生补充答案, 去重保留医生答案优先)
        saveAnswers(record.getRecordId(), questionnaire, mergeAnswers(patientAnswers, bo.getAnswers()));

        // 4. 自动提取健康体征指标并入库(合并后的体征) (核心:进入健康数据表,联动预警与达标判定)
        saveHealthMetricsFromFollowup(task.getPatientId(), mergedVital);

        // 5. 沉淀用药与康复病情到时间线
        recordPatientTimeline(task.getPatientId(), bo);

        // 6. 更新任务状态与方案轮次进度
        task.setTaskStatus("DONE");
        followupTaskMapper.updateById(task);
        updatePlanProgress(task);

        // 7. 动态调整状态机评估（控制不满意14天核查、连续不满意/转诊跟踪）
        if (dynamicAdjuster != null) {
            try {
                dynamicAdjuster.evaluateAndAdjust(task, record, bo);
            } catch (Exception e) {
                log.warn("随访动态调整评估失败 taskId={}, err={}", taskId, e.getMessage());
            }
        }

        return record.getRecordId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitSelfFill(Long taskId, ChFollowupSubmitBo bo, Long patientId, Long accountId, String forcedVisitType) {
        ChFollowupTask task = followupTaskMapper.selectOne(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getTaskId, taskId)
                .last("for update"));
        if (task == null) {
            throw new ServiceException("随访任务不存在");
        }
        if (patientId != null && !patientId.equals(task.getPatientId())) {
            throw new ServiceException("无权操作该患者随访任务");
        }
        if (Set.of("DONE", "CANCELLED", "PATIENT_FILLED").contains(task.getTaskStatus())) {
            throw new ServiceException("该任务当前状态不可自填");
        }
        // 患者可自填所有常规轮次任务(ONLINE/OFFLINE/PHONE/VIDEO),用于线下门诊前预填、电话/视频回访预填及线上自填;
        // 仅排除动态调整/转诊追踪/预警临时等医生专属任务类型,不开放患者自填。
        if (!"NORMAL".equals(task.getTaskType())) {
            throw new ServiceException("该任务需由医生执行,不可自填");
        }

        Map<String, Object> fill = new HashMap<>();
        fill.put("summary", bo.getVisitContent());
        fill.put("vitalSigns", bo.getVitalSigns());
        fill.put("questionnaireId", bo.getQuestionnaireId());
        fill.put("answers", bo.getAnswers());
        try {
            task.setPatientFillContent(objectMapper.writeValueAsString(fill));
        } catch (Exception e) {
            throw new ServiceException("患者自填内容格式化失败");
        }
        task.setPatientFillTime(new Date());
        task.setTaskStatus("PATIENT_FILLED");
        followupTaskMapper.updateById(task);
        return 0L;
    }

    /**
     * 合并体征: 医生评估体征优先, 患者自填体征补缺。二者均为空则返回空 Map。
     */
    private Map<String, Object> mergeVitalSigns(Map<String, Object> patientVital, Map<String, Object> doctorVital) {
        Map<String, Object> result = new LinkedHashMap<>();
        if (CollUtil.isNotEmpty(patientVital)) {
            result.putAll(patientVital);
        }
        if (CollUtil.isNotEmpty(doctorVital)) {
            result.putAll(doctorVital);
        }
        return result;
    }

    /**
     * 合并问卷答案: 医生答案优先, 患者答案补存未覆盖题目; 按 questionId 去重。
     */
    private List<ChFollowupAnswerInputBo> mergeAnswers(List<ChFollowupAnswerInputBo> patientAnswers,
                                                      List<ChFollowupAnswerInputBo> doctorAnswers) {
        if (CollUtil.isEmpty(patientAnswers)) {
            return doctorAnswers;
        }
        if (CollUtil.isEmpty(doctorAnswers)) {
            return patientAnswers;
        }
        Map<String, ChFollowupAnswerInputBo> map = new LinkedHashMap<>();
        for (ChFollowupAnswerInputBo a : patientAnswers) {
            if (a.getQuestionId() != null) {
                map.put(a.getQuestionId(), a);
            }
        }
        for (ChFollowupAnswerInputBo a : doctorAnswers) {
            if (a.getQuestionId() != null) {
                map.put(a.getQuestionId(), a);
            }
        }
        return new ArrayList<>(map.values());
    }

    /**
     * 从随访生命体征数据中解析并沉淀健康指标到 ch_health_metric_record
     */
    private void saveHealthMetricsFromFollowup(Long patientId, Map<String, Object> vitalSigns) {
        if (patientId == null || CollUtil.isEmpty(vitalSigns)) {
            return;
        }
        List<ChHealthMetricRecordBo> metricBoList = new ArrayList<>();

        // 血压解析 (收缩压/舒张压 或 组合字符串 "120/80")
        Object systolic = vitalSigns.get("systolicBp");
        if (systolic == null) systolic = vitalSigns.get("systolic");
        Object diastolic = vitalSigns.get("diastolicBp");
        if (diastolic == null) diastolic = vitalSigns.get("diastolic");
        Object bloodPressure = vitalSigns.get("bloodPressure");

        if (systolic != null && diastolic != null) {
            ChHealthMetricRecordBo sBo = new ChHealthMetricRecordBo();
            sBo.setPatientId(patientId);
            sBo.setMetricType("BP_SYSTOLIC");
            sBo.setMetricValue(String.valueOf(systolic));
            sBo.setUnit("mmHg");
            sBo.setMeasureScene("FOLLOWUP");
            sBo.setDataSource("MANUAL");
            metricBoList.add(sBo);

            ChHealthMetricRecordBo dBo = new ChHealthMetricRecordBo();
            dBo.setPatientId(patientId);
            dBo.setMetricType("BP_DIASTOLIC");
            dBo.setMetricValue(String.valueOf(diastolic));
            dBo.setUnit("mmHg");
            dBo.setMeasureScene("FOLLOWUP");
            dBo.setDataSource("MANUAL");
            metricBoList.add(dBo);
        } else if (bloodPressure != null) {
            String bpStr = String.valueOf(bloodPressure).trim();
            if (bpStr.contains("/")) {
                String[] parts = bpStr.split("/");
                if (parts.length == 2) {
                    ChHealthMetricRecordBo sBo = new ChHealthMetricRecordBo();
                    sBo.setPatientId(patientId);
                    sBo.setMetricType("BP_SYSTOLIC");
                    sBo.setMetricValue(parts[0].trim());
                    sBo.setUnit("mmHg");
                    sBo.setMeasureScene("FOLLOWUP");
                    sBo.setDataSource("MANUAL");
                    metricBoList.add(sBo);

                    ChHealthMetricRecordBo dBo = new ChHealthMetricRecordBo();
                    dBo.setPatientId(patientId);
                    dBo.setMetricType("BP_DIASTOLIC");
                    dBo.setMetricValue(parts[1].trim());
                    dBo.setUnit("mmHg");
                    dBo.setMeasureScene("FOLLOWUP");
                    dBo.setDataSource("MANUAL");
                    metricBoList.add(dBo);
                }
            }
        }

        // 血糖解析 (空腹血糖 / 餐后血糖)
        Object fastingGlu = vitalSigns.get("fastingGlucose");
        if (fastingGlu == null) fastingGlu = vitalSigns.get("fbg");
        if (fastingGlu != null) {
            ChHealthMetricRecordBo bo = new ChHealthMetricRecordBo();
            bo.setPatientId(patientId);
            bo.setMetricType("BLOOD_GLUCOSE");
            bo.setMetricValue(String.valueOf(fastingGlu));
            bo.setUnit("mmol/L");
            bo.setMeasureScene("FOLLOWUP");
            bo.setDataSource("MANUAL");
            metricBoList.add(bo);
        }

        Object postprandialGlu = vitalSigns.get("postprandialGlucose");
        if (postprandialGlu == null) postprandialGlu = vitalSigns.get("pbg");
        if (postprandialGlu != null) {
            ChHealthMetricRecordBo bo = new ChHealthMetricRecordBo();
            bo.setPatientId(patientId);
            bo.setMetricType("POSTPRANDIAL_GLUCOSE");
            bo.setMetricValue(String.valueOf(postprandialGlu));
            bo.setUnit("mmol/L");
            bo.setMeasureScene("FOLLOWUP");
            bo.setDataSource("MANUAL");
            metricBoList.add(bo);
        }

        // 心率解析
        Object heartRate = vitalSigns.get("heartRate");
        if (heartRate == null) heartRate = vitalSigns.get("pulse");
        if (heartRate != null) {
            ChHealthMetricRecordBo bo = new ChHealthMetricRecordBo();
            bo.setPatientId(patientId);
            bo.setMetricType("HEART_RATE");
            bo.setMetricValue(String.valueOf(heartRate));
            bo.setUnit("bpm");
            bo.setMeasureScene("FOLLOWUP");
            bo.setDataSource("MANUAL");
            metricBoList.add(bo);
        }

        // BMI / 体重解析
        Object weight = vitalSigns.get("weight");
        if (weight != null) {
            ChHealthMetricRecordBo bo = new ChHealthMetricRecordBo();
            bo.setPatientId(patientId);
            bo.setMetricType("WEIGHT");
            bo.setMetricValue(String.valueOf(weight));
            bo.setUnit("kg");
            bo.setMeasureScene("FOLLOWUP");
            bo.setDataSource("MANUAL");
            metricBoList.add(bo);
        }

        Object bmi = vitalSigns.get("bmi");
        if (bmi != null) {
            ChHealthMetricRecordBo bo = new ChHealthMetricRecordBo();
            bo.setPatientId(patientId);
            bo.setMetricType("BMI");
            bo.setMetricValue(String.valueOf(bmi));
            bo.setUnit("kg/m²");
            bo.setMeasureScene("FOLLOWUP");
            bo.setDataSource("MANUAL");
            metricBoList.add(bo);
        }

        if (!metricBoList.isEmpty()) {
            try {
                healthMetricManager.reportAndCheckBatch(metricBoList);
                log.info("随访体征指标自动入库成功, patientId={}, 指标数={}", patientId, metricBoList.size());
            } catch (Exception e) {
                log.warn("随访体征指标自动入库失败, patientId={}, err={}", patientId, e.getMessage());
            }
        }
    }

    /**
     * 沉淀随访事件到患者时间线
     */
    private void recordPatientTimeline(Long patientId, ChFollowupSubmitBo bo) {
        try {
            ChPatientTimeline timeline = new ChPatientTimeline();
            timeline.setPatientId(patientId);
            timeline.setEventType("FOLLOWUP");
            timeline.setEventTitle("完成慢病随访");
            StringBuilder detail = new StringBuilder();
            if (StringUtils.isNotBlank(bo.getFollowupResult())) {
                detail.append("随访结论: ").append(bo.getFollowupResult()).append("; ");
            }
            if (StringUtils.isNotBlank(bo.getRehabLevel())) {
                detail.append("康复评级: ").append(bo.getRehabLevel()).append("; ");
            }
            if (StringUtils.isNotBlank(bo.getVisitContent())) {
                detail.append("摘要: ").append(bo.getVisitContent()).append("; ");
            }
            if (StringUtils.isNotBlank(bo.getFeedbackAdvice())) {
                detail.append("回报指导: ").append(bo.getFeedbackAdvice());
            }
            timeline.setEventDetail(detail.toString());
            timeline.setEventTime(new Date());
            patientTimelineMapper.insert(timeline);
        } catch (Exception e) {
            log.warn("随访事件沉淀时间线失败, patientId={}, err={}", patientId, e.getMessage());
        }
    }

    @Override
    public List<ChFollowupRecordVo> queryRecordList(Long patientId) {
        List<ChFollowupRecordVo> list = followupRecordMapper.selectVoList(
            Wrappers.<ChFollowupRecord>lambdaQuery()
                .eq(ChFollowupRecord::getPatientId, patientId)
                .orderByDesc(ChFollowupRecord::getVisitDate));
        fillRecordDetails(list);
        return list;
    }

    @Override
    public TableDataInfo<ChFollowupRecordVo> queryRecordPage(Long patientId, String visitType,
                                                               PageQuery pageQuery) {
        Page<ChFollowupRecordVo> page = followupRecordMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.<ChFollowupRecord>lambdaQuery()
                .eq(ObjectUtil.isNotNull(patientId), ChFollowupRecord::getPatientId, patientId)
                .eq(StringUtils.isNotBlank(visitType), ChFollowupRecord::getVisitType, visitType)
                .orderByDesc(ChFollowupRecord::getVisitDate));
        fillRecordDetails(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public ChFollowupRecordVo queryRecordDetail(Long recordId) {
        ChFollowupRecordVo vo = followupRecordMapper.selectVoById(recordId);
        if (vo == null) {
            throw new ServiceException("随访记录不存在");
        }
        fillRecordDetails(Collections.singletonList(vo));
        return vo;
    }

    @Override
    public List<ChFollowupTaskVo> queryTodoTasks(Long assigneeUserId, String taskStatus) {
        if (assigneeUserId == null) {
            throw new ServiceException("未获取当前执行人身份");
        }
        overdueRefresher.refreshIfNeeded();
        List<ChFollowupTaskVo> list = followupTaskMapper.selectVoList(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getAssigneeUserId, assigneeUserId)
                .eq(StringUtils.isNotBlank(taskStatus), ChFollowupTask::getTaskStatus, taskStatus)
                .in(StringUtils.isBlank(taskStatus), ChFollowupTask::getTaskStatus,
                    List.of("PENDING", "REMINDING", "OVERDUE", "PATIENT_FILLED"))
                .orderByAsc(ChFollowupTask::getPlanDueDate));
        fillTaskMetadata(list);
        return list;
    }

    @Override
    public ChFollowupTaskDetailVo queryTaskDetail(Long taskId, Long expectedPatientId,
                                                   Long expectedAssigneeUserId) {
        ChFollowupTask task = followupTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("随访任务不存在");
        }
        if (expectedPatientId != null && !expectedPatientId.equals(task.getPatientId())) {
            throw new ServiceException("无权查看该患者随访任务");
        }
        if (expectedAssigneeUserId != null && !expectedAssigneeUserId.equals(task.getAssigneeUserId())) {
            throw new ServiceException("无权查看该执行人随访任务");
        }

        ChFollowupTaskVo taskVo = followupTaskMapper.selectVoById(taskId);
        fillTaskMetadata(Collections.singletonList(taskVo));
        ChFollowupTaskDetailVo detail = new ChFollowupTaskDetailVo();
        detail.setTask(taskVo);
        ChFollowupQuestionnaire questionnaire = resolveQuestionnaire(task);
        if (questionnaire != null) {
            detail.setQuestionnaire(MapstructUtils.convert(questionnaire, ChFollowupQuestionnaireVo.class));
        }

        ChFollowupRecord record = followupRecordMapper.selectOne(
            Wrappers.<ChFollowupRecord>lambdaQuery().eq(ChFollowupRecord::getTaskId, taskId)
                .orderByDesc(ChFollowupRecord::getVisitDate).last("limit 1"));
        if (record != null) {
            ChFollowupRecordVo recordVo = MapstructUtils.convert(record, ChFollowupRecordVo.class);
            fillRecordContent(recordVo);
            List<ChFollowupAnswerVo> answers = answerMapper.selectVoList(
                Wrappers.<ChFollowupAnswer>lambdaQuery().eq(ChFollowupAnswer::getRecordId, record.getRecordId())
                    .orderByAsc(ChFollowupAnswer::getCreateTime));
            recordVo.setAnswers(answers);
            detail.setRecord(recordVo);
            detail.setAnswers(answers);
        }

        // 组装智能预填与参考数据 (最新体征、当前用药、历史随访)
        detail.setPrefillData(buildPrefillData(task.getPatientId(), taskId));

        return detail;
    }

    @Override
    public ChFollowupPlanVo queryCurrentPlan(Long patientId) {
        ChFollowupPlan plan = followupPlanMapper.selectOne(
            Wrappers.<ChFollowupPlan>lambdaQuery()
                .eq(ChFollowupPlan::getPatientId, patientId)
                .eq(ChFollowupPlan::getPlanStatus, "ACTIVE")
                .orderByDesc(ChFollowupPlan::getCreateTime)
                .last("limit 1"));
        if (plan == null) {
            return null;
        }
        ChFollowupPlanVo vo = MapstructUtils.convert(plan, ChFollowupPlanVo.class);
        vo.setItemList(followupPlanItemMapper.selectVoList(
            Wrappers.<ChFollowupPlanItem>lambdaQuery().eq(ChFollowupPlanItem::getPlanId, plan.getPlanId())));
        fillFollowupPlanNames(Collections.singletonList(vo));
        return vo;
    }

    @Override
    public List<ChFollowupTaskVo> queryPatientTasks(Long patientId) {
        overdueRefresher.refreshIfNeeded();
        List<ChFollowupTaskVo> list = followupTaskMapper.selectVoList(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getPatientId, patientId)
                .orderByAsc(ChFollowupTask::getPlanDueDate));
        fillTaskMetadata(list);
        return list;
    }

    private void validatePlan(ChFollowupPlanBo bo) {
        if (bo.getPatientId() == null) {
            throw new ServiceException("患者ID不能为空");
        }
        if (StringUtils.isBlank(bo.getDiseaseCode())) {
            throw new ServiceException("病种编码不能为空");
        }
        if (StringUtils.isNotBlank(bo.getPlanStatus())
            && !Set.of("ACTIVE", "DISABLED").contains(bo.getPlanStatus())) {
            throw new ServiceException("计划状态仅支持启用或停用");
        }
        if (bo.getCycleDays() == null || bo.getCycleDays() <= 0) {
            throw new ServiceException("随访周期必须大于0");
        }
        if (bo.getTotalRounds() == null || bo.getTotalRounds() <= 0) {
            throw new ServiceException("随访轮次必须大于0");
        }
        if (CollUtil.isEmpty(bo.getItemList())) {
            throw new ServiceException("至少配置一个随访计划项");
        }
        for (ChFollowupPlanItemBo item : bo.getItemList()) {
            if (StringUtils.isNotBlank(item.getVisitType()) && !VALID_VISIT_TYPES.contains(item.getVisitType())) {
                throw new ServiceException("随访方式不合法，仅支持线上(ONLINE)、线下(OFFLINE)、电话(PHONE)");
            }
        }
        boolean hasVisitItem = bo.getItemList().stream().anyMatch(item -> StringUtils.isNotBlank(item.getVisitType()));
        if (!hasVisitItem) {
            throw new ServiceException("随访计划项必须配置随访方式");
        }
    }

    private void savePlanItems(Long planId, List<ChFollowupPlanItemBo> itemList) {
        List<ChFollowupPlanItem> items = MapstructUtils.convert(itemList, ChFollowupPlanItem.class);
        items.forEach(item -> {
            item.setId(null);
            item.setPlanId(planId);
        });
        followupPlanItemMapper.insertBatch(items);
    }

    private void generateTasks(ChFollowupPlan plan, List<ChFollowupPlanItemBo> itemList) {
        ChFollowupPlanItemBo visitItem = itemList.stream()
            .filter(item -> StringUtils.isNotBlank(item.getVisitType()))
            .findFirst()
            .orElseThrow(() -> new ServiceException("随访计划项缺少随访方式"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(visitItem.getDueDate() == null ? new Date() : visitItem.getDueDate());
        for (int round = 1; round <= plan.getTotalRounds(); round++) {
            ChFollowupTask task = new ChFollowupTask();
            task.setPatientId(plan.getPatientId());
            task.setPlanId(plan.getPlanId());
            task.setTaskRound(round);
            task.setPlanDueDate(calendar.getTime());
            task.setTaskStatus("PENDING");
            task.setTaskType("NORMAL");
            boolean faceToFace = "OFFLINE".equalsIgnoreCase(visitItem.getVisitType());
            task.setIsFaceToFace(faceToFace);
            task.setVisitType(visitItem.getVisitType());
            task.setAssigneeUserId(plan.getAssigneeUserId()); // 可为 null 进入任务池
            followupTaskMapper.insert(task);
            calendar.add(Calendar.DAY_OF_MONTH, plan.getCycleDays());
        }
    }

    private void syncUnfinishedTasks(ChFollowupPlan plan, List<ChFollowupPlanItemBo> itemList) {
        ChFollowupPlanItemBo visitItem = itemList.stream()
            .filter(item -> StringUtils.isNotBlank(item.getVisitType()))
            .findFirst()
            .orElseThrow(() -> new ServiceException("随访计划项缺少随访方式"));
        Date firstDueDate = visitItem.getDueDate() == null ? new Date() : visitItem.getDueDate();
        List<ChFollowupTask> tasks = followupTaskMapper.selectList(
            Wrappers.<ChFollowupTask>lambdaQuery().eq(ChFollowupTask::getPlanId, plan.getPlanId()));
        Set<Integer> taskRounds = tasks.stream().map(ChFollowupTask::getTaskRound)
            .filter(ObjectUtil::isNotNull).collect(Collectors.toSet());
        for (ChFollowupTask task : tasks) {
            if (Set.of("DONE", "CANCELLED").contains(task.getTaskStatus())) {
                continue;
            }
            if ("DISABLED".equals(plan.getPlanStatus()) || task.getTaskRound() > plan.getTotalRounds()) {
                task.setTaskStatus("CANCELLED");
            } else {
                task.setPatientId(plan.getPatientId());
                task.setAssigneeUserId(plan.getAssigneeUserId());
                task.setVisitType(visitItem.getVisitType());
                task.setPlanDueDate(calculateDueDate(firstDueDate, plan.getCycleDays(), task.getTaskRound()));
            }
            followupTaskMapper.updateById(task);
        }
        if ("DISABLED".equals(plan.getPlanStatus())) {
            return;
        }
        for (int round = 1; round <= plan.getTotalRounds(); round++) {
            if (taskRounds.contains(round)) {
                continue;
            }
            ChFollowupTask task = new ChFollowupTask();
            task.setPatientId(plan.getPatientId());
            task.setPlanId(plan.getPlanId());
            task.setTaskRound(round);
            task.setPlanDueDate(calculateDueDate(firstDueDate, plan.getCycleDays(), round));
            task.setTaskStatus("PENDING");
            task.setTaskType("NORMAL");
            task.setIsFaceToFace("OFFLINE".equalsIgnoreCase(visitItem.getVisitType()));
            task.setVisitType(visitItem.getVisitType());
            task.setAssigneeUserId(plan.getAssigneeUserId());
            followupTaskMapper.insert(task);
        }
    }

    private Date calculateDueDate(Date firstDueDate, int cycleDays, int round) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDueDate);
        calendar.add(Calendar.DAY_OF_MONTH, cycleDays * (round - 1));
        return calendar.getTime();
    }

    private ChFollowupQuestionnaire resolveQuestionnaire(ChFollowupTask task) {
        Long questionnaireId = resolveQuestionnaireId(task);
        if (questionnaireId != null) {
            ChFollowupQuestionnaire questionnaire = questionnaireMapper.selectById(questionnaireId);
            if (questionnaire != null && Boolean.TRUE.equals(questionnaire.getIsActive())) {
                return questionnaire;
            }
        }
        // 智能兜底：根据计划所选病种，自动匹配该病种下激活的标准问卷
        if (task != null && task.getPlanId() != null) {
            ChFollowupPlan plan = followupPlanMapper.selectById(task.getPlanId());
            if (plan != null && StringUtils.isNotBlank(plan.getDiseaseCode())) {
                return questionnaireMapper.selectOne(
                    Wrappers.<ChFollowupQuestionnaire>lambdaQuery()
                        .eq(ChFollowupQuestionnaire::getDiseaseCode, plan.getDiseaseCode())
                        .eq(ChFollowupQuestionnaire::getIsActive, true)
                        .orderByDesc(ChFollowupQuestionnaire::getIsNationalStandard)
                        .orderByDesc(ChFollowupQuestionnaire::getVersion)
                        .last("limit 1"));
            }
        }
        return null;
    }

    private Long resolveQuestionnaireId(ChFollowupTask task) {
        List<ChFollowupPlanItem> items = followupPlanItemMapper.selectList(
            Wrappers.<ChFollowupPlanItem>lambdaQuery().eq(ChFollowupPlanItem::getPlanId, task.getPlanId())
                .eq(StringUtils.isNotBlank(task.getVisitType()), ChFollowupPlanItem::getVisitType, task.getVisitType()));
        for (ChFollowupPlanItem item : items) {
            try {
                JsonNode node = objectMapper.readTree(item.getItemConfig());
                JsonNode questionnaireId = node == null ? null : node.get("questionnaireId");
                if (questionnaireId != null && questionnaireId.canConvertToLong()) {
                    return questionnaireId.longValue();
                }
            } catch (Exception e) {
                throw new ServiceException("随访计划项配置格式不合法");
            }
        }
        return null;
    }

    private void validateAnswers(ChFollowupSubmitBo bo, ChFollowupQuestionnaire questionnaire) {
        if (CollUtil.isEmpty(bo.getAnswers())) {
            if (questionnaire != null && hasRequiredQuestion(questionnaire.getQuestions())) {
                throw new ServiceException("请完成问卷必填项");
            }
            return;
        }
        if (questionnaire == null || bo.getQuestionnaireId() == null
            || !bo.getQuestionnaireId().equals(questionnaire.getQuestionnaireId())) {
            throw new ServiceException("问卷与任务不匹配");
        }
        try {
            JsonNode questions = objectMapper.readTree(questionnaire.getQuestions());
            Map<String, JsonNode> questionMap = new HashMap<>();
            if (questions != null && questions.isArray()) {
                for (JsonNode question : questions) {
                    if (question.hasNonNull("id")) {
                        questionMap.put(question.get("id").asText(), question);
                    }
                }
            }
            Set<String> answerIds = new HashSet<>();
            for (ChFollowupAnswerInputBo answer : bo.getAnswers()) {
                if (!answerIds.add(answer.getQuestionId()) || !questionMap.containsKey(answer.getQuestionId())) {
                    throw new ServiceException("问卷答案包含无效或重复题目");
                }
            }
            for (Map.Entry<String, JsonNode> entry : questionMap.entrySet()) {
                if (entry.getValue().path("required").asBoolean(false) && !answerIds.contains(entry.getKey())) {
                    throw new ServiceException("请完成问卷必填项");
                }
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("问卷题目格式不合法");
        }
    }

    private boolean hasRequiredQuestion(String questionsJson) {
        try {
            JsonNode questions = objectMapper.readTree(questionsJson);
            if (questions != null && questions.isArray()) {
                for (JsonNode question : questions) {
                    if (question.path("required").asBoolean(false)) return true;
                }
            }
            return false;
        } catch (Exception e) {
            throw new ServiceException("问卷题目格式不合法");
        }
    }

    private void saveAnswers(Long recordId, ChFollowupQuestionnaire questionnaire,
                             List<ChFollowupAnswerInputBo> answers) {
        if (questionnaire == null || CollUtil.isEmpty(answers)) return;
        List<ChFollowupAnswer> entities = new ArrayList<>(answers.size());
        for (ChFollowupAnswerInputBo input : answers) {
            ChFollowupAnswerBo answer = new ChFollowupAnswerBo();
            answer.setRecordId(recordId);
            answer.setQuestionnaireId(questionnaire.getQuestionnaireId());
            answer.setQuestionId(input.getQuestionId());
            answer.setAnswerValue(input.getAnswerValue());
            entities.add(MapstructUtils.convert(answer, ChFollowupAnswer.class));
        }
        answerMapper.insertBatch(entities);
    }

    private void updatePlanProgress(ChFollowupTask task) {
        ChFollowupPlan plan = followupPlanMapper.selectById(task.getPlanId());
        if (plan == null) return;
        // 紧急/动态等计划外任务 taskRound 为空, 不参与轮次推进(直接取 max 会在拆箱时 NPE)
        if (task.getTaskRound() != null) {
            plan.setCurrentRound(Math.max(ObjectUtil.defaultIfNull(plan.getCurrentRound(), 0), task.getTaskRound()));
        }
        if (countUnfinishedTasks(task.getPlanId()) == 0) plan.setPlanStatus("COMPLETED");
        followupPlanMapper.updateById(plan);
    }

    private void refreshPlanCompletion(Long planId) {
        ChFollowupPlan plan = followupPlanMapper.selectById(planId);
        if (plan == null || !"ACTIVE".equals(plan.getPlanStatus())) return;
        if (countUnfinishedTasks(planId) == 0) {
            plan.setPlanStatus("COMPLETED");
            followupPlanMapper.updateById(plan);
        }
    }

    private long countUnfinishedTasks(Long planId) {
        // 仅统计计划内常规轮次任务: 紧急/动态/转诊跟踪属计划外临时任务, 否则一条挂着的预警任务
        // 会让计划永远收敛不到 COMPLETED
        return followupTaskMapper.selectCount(
            Wrappers.<ChFollowupTask>lambdaQuery().eq(ChFollowupTask::getPlanId, planId)
                .notIn(ChFollowupTask::getTaskStatus, List.of("DONE", "CANCELLED"))
                .and(w -> w.isNull(ChFollowupTask::getTaskType)
                    .or().notIn(ChFollowupTask::getTaskType, List.of("EMERGENCY", "DYNAMIC", "REFERRAL_TRACK"))));
    }

    private void fillTaskMetadata(List<ChFollowupTaskVo> tasks) {
        if (CollUtil.isEmpty(tasks)) return;
        List<Long> patientIds = tasks.stream().map(ChFollowupTaskVo::getPatientId).filter(ObjectUtil::isNotNull).distinct().toList();
        if (!patientIds.isEmpty()) {
            Map<Long, ChPatientProfile> patients = patientProfileMapper.selectBatchIds(patientIds).stream()
                .collect(Collectors.toMap(ChPatientProfile::getPatientId, Function.identity(), (a, b) -> a));
            tasks.forEach(task -> {
                ChPatientProfile patient = patients.get(task.getPatientId());
                if (patient != null) task.setPatientName(patient.getName());
            });
        }
        List<Long> planIds = tasks.stream().map(ChFollowupTaskVo::getPlanId).filter(ObjectUtil::isNotNull).distinct().toList();
        if (!planIds.isEmpty()) {
            List<ChFollowupPlan> plans = followupPlanMapper.selectBatchIds(planIds);
            Map<Long, ChFollowupPlan> planMap = plans.stream().collect(Collectors.toMap(ChFollowupPlan::getPlanId, Function.identity(), (a, b) -> a));
            List<String> diseaseCodes = plans.stream().map(ChFollowupPlan::getDiseaseCode).filter(StringUtils::isNotBlank).distinct().toList();
            Map<String, String> diseaseNameMap = diseaseCodes.isEmpty() ? Collections.emptyMap() : diseaseNameHelper.batchGetDiseaseName(diseaseCodes);

            List<ChFollowupPlanItem> items = followupPlanItemMapper.selectList(
                Wrappers.<ChFollowupPlanItem>lambdaQuery().in(ChFollowupPlanItem::getPlanId, planIds));
            Map<Long, Long> questionnaireByPlan = new HashMap<>();
            for (ChFollowupPlanItem item : items) {
                try {
                    JsonNode node = objectMapper.readTree(item.getItemConfig());
                    if (node != null && node.has("questionnaireId")) {
                        questionnaireByPlan.putIfAbsent(item.getPlanId(), node.get("questionnaireId").asLong());
                    }
                } catch (Exception e) {
                    throw new ServiceException("随访计划项配置格式不合法");
                }
            }
            tasks.forEach(task -> {
                task.setQuestionnaireId(questionnaireByPlan.get(task.getPlanId()));
                ChFollowupPlan p = planMap.get(task.getPlanId());
                if (p != null) {
                    task.setDiseaseCode(p.getDiseaseCode());
                    task.setDiseaseName(diseaseNameMap.get(p.getDiseaseCode()));
                }
            });
        }

        // 填充随访方式名称与状态中文名称
        Map<String, String> visitTypeMap = Map.of(
            "ONLINE", "线上随访",
            "OFFLINE", "线下随访",
            "PHONE", "电话随访",
            "VIDEO", "视频随访",
            "SELF_FILL", "患者自填",
            "ADMIN_PROXY", "医护代填"
        );
        Map<String, String> statusMap = Map.of(
            "PENDING", "待执行",
            "REMINDING", "提醒中",
            "DONE", "已完成",
            "OVERDUE", "已逾期",
            "CANCELLED", "已取消",
            "PATIENT_FILLED", "已自填待评估"
        );
        Map<String, String> taskTypeMap = Map.of(
            "NORMAL", "常规随访",
            "DYNAMIC", "动态调整随访",
            "REFERRAL_TRACK", "转诊跟踪随访",
            "EMERGENCY", "预警临时随访"
        );
        tasks.forEach(task -> {
            if (StringUtils.isBlank(task.getVisitTypeName()) && StringUtils.isNotBlank(task.getVisitType())) {
                task.setVisitTypeName(visitTypeMap.getOrDefault(task.getVisitType(), task.getVisitType()));
            }
            if (StringUtils.isBlank(task.getTaskStatusName()) && StringUtils.isNotBlank(task.getTaskStatus())) {
                task.setTaskStatusName(statusMap.getOrDefault(task.getTaskStatus(), task.getTaskStatus()));
            }
            if (StringUtils.isBlank(task.getTaskTypeName()) && StringUtils.isNotBlank(task.getTaskType())) {
                task.setTaskTypeName(taskTypeMap.getOrDefault(task.getTaskType(), task.getTaskType()));
            }
        });
    }

    private void fillRecordDetails(List<ChFollowupRecordVo> records) {
        if (CollUtil.isEmpty(records)) return;
        List<Long> patientIds = records.stream().map(ChFollowupRecordVo::getPatientId).filter(ObjectUtil::isNotNull).distinct().toList();
        Map<Long, String> patientNames = patientIds.isEmpty() ? Collections.emptyMap()
            : patientProfileMapper.selectBatchIds(patientIds).stream()
                .collect(Collectors.toMap(ChPatientProfile::getPatientId, ChPatientProfile::getName, (a, b) -> a));
        records.forEach(record -> {
            record.setPatientName(patientNames.get(record.getPatientId()));
            fillRecordContent(record);
            record.setAnswers(answerMapper.selectVoList(
                Wrappers.<ChFollowupAnswer>lambdaQuery().eq(ChFollowupAnswer::getRecordId, record.getRecordId())
                    .orderByAsc(ChFollowupAnswer::getCreateTime)));
        });
    }

    private void fillRecordContent(ChFollowupRecordVo record) {
        try {
            JsonNode node = objectMapper.readTree(record.getVisitContent());
            if (node != null && node.isObject()) {
                record.setContent(objectMapper.convertValue(node, Map.class));
            }
        } catch (Exception e) {
            record.setContent(Collections.singletonMap("summary", record.getVisitContent()));
        }
    }

    private void fillFollowupPlanNames(List<ChFollowupPlanVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<String> diseaseCodes = list.stream().map(ChFollowupPlanVo::getDiseaseCode)
            .filter(StringUtils::isNotBlank).distinct().toList();
        if (!diseaseCodes.isEmpty()) {
            Map<String, String> diseaseNameMap = diseaseNameHelper.batchGetDiseaseName(diseaseCodes);
            list.forEach(v -> v.setDiseaseName(diseaseNameMap.get(v.getDiseaseCode())));
        }
    }

    private void fillPlanPatientNames(List<ChFollowupPlanVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> patientIds = list.stream().map(ChFollowupPlanVo::getPatientId)
            .filter(ObjectUtil::isNotNull).distinct().toList();
        if (patientIds.isEmpty()) return;
        Map<Long, String> patientNames = patientProfileMapper.selectBatchIds(patientIds).stream()
            .collect(Collectors.toMap(ChPatientProfile::getPatientId, ChPatientProfile::getName, (a, b) -> a));
        list.forEach(v -> v.setPatientName(patientNames.get(v.getPatientId())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendTaskRemind(Long taskId, Long operatorUserId) {
        if (taskId == null) {
            throw new ServiceException("随访任务ID不能为空");
        }
        ChFollowupTask task = followupTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("随访任务不存在");
        }
        if (Set.of("DONE", "CANCELLED").contains(task.getTaskStatus())) {
            throw new ServiceException("已结束的任务无法发送随访提醒");
        }

        // 1. 查询患者信息
        ChPatientProfile patient = patientProfileMapper.selectById(task.getPatientId());
        String patientName = patient != null ? patient.getName() : "患者";
        String patientPhone = patient != null ? patient.getPhone() : null;

        // 2. 构造通知文案
        String dueDateStr = task.getPlanDueDate() != null
            ? DateUtil.format(task.getPlanDueDate(), "yyyy-MM-dd") : "近期";
        String message = String.format("【慢病管理】尊敬的%s，您有一条慢病随访计划将于%s进行，请留意医护人员联系或通过小程序自填完成随访。",
            patientName, dueDateStr);

        // 如果配置了模板，尝试使用模板渲染
        if (notificationTemplateService != null) {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("name", patientName);
                params.put("date", dueDateStr);
                params.put("dueDate", dueDateStr);
                params.put("patientId", String.valueOf(task.getPatientId()));
                params.put("taskId", String.valueOf(task.getTaskId()));
                String rendered = notificationTemplateService.render("FOLLOWUP_REMIND", null, params);
                if (StringUtils.isNotBlank(rendered)) {
                    message = rendered;
                }
            } catch (Exception e) {
                log.warn("渲染随访提醒模板失败: {}", e.getMessage());
            }
        }

        // 3. 推送短信给患者
        if (StringUtils.isNotBlank(patientPhone) && remoteSmsService != null) {
            try {
                remoteSmsService.sendMessageAsync(patientPhone, message);
                log.info("已向患者发送随访提醒短信: taskId={}, phone={}", taskId, patientPhone);
            } catch (Exception e) {
                log.warn("向患者发送随访提醒短信失败: taskId={}, err={}", taskId, e.getMessage());
            }
        }

        // 4. 若有执行人，向执行人推送待办提醒
        if (task.getAssigneeUserId() != null && remoteMessageService != null) {
            try {
                String docMsg = String.format("随访催办提醒：患者【%s】的第%s轮随访任务待执行（到期日：%s）",
                    patientName, task.getTaskRound() != null ? task.getTaskRound() : "-", dueDateStr);
                remoteMessageService.publishMessage(List.of(task.getAssigneeUserId()), docMsg);
            } catch (Exception e) {
                log.warn("向执行人推送随访提醒失败: taskId={}, err={}", taskId, e.getMessage());
            }
        }

        // 5. 沉淀至患者时间线与动态记录
        if (patientTimelineMapper != null && task.getPatientId() != null) {
            try {
                ChPatientTimeline timeline = new ChPatientTimeline();
                timeline.setPatientId(task.getPatientId());
                timeline.setEventType("FOLLOWUP_REMIND");
                timeline.setEventTitle("随访提醒通知");
                timeline.setEventDetail(String.format("您的慢病管理团队向您发送了第%s轮随访提醒（到期日：%s），请按期通过小程序完成随访自填或配合医护随访。",
                    task.getTaskRound() != null ? task.getTaskRound() : "1", dueDateStr));
                timeline.setEventTime(new Date());
                timeline.setTenantId(task.getTenantId());
                patientTimelineMapper.insert(timeline);
            } catch (Exception e) {
                log.warn("向患者时间线沉淀随访提醒事件失败: taskId={}, err={}", taskId, e.getMessage());
            }
        }

        // 6. 更新任务状态为 REMINDING（若原为 PENDING）
        if ("PENDING".equals(task.getTaskStatus())) {
            task.setTaskStatus("REMINDING");
            followupTaskMapper.updateById(task);
        }

        // 7. 同步写入基于任务的医患会话(TASK_CHAT): 测试/未接入短信通道时, 患者打开"与医生沟通"
        //    即可看到提醒内容, 避免医生点提醒后患者端无任何可见反馈
        try {
            Long sessionId = messageSessionService.getOrCreateTaskSession(
                task.getPatientId(), task.getAssigneeUserId(), taskId);
            ChMessageContentBo chatMsg = new ChMessageContentBo();
            chatMsg.setSessionId(sessionId);
            chatMsg.setSenderType("DOCTOR");
            chatMsg.setContentType("TEXT");
            chatMsg.setContent(message);
            messageSessionService.sendMessage(chatMsg);
        } catch (Exception e) {
            log.warn("随访提醒写入任务会话失败: taskId={}, err={}", taskId, e.getMessage());
        }
    }

    /**
     * 组装随访智能预填与参考数据 (最新体征、当前用药、历史随访)
     */
    private ChFollowupPrefillVo buildPrefillData(Long patientId, Long currentTaskId) {
        if (patientId == null) {
            return null;
        }
        ChFollowupPrefillVo prefill = new ChFollowupPrefillVo();

        // 1. 获取最新生命体征数据
        if (healthMetricRecordMapper != null) {
            try {
                List<ChHealthMetricRecordVo> latestMetrics = healthMetricRecordMapper.selectLatestByPatientId(patientId);
                if (CollUtil.isNotEmpty(latestMetrics)) {
                    Map<String, Object> metricMap = new HashMap<>();
                    Date latestTime = null;
                    for (ChHealthMetricRecordVo m : latestMetrics) {
                        if (m == null || m.getMetricType() == null || m.getMetricValue() == null) continue;
                        if (latestTime == null || (m.getCreateTime() != null && m.getCreateTime().after(latestTime))) {
                            latestTime = m.getCreateTime();
                        }
                        String type = m.getMetricType().toUpperCase();
                        String val = m.getMetricValue();
                        switch (type) {
                            case "SYSTOLIC_BP", "BP_SYSTOLIC", "SBP" -> metricMap.put("systolicBp", val);
                            case "DIASTOLIC_BP", "BP_DIASTOLIC", "DBP" -> metricMap.put("diastolicBp", val);
                            case "FASTING_GLUCOSE", "BLOOD_GLUCOSE", "FBG" -> metricMap.put("fastingGlucose", val);
                            case "POSTPRANDIAL_GLUCOSE", "PBG" -> metricMap.put("postprandialGlucose", val);
                            case "HEART_RATE", "PULSE" -> metricMap.put("heartRate", val);
                            case "WEIGHT" -> metricMap.put("weight", val);
                            case "BMI" -> metricMap.put("bmi", val);
                            case "SPO2" -> metricMap.put("spo2", val);
                            default -> metricMap.put(m.getMetricType(), val);
                        }
                    }
                    prefill.setLatestMetrics(metricMap);
                    prefill.setLatestMetricTime(latestTime);
                }
            } catch (Exception e) {
                log.warn("组装随访预填指标失败 patientId={} err={}", patientId, e.getMessage());
            }
        }

        // 2. 获取当前在服药物列表与格式化描述
        if (medicationRecordMapper != null) {
            try {
                List<ChMedicationRecordVo> medList = medicationRecordMapper.selectVoList(
                    Wrappers.<ChMedicationRecord>lambdaQuery()
                        .eq(ChMedicationRecord::getPatientId, patientId)
                        .eq(ChMedicationRecord::getStatus, "ACTIVE")
                        .orderByDesc(ChMedicationRecord::getStartDate));
                if (CollUtil.isNotEmpty(medList)) {
                    prefill.setActiveMedications(medList);
                    String medDesc = medList.stream().map(m -> {
                        StringBuilder sb = new StringBuilder();
                        sb.append(m.getDrugName());
                        if (StringUtils.isNotBlank(m.getDosage())) sb.append(" ").append(m.getDosage());
                        if (StringUtils.isNotBlank(m.getFrequency())) sb.append(" ").append(m.getFrequency());
                        return sb.toString();
                    }).collect(Collectors.joining(", "));
                    prefill.setMedicationDescription(medDesc);
                }
            } catch (Exception e) {
                log.warn("组装随访预填用药失败 patientId={} err={}", patientId, e.getMessage());
            }
        }

        // 3. 获取上一轮随访历史记录与答卷
        try {
            var wrapper = Wrappers.<ChFollowupRecord>lambdaQuery()
                .eq(ChFollowupRecord::getPatientId, patientId);
            if (currentTaskId != null) {
                wrapper.ne(ChFollowupRecord::getTaskId, currentTaskId);
            }
            ChFollowupRecord lastRec = followupRecordMapper.selectOne(
                wrapper.orderByDesc(ChFollowupRecord::getVisitDate).last("limit 1"));
            if (lastRec != null) {
                ChFollowupRecordVo lastRecVo = MapstructUtils.convert(lastRec, ChFollowupRecordVo.class);
                fillRecordContent(lastRecVo);
                prefill.setLastRecord(lastRecVo);

                List<ChFollowupAnswerVo> lastAns = answerMapper.selectVoList(
                    Wrappers.<ChFollowupAnswer>lambdaQuery()
                        .eq(ChFollowupAnswer::getRecordId, lastRec.getRecordId())
                        .orderByAsc(ChFollowupAnswer::getCreateTime));
                prefill.setLastAnswers(lastAns);
            }
        } catch (Exception e) {
            log.warn("组装上次随访历史失败 patientId={} err={}", patientId, e.getMessage());
        }

        return prefill;
    }
}
