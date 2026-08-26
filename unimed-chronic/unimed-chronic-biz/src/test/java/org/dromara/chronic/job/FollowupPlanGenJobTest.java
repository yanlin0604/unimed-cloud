package org.dromara.chronic.job;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.dromara.chronic.domain.entity.*;
import org.dromara.chronic.manager.FollowupEnrollmentManager;
import org.dromara.chronic.mapper.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class FollowupPlanGenJobTest {

    private ChPatientDiseaseMapper patientDiseaseMapper;
    private ChPatientProfileMapper patientProfileMapper;
    private ChFollowupPlanMapper followupPlanMapper;
    private FollowupEnrollmentManager followupEnrollmentManager;
    private FollowupPlanGenJob job;

    @BeforeEach
    void setUp() {
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupTask.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupPlan.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChFollowupPlanItem.class);
        TableInfoHelper.initTableInfo(new MapperBuilderAssistant(new MybatisConfiguration(), ""), ChPatientDisease.class);

        patientDiseaseMapper = mock(ChPatientDiseaseMapper.class);
        patientProfileMapper = mock(ChPatientProfileMapper.class);
        followupPlanMapper = mock(ChFollowupPlanMapper.class);
        followupEnrollmentManager = mock(FollowupEnrollmentManager.class);

        job = new FollowupPlanGenJob(patientDiseaseMapper, patientProfileMapper, followupPlanMapper, followupEnrollmentManager);
    }

    @Test
    void testExecuteGeneratesPlans() {
        ChPatientDisease d1 = new ChPatientDisease();
        d1.setPatientId(1001L);
        d1.setDiseaseCode("HTN");
        d1.setEnableStatus(true);
        d1.setDiagnosisDoctorUserId(2001L);

        when(patientDiseaseMapper.selectList(any())).thenReturn(List.of(d1));
        when(followupPlanMapper.selectCount(any())).thenReturn(0L);
        when(followupEnrollmentManager.autoEnrollAndGeneratePlan(1001L, "HTN", 2001L)).thenReturn(101L);

        ExecuteResult result = job.jobExecute(new JobArgs());
        assertEquals(1, result.getStatus());
        assertTrue(result.getResult().toString().contains("1份"));
        verify(followupEnrollmentManager, times(1)).autoEnrollAndGeneratePlan(1001L, "HTN", 2001L);
    }
}
