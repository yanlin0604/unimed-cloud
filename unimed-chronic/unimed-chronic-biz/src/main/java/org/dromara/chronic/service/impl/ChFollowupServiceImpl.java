package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.common.helper.DiseaseNameHelper;
import org.dromara.chronic.domain.bo.ChFollowupPlanBo;
import org.dromara.chronic.domain.bo.ChFollowupPlanItemBo;
import org.dromara.chronic.domain.bo.ChFollowupRecordBo;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChFollowupPlanItem;
import org.dromara.chronic.domain.entity.ChFollowupRecord;
import org.dromara.chronic.domain.entity.ChFollowupTask;
import org.dromara.chronic.domain.vo.ChFollowupPlanVo;
import org.dromara.chronic.domain.vo.ChFollowupRecordVo;
import org.dromara.chronic.domain.vo.ChFollowupTaskVo;
import org.dromara.chronic.mapper.ChFollowupPlanItemMapper;
import org.dromara.chronic.mapper.ChFollowupPlanMapper;
import org.dromara.chronic.mapper.ChFollowupRecordMapper;
import org.dromara.chronic.mapper.ChFollowupTaskMapper;
import org.dromara.chronic.service.IChFollowupService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    private final DiseaseNameHelper diseaseNameHelper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createPlan(ChFollowupPlanBo bo) {
        ChFollowupPlan plan = MapstructUtils.convert(bo, ChFollowupPlan.class);
        if (plan.getPlanStatus() == null) {
            plan.setPlanStatus("ACTIVE");
        }
        followupPlanMapper.insert(plan);
        savePlanItems(plan.getPlanId(), bo.getItemList());
        generateTasks(plan, bo.getAssigneeUserId());
        return plan.getPlanId();
    }

    @Override
    public TableDataInfo<ChFollowupTaskVo> queryTaskPage(Long assigneeUserId, String taskStatus, PageQuery pageQuery) {
        Page<ChFollowupTaskVo> page = followupTaskMapper.selectVoPage(
            pageQuery.build(),
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ObjectUtil.isNotNull(assigneeUserId), ChFollowupTask::getAssigneeUserId, assigneeUserId)
                .eq(taskStatus != null && !taskStatus.isBlank(), ChFollowupTask::getTaskStatus, taskStatus)
                .orderByAsc(ChFollowupTask::getPlanDueDate)
        );
        return TableDataInfo.build(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long completeTask(ChFollowupRecordBo bo) {
        ChFollowupTask task = followupTaskMapper.selectById(bo.getTaskId());
        if (task == null) {
            throw new ServiceException("随访任务不存在");
        }
        ChFollowupRecord record = MapstructUtils.convert(bo, ChFollowupRecord.class);
        record.setVisitDate(new Date());
        followupRecordMapper.insert(record);
        task.setTaskStatus("DONE");
        followupTaskMapper.updateById(task);
        return record.getRecordId();
    }

    @Override
    public List<ChFollowupRecordVo> queryRecordList(Long patientId) {
        return followupRecordMapper.selectVoList(
            Wrappers.<ChFollowupRecord>lambdaQuery()
                .eq(ChFollowupRecord::getPatientId, patientId)
                .orderByDesc(ChFollowupRecord::getVisitDate)
        );
    }

    @Override
    public List<ChFollowupTaskVo> queryTodoTasks(Long assigneeUserId) {
        return followupTaskMapper.selectVoList(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ObjectUtil.isNotNull(assigneeUserId), ChFollowupTask::getAssigneeUserId, assigneeUserId)
                .in(ChFollowupTask::getTaskStatus, List.of("PENDING", "REMINDING", "OVERDUE"))
                .orderByAsc(ChFollowupTask::getPlanDueDate)
        );
    }

    @Override
    public ChFollowupPlanVo queryCurrentPlan(Long patientId) {
        ChFollowupPlan plan = followupPlanMapper.selectOne(
            Wrappers.<ChFollowupPlan>lambdaQuery()
                .eq(ChFollowupPlan::getPatientId, patientId)
                .eq(ChFollowupPlan::getPlanStatus, "ACTIVE")
                .orderByDesc(ChFollowupPlan::getCreateTime)
                .last("limit 1")
        );
        if (plan == null) {
            return null;
        }
        ChFollowupPlanVo vo = MapstructUtils.convert(plan, ChFollowupPlanVo.class);
        vo.setItemList(followupPlanItemMapper.selectVoList(
            Wrappers.<ChFollowupPlanItem>lambdaQuery().eq(ChFollowupPlanItem::getPlanId, plan.getPlanId())
        ));
        fillFollowupPlanNames(Collections.singletonList(vo));
        return vo;
    }

    @Override
    public List<ChFollowupTaskVo> queryPatientTasks(Long patientId) {
        return followupTaskMapper.selectVoList(
            Wrappers.<ChFollowupTask>lambdaQuery()
                .eq(ChFollowupTask::getPatientId, patientId)
                .orderByAsc(ChFollowupTask::getPlanDueDate)
        );
    }

    private void savePlanItems(Long planId, List<ChFollowupPlanItemBo> itemList) {
        if (CollUtil.isEmpty(itemList)) {
            return;
        }
        List<ChFollowupPlanItem> items = MapstructUtils.convert(itemList, ChFollowupPlanItem.class);
        items.forEach(item -> item.setPlanId(planId));
        followupPlanItemMapper.insertBatch(items);
    }

    private void generateTasks(ChFollowupPlan plan, Long assigneeUserId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        for (int round = 1; round <= plan.getTotalRounds(); round++) {
            ChFollowupTask task = new ChFollowupTask();
            task.setPatientId(plan.getPatientId());
            task.setPlanId(plan.getPlanId());
            task.setTaskRound(round);
            task.setPlanDueDate(calendar.getTime());
            task.setTaskStatus("PENDING");
            task.setAssigneeUserId(assigneeUserId);
            followupTaskMapper.insert(task);
            calendar.add(Calendar.DAY_OF_MONTH, plan.getCycleDays());
        }
    }

    private void fillFollowupPlanNames(List<ChFollowupPlanVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<String> diseaseCodes = list.stream()
            .map(ChFollowupPlanVo::getDiseaseCode)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        if (!diseaseCodes.isEmpty()) {
            try {
                Map<String, String> diseaseNameMap = diseaseNameHelper.batchGetDiseaseName(diseaseCodes);
                list.forEach(v -> v.setDiseaseName(diseaseNameMap.get(v.getDiseaseCode())));
            } catch (Exception e) {
                /* ignore */
            }
        }
    }
}
