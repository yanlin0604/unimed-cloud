package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.domain.entity.ChStatAreaDay;
import org.dromara.chronic.domain.vo.ChAreaDictVo;
import org.dromara.chronic.domain.vo.ChDiseaseAnalysisVo;
import org.dromara.chronic.domain.vo.ChKpiDefinitionVo;
import org.dromara.chronic.domain.vo.ChPatientProfileVo;
import org.dromara.chronic.domain.vo.ChStatAreaDayVo;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.entity.ChWarningEvent;
import org.dromara.chronic.domain.entity.ChManagePlan;
import org.dromara.chronic.mapper.ChAreaDictMapper;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.mapper.ChKpiDefinitionMapper;
import org.dromara.chronic.mapper.ChManagePlanMapper;
import org.dromara.chronic.mapper.ChPatientDiseaseMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.mapper.ChStatAreaDayMapper;
import org.dromara.chronic.mapper.ChWarningEventMapper;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 看板管理器：区域统计聚合+KPI计算+大屏数据
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardManager {

    private final ChAreaDictMapper areaDictMapper;
    private final ChStatAreaDayMapper statAreaDayMapper;
    private final ChKpiDefinitionMapper kpiDefinitionMapper;
    private final ChPatientDiseaseMapper patientDiseaseMapper;
    private final ChPatientProfileMapper patientProfileMapper;
    private final ChWarningEventMapper warningEventMapper;
    private final ChFollowupTaskMapper followupTaskMapper;
    private final ChManagePlanMapper managePlanMapper;

    /**
     * 递归构建行政区划树
     */
    public List<ChAreaDictVo> buildAreaTree(String parentAreaCode) {
        List<ChAreaDictVo> allAreas = areaDictMapper.selectVoList(
            Wrappers.<org.dromara.chronic.domain.entity.ChAreaDict>lambdaQuery()
                .orderByAsc(org.dromara.chronic.domain.entity.ChAreaDict::getAreaLevel)
                .orderByAsc(org.dromara.chronic.domain.entity.ChAreaDict::getAreaCode)
        );
        Map<String, List<ChAreaDictVo>> childrenMap = allAreas.stream()
            .filter(a -> a.getParentAreaCode() != null)
            .collect(Collectors.groupingBy(ChAreaDictVo::getParentAreaCode));
        List<ChAreaDictVo> roots;
        if (parentAreaCode == null) {
            roots = allAreas.stream().filter(a -> a.getParentAreaCode() == null || "0".equals(a.getParentAreaCode())).toList();
        } else {
            roots = allAreas.stream().filter(a -> parentAreaCode.equals(a.getAreaCode())).toList();
        }
        for (ChAreaDictVo root : allAreas) {
            root.setChildren(childrenMap.getOrDefault(root.getAreaCode(), List.of()));
        }
        return roots;
    }

    /**
     * 查询区域日统计
     */
    public List<ChStatAreaDayVo> queryAreaStats(String areaCode, Date statDate) {
        var lqw = Wrappers.<ChStatAreaDay>lambdaQuery();
        lqw.eq(areaCode != null, ChStatAreaDay::getAreaCode, areaCode);
        lqw.eq(statDate != null, ChStatAreaDay::getStatDate, statDate);
        lqw.orderByDesc(ChStatAreaDay::getStatDate);
        return statAreaDayMapper.selectVoList(lqw);
    }

    /**
     * 查询KPI定义
     */
    public List<ChKpiDefinitionVo> queryKpiList() {
        return kpiDefinitionMapper.selectVoList(
            Wrappers.<org.dromara.chronic.domain.entity.ChKpiDefinition>lambdaQuery()
                .orderByAsc(org.dromara.chronic.domain.entity.ChKpiDefinition::getKpiCategory)
        );
    }

    /**
     * 大屏专用端点：聚合关键指标（真实库表动态汇总）
     */
    public Map<String, Object> bigScreenSummary(String areaCode) {
        Map<String, Object> result = new java.util.HashMap<>();
        try {
            Long totalPatients = patientProfileMapper.selectCount(
                Wrappers.<org.dromara.chronic.domain.entity.ChPatientProfile>lambdaQuery()
                    .eq(org.dromara.chronic.domain.entity.ChPatientProfile::getDelFlag, "0")
            );
            Long totalPlans = managePlanMapper.selectCount(
                Wrappers.<ChManagePlan>lambdaQuery()
                    .eq(ChManagePlan::getPlanStatus, "ACTIVE")
            );
            Long warningCount = warningEventMapper.selectCount(
                Wrappers.<org.dromara.chronic.domain.entity.ChWarningEvent>lambdaQuery()
                    .in(org.dromara.chronic.domain.entity.ChWarningEvent::getEventStatus, List.of("NEW", "PROCESSING"))
            );
            Long followupCount = followupTaskMapper.selectCount(null);

            result.put("patientCount", totalPatients != null ? totalPatients : 0L);
            result.put("managedCount", totalPlans != null ? totalPlans : 0L);
            result.put("warningCount", warningCount != null ? warningCount : 0L);
            result.put("followupCount", followupCount != null ? followupCount : 0L);
            result.put("statDate", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        } catch (Exception e) {
            log.error("聚合大屏数据失败", e);
        }
        return result;
    }

    /** 首期与全量支持病种编码 */
    private static final List<String> FIRST_PHASE_DISEASE_CODES = List.of(
        "HYPERTENSION", "HTN", "DIABETES", "T2DM", "COPD", "ASTHMA",
        "CHD", "STROKE", "CANCER", "TUMOR", "CKD", "DYSLIPID", "MENTAL_DISORDER"
    );

    public TableDataInfo<ChPatientProfileVo> querySpecialDiseasePatientPage(ChPatientDiseaseBo bo, PageQuery pageQuery, List<String> diseaseScope) {
        List<String> scope = (diseaseScope != null && !diseaseScope.isEmpty()) ? diseaseScope : FIRST_PHASE_DISEASE_CODES;
        // 先按病种范围查询患者ID（加 LIMIT 防止全表扫描）
        LambdaQueryWrapper<ChPatientDisease> lqw = Wrappers.lambdaQuery();
        lqw.in(ChPatientDisease::getDiseaseCode, scope);
        lqw.eq(bo.getPatientId() != null, ChPatientDisease::getPatientId, bo.getPatientId());
        // manageLevel 不在 ChPatientDisease 实体上，需关联 ch_manage_plan 查询；此处暂不加该过滤
        lqw.select(ChPatientDisease::getPatientId);
        List<ChPatientDisease> diseaseList = patientDiseaseMapper.selectList(lqw);
        List<Long> patientIds = diseaseList.stream().map(ChPatientDisease::getPatientId).distinct().toList();
        if (patientIds.isEmpty()) {
            return TableDataInfo.build(new Page<>());
        }
        LambdaQueryWrapper<org.dromara.chronic.domain.entity.ChPatientProfile> pq = Wrappers.lambdaQuery();
        pq.in(org.dromara.chronic.domain.entity.ChPatientProfile::getPatientId, patientIds);
        var page = patientProfileMapper.selectVoPage(pageQuery.build(), pq);
        return TableDataInfo.build(page);
    }

    public TableDataInfo<ChPatientProfileVo> queryComorbidityPatientPage(ChPatientDiseaseBo bo, PageQuery pageQuery) {
        // 只查 patientId + diseaseCode 两列，避免全字段扫描
        LambdaQueryWrapper<ChPatientDisease> lqw = Wrappers.lambdaQuery();
        lqw.in(ChPatientDisease::getDiseaseCode, FIRST_PHASE_DISEASE_CODES);
        lqw.select(ChPatientDisease::getPatientId, ChPatientDisease::getDiseaseCode);
        List<ChPatientDisease> diseaseList = patientDiseaseMapper.selectList(lqw);
        // 去重后再统计（同一患者同一病种只计一次）
        Map<Long, Long> diseaseCountMap = diseaseList.stream()
            .collect(Collectors.groupingBy(
                ChPatientDisease::getPatientId,
                Collectors.mapping(ChPatientDisease::getDiseaseCode, Collectors.toSet()))
            )
            .entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));
        List<Long> comorbidityPatientIds = diseaseCountMap.entrySet().stream()
            .filter(e -> e.getValue() > 1)
            .map(Map.Entry::getKey)
            .toList();
        if (comorbidityPatientIds.isEmpty()) {
            return TableDataInfo.build(new Page<>());
        }
        LambdaQueryWrapper<org.dromara.chronic.domain.entity.ChPatientProfile> pq = Wrappers.lambdaQuery();
        pq.in(org.dromara.chronic.domain.entity.ChPatientProfile::getPatientId, comorbidityPatientIds);
        var page = patientProfileMapper.selectVoPage(pageQuery.build(), pq);
        return TableDataInfo.build(page);
    }

    public ChDiseaseAnalysisVo queryDiseaseAnalysis(String diseaseCode) {
        if (!FIRST_PHASE_DISEASE_CODES.contains(diseaseCode)) {
            throw new RuntimeException("首期不支持该病种: " + diseaseCode);
        }
        ChDiseaseAnalysisVo vo = new ChDiseaseAnalysisVo();
        vo.setDiseaseCode(diseaseCode);
        vo.setDiseaseName(resolveDiseaseName(diseaseCode));

        // 患者总数 & 控制率（使用 selectCount 避免全量加载）
        LambdaQueryWrapper<ChPatientDisease> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChPatientDisease::getDiseaseCode, diseaseCode);
        Long totalCount = patientDiseaseMapper.selectCount(lqw);
        long total = totalCount != null ? totalCount : 0L;
        vo.setTotalPatientCount(total);

        // 控制率：通过 ch_manage_plan 查询有 ACTIVE 方案的患者数
        // "已控制" = 有该病种的 ACTIVE 管理方案（即已纳入规范管理的患者）
        // 注意：若同一患者同一病种存在多条 ACTIVE 方案（数据质量问题），会导致控制数偏高；
        // 当前依赖业务约束保证同一患者同一病种最多一条 ACTIVE 方案
        try {
            LambdaQueryWrapper<ChManagePlan> mlqw = Wrappers.lambdaQuery();
            mlqw.eq(ChManagePlan::getDiseaseCode, diseaseCode);
            mlqw.eq(ChManagePlan::getPlanStatus, "ACTIVE");
            Long controlledCount = managePlanMapper.selectCount(mlqw);
            long controlled = controlledCount != null ? controlledCount : 0L;
            vo.setControlledCount(controlled);
            vo.setControlRate(total == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(controlled * 100.0 / total).setScale(2, java.math.RoundingMode.HALF_UP));
        } catch (Exception e) {
            log.warn("查询控制率失败, diseaseCode={}", diseaseCode, e);
            vo.setControlledCount(0L);
            vo.setControlRate(BigDecimal.ZERO);
        }

        // 按病种关联的患者ID列表（用于预警和随访查询）
        LambdaQueryWrapper<ChPatientDisease> plqw = Wrappers.lambdaQuery();
        plqw.eq(ChPatientDisease::getDiseaseCode, diseaseCode);
        plqw.select(ChPatientDisease::getPatientId);
        List<ChPatientDisease> patients = patientDiseaseMapper.selectList(plqw);
        List<Long> patientIds = patients.stream().map(ChPatientDisease::getPatientId).distinct().toList();

        // 无患者时直接跳过预警和随访查询，避免无过滤的全表扫描
        if (patientIds.isEmpty()) {
            vo.setWarningCount(0L);
            vo.setFollowupCompletedCount(0L);
            vo.setFollowupRate(BigDecimal.ZERO);
        } else {
            // 预警数（NEW + PROCESSING 状态的预警事件）
            try {
                LambdaQueryWrapper<org.dromara.chronic.domain.entity.ChWarningEvent> wlqw = Wrappers.lambdaQuery();
                wlqw.in(org.dromara.chronic.domain.entity.ChWarningEvent::getEventStatus, List.of("NEW", "PROCESSING"));
                wlqw.in(org.dromara.chronic.domain.entity.ChWarningEvent::getPatientId, patientIds);
                Long warningCount = warningEventMapper.selectCount(wlqw);
                vo.setWarningCount(warningCount != null ? warningCount : 0L);
            } catch (Exception e) {
                log.warn("查询预警数失败, diseaseCode={}", diseaseCode, e);
                vo.setWarningCount(0L);
            }

            // 随访完成数 & 随访完成率
            try {
                LambdaQueryWrapper<org.dromara.chronic.domain.entity.ChFollowupTask> flqw = Wrappers.lambdaQuery();
                flqw.eq(org.dromara.chronic.domain.entity.ChFollowupTask::getTaskStatus, "DONE");
                flqw.in(org.dromara.chronic.domain.entity.ChFollowupTask::getPatientId, patientIds);
                Long doneCount = followupTaskMapper.selectCount(flqw);
                vo.setFollowupCompletedCount(doneCount != null ? doneCount : 0L);
                // 随访完成率 = 已完成 / (已完成 + 待完成 + 逾期)
                LambdaQueryWrapper<org.dromara.chronic.domain.entity.ChFollowupTask> allFlqw = Wrappers.lambdaQuery();
                allFlqw.in(org.dromara.chronic.domain.entity.ChFollowupTask::getPatientId, patientIds);
                Long totalTaskCount = followupTaskMapper.selectCount(allFlqw);
                long totalTasks = totalTaskCount != null ? totalTaskCount : 0L;
                vo.setFollowupRate(totalTasks == 0 ? BigDecimal.ZERO :
                    BigDecimal.valueOf((doneCount != null ? doneCount : 0L) * 100.0 / totalTasks).setScale(2, java.math.RoundingMode.HALF_UP));
            } catch (Exception e) {
                log.warn("查询随访完成率失败, diseaseCode={}", diseaseCode, e);
                vo.setFollowupCompletedCount(0L);
                vo.setFollowupRate(BigDecimal.ZERO);
            }
        }

        // 新增患者数（近30天确诊）
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -30);
            Date thirtyDaysAgo = cal.getTime();
            LambdaQueryWrapper<ChPatientDisease> nlqw = Wrappers.lambdaQuery();
            nlqw.eq(ChPatientDisease::getDiseaseCode, diseaseCode);
            nlqw.ge(ChPatientDisease::getConfirmDate, thirtyDaysAgo);
            Long newCount = patientDiseaseMapper.selectCount(nlqw);
            vo.setNewPatientCount(newCount != null ? newCount : 0L);
        } catch (Exception e) {
            log.warn("查询新增患者数失败, diseaseCode={}", diseaseCode, e);
            vo.setNewPatientCount(0L);
        }

        vo.setStatPeriod("近30天");
        return vo;
    }

    private static String resolveDiseaseName(String diseaseCode) {
        return switch (diseaseCode) {
            case "HYPERTENSION", "HTN" -> "原发性高血压";
            case "DIABETES", "T2DM" -> "2型糖尿病";
            case "COPD" -> "慢性阻塞性肺疾病";
            case "ASTHMA" -> "支气管哮喘";
            case "CHD" -> "冠状动脉粥样硬化性心脏病";
            case "STROKE" -> "脑卒中";
            case "CANCER", "TUMOR" -> "恶性肿瘤";
            case "CKD" -> "慢性肾脏病";
            case "DYSLIPID" -> "血脂异常";
            case "MENTAL_DISORDER" -> "严重精神障碍";
            default -> diseaseCode;
        };
    }
}
