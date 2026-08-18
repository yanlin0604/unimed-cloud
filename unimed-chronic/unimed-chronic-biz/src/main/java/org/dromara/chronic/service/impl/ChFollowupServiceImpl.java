package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.common.helper.DiseaseNameHelper;
import org.dromara.chronic.domain.bo.ChFollowupAnswerBo;
import org.dromara.chronic.domain.bo.ChFollowupAnswerInputBo;
import org.dromara.chronic.domain.bo.ChFollowupPlanBo;
import org.dromara.chronic.domain.bo.ChFollowupPlanItemBo;
import org.dromara.chronic.domain.bo.ChFollowupSubmitBo;
import org.dromara.chronic.domain.dto.FollowupContentJson;
import org.dromara.chronic.domain.entity.ChFollowupAnswer;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupPlanItem;
import org.dromara.chronic.domain.entity.ChFollowupQuestionnaire;
import org.dromara.chronic.domain.entity.ChFollowupRecord;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.vo.ChFollowupAnswerVo;
import org.dromara.chronic.domain.vo.ChFollowupPlanVo;
import org.dromara.chronic.domain.vo.ChFollowupQuestionnaireVo;
import org.dromara.chronic.domain.vo.ChFollowupRecordVo;
import org.dromara.chronic.domain.vo.ChFollowupTaskDetailVo;
import org.dromara.chronic.domain.vo.ChFollowupTaskVo;
import org.dromara.chronic.mapper.ChFollowupAnswerMapper;
import org.dromara.chronic.mapper.ChFollowupPlanItemMapper;
import org.dromara.chronic.mapper.ChFollowupPlanMapper;
import org.dromara.chronic.mapper.ChFollowupQuestionnaireMapper;
import org.dromara.chronic.mapper.ChFollowupRecordMapper;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChFollowupService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 随访服务实现
 *
 * @author unimed
 */
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
    private final DiseaseNameHelper diseaseNameHelper;
    private final ObjectMapper objectMapper;

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
        generateTasks(plan, bo.getItemList(), bo.getAssigneeUserId());
        return plan.getPlanId();
    }

    @Override
    public TableDataInfo<ChFollowupPlanVo> queryPlanPage(Long patientId, String diseaseCode,
                                                          String planStatus, PageQuery pageQuery) {
        Page<ChFollowupPlanVo> page = followupPlanMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.<ChFollowupPlan>lambdaQuery()
                .eq(ObjectUtil.isNotNull(patientId), ChFollowupPlan::getPatientId, patientId)
                .eq(StringUtils.isNotBlank(diseaseCode), ChFollowupPlan::getDiseaseCode, diseaseCode)
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
    public TableDataInfo<ChFollowupTaskVo> queryTaskPage(Long patientId, Long assigneeUserId,
                                                          String taskStatus, String visitType,
                                                          PageQuery pageQuery) {
        Page<ChFollowupTaskVo> page = followupTaskMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ObjectUtil.isNotNull(patientId), ChFollowupTask::getPatientId, patientId)
                .eq(ObjectUtil.isNotNull(assigneeUserId), ChFollowupTask::getAssigneeUserId, assigneeUserId)
                .eq(StringUtils.isNotBlank(taskStatus), ChFollowupTask::getTaskStatus, taskStatus)
                .eq(StringUtils.isNotBlank(visitType), ChFollowupTask::getVisitType, visitType)
                .orderByAsc(ChFollowupTask::getPlanDueDate));
        fillTaskMetadata(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTask(Long taskId, Long assigneeUserId) {
        if (assigneeUserId == null) {
            throw new ServiceException("指派人不能为空");
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
    public void cancelTask(Long taskId) {
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
            throw new ServiceException("该任务未指派给当前医生");
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
        validateAnswers(bo, questionnaire);

        FollowupContentJson content = new FollowupContentJson();
        content.setSummary(bo.getVisitContent());
        content.setVitalSigns(bo.getVitalSigns());
        content.setMedicationStatus(bo.getMedicationStatus());
        content.setAdherence(bo.getAdherence());
        content.setLifestyle(bo.getLifestyle());
        content.setAdvice(bo.getAdvice());
        content.setNextFollowupDate(bo.getNextFollowupDate());

        ChFollowupRecord record = new ChFollowupRecord();
        record.setTaskId(taskId);
        record.setPatientId(task.getPatientId());
        record.setVisitType(StringUtils.isNotBlank(forcedVisitType) ? forcedVisitType : task.getVisitType());
        record.setVisitorUserId(visitorUserId);
        record.setVisitDate(new Date());
        try {
            record.setVisitContent(objectMapper.writeValueAsString(content));
        } catch (Exception e) {
            throw new ServiceException("随访内容格式化失败");
        }
        followupRecordMapper.insert(record);

        saveAnswers(record.getRecordId(), questionnaire, bo.getAnswers());

        task.setTaskStatus("DONE");
        followupTaskMapper.updateById(task);
        updatePlanProgress(task);
        return record.getRecordId();
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
            throw new ServiceException("未获取当前医生身份");
        }
        List<ChFollowupTaskVo> list = followupTaskMapper.selectVoList(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getAssigneeUserId, assigneeUserId)
                .eq(StringUtils.isNotBlank(taskStatus), ChFollowupTask::getTaskStatus, taskStatus)
                .in(StringUtils.isBlank(taskStatus), ChFollowupTask::getTaskStatus,
                    List.of("PENDING", "REMINDING", "OVERDUE"))
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
            throw new ServiceException("无权查看该医生随访任务");
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
        List<ChFollowupTaskVo> list = followupTaskMapper.selectVoList(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getPatientId, patientId)
                .orderByAsc(ChFollowupTask::getPlanDueDate));
        fillTaskMetadata(list);
        return list;
    }

    private void validatePlan(ChFollowupPlanBo bo) {
        if (bo.getCycleDays() == null || bo.getCycleDays() <= 0) {
            throw new ServiceException("随访周期必须大于0");
        }
        if (bo.getTotalRounds() == null || bo.getTotalRounds() <= 0) {
            throw new ServiceException("随访轮次必须大于0");
        }
        if (CollUtil.isEmpty(bo.getItemList())) {
            throw new ServiceException("至少配置一个随访计划项");
        }
        boolean hasVisitItem = bo.getItemList().stream().anyMatch(item -> StringUtils.isNotBlank(item.getVisitType()));
        if (!hasVisitItem) {
            throw new ServiceException("随访计划项必须配置随访方式");
        }
    }

    private void savePlanItems(Long planId, List<ChFollowupPlanItemBo> itemList) {
        List<ChFollowupPlanItem> items = MapstructUtils.convert(itemList, ChFollowupPlanItem.class);
        items.forEach(item -> item.setPlanId(planId));
        followupPlanItemMapper.insertBatch(items);
    }

    private void generateTasks(ChFollowupPlan plan, List<ChFollowupPlanItemBo> itemList, Long assigneeUserId) {
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
            task.setVisitType(visitItem.getVisitType());
            task.setAssigneeUserId(assigneeUserId);
            followupTaskMapper.insert(task);
            calendar.add(Calendar.DAY_OF_MONTH, plan.getCycleDays());
        }
    }

    private ChFollowupQuestionnaire resolveQuestionnaire(ChFollowupTask task) {
        Long questionnaireId = resolveQuestionnaireId(task);
        if (questionnaireId == null) {
            return null;
        }
        ChFollowupQuestionnaire questionnaire = questionnaireMapper.selectById(questionnaireId);
        if (questionnaire == null) {
            throw new ServiceException("任务关联的问卷不存在");
        }
        if (Boolean.FALSE.equals(questionnaire.getIsActive())) {
            throw new ServiceException("任务关联的问卷已停用");
        }
        return questionnaire;
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
        plan.setCurrentRound(Math.max(ObjectUtil.defaultIfNull(plan.getCurrentRound(), 0), task.getTaskRound()));
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
        return followupTaskMapper.selectCount(
            Wrappers.<ChFollowupTask>lambdaQuery().eq(ChFollowupTask::getPlanId, planId)
                .notIn(ChFollowupTask::getTaskStatus, List.of("DONE", "CANCELLED")));
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
            tasks.forEach(task -> task.setQuestionnaireId(questionnaireByPlan.get(task.getPlanId())));
        }
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
}
