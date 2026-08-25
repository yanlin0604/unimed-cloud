package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.chronic.domain.bo.ChFollowupDispatchPoolBo;
import org.dromara.chronic.domain.bo.ChFollowupDispatchRunBo;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.entity.ChFollowupDispatchPool;
import org.dromara.chronic.domain.vo.ChFollowupDispatchPoolVo;
import org.dromara.chronic.domain.vo.ChFollowupDispatchResultVo;
import org.dromara.chronic.mapper.ChFollowupDispatchPoolMapper;
import org.dromara.chronic.mapper.ChFollowupPlanMapper;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.service.IChFollowupDispatchPoolService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteUserVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 随访任务自动分发人员池服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChFollowupDispatchPoolServiceImpl implements IChFollowupDispatchPoolService {

    private final ChFollowupDispatchPoolMapper dispatchPoolMapper;
    private final ChFollowupTaskMapper followupTaskMapper;
    private final ChFollowupPlanMapper followupPlanMapper;
    private final org.dromara.chronic.common.helper.DiseaseNameHelper diseaseNameHelper;

    @DubboReference
    private RemoteUserService remoteUserService;

    private static final AtomicInteger ROUND_ROBIN_INDEX = new AtomicInteger(0);

    @Override
    public TableDataInfo<ChFollowupDispatchPoolVo> queryPageList(ChFollowupDispatchPoolBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChFollowupDispatchPool> lqw = buildQueryWrapper(bo);
        Page<ChFollowupDispatchPoolVo> result = dispatchPoolMapper.selectVoPage(pageQuery.build(), lqw);
        
        // 填充每个执行人名下的实时待办任务数与累计完成数以及专病名称
        if (CollUtil.isNotEmpty(result.getRecords())) {
            fillDispatchPoolDiseaseNames(result.getRecords());
            for (ChFollowupDispatchPoolVo vo : result.getRecords()) {
                if (vo.getUserId() != null) {
                    Long pendingCount = followupTaskMapper.selectCount(
                        Wrappers.<ChFollowupTask>lambdaQuery()
                            .eq(ChFollowupTask::getAssigneeUserId, vo.getUserId())
                            .eq(ChFollowupTask::getTaskStatus, "PENDING")
                    );
                    Long doneCount = followupTaskMapper.selectCount(
                        Wrappers.<ChFollowupTask>lambdaQuery()
                            .eq(ChFollowupTask::getAssigneeUserId, vo.getUserId())
                            .eq(ChFollowupTask::getTaskStatus, "DONE")
                    );
                    vo.setCurrentPendingCount(pendingCount != null ? pendingCount.intValue() : 0);
                    vo.setTotalCompletedCount(doneCount != null ? doneCount.intValue() : 0);
                }
            }
        }
        return TableDataInfo.build(result);
    }

    @Override
    public List<ChFollowupDispatchPoolVo> queryActiveList() {
        List<ChFollowupDispatchPoolVo> list = dispatchPoolMapper.selectVoList(
            Wrappers.<ChFollowupDispatchPool>lambdaQuery()
                .eq(ChFollowupDispatchPool::getIsActive, true)
                .orderByDesc(ChFollowupDispatchPool::getWeight)
        );
        fillDispatchPoolDiseaseNames(list);
        return list;
    }

    private void fillDispatchPoolDiseaseNames(List<ChFollowupDispatchPoolVo> list) {
        if (CollUtil.isEmpty(list)) return;
        Set<String> allCodes = new HashSet<>();
        for (ChFollowupDispatchPoolVo vo : list) {
            String codesStr = vo.getDiseaseCodes();
            if (StringUtils.isNotBlank(codesStr) && !"*".equals(codesStr.trim())) {
                String[] split = codesStr.split(",");
                for (String c : split) {
                    if (StringUtils.isNotBlank(c) && !"*".equals(c.trim())) {
                        allCodes.add(c.trim());
                    }
                }
            }
        }
        Map<String, String> diseaseNameMap = allCodes.isEmpty() ? Collections.emptyMap() : diseaseNameHelper.batchGetDiseaseName(new ArrayList<>(allCodes));

        for (ChFollowupDispatchPoolVo vo : list) {
            String codesStr = vo.getDiseaseCodes();
            if (StringUtils.isBlank(codesStr) || "*".equals(codesStr.trim())) {
                vo.setDiseaseNameList(Collections.singletonList("全部病种 (通用)"));
            } else {
                List<String> names = new ArrayList<>();
                for (String c : codesStr.split(",")) {
                    String trimmed = c.trim();
                    if (StringUtils.isNotBlank(trimmed)) {
                        String name = diseaseNameMap.get(trimmed);
                        names.add(StringUtils.isNotBlank(name) ? name : trimmed);
                    }
                }
                vo.setDiseaseNameList(names);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addUsersToPool(ChFollowupDispatchPoolBo bo) {
        List<Long> userIds = bo.getUserIds();
        if (CollUtil.isEmpty(userIds) && bo.getUserId() != null) {
            userIds = Collections.singletonList(bo.getUserId());
        }
        if (CollUtil.isEmpty(userIds)) {
            throw new ServiceException("请选择要加入分发池的用户");
        }

        List<RemoteUserVo> users = remoteUserService.selectListByIds(userIds);
        if (CollUtil.isEmpty(users)) {
            throw new ServiceException("未找到选中的系统用户");
        }

        List<String> alreadyActiveNames = new ArrayList<>();
        int newAddedCount = 0;

        for (RemoteUserVo user : users) {
            // 检查是否已存在于分发池
            ChFollowupDispatchPool existing = dispatchPoolMapper.selectOne(
                Wrappers.<ChFollowupDispatchPool>lambdaQuery()
                    .eq(ChFollowupDispatchPool::getUserId, user.getUserId())
            );

            if (existing != null) {
                if (Boolean.TRUE.equals(existing.getIsActive())) {
                    alreadyActiveNames.add(user.getNickName() != null ? user.getNickName() : user.getUserName());
                    continue;
                }
                // 之前处于暂停状态，恢复启用并更新配置
                dispatchPoolMapper.update(null,
                    Wrappers.<ChFollowupDispatchPool>lambdaUpdate()
                        .set(ChFollowupDispatchPool::getIsActive, true)
                        .set(StringUtils.isNotBlank(bo.getDiseaseCodes()), ChFollowupDispatchPool::getDiseaseCodes, bo.getDiseaseCodes())
                        .set(StringUtils.isNotBlank(bo.getVisitTypes()), ChFollowupDispatchPool::getVisitTypes, bo.getVisitTypes())
                        .set(bo.getMaxPendingTasks() != null, ChFollowupDispatchPool::getMaxPendingTasks, bo.getMaxPendingTasks())
                        .set(bo.getWeight() != null, ChFollowupDispatchPool::getWeight, bo.getWeight())
                        .set(ChFollowupDispatchPool::getRemark, bo.getRemark())
                        .eq(ChFollowupDispatchPool::getId, existing.getId())
                );
                newAddedCount++;
                continue;
            }

            ChFollowupDispatchPool poolMember = new ChFollowupDispatchPool();
            poolMember.setUserId(user.getUserId());
            poolMember.setUserName(user.getUserName());
            poolMember.setNickName(StringUtils.defaultIfBlank(user.getNickName(), user.getUserName()));
            poolMember.setPhonenumber(user.getPhonenumber());
            poolMember.setDiseaseCodes(StringUtils.defaultIfBlank(bo.getDiseaseCodes(), "*"));
            poolMember.setVisitTypes(StringUtils.defaultIfBlank(bo.getVisitTypes(), "*"));
            poolMember.setMaxPendingTasks(bo.getMaxPendingTasks() != null ? bo.getMaxPendingTasks() : 50);
            poolMember.setWeight(bo.getWeight() != null ? bo.getWeight() : 1);
            poolMember.setIsActive(bo.getIsActive() != null ? bo.getIsActive() : true);
            poolMember.setRemark(bo.getRemark());
            dispatchPoolMapper.insert(poolMember);
            newAddedCount++;
        }

        if (newAddedCount == 0 && CollUtil.isNotEmpty(alreadyActiveNames)) {
            if (alreadyActiveNames.size() == 1) {
                throw new ServiceException("执行人「" + alreadyActiveNames.get(0) + "」已在随访分发池中，请勿重复添加！");
            } else {
                throw new ServiceException("所选人员（" + String.join("、", alreadyActiveNames) + "）均已在分发池中，请勿重复添加！");
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePoolMember(ChFollowupDispatchPoolBo bo) {
        if (bo.getId() == null) {
            throw new ServiceException("ID不能为空");
        }
        ChFollowupDispatchPool entity = MapstructUtils.convert(bo, ChFollowupDispatchPool.class);
        return dispatchPoolMapper.updateById(entity) > 0;
    }

    @Override
    public Boolean toggleActive(Long id, Boolean isActive) {
        if (id == null) {
            throw new ServiceException("ID不能为空");
        }
        return dispatchPoolMapper.update(null,
            Wrappers.<ChFollowupDispatchPool>lambdaUpdate()
                .set(ChFollowupDispatchPool::getIsActive, isActive)
                .eq(ChFollowupDispatchPool::getId, id)
        ) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeFromPool(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return false;
        }
        return dispatchPoolMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ChFollowupDispatchResultVo executeBatchDispatch(ChFollowupDispatchRunBo bo) {
        String strategy = StringUtils.defaultIfBlank(bo.getStrategy(), "RANDOM").toUpperCase();
        int limit = bo.getLimit() != null && bo.getLimit() > 0 ? bo.getLimit() : 100;

        // 1. 查询当前所有启用的分发池成员
        List<ChFollowupDispatchPool> activeMembers = dispatchPoolMapper.selectList(
            Wrappers.<ChFollowupDispatchPool>lambdaQuery()
                .eq(ChFollowupDispatchPool::getIsActive, true)
        );

        if (CollUtil.isEmpty(activeMembers)) {
            return ChFollowupDispatchResultVo.builder()
                .totalPendingTasks(0)
                .dispatchedCount(0)
                .skippedCount(0)
                .activeAssigneesCount(0)
                .strategy(strategy)
                .assigneeDispatchedMap(Collections.emptyMap())
                .message("分发失败：当前随访分发池中没有任何启用的执行人，请先添加并启用分发人员！")
                .build();
        }

        // 2. 统计每位执行人当前的待办数
        Map<Long, Integer> memberPendingCountMap = new HashMap<>();
        for (ChFollowupDispatchPool m : activeMembers) {
            Long count = followupTaskMapper.selectCount(
                Wrappers.<ChFollowupTask>lambdaQuery()
                    .eq(ChFollowupTask::getAssigneeUserId, m.getUserId())
                    .eq(ChFollowupTask::getTaskStatus, "PENDING")
            );
            memberPendingCountMap.put(m.getUserId(), count != null ? count.intValue() : 0);
        }

        // 3. 扫描任务池中待分发的随访任务 (assigneeUserId IS NULL, taskStatus = 'PENDING')
        LambdaQueryWrapper<ChFollowupTask> taskQuery = Wrappers.<ChFollowupTask>lambdaQuery()
            .isNull(ChFollowupTask::getAssigneeUserId)
            .eq(ChFollowupTask::getTaskStatus, "PENDING")
            .eq(StringUtils.isNotBlank(bo.getVisitType()), ChFollowupTask::getVisitType, bo.getVisitType())
            .orderByAsc(ChFollowupTask::getPlanDueDate)
            .orderByAsc(ChFollowupTask::getTaskId)
            .last("LIMIT " + limit);

        List<ChFollowupTask> pendingTasks = followupTaskMapper.selectList(taskQuery);
        if (CollUtil.isEmpty(pendingTasks)) {
            return ChFollowupDispatchResultVo.builder()
                .totalPendingTasks(0)
                .dispatchedCount(0)
                .skippedCount(0)
                .activeAssigneesCount(activeMembers.size())
                .strategy(strategy)
                .assigneeDispatchedMap(Collections.emptyMap())
                .message("任务池中暂无待分发的随访任务。")
                .build();
        }

        // 4. 加载所有相关计划的病种信息
        Set<Long> planIds = pendingTasks.stream().map(ChFollowupTask::getPlanId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, String> planDiseaseMap = new HashMap<>();
        if (CollUtil.isNotEmpty(planIds)) {
            List<ChFollowupPlan> plans = followupPlanMapper.selectBatchIds(planIds);
            for (ChFollowupPlan p : plans) {
                planDiseaseMap.put(p.getPlanId(), p.getDiseaseCode());
            }
        }

        int dispatchedCount = 0;
        int skippedCount = 0;
        Map<String, Integer> assigneeDispatchedMap = new LinkedHashMap<>();

        // 5. 遍历任务逐个执行策略分配
        for (ChFollowupTask task : pendingTasks) {
            String taskDisease = planDiseaseMap.get(task.getPlanId());
            if (StringUtils.isNotBlank(bo.getDiseaseCode()) && !StringUtils.equalsIgnoreCase(bo.getDiseaseCode(), taskDisease)) {
                skippedCount++;
                continue;
            }

            // 筛选符合该任务条件且未达到上限的候选人
            List<ChFollowupDispatchPool> candidates = activeMembers.stream().filter(m -> {
                // 检查待办上限
                int curPending = memberPendingCountMap.getOrDefault(m.getUserId(), 0);
                int maxPending = m.getMaxPendingTasks() != null ? m.getMaxPendingTasks() : 50;
                if (curPending >= maxPending) {
                    return false;
                }
                // 检查病种匹配
                if (!matchPattern(m.getDiseaseCodes(), taskDisease)) {
                    return false;
                }
                // 检查随访方式匹配
                if (!matchPattern(m.getVisitTypes(), task.getVisitType())) {
                    return false;
                }
                return true;
            }).collect(Collectors.toList());

            if (CollUtil.isEmpty(candidates)) {
                // 如果严格病种匹配无候选人，尝试兜底全病种通用候选人 (*)
                candidates = activeMembers.stream().filter(m -> {
                    int curPending = memberPendingCountMap.getOrDefault(m.getUserId(), 0);
                    int maxPending = m.getMaxPendingTasks() != null ? m.getMaxPendingTasks() : 50;
                    return curPending < maxPending && "*".equals(m.getDiseaseCodes());
                }).collect(Collectors.toList());
            }

            if (CollUtil.isEmpty(candidates)) {
                skippedCount++;
                continue;
            }

            // 执行策略选择目标执行人
            ChFollowupDispatchPool selected = selectAssigneeByStrategy(candidates, strategy, memberPendingCountMap, taskDisease);
            if (selected == null) {
                skippedCount++;
                continue;
            }

            // 分配任务
            task.setAssigneeUserId(selected.getUserId());
            followupTaskMapper.updateById(task);

            // 更新计数
            memberPendingCountMap.put(selected.getUserId(), memberPendingCountMap.getOrDefault(selected.getUserId(), 0) + 1);
            assigneeDispatchedMap.put(selected.getNickName(), assigneeDispatchedMap.getOrDefault(selected.getNickName(), 0) + 1);
            dispatchedCount++;
        }

        String summaryMsg = String.format("跑批分发完成：成功分发 %d 个任务，%d 个任务因配额已满或无匹配人暂未分发（分发策略：%s）",
            dispatchedCount, skippedCount, getStrategyName(strategy));

        log.info("[随访任务跑批分发] {}", summaryMsg);

        return ChFollowupDispatchResultVo.builder()
            .totalPendingTasks(pendingTasks.size())
            .dispatchedCount(dispatchedCount)
            .skippedCount(skippedCount)
            .activeAssigneesCount(activeMembers.size())
            .strategy(strategy)
            .assigneeDispatchedMap(assigneeDispatchedMap)
            .message(summaryMsg)
            .build();
    }

    /**
     * 根据分发策略选择执行人
     */
    private ChFollowupDispatchPool selectAssigneeByStrategy(
        List<ChFollowupDispatchPool> candidates,
        String strategy,
        Map<Long, Integer> memberPendingMap,
        String diseaseCode
    ) {
        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        switch (strategy) {
            case "LEAST_LOADED":
                // 最少待办负载均衡：选择 (currentPending / maxPending) 比例最小的
                return candidates.stream().min(Comparator.comparingDouble(m -> {
                    int cur = memberPendingMap.getOrDefault(m.getUserId(), 0);
                    int max = m.getMaxPendingTasks() != null && m.getMaxPendingTasks() > 0 ? m.getMaxPendingTasks() : 50;
                    return (double) cur / max;
                })).orElse(candidates.get(0));

            case "ROUND_ROBIN":
                // 轮询分发
                int idx = Math.abs(ROUND_ROBIN_INDEX.getAndIncrement()) % candidates.size();
                return candidates.get(idx);

            case "DISEASE_MATCH":
                // 专病优先加权：优先选明确指定了当前病种的专家，再按权重随机
                List<ChFollowupDispatchPool> exactDisease = candidates.stream()
                    .filter(m -> StringUtils.isNotBlank(diseaseCode) && matchPattern(m.getDiseaseCodes(), diseaseCode) && !"*".equals(m.getDiseaseCodes()))
                    .collect(Collectors.toList());
                List<ChFollowupDispatchPool> pool = CollUtil.isNotEmpty(exactDisease) ? exactDisease : candidates;
                return selectWeightedRandom(pool);

            case "RANDOM":
            default:
                // 加权随机轮盘
                return selectWeightedRandom(candidates);
        }
    }

    /**
     * 加权随机轮盘算法 (Weighted Random)
     */
    private ChFollowupDispatchPool selectWeightedRandom(List<ChFollowupDispatchPool> list) {
        int totalWeight = list.stream().mapToInt(m -> m.getWeight() != null && m.getWeight() > 0 ? m.getWeight() : 1).sum();
        if (totalWeight <= 0) {
            return list.get(RandomUtil.randomInt(0, list.size()));
        }

        int randomVal = RandomUtil.randomInt(1, totalWeight + 1);
        int cursor = 0;
        for (ChFollowupDispatchPool item : list) {
            int w = item.getWeight() != null && item.getWeight() > 0 ? item.getWeight() : 1;
            cursor += w;
            if (randomVal <= cursor) {
                return item;
            }
        }
        return list.get(0);
    }

    private boolean matchPattern(String pattern, String target) {
        if (StringUtils.isBlank(pattern) || "*".equals(pattern.trim())) {
            return true;
        }
        if (StringUtils.isBlank(target)) {
            return true;
        }
        String[] parts = pattern.split(",");
        for (String p : parts) {
            if (StringUtils.equalsIgnoreCase(p.trim(), target.trim())) {
                return true;
            }
        }
        return false;
    }

    private String getStrategyName(String strategy) {
        return switch (strategy) {
            case "RANDOM" -> "加权随机分发";
            case "LEAST_LOADED" -> "最少待办负载均衡";
            case "ROUND_ROBIN" -> "轮询分发";
            case "DISEASE_MATCH" -> "专病匹配优先加权";
            default -> strategy;
        };
    }

    private LambdaQueryWrapper<ChFollowupDispatchPool> buildQueryWrapper(ChFollowupDispatchPoolBo bo) {
        LambdaQueryWrapper<ChFollowupDispatchPool> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getUserId() != null, ChFollowupDispatchPool::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getNickName()), ChFollowupDispatchPool::getNickName, bo.getNickName());
        lqw.like(StringUtils.isNotBlank(bo.getUserName()), ChFollowupDispatchPool::getUserName, bo.getUserName());
        lqw.eq(bo.getIsActive() != null, ChFollowupDispatchPool::getIsActive, bo.getIsActive());
        lqw.like(StringUtils.isNotBlank(bo.getDiseaseCodes()), ChFollowupDispatchPool::getDiseaseCodes, bo.getDiseaseCodes());
        lqw.orderByDesc(ChFollowupDispatchPool::getIsActive);
        lqw.orderByDesc(ChFollowupDispatchPool::getWeight);
        lqw.orderByDesc(ChFollowupDispatchPool::getCreateTime);
        return lqw;
    }
}
