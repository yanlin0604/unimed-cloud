package org.dromara.chronic.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.chronic.common.helper.DiseaseNameHelper;
import org.dromara.chronic.domain.entity.*;
import org.dromara.chronic.domain.vo.ChFollowupStatVo;
import org.dromara.chronic.mapper.*;
import org.dromara.chronic.service.IChFollowupStatService;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.system.api.RemoteUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 随访多维统计服务实现
 * <p>
 * 所有分布/趋势口径均以 GROUP BY 聚合下推到数据库,不再整表加载后在内存分组。
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChFollowupStatServiceImpl implements IChFollowupStatService {

    // TODO: areaCode/orgId 参数当前仅作签名透传,未参与实际过滤。
    // 数据模型下随访/任务/记录表均无 areaCode 字段,行政区划维度暂不可覆盖;
    // orgId 需经 ch_patient_profile 关联,待多机构维度上线后再接入过滤。

    /** 聚合计数列别名 */
    private static final String CNT = "COUNT(*) AS cnt";

    /** 按天分组结果的日期列别名 */
    private static final String STAT_DAY = "stat_day";

    private final ChFollowupTaskMapper followupTaskMapper;
    private final ChFollowupRecordMapper followupRecordMapper;
    private final ChFollowupPlanMapper followupPlanMapper;
    private final ChStatFollowupDayMapper statFollowupDayMapper;
    private final DiseaseNameHelper diseaseNameHelper;

    @DubboReference(mock = "true")
    private RemoteUserService remoteUserService;

    @Override
    public ChFollowupStatVo getFullStatDashboard(String areaCode, Long orgId) {
        ChFollowupStatVo vo = new ChFollowupStatVo();
        vo.setOverview(getOverview(areaCode, orgId));
        vo.setTrendList(getTrend(15, areaCode, orgId));
        vo.setTypeDistribution(getTypeDistribution(areaCode, orgId));
        vo.setAssigneeRanking(getAssigneeRanking(10, areaCode, orgId));
        vo.setDiseaseStats(getDiseaseStats(areaCode, orgId));
        vo.setResultDistribution(getResultDistribution(areaCode, orgId));
        vo.setRehabDistribution(getRehabDistribution(areaCode, orgId));
        vo.setStatusDistribution(getStatusDistribution(areaCode, orgId));
        vo.setTaskTypeDistribution(getTaskTypeDistribution(areaCode, orgId));
        vo.setLostReasonStats(getLostReasonStats(areaCode, orgId));
        vo.setControlledTrend(getControlledTrend(15, areaCode, orgId));
        return vo;
    }

    @Override
    public ChFollowupStatVo.Overview getOverview(String areaCode, Long orgId) {
        ChFollowupStatVo.Overview overview = new ChFollowupStatVo.Overview();

        // 任务状态/任务类型各一次 GROUP BY 即可覆盖 已完成/逾期/已取消/动态/转诊追踪/预警临时 六个口径
        Map<String, Long> statusMap = groupCount(followupTaskMapper, "task_status", null);
        Map<String, Long> taskTypeMap = groupCount(followupTaskMapper, "task_type", null);
        Map<String, Long> resultMap = groupCount(followupRecordMapper, "followup_result", null);

        // 总数需含状态/结论为 NULL 的行,口径与分组结果不同,单独 count
        Long totalCount = followupTaskMapper.selectCount(Wrappers.<ChFollowupTask>lambdaQuery());
        Long totalRecords = followupRecordMapper.selectCount(Wrappers.<ChFollowupRecord>lambdaQuery());

        Long doneCount = statusMap.getOrDefault("DONE", 0L);
        Long overdueCount = statusMap.getOrDefault("OVERDUE", 0L);
        Long unassignedCount = followupTaskMapper.selectCount(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .isNull(ChFollowupTask::getAssigneeUserId)
                .in(ChFollowupTask::getTaskStatus, List.of("PENDING", "REMINDING", "OVERDUE")));

        Date todayStart = DateUtil.beginOfDay(new Date());
        Date todayEnd = DateUtil.endOfDay(new Date());

        Long todayPendingCount = followupTaskMapper.selectCount(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .ge(ChFollowupTask::getPlanDueDate, todayStart)
                .le(ChFollowupTask::getPlanDueDate, todayEnd)
                .in(ChFollowupTask::getTaskStatus, List.of("PENDING", "REMINDING")));

        Long todayDoneCount = followupRecordMapper.selectCount(
            Wrappers.<ChFollowupRecord>lambdaQuery()
                .ge(ChFollowupRecord::getVisitDate, todayStart)
                .le(ChFollowupRecord::getVisitDate, todayEnd));

        overview.setTotalCount(totalCount);
        overview.setDoneCount(doneCount);
        overview.setOverdueCount(overdueCount);
        overview.setUnassignedCount(unassignedCount);
        overview.setTodayPendingCount(todayPendingCount);
        overview.setTodayDoneCount(todayDoneCount);

        if (totalCount > 0) {
            BigDecimal compRate = BigDecimal.valueOf(doneCount * 100.0 / totalCount)
                .setScale(2, RoundingMode.HALF_UP);
            overview.setCompletionRate(compRate);
        }

        Long controlledRecords = resultMap.getOrDefault("CONTROLLED", 0L);
        if (totalRecords > 0) {
            BigDecimal ctrlRate = BigDecimal.valueOf(controlledRecords * 100.0 / totalRecords)
                .setScale(2, RoundingMode.HALF_UP);
            overview.setControlledRate(ctrlRate);
        }

        Long faceToFaceDone = followupTaskMapper.selectCount(
            Wrappers.<ChFollowupTask>lambdaQuery().eq(ChFollowupTask::getTaskStatus, "DONE").eq(ChFollowupTask::getIsFaceToFace, true));
        overview.setFaceToFaceDoneCount(faceToFaceDone);
        if (doneCount > 0) {
            overview.setFaceToFaceRate(BigDecimal.valueOf(faceToFaceDone * 100.0 / doneCount).setScale(2, RoundingMode.HALF_UP));
        }

        overview.setDynamicTaskCount(taskTypeMap.getOrDefault("DYNAMIC", 0L));
        overview.setReferralTrackCount(taskTypeMap.getOrDefault("REFERRAL_TRACK", 0L));
        overview.setEmergencyTaskCount(taskTypeMap.getOrDefault("EMERGENCY", 0L));
        overview.setLostCancelCount(statusMap.getOrDefault("CANCELLED", 0L));

        return overview;
    }

    @Override
    public List<ChFollowupStatVo.TrendItem> getTrend(int days, String areaCode, Long orgId) {
        if (days <= 0) {
            days = 7;
        }
        Date today = new Date();
        Date rangeStart = DateUtil.beginOfDay(DateUtil.offsetDay(today, -(days - 1)));
        Date rangeEnd = DateUtil.endOfDay(today);

        // 整个区间各一次按天 GROUP BY,替代原先逐日 3 次 selectCount
        Map<String, Long> plannedMap = dailyCount(followupTaskMapper, "plan_due_date", rangeStart, rangeEnd, null);
        Map<String, Long> doneMap = dailyCount(followupRecordMapper, "visit_date", rangeStart, rangeEnd, null);
        Map<String, Long> overdueMap = dailyCount(followupTaskMapper, "plan_due_date", rangeStart, rangeEnd,
            w -> w.eq("task_status", "OVERDUE"));

        List<ChFollowupStatVo.TrendItem> trendList = new ArrayList<>(days);
        for (int i = days - 1; i >= 0; i--) {
            String dateStr = DateUtil.format(DateUtil.offsetDay(today, -i), "yyyy-MM-dd");
            Long planned = plannedMap.getOrDefault(dateStr, 0L);
            Long done = doneMap.getOrDefault(dateStr, 0L);

            ChFollowupStatVo.TrendItem item = new ChFollowupStatVo.TrendItem();
            item.setDate(dateStr);
            item.setPlannedCount(planned);
            item.setDoneCount(done);
            item.setOverdueCount(overdueMap.getOrDefault(dateStr, 0L));
            if (planned > 0) {
                item.setCompletionRate(BigDecimal.valueOf(done * 100.0 / planned).setScale(2, RoundingMode.HALF_UP));
            }
            trendList.add(item);
        }
        return trendList;
    }

    @Override
    public List<ChFollowupStatVo.TypeDistributionItem> getTypeDistribution(String areaCode, Long orgId) {
        Map<String, Long> countMap = groupCount(followupTaskMapper, "visit_type", null);
        // 分母沿用全部任务数(含 visit_type 为空的任务)
        long total = followupTaskMapper.selectCount(Wrappers.<ChFollowupTask>lambdaQuery());

        Map<String, String> typeLabelMap = Map.of(
            "ONLINE", "线上随访",
            "OFFLINE", "线下随访",
            "PHONE", "电话随访"
        );

        List<ChFollowupStatVo.TypeDistributionItem> result = new ArrayList<>();
        for (String type : List.of("ONLINE", "OFFLINE", "PHONE")) {
            Long count = countMap.getOrDefault(type, 0L);
            ChFollowupStatVo.TypeDistributionItem item = new ChFollowupStatVo.TypeDistributionItem();
            item.setVisitType(type);
            item.setVisitTypeName(typeLabelMap.getOrDefault(type, type));
            item.setCount(count);
            if (total > 0) {
                item.setPercentage(BigDecimal.valueOf(count * 100.0 / total).setScale(2, RoundingMode.HALF_UP));
            }
            result.add(item);
        }
        return result;
    }

    @Override
    public List<ChFollowupStatVo.AssigneeRankItem> getAssigneeRanking(int limit, String areaCode, Long orgId) {
        if (limit <= 0) {
            limit = 10;
        }
        // 执行人 × 状态两列聚合,行数为 执行人数 × 状态数,替代原先整表(全字段)加载后内存分组
        List<Map<String, Object>> rows = groupRows(followupTaskMapper, "assignee_user_id", "task_status",
            w -> w.isNotNull("assignee_user_id"));

        Map<Long, StatAgg> userAgg = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Long userId = asLong(row.get("assignee_user_id"));
            Long cnt = asLong(row.get("cnt"));
            if (userId == null || cnt == null) {
                continue;
            }
            StatAgg agg = userAgg.computeIfAbsent(userId, k -> new StatAgg());
            agg.total += cnt;
            String status = asString(row.get("task_status"));
            if ("DONE".equals(status)) {
                agg.done += cnt;
            } else if ("OVERDUE".equals(status)) {
                agg.overdue += cnt;
            }
        }

        Map<Long, String> nickNames = new HashMap<>();
        if (!userAgg.isEmpty()) {
            try {
                nickNames = remoteUserService.selectUserNicksByIds(new ArrayList<>(userAgg.keySet()));
            } catch (Exception e) {
                log.warn("获取执行人昵称失败", e);
            }
        }

        List<ChFollowupStatVo.AssigneeRankItem> rankList = new ArrayList<>();
        for (Map.Entry<Long, StatAgg> entry : userAgg.entrySet()) {
            Long userId = entry.getKey();
            StatAgg agg = entry.getValue();

            ChFollowupStatVo.AssigneeRankItem item = new ChFollowupStatVo.AssigneeRankItem();
            item.setAssigneeUserId(userId);
            item.setAssigneeNickName(nickNames.getOrDefault(userId, "执行人" + userId));
            item.setTotalTasks(agg.total);
            item.setDoneTasks(agg.done);
            item.setPendingTasks(agg.total - agg.done);
            item.setOverdueTasks(agg.overdue);
            if (agg.total > 0) {
                item.setCompletionRate(BigDecimal.valueOf(agg.done * 100.0 / agg.total).setScale(2, RoundingMode.HALF_UP));
            }
            rankList.add(item);
        }

        rankList.sort((a, b) -> Long.compare(b.getDoneTasks(), a.getDoneTasks()));
        return rankList.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<ChFollowupStatVo.DiseaseStatItem> getDiseaseStats(String areaCode, Long orgId) {
        // 仅计划表取 plan_id -> disease_code 映射(两列),任务/记录侧全部走聚合,不再整表加载
        List<ChFollowupPlan> plans = followupPlanMapper.selectList(
            Wrappers.<ChFollowupPlan>lambdaQuery()
                .select(ChFollowupPlan::getPlanId, ChFollowupPlan::getDiseaseCode)
                .isNotNull(ChFollowupPlan::getDiseaseCode));

        Map<Long, String> planDiseaseMap = plans.stream()
            .filter(Objects::nonNull)
            .filter(p -> p.getPlanId() != null && StringUtils.isNotBlank(p.getDiseaseCode()))
            .collect(Collectors.toMap(ChFollowupPlan::getPlanId, ChFollowupPlan::getDiseaseCode, (a, b) -> a));
        if (planDiseaseMap.isEmpty()) {
            return new ArrayList<>();
        }

        // 计划 × 状态聚合:行数为 计划数 × 状态数
        List<Map<String, Object>> taskRows = groupRows(followupTaskMapper, "plan_id", "task_status", null);

        // 控制良好数:以 task_id 子查询关联随访记录后按 plan_id 聚合。
        // 子查询手写 del_flag 条件(逻辑删除插件不覆盖 inSql 片段),租户条件由租户插件解析补全。
        Map<String, Long> planControlledMap = groupCount(followupTaskMapper, "plan_id",
            w -> w.inSql("task_id",
                "SELECT task_id FROM ch_followup_record WHERE followup_result = 'CONTROLLED' AND del_flag = '0'"));

        Map<String, StatAgg> diseaseAgg = new HashMap<>();
        for (Map<String, Object> row : taskRows) {
            Long planId = asLong(row.get("plan_id"));
            Long cnt = asLong(row.get("cnt"));
            if (planId == null || cnt == null) {
                continue;
            }
            String code = planDiseaseMap.get(planId);
            if (StringUtils.isBlank(code)) {
                continue;
            }
            StatAgg agg = diseaseAgg.computeIfAbsent(code, k -> new StatAgg());
            agg.total += cnt;
            if ("DONE".equals(asString(row.get("task_status")))) {
                agg.done += cnt;
            }
        }
        planControlledMap.forEach((planIdStr, cnt) -> {
            Long planId = asLong(planIdStr);
            String code = planId == null ? null : planDiseaseMap.get(planId);
            if (StringUtils.isNotBlank(code)) {
                diseaseAgg.computeIfAbsent(code, k -> new StatAgg()).controlled += cnt;
            }
        });

        Map<String, String> diseaseNameMap = diseaseNameHelper.batchGetDiseaseName(new ArrayList<>(diseaseAgg.keySet()));

        List<ChFollowupStatVo.DiseaseStatItem> statList = new ArrayList<>();
        for (Map.Entry<String, StatAgg> entry : diseaseAgg.entrySet()) {
            String code = entry.getKey();
            StatAgg agg = entry.getValue();

            ChFollowupStatVo.DiseaseStatItem item = new ChFollowupStatVo.DiseaseStatItem();
            item.setDiseaseCode(code);
            item.setDiseaseName(diseaseNameMap.getOrDefault(code, code));
            item.setTotalCount(agg.total);
            item.setDoneCount(agg.done);
            item.setControlledCount(agg.controlled);
            if (agg.total > 0) {
                item.setCompletionRate(BigDecimal.valueOf(agg.done * 100.0 / agg.total).setScale(2, RoundingMode.HALF_UP));
            }
            if (agg.done > 0) {
                item.setControlledRate(BigDecimal.valueOf(agg.controlled * 100.0 / agg.done).setScale(2, RoundingMode.HALF_UP));
            }
            statList.add(item);
        }

        statList.sort((a, b) -> Long.compare(b.getTotalCount(), a.getTotalCount()));
        return statList;
    }

    @Override
    public List<ChFollowupStatVo.ResultDistributionItem> getResultDistribution(String areaCode, Long orgId) {
        Map<String, Long> countMap = groupCount(followupRecordMapper, "followup_result", null);
        List<String> order = List.of("CONTROLLED", "IMPROVING", "UNCONTROLLED", "DETERIORATING", "REFERRAL");
        return buildDistributionCnt(order, countMap, this::resultName);
    }

    @Override
    public List<ChFollowupStatVo.RehabDistributionItem> getRehabDistribution(String areaCode, Long orgId) {
        Map<String, Long> countMap = groupCount(followupRecordMapper, "rehab_level", null);

        List<String> order = List.of("EXCELLENT", "GOOD", "FAIR", "POOR");
        List<ChFollowupStatVo.RehabDistributionItem> result = new ArrayList<>();
        long total = totalOf(countMap);
        for (String level : order) {
            Long count = countMap.getOrDefault(level, 0L);
            ChFollowupStatVo.RehabDistributionItem item = new ChFollowupStatVo.RehabDistributionItem();
            item.setRehabLevel(level);
            item.setRehabLevelName(rehabLevelName(level));
            item.setCount(count);
            setPercentage(item, count, total);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<ChFollowupStatVo.StatusDistributionItem> getStatusDistribution(String areaCode, Long orgId) {
        Map<String, Long> countMap = groupCount(followupTaskMapper, "task_status", null);

        List<String> order = List.of("DONE", "PATIENT_FILLED", "PENDING", "REMINDING", "OVERDUE", "CANCELLED");
        List<ChFollowupStatVo.StatusDistributionItem> result = new ArrayList<>();
        long total = totalOf(countMap);
        for (String status : order) {
            Long count = countMap.getOrDefault(status, 0L);
            ChFollowupStatVo.StatusDistributionItem item = new ChFollowupStatVo.StatusDistributionItem();
            item.setTaskStatus(status);
            item.setTaskStatusName(taskStatusName(status));
            item.setCount(count);
            setPercentage(item, count, total);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<ChFollowupStatVo.TaskTypeDistributionItem> getTaskTypeDistribution(String areaCode, Long orgId) {
        Map<String, Long> countMap = groupCount(followupTaskMapper, "task_type", null);

        List<String> order = List.of("NORMAL", "DYNAMIC", "REFERRAL_TRACK", "EMERGENCY");
        List<ChFollowupStatVo.TaskTypeDistributionItem> result = new ArrayList<>();
        long total = totalOf(countMap);
        for (String type : order) {
            Long count = countMap.getOrDefault(type, 0L);
            ChFollowupStatVo.TaskTypeDistributionItem item = new ChFollowupStatVo.TaskTypeDistributionItem();
            item.setTaskType(type);
            item.setTaskTypeName(taskTypeName(type));
            item.setCount(count);
            setPercentage(item, count, total);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<ChFollowupStatVo.LostReasonItem> getLostReasonStats(String areaCode, Long orgId) {
        Map<String, Long> countMap = groupCount(followupTaskMapper, "cancel_reason_code", null);

        List<String> order = List.of("LOST", "REFUSED", "RELOCATED", "DECEASED", "OTHER");
        List<ChFollowupStatVo.LostReasonItem> result = new ArrayList<>();
        long total = totalOf(countMap);
        for (String code : order) {
            Long count = countMap.getOrDefault(code, 0L);
            ChFollowupStatVo.LostReasonItem item = new ChFollowupStatVo.LostReasonItem();
            item.setCancelReasonCode(code);
            item.setReasonName(lostReasonName(code));
            item.setCount(count);
            setPercentage(item, count, total);
            result.add(item);
        }
        return result;
    }

    @Override
    public List<ChFollowupStatVo.RateTrendItem> getControlledTrend(int days, String areaCode, Long orgId) {
        if (days <= 0) {
            days = 7;
        }
        Date today = new Date();
        Date rangeStart = DateUtil.beginOfDay(DateUtil.offsetDay(today, -(days - 1)));
        Date rangeEnd = DateUtil.endOfDay(today);

        Map<String, Long> plannedMap = dailyCount(followupTaskMapper, "plan_due_date", rangeStart, rangeEnd, null);
        Map<String, Long> doneMap = dailyCount(followupRecordMapper, "visit_date", rangeStart, rangeEnd, null);
        Map<String, Long> overdueMap = dailyCount(followupTaskMapper, "plan_due_date", rangeStart, rangeEnd,
            w -> w.eq("task_status", "OVERDUE"));
        Map<String, Long> controlledMap = dailyCount(followupRecordMapper, "visit_date", rangeStart, rangeEnd,
            w -> w.eq("followup_result", "CONTROLLED"));

        List<ChFollowupStatVo.RateTrendItem> trendList = new ArrayList<>(days);
        for (int i = days - 1; i >= 0; i--) {
            String dateStr = DateUtil.format(DateUtil.offsetDay(today, -i), "yyyy-MM-dd");
            Long done = doneMap.getOrDefault(dateStr, 0L);
            Long controlled = controlledMap.getOrDefault(dateStr, 0L);

            ChFollowupStatVo.RateTrendItem item = new ChFollowupStatVo.RateTrendItem();
            item.setDate(dateStr);
            item.setPlannedCount(plannedMap.getOrDefault(dateStr, 0L));
            item.setDoneCount(done);
            item.setOverdueCount(overdueMap.getOrDefault(dateStr, 0L));
            item.setControlledCount(controlled);
            if (done > 0) {
                item.setCompletionRate(BigDecimal.valueOf(controlled * 100.0 / done).setScale(2, RoundingMode.HALF_UP));
            }
            trendList.add(item);
        }
        return trendList;
    }

    // ===== GROUP BY 聚合下推工具 =====

    /**
     * 单列分组计数,已剔除该列为 NULL/空串的分组
     *
     * @param mapper 目标表 Mapper
     * @param column 分组列(下划线列名)
     * @param extra  额外过滤条件,可为 null
     */
    private <T> Map<String, Long> groupCount(BaseMapper<T> mapper, String column, Consumer<QueryWrapper<T>> extra) {
        QueryWrapper<T> wrapper = Wrappers.<T>query().select(column, CNT).groupBy(column);
        if (extra != null) {
            extra.accept(wrapper);
        }
        List<Map<String, Object>> rows = mapper.selectMaps(wrapper);
        Map<String, Long> countMap = new HashMap<>(rows.size());
        for (Map<String, Object> row : rows) {
            String key = asString(row.get(column));
            Long cnt = asLong(row.get("cnt"));
            if (StringUtils.isNotBlank(key) && cnt != null) {
                countMap.merge(key, cnt, Long::sum);
            }
        }
        return countMap;
    }

    /**
     * 双列分组计数,返回原始行(交由调用方按各自类型解析 key)
     */
    private <T> List<Map<String, Object>> groupRows(BaseMapper<T> mapper, String col1, String col2,
                                                    Consumer<QueryWrapper<T>> extra) {
        QueryWrapper<T> wrapper = Wrappers.<T>query().select(col1, col2, CNT).groupBy(col1, col2);
        if (extra != null) {
            extra.accept(wrapper);
        }
        return mapper.selectMaps(wrapper);
    }

    /**
     * 按天分组计数,返回 yyyy-MM-dd -> 数量
     *
     * @param dateColumn 日期列(下划线列名)
     * @param start      区间起(含)
     * @param end        区间止(含)
     * @param extra      额外过滤条件,可为 null
     */
    private <T> Map<String, Long> dailyCount(BaseMapper<T> mapper, String dateColumn, Date start, Date end,
                                             Consumer<QueryWrapper<T>> extra) {
        String dayExpr = "DATE_FORMAT(" + dateColumn + ", '%Y-%m-%d')";
        QueryWrapper<T> wrapper = Wrappers.<T>query()
            .select(dayExpr + " AS " + STAT_DAY, CNT)
            .ge(dateColumn, start)
            .le(dateColumn, end)
            .groupBy(dayExpr);
        if (extra != null) {
            extra.accept(wrapper);
        }
        List<Map<String, Object>> rows = mapper.selectMaps(wrapper);
        Map<String, Long> dayMap = new HashMap<>(rows.size());
        for (Map<String, Object> row : rows) {
            String day = asString(row.get(STAT_DAY));
            Long cnt = asLong(row.get("cnt"));
            if (StringUtils.isNotBlank(day) && cnt != null) {
                dayMap.merge(day, cnt, Long::sum);
            }
        }
        return dayMap;
    }

    private String asString(Object value) {
        return value == null ? null : value.toString();
    }

    private Long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof CharSequence text && StringUtils.isNotBlank(text)) {
            try {
                return Long.parseLong(text.toString().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /** 分组累加中间量(执行人/病种维度共用) */
    private static final class StatAgg {
        private long total;
        private long done;
        private long overdue;
        private long controlled;
    }

    // ===== 分布统计通用工具 =====

    private long totalOf(Map<String, Long> countMap) {
        return countMap.values().stream().mapToLong(Long::longValue).sum();
    }

    private void setPercentage(Object item, long count, long total) {
        if (item instanceof ChFollowupStatVo.ResultDistributionItem ri) {
            ri.setPercentage(total > 0 ? BigDecimal.valueOf(count * 100.0 / total).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        } else if (item instanceof ChFollowupStatVo.RehabDistributionItem rd) {
            rd.setPercentage(total > 0 ? BigDecimal.valueOf(count * 100.0 / total).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        } else if (item instanceof ChFollowupStatVo.StatusDistributionItem sd) {
            sd.setPercentage(total > 0 ? BigDecimal.valueOf(count * 100.0 / total).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        } else if (item instanceof ChFollowupStatVo.TaskTypeDistributionItem tt) {
            tt.setPercentage(total > 0 ? BigDecimal.valueOf(count * 100.0 / total).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        } else if (item instanceof ChFollowupStatVo.LostReasonItem lr) {
            lr.setPercentage(total > 0 ? BigDecimal.valueOf(count * 100.0 / total).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        }
    }

    private List<ChFollowupStatVo.ResultDistributionItem> buildDistributionCnt(List<String> order, Map<String, Long> countMap, Function<String, String> nameFn) {
        List<ChFollowupStatVo.ResultDistributionItem> result = new ArrayList<>();
        long total = totalOf(countMap);
        for (String key : order) {
            Long count = countMap.getOrDefault(key, 0L);
            ChFollowupStatVo.ResultDistributionItem item = new ChFollowupStatVo.ResultDistributionItem();
            item.setResult(key);
            item.setResultName(nameFn.apply(key));
            item.setCount(count);
            item.setPercentage(total > 0 ? BigDecimal.valueOf(count * 100.0 / total).setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO);
            result.add(item);
        }
        return result;
    }

    private String resultName(String code) {
        return switch (StringUtils.defaultString(code)) {
            case "CONTROLLED" -> "控制良好";
            case "IMPROVING" -> "好转中";
            case "UNCONTROLLED" -> "控制不佳";
            case "DETERIORATING" -> "恶化";
            case "REFERRAL" -> "建议转诊";
            default -> code;
        };
    }

    private String rehabLevelName(String code) {
        return switch (StringUtils.defaultString(code)) {
            case "EXCELLENT" -> "优秀";
            case "GOOD" -> "良好";
            case "FAIR" -> "一般";
            case "POOR" -> "较差";
            default -> code;
        };
    }

    private String taskStatusName(String code) {
        return switch (StringUtils.defaultString(code)) {
            case "DONE" -> "已完成";
            case "PATIENT_FILLED" -> "已自填待评估";
            case "PENDING" -> "待处理";
            case "REMINDING" -> "提醒中";
            case "OVERDUE" -> "已逾期";
            case "CANCELLED" -> "已取消";
            default -> code;
        };
    }

    private String taskTypeName(String code) {
        return switch (StringUtils.defaultString(code)) {
            case "NORMAL" -> "常规随访";
            case "DYNAMIC" -> "动态调整";
            case "REFERRAL_TRACK" -> "转诊追踪";
            case "EMERGENCY" -> "预警临时";
            default -> code;
        };
    }

    private String lostReasonName(String code) {
        return switch (StringUtils.defaultString(code)) {
            case "LOST" -> "失访";
            case "REFUSED" -> "拒绝随访";
            case "RELOCATED" -> "搬迁";
            case "DECEASED" -> "死亡";
            case "OTHER" -> "其他";
            default -> code;
        };
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void aggregateDailyStat(Date statDate) {
        if (statDate == null) {
            statDate = DateUtil.offsetDay(new Date(), -1);
        }
        Date start = DateUtil.beginOfDay(statDate);
        Date end = DateUtil.endOfDay(statDate);

        Long totalCount = followupTaskMapper.selectCount(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .ge(ChFollowupTask::getPlanDueDate, start)
                .le(ChFollowupTask::getPlanDueDate, end));

        Long doneCount = followupRecordMapper.selectCount(
            Wrappers.<ChFollowupRecord>lambdaQuery()
                .ge(ChFollowupRecord::getVisitDate, start)
                .le(ChFollowupRecord::getVisitDate, end));

        Long overdueCount = followupTaskMapper.selectCount(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getTaskStatus, "OVERDUE")
                .ge(ChFollowupTask::getPlanDueDate, start)
                .le(ChFollowupTask::getPlanDueDate, end));

        BigDecimal rate = BigDecimal.ZERO;
        if (totalCount > 0) {
            rate = BigDecimal.valueOf(doneCount * 100.0 / totalCount).setScale(2, RoundingMode.HALF_UP);
        }

        ChStatFollowupDay stat = statFollowupDayMapper.selectOne(
            Wrappers.<ChStatFollowupDay>lambdaQuery().eq(ChStatFollowupDay::getStatDate, start));
        if (stat == null) {
            stat = new ChStatFollowupDay();
            stat.setStatDate(start);
            stat.setTotalCount(totalCount);
            stat.setDoneCount(doneCount);
            stat.setOverdueCount(overdueCount);
            stat.setCompletionRate(rate);
            statFollowupDayMapper.insert(stat);
        } else {
            stat.setTotalCount(totalCount);
            stat.setDoneCount(doneCount);
            stat.setOverdueCount(overdueCount);
            stat.setCompletionRate(rate);
            statFollowupDayMapper.updateById(stat);
        }
        log.info("随访日统计聚合完成 statDate={}, total={}, done={}, overdue={}, rate={}%",
            DateUtil.formatDate(start), totalCount, doneCount, overdueCount, rate);
    }
}
