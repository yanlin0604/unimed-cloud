package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
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
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 随访多维统计服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChFollowupStatServiceImpl implements IChFollowupStatService {

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
        return vo;
    }

    @Override
    public ChFollowupStatVo.Overview getOverview(String areaCode, Long orgId) {
        ChFollowupStatVo.Overview overview = new ChFollowupStatVo.Overview();

        Long totalCount = followupTaskMapper.selectCount(Wrappers.<ChFollowupTask>lambdaQuery());
        Long doneCount = followupTaskMapper.selectCount(
            Wrappers.<ChFollowupTask>lambdaQuery().eq(ChFollowupTask::getTaskStatus, "DONE"));
        Long overdueCount = followupTaskMapper.selectCount(
            Wrappers.<ChFollowupTask>lambdaQuery().eq(ChFollowupTask::getTaskStatus, "OVERDUE"));
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

        Long totalRecords = followupRecordMapper.selectCount(Wrappers.<ChFollowupRecord>lambdaQuery());
        Long controlledRecords = followupRecordMapper.selectCount(
            Wrappers.<ChFollowupRecord>lambdaQuery().eq(ChFollowupRecord::getFollowupResult, "CONTROLLED"));
        if (totalRecords > 0) {
            BigDecimal ctrlRate = BigDecimal.valueOf(controlledRecords * 100.0 / totalRecords)
                .setScale(2, RoundingMode.HALF_UP);
            overview.setControlledRate(ctrlRate);
        }

        return overview;
    }

    @Override
    public List<ChFollowupStatVo.TrendItem> getTrend(int days, String areaCode, Long orgId) {
        if (days <= 0) {
            days = 7;
        }
        List<ChFollowupStatVo.TrendItem> trendList = new ArrayList<>(days);
        Date today = new Date();

        for (int i = days - 1; i >= 0; i--) {
            Date targetDate = DateUtil.offsetDay(today, -i);
            Date start = DateUtil.beginOfDay(targetDate);
            Date end = DateUtil.endOfDay(targetDate);
            String dateStr = DateUtil.format(targetDate, "yyyy-MM-dd");

            Long planned = followupTaskMapper.selectCount(
                Wrappers.<ChFollowupTask>lambdaQuery()
                    .ge(ChFollowupTask::getPlanDueDate, start)
                    .le(ChFollowupTask::getPlanDueDate, end));

            Long done = followupRecordMapper.selectCount(
                Wrappers.<ChFollowupRecord>lambdaQuery()
                    .ge(ChFollowupRecord::getVisitDate, start)
                    .le(ChFollowupRecord::getVisitDate, end));

            Long overdue = followupTaskMapper.selectCount(
                Wrappers.<ChFollowupTask>lambdaQuery()
                    .eq(ChFollowupTask::getTaskStatus, "OVERDUE")
                    .ge(ChFollowupTask::getPlanDueDate, start)
                    .le(ChFollowupTask::getPlanDueDate, end));

            ChFollowupStatVo.TrendItem item = new ChFollowupStatVo.TrendItem();
            item.setDate(dateStr);
            item.setPlannedCount(planned);
            item.setDoneCount(done);
            item.setOverdueCount(overdue);
            if (planned > 0) {
                item.setCompletionRate(BigDecimal.valueOf(done * 100.0 / planned).setScale(2, RoundingMode.HALF_UP));
            }
            trendList.add(item);
        }
        return trendList;
    }

    @Override
    public List<ChFollowupStatVo.TypeDistributionItem> getTypeDistribution(String areaCode, Long orgId) {
        List<ChFollowupTask> tasks = followupTaskMapper.selectList(
            Wrappers.<ChFollowupTask>lambdaQuery().select(ChFollowupTask::getVisitType));

        long total = tasks.size();
        Map<String, Long> countMap = tasks.stream()
            .map(ChFollowupTask::getVisitType)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

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
        List<ChFollowupTask> tasks = followupTaskMapper.selectList(
            Wrappers.<ChFollowupTask>lambdaQuery().isNotNull(ChFollowupTask::getAssigneeUserId));

        Map<Long, List<ChFollowupTask>> userTasksMap = tasks.stream()
            .collect(Collectors.groupingBy(ChFollowupTask::getAssigneeUserId));

        List<Long> userIds = new ArrayList<>(userTasksMap.keySet());
        Map<Long, String> nickNames = new HashMap<>();
        if (!userIds.isEmpty()) {
            try {
                nickNames = remoteUserService.selectUserNicksByIds(userIds);
            } catch (Exception e) {
                log.warn("获取执行人昵称失败", e);
            }
        }

        List<ChFollowupStatVo.AssigneeRankItem> rankList = new ArrayList<>();
        for (Map.Entry<Long, List<ChFollowupTask>> entry : userTasksMap.entrySet()) {
            Long userId = entry.getKey();
            List<ChFollowupTask> uTasks = entry.getValue();

            long total = uTasks.size();
            long done = uTasks.stream().filter(t -> "DONE".equals(t.getTaskStatus())).count();
            long overdue = uTasks.stream().filter(t -> "OVERDUE".equals(t.getTaskStatus())).count();
            long pending = total - done;

            ChFollowupStatVo.AssigneeRankItem item = new ChFollowupStatVo.AssigneeRankItem();
            item.setAssigneeUserId(userId);
            item.setAssigneeNickName(nickNames.getOrDefault(userId, "执行人" + userId));
            item.setTotalTasks(total);
            item.setDoneTasks(done);
            item.setPendingTasks(pending);
            item.setOverdueTasks(overdue);
            if (total > 0) {
                item.setCompletionRate(BigDecimal.valueOf(done * 100.0 / total).setScale(2, RoundingMode.HALF_UP));
            }
            rankList.add(item);
        }

        rankList.sort((a, b) -> Long.compare(b.getDoneTasks(), a.getDoneTasks()));
        return rankList.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<ChFollowupStatVo.DiseaseStatItem> getDiseaseStats(String areaCode, Long orgId) {
        List<ChFollowupPlan> plans = followupPlanMapper.selectList(
            Wrappers.<ChFollowupPlan>lambdaQuery().select(ChFollowupPlan::getPlanId, ChFollowupPlan::getDiseaseCode));

        Map<Long, String> planDiseaseMap = plans.stream()
            .filter(p -> StringUtils.isNotBlank(p.getDiseaseCode()))
            .collect(Collectors.toMap(ChFollowupPlan::getPlanId, ChFollowupPlan::getDiseaseCode, (a, b) -> a));

        List<ChFollowupTask> tasks = followupTaskMapper.selectList(
            Wrappers.<ChFollowupTask>lambdaQuery().select(ChFollowupTask::getTaskId, ChFollowupTask::getPlanId, ChFollowupTask::getTaskStatus));

        List<ChFollowupRecord> records = followupRecordMapper.selectList(
            Wrappers.<ChFollowupRecord>lambdaQuery().select(ChFollowupRecord::getTaskId, ChFollowupRecord::getFollowupResult));

        Map<Long, String> recordResultMap = records.stream()
            .filter(r -> r.getTaskId() != null)
            .collect(Collectors.toMap(ChFollowupRecord::getTaskId, r -> StringUtils.defaultString(r.getFollowupResult()), (a, b) -> a));

        Map<String, List<ChFollowupTask>> diseaseTasks = new HashMap<>();
        for (ChFollowupTask task : tasks) {
            String dCode = planDiseaseMap.get(task.getPlanId());
            if (StringUtils.isNotBlank(dCode)) {
                diseaseTasks.computeIfAbsent(dCode, k -> new ArrayList<>()).add(task);
            }
        }

        List<String> diseaseCodes = new ArrayList<>(diseaseTasks.keySet());
        Map<String, String> diseaseNameMap = diseaseNameHelper.batchGetDiseaseName(diseaseCodes);

        List<ChFollowupStatVo.DiseaseStatItem> statList = new ArrayList<>();
        for (Map.Entry<String, List<ChFollowupTask>> entry : diseaseTasks.entrySet()) {
            String code = entry.getKey();
            List<ChFollowupTask> dTasks = entry.getValue();

            long total = dTasks.size();
            long done = dTasks.stream().filter(t -> "DONE".equals(t.getTaskStatus())).count();
            long controlled = dTasks.stream()
                .filter(t -> "CONTROLLED".equals(recordResultMap.get(t.getTaskId())))
                .count();

            ChFollowupStatVo.DiseaseStatItem item = new ChFollowupStatVo.DiseaseStatItem();
            item.setDiseaseCode(code);
            item.setDiseaseName(diseaseNameMap.getOrDefault(code, code));
            item.setTotalCount(total);
            item.setDoneCount(done);
            item.setControlledCount(controlled);
            if (total > 0) {
                item.setCompletionRate(BigDecimal.valueOf(done * 100.0 / total).setScale(2, RoundingMode.HALF_UP));
            }
            if (done > 0) {
                item.setControlledRate(BigDecimal.valueOf(controlled * 100.0 / done).setScale(2, RoundingMode.HALF_UP));
            }
            statList.add(item);
        }

        statList.sort((a, b) -> Long.compare(b.getTotalCount(), a.getTotalCount()));
        return statList;
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
