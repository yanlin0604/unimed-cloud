package org.dromara.chronic.job;

import cn.hutool.core.collection.CollUtil;
import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChFollowupPlan;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.manager.FollowupEnrollmentManager;
import org.dromara.chronic.mapper.ChFollowupPlanMapper;
import org.dromara.chronic.mapper.ChPatientDiseaseMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 慢病随访计划自动生成/入组定时任务
 * <p>
 * 定时扫描确诊慢病且尚未生成生效中随访计划的患者，
 * 自动触发规则引擎与多病共管合并算法，生成年度随访计划并排期随访任务。
 *
 * @author unimed
 */
@Slf4j
@Component
@JobExecutor(name = "followupPlanGenJob")
@RequiredArgsConstructor
public class FollowupPlanGenJob {

    private final ChPatientDiseaseMapper patientDiseaseMapper;
    private final ChPatientProfileMapper patientProfileMapper;
    private final ChFollowupPlanMapper followupPlanMapper;
    private final FollowupEnrollmentManager followupEnrollmentManager;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("随访计划自动生成/入组扫描开始");
        int generatedCount = 0;

        // 1. 查询所有在管的慢病患者档案
        List<ChPatientProfile> patientList = patientProfileMapper.selectList(
            Wrappers.<ChPatientProfile>lambdaQuery()
                .orderByAsc(ChPatientProfile::getPatientId)
        );
        SnailJobLog.LOCAL.info("扫描到慢病患者总数: {}", patientList.size());

        for (ChPatientProfile patient : patientList) {
            Long patientId = patient.getPatientId();

            // 2. 检查该患者是否已有 ACTIVE 随访计划（幂等防重）
            Long existing = followupPlanMapper.selectCount(
                Wrappers.<ChFollowupPlan>lambdaQuery()
                    .eq(ChFollowupPlan::getPatientId, patientId)
                    .eq(ChFollowupPlan::getPlanStatus, "ACTIVE")
            );
            if (existing > 0) {
                continue;
            }

            // 3. 查询该患者确诊的所有慢病
            List<ChPatientDisease> diseases = patientDiseaseMapper.selectList(
                Wrappers.<ChPatientDisease>lambdaQuery()
                    .eq(ChPatientDisease::getPatientId, patientId)
            );

            String primaryDiseaseCode = "HTN";
            Long doctorUserId = patient.getDoctorUserId();

            if (CollUtil.isNotEmpty(diseases)) {
                primaryDiseaseCode = diseases.get(0).getDiseaseCode();
                if (doctorUserId == null && diseases.get(0).getDiagnosisDoctorUserId() != null) {
                    doctorUserId = diseases.get(0).getDiagnosisDoctorUserId();
                }
            }

            // 4. 若未指定责任医生，按科室医生列表分配
            if (doctorUserId == null) {
                doctorUserId = 2001L + (Math.abs(patientId.hashCode()) % 8);
            }

            // 5. 调用多病共管与规则引擎生成计划与排期任务
            Long planId = followupEnrollmentManager.autoEnrollAndGeneratePlan(
                patientId, primaryDiseaseCode, doctorUserId
            );
            if (planId != null) {
                generatedCount++;
                SnailJobLog.LOCAL.info("为患者 [{}] (ID: {}) 成功生成随访计划 planId={}",
                    patient.getName(), patientId, planId);
            }
        }

        SnailJobLog.REMOTE.info("随访计划自动生成完成, 新增入组计划数: {}", generatedCount);
        return ExecuteResult.success("自动生成随访计划" + generatedCount + "份");
    }
}
