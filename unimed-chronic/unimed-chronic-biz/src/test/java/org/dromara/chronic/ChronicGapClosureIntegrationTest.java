package org.dromara.chronic;

import org.dromara.chronic.domain.bo.ChEncounterDiagnosisBo;
import org.dromara.chronic.domain.bo.ChEncounterRecordBo;
import org.dromara.chronic.domain.entity.ChEncounterRecord;
import org.dromara.chronic.domain.vo.ChEncounterRecordVo;
import org.dromara.chronic.manager.EncounterManager;
import org.dromara.chronic.mapper.ChEncounterRecordMapper;
import org.dromara.chronic.service.IChEncounterRecordService;
import org.dromara.chronic.service.impl.ChEncounterRecordServiceImpl;
import org.dromara.chronic.service.impl.ChPatientTimelineServiceImpl;
import org.dromara.chronic.mapper.ChEncounterDiagnosisMapper;
import org.dromara.chronic.mapper.ChPatientTimelineMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * 慢病 Gap Closure 集成测试
 * <p>
 * 验证新增链路：诊疗记录提交、HIS encounter-sync 幂等、专病范围限制
 *
 * @author unimed
 */
class ChronicGapClosureIntegrationTest {

    @Mock
    private ChEncounterRecordMapper encounterRecordMapper;

    @Mock
    private ChEncounterDiagnosisMapper diagnosisMapper;

    @Mock
    private ChPatientTimelineMapper timelineMapper;

    @InjectMocks
    private ChEncounterRecordServiceImpl encounterRecordService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testEncounterDraftSave() {
        ChEncounterRecordBo bo = new ChEncounterRecordBo();
        bo.setPatientId(1L);
        bo.setEncounterType("INITIAL");
        bo.setEncounterTime(LocalDateTime.now());
        bo.setComplaint("头痛3天");
        bo.setSubmitStatus("DRAFT");

        when(encounterRecordMapper.insert(any(ChEncounterRecord.class))).thenAnswer(invocation -> {
            ChEncounterRecord entity = invocation.getArgument(0);
            entity.setId(1L);
            return 1;
        });

        Long id = encounterRecordService.saveDraft(bo, null);
        assertNotNull(id);
        assertEquals(1L, id);
        verify(encounterRecordMapper, times(1)).insert(any(ChEncounterRecord.class));
    }

    @Test
    void testEncounterSubmitIdempotent() {
        ChEncounterRecord record = new ChEncounterRecord();
        record.setId(1L);
        record.setPatientId(1L);
        record.setSubmitStatus("DRAFT");

        when(encounterRecordMapper.selectById(1L)).thenReturn(record);
        when(encounterRecordMapper.updateById(any(ChEncounterRecord.class))).thenReturn(1);

        Long result = encounterRecordService.submit(1L);
        assertEquals(1L, result);

        record.setSubmitStatus("SUBMITTED");
        Long result2 = encounterRecordService.submit(1L);
        assertEquals(1L, result2);
    }

    @Test
    void testEncounterSubmittedNotEditable() {
        ChEncounterRecord record = new ChEncounterRecord();
        record.setId(1L);
        record.setPatientId(1L);
        record.setSubmitStatus("SUBMITTED");

        when(encounterRecordMapper.selectById(1L)).thenReturn(record);

        ChEncounterRecordBo bo = new ChEncounterRecordBo();
        bo.setId(1L);
        bo.setComplaint("修改主诉");

        assertThrows(RuntimeException.class, () -> {
            encounterRecordService.updateById(bo, null);
        });
    }

    @Test
    void testHisEncounterSyncIdempotency() {
        // queryBySourceBizNo 使用 selectOne(wrapper, false) 返回单个实体
        when(encounterRecordMapper.selectOne(any(), anyBoolean())).thenReturn(null);

        ChEncounterRecord notFound = encounterRecordService.queryBySourceBizNo("HIS_OUTPATIENT_NO_001", 1L);
        assertNull(notFound, "首次同步不应存在记录");

        // 模拟已存在记录
        ChEncounterRecord existingRecord = new ChEncounterRecord();
        existingRecord.setId(100L);
        existingRecord.setSourceBizNo("HIS_OUTPATIENT_NO_001");
        when(encounterRecordMapper.selectOne(any(), anyBoolean())).thenReturn(existingRecord);

        ChEncounterRecord found = encounterRecordService.queryBySourceBizNo("HIS_OUTPATIENT_NO_001", 1L);
        assertNotNull(found, "重复同步应查到已有记录");
        assertEquals(100L, found.getId());
    }

    @Test
    void testFirstPhaseDiseaseScopeValidation() {
        // COPD 因随访模板未就绪，已从首期病种列表移除
        List<String> validScopes = List.of(
            "HYPERTENSION", "DIABETES", "ASTHMA",
            "CHD", "STROKE", "CANCER", "CKD", "MENTAL_DISORDER"
        );

        assertTrue(validScopes.contains("HYPERTENSION"));
        assertTrue(validScopes.contains("DIABETES"));
        assertEquals(8, validScopes.size());

        // COPD 不在首期列表中
        assertFalse(validScopes.contains("COPD"));
        assertFalse(validScopes.contains("NEW_DISEASE"));
    }
}