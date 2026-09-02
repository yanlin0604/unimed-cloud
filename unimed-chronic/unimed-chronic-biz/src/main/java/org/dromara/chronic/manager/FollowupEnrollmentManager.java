package org.dromara.chronic.manager;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.chronic.domain.entity.*;
import org.dromara.chronic.mapper.*;
import org.dromara.chronic.support.rule.FollowupRoundTaskGenerator;
import org.dromara.chronic.support.rule.MultiDiseaseFollowupMerger;
import org.dromara.chronic.support.rule.MultiDiseaseFollowupMerger.MergedProposal;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.resource.api.RemoteMessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 慢病随访自动入组与计划生成编排层
 * <p>
 * 当患者确诊慢病（新建档案、HIS确诊同步、PHS基层同步、筛查阳性确诊）时，
 * 自动触发风险评估与多病共管合并引擎，生成对应的活跃随访计划与首轮随访任务。
 * 后续轮次不再预生成，由医生在完成每轮随访时填写「下次随访日期」逐轮驱动。
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FollowupEnrollmentManager {

    private final ChFollowupPlanMapper followupPlanMapper;
    private final ChFollowupPlanItemMapper followupPlanItemMapper;
    private final FollowupRoundTaskGenerator roundTaskGenerator;
    private final ChPatientDiseaseMapper patientDiseaseMapper;
    private final ChRiskAssessmentMapper riskAssessmentMapper;
    private final ChPatientProfileMapper patientProfileMapper;
    private final ChPatientTimelineMapper patientTimelineMapper;
    private final MultiDiseaseFollowupMerger multiDiseaseMerger;
    private final ObjectMapper objectMapper;

    @DubboReference(mock = "org.dromara.resource.api.RemoteMessageServiceStub")
    private RemoteMessageService remoteMessageService;

    /**
     * 自动入组并生成随访计划与任务
     *
     * @param patientId    患者ID
     * @param diseaseCode  本次确诊/绑定的病种编码
     * @param doctorUserId 责任医生用户ID（可为空）
     * @return 随访计划ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long autoEnrollAndGeneratePlan(Long patientId, String diseaseCode, Long doctorUserId) {
        if (patientId == null || StringUtils.isBlank(diseaseCode)) {
            log.warn("自动入组参数不完整: patientId={}, diseaseCode={}", patientId, diseaseCode);
            return null;
        }

        // 1. 幂等性检查：若该患者已存在同病种的 ACTIVE 随访计划，直接复用返回
        ChFollowupPlan existingPlan = followupPlanMapper.selectOne(
            Wrappers.<ChFollowupPlan>lambdaQuery()
                .eq(ChFollowupPlan::getPatientId, patientId)
                .eq(ChFollowupPlan::getDiseaseCode, diseaseCode)
                .eq(ChFollowupPlan::getPlanStatus, "ACTIVE")
                .orderByDesc(ChFollowupPlan::getCreateTime)
                .last("limit 1")
        );
        if (existingPlan != null) {
            log.info("患者已有生效中的随访计划, 复用 planId={}, patientId={}, diseaseCode={}",
                existingPlan.getPlanId(), patientId, diseaseCode);
            return existingPlan.getPlanId();
        }

        // 2. 查询患者责任医生（若入参未传）
        Long finalDoctorId = doctorUserId;
        if (finalDoctorId == null) {
            ChPatientProfile profile = patientProfileMapper.selectById(patientId);
            if (profile != null && profile.getDoctorUserId() != null) {
                finalDoctorId = profile.getDoctorUserId();
            }
        }

        // 3. 查询患者已绑定的所有专病及最新风险等级
        List<ChPatientDisease> diseases = patientDiseaseMapper.selectList(
            Wrappers.<ChPatientDisease>lambdaQuery()
                .eq(ChPatientDisease::getPatientId, patientId)
                .eq(ChPatientDisease::getEnableStatus, true)
        );
        List<String> diseaseCodes = diseases.stream()
            .map(ChPatientDisease::getDiseaseCode)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        if (!diseaseCodes.contains(diseaseCode)) {
            diseaseCodes.add(diseaseCode);
        }

        // 查询各病种风险评估结果
        Map<String, String> riskLevels = new HashMap<>();
        for (String code : diseaseCodes) {
            ChRiskAssessment latestRisk = riskAssessmentMapper.selectOne(
                Wrappers.<ChRiskAssessment>lambdaQuery()
                    .eq(ChRiskAssessment::getPatientId, patientId)
                    .eq(ChRiskAssessment::getDiseaseCode, code)
                    .orderByDesc(ChRiskAssessment::getCreateTime)
                    .last("limit 1")
            );
            if (latestRisk != null && StringUtils.isNotBlank(latestRisk.getRiskLevel())) {
                riskLevels.put(code, latestRisk.getRiskLevel());
            }
        }

        // 4. 多病共管合并引擎推导方案
        MergedProposal proposal = multiDiseaseMerger.mergeProposals(diseaseCodes, riskLevels);

        // 5. 创建随访计划主记录
        ChFollowupPlan plan = new ChFollowupPlan();
        plan.setPatientId(patientId);
        plan.setDiseaseCode(proposal.primaryDiseaseCode());
        plan.setAssigneeUserId(finalDoctorId);
        plan.setCycleDays(proposal.cycleDays());
        plan.setTotalRounds(proposal.totalRounds());
        plan.setCurrentRound(0);
        plan.setPlanStatus("ACTIVE");
        plan.setManagementLevel(proposal.managementLevel());
        plan.setIsMultiDisease(proposal.isMultiDisease());
        plan.setMergedDiseaseCodes(proposal.mergedDiseaseCodesJson());
        plan.setCreateDept(103L);
        plan.setTenantId("000000");
        plan.setDelFlag("0");
        followupPlanMapper.insert(plan);

        // 6. 保存随访计划项配置
        ChFollowupPlanItem item = new ChFollowupPlanItem();
        item.setPlanId(plan.getPlanId());
        item.setVisitType(proposal.defaultVisitType());
        Date firstDueDate = DateUtil.offsetDay(new Date(), proposal.firstDueDays());
        item.setDueDate(firstDueDate);
        if (proposal.questionnaireId() != null) {
            item.setItemConfig(String.format("{\"questionnaireId\":%d}", proposal.questionnaireId()));
        }
        item.setCreateDept(103L);
        item.setTenantId("000000");
        item.setDelFlag("0");
        followupPlanItemMapper.insert(item);

        // 7. 仅生成首轮随访任务，后续轮次由医生完成本轮后决定是否继续
        roundTaskGenerator.ensureRound(plan, 1, firstDueDate, proposal.defaultVisitType());

        // 8. 沉淀至患者时间线
        recordTimeline(patientId, "FOLLOWUP_PLAN_AUTO_GEN", "自动生成随访计划",
            String.format("患者确诊慢病，系统自动生成%s管理计划（管理分级：%s，周期：%d天，管理目标 %d 轮）。"
                    + "已安排首轮随访（到期日：%s），后续轮次由医生随访后逐轮确定。%s",
                proposal.isMultiDisease() ? "多病共管" : proposal.primaryDiseaseCode(),
                proposal.managementLevel(), proposal.cycleDays(), proposal.totalRounds(),
                DateUtil.format(firstDueDate, "yyyy-MM-dd"), proposal.summaryAdvice()));

        // 9. 向责任医生推送工作待办通知
        if (finalDoctorId != null && remoteMessageService != null) {
            try {
                ChPatientProfile profile = patientProfileMapper.selectById(patientId);
                String pName = profile != null ? profile.getName() : "患者";
                String msg = String.format("【慢病入组通知】患者【%s】已确诊并自动生成慢病随访计划（管理目标 %d 轮，首轮随访到期日：%s），请按期跟进。",
                    pName, proposal.totalRounds(), DateUtil.format(firstDueDate, "yyyy-MM-dd"));
                remoteMessageService.publishMessage(List.of(finalDoctorId), msg);
            } catch (Exception e) {
                log.warn("向医生推送新入组通知失败 docId={}, err={}", finalDoctorId, e.getMessage());
            }
        }

        log.info("患者自动入组成功: patientId={}, diseaseCode={}, planId={}, multiDisease={}, totalRounds={}",
            patientId, diseaseCode, plan.getPlanId(), proposal.isMultiDisease(), proposal.totalRounds());
        return plan.getPlanId();
    }

    private void recordTimeline(Long patientId, String eventType, String title, String detail) {
        if (patientTimelineMapper == null || patientId == null) return;
        try {
            ChPatientTimeline timeline = new ChPatientTimeline();
            timeline.setPatientId(patientId);
            timeline.setEventType(eventType);
            timeline.setEventTitle(title);
            timeline.setEventDetail(detail);
            timeline.setEventTime(new Date());
            patientTimelineMapper.insert(timeline);
        } catch (Exception e) {
            log.warn("写入自动入组时间线失败 patientId={}, err={}", patientId, e.getMessage());
        }
    }
}
