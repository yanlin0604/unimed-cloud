package org.dromara.chronic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChClinicalPathwayStatus;
import org.dromara.chronic.domain.vo.PathwayProgressVo;
import org.dromara.chronic.mapper.ChClinicalPathwayStatusMapper;
import org.dromara.chronic.service.IClinicalPathwayService;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 临床管理路径进度 Service 实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClinicalPathwayServiceImpl implements IClinicalPathwayService {

    private final ChClinicalPathwayStatusMapper pathwayMapper;

    /**
     * 管理路径标准阶段定义（可以后续配置化）
     */
    private static final List<String[]> STANDARD_STAGES = List.of(
        new String[]{"SCREENING", "筛查与建档"},
        new String[]{"FIRST_EVAL", "初次评估与定级"},
        new String[]{"PLAN_EXECUTING", "方案执行与随访"},
        new String[]{"RE_EVAL", "周期再评估"}
    );

    @Override
    public PathwayProgressVo getPathwayProgress(Long patientId, String diseaseCode) {
        // 查询路径状态记录
        LambdaQueryWrapper<ChClinicalPathwayStatus> wrapper = new LambdaQueryWrapper<ChClinicalPathwayStatus>()
            .eq(ChClinicalPathwayStatus::getPatientId, patientId);
        if (diseaseCode != null && !diseaseCode.isBlank()) {
            wrapper.eq(ChClinicalPathwayStatus::getDiseaseCode, diseaseCode);
        }
        wrapper.last("LIMIT 1");
        ChClinicalPathwayStatus status = pathwayMapper.selectOne(wrapper);

        PathwayProgressVo vo = new PathwayProgressVo();
        vo.setPatientId(patientId);

        if (status == null) {
            // 没有路径记录，返回空进度
            vo.setDiseaseCode(diseaseCode);
            vo.setCurrentStage(null);
            vo.setIsOverdue(false);
            vo.setStages(Collections.emptyList());
            return vo;
        }

        vo.setDiseaseCode(status.getDiseaseCode());
        vo.setCurrentStage(status.getCurrentStage());

        // 判断是否逾期
        boolean isOverdue = false;
        if (status.getStageDeadline() != null) {
            isOverdue = new Date().after(status.getStageDeadline());
        }
        vo.setIsOverdue(isOverdue);

        // 组装阶段列表
        List<PathwayProgressVo.StageInfo> stages = new ArrayList<>();
        boolean reachedCurrent = false;
        for (String[] stageDef : STANDARD_STAGES) {
            PathwayProgressVo.StageInfo stageInfo = new PathwayProgressVo.StageInfo();
            stageInfo.setStageCode(stageDef[0]);
            stageInfo.setStageName(stageDef[1]);

            if (reachedCurrent) {
                stageInfo.setStatus("PENDING");
            } else if (stageDef[0].equals(status.getCurrentStage())) {
                stageInfo.setStatus("IN_PROGRESS");
                stageInfo.setDueDate(status.getStageDeadline());
                reachedCurrent = true;
            } else {
                stageInfo.setStatus("COMPLETED");
            }
            stages.add(stageInfo);
        }
        vo.setStages(stages);

        return vo;
    }
}
