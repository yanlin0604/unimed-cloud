package org.dromara.chronic.service.impl;

import org.dromara.chronic.domain.entity.ChMedicationCheckin;
import org.dromara.chronic.domain.entity.ChMedicationRecord;
import org.dromara.chronic.domain.vo.ChMedicationCheckinStatVo;
import org.dromara.chronic.domain.vo.ChMedicationRecordVo;
import org.dromara.chronic.mapper.ChDrugInteractionMapper;
import org.dromara.chronic.mapper.ChMedicationAdjustMapper;
import org.dromara.chronic.mapper.ChMedicationCheckinMapper;
import org.dromara.chronic.mapper.ChMedicationRecordMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 患者用药打卡统计测试。 */
@Tag("chronic-dev")
class ChMedicationCheckinStatServiceTest {

    @Test
    void shouldKeepRepeatedCheckinIdempotentByUpdatingExistingRecord() {
        Fixture fixture = fixture();
        ChMedicationRecord medication = new ChMedicationRecord();
        medication.setMedId(11L);
        medication.setPatientId(1001L);
        medication.setStatus("ACTIVE");
        ChMedicationCheckin existing = checkin(11L, LocalDate.now());
        existing.setCheckinId(99L);
        when(fixture.medicationRecordMapper.selectById(11L)).thenReturn(medication);
        when(fixture.checkinMapper.selectOne(any())).thenReturn(existing);

        assertTrue(fixture.service.checkinMedication(11L, 1001L));

        verify(fixture.checkinMapper).updateById(existing);
        verify(fixture.checkinMapper, never()).insert(any(ChMedicationCheckin.class));
    }

    @Test
    void shouldRejectCheckinForAnotherPatient() {
        Fixture fixture = fixture();
        ChMedicationRecord medication = new ChMedicationRecord();
        medication.setMedId(11L);
        medication.setPatientId(2002L);
        medication.setStatus("ACTIVE");
        when(fixture.medicationRecordMapper.selectById(11L)).thenReturn(medication);

        assertThrows(ServiceException.class, () -> fixture.service.checkinMedication(11L, 1001L));
        verify(fixture.checkinMapper, never()).insert(any(ChMedicationCheckin.class));
    }

    @Test
    void shouldRejectCheckinForStoppedMedication() {
        Fixture fixture = fixture();
        ChMedicationRecord medication = new ChMedicationRecord();
        medication.setMedId(11L);
        medication.setPatientId(1001L);
        medication.setStatus("STOPPED");
        when(fixture.medicationRecordMapper.selectById(11L)).thenReturn(medication);

        assertThrows(ServiceException.class, () -> fixture.service.checkinMedication(11L, 1001L));
        verify(fixture.checkinMapper, never()).insert(any(ChMedicationCheckin.class));
    }

    @Test
    void shouldReturnEmptyStatWhenPatientHasNoActiveMedication() {
        Fixture fixture = fixture();
        when(fixture.medicationRecordMapper.selectVoList(any())).thenReturn(List.of());

        ChMedicationCheckinStatVo result = fixture.service.queryCheckinStat(1001L);

        assertFalse(result.getHasActiveMedication());
        assertEquals(0, result.getConsecutiveDays());
        assertEquals(0, result.getWeekExpectedDays());
        assertTrue(result.getMedications().isEmpty());
    }

    @Test
    void shouldCalculateConsecutiveDaysAndCurrentWeekRateFromDistinctDates() {
        Fixture fixture = fixture();
        ChMedicationRecordVo medication = medication(11L, "苯磺酸氨氯地平片");
        when(fixture.medicationRecordMapper.selectVoList(any())).thenReturn(List.of(medication));

        LocalDate today = LocalDate.now();
        List<ChMedicationCheckin> checkins = List.of(
            checkin(11L, today),
            checkin(11L, today),
            checkin(11L, today.minusDays(1)),
            checkin(11L, today.minusDays(2))
        );
        when(fixture.checkinMapper.selectList(any())).thenReturn(checkins);

        ChMedicationCheckinStatVo result = fixture.service.queryCheckinStat(1001L);

        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        long expectedCompletedDays = checkins.stream()
            .map(ChMedicationCheckin::getCheckinDate)
            .distinct()
            .filter(date -> !date.isBefore(weekStart) && !date.isAfter(today))
            .count();
        int expectedRate = (int) Math.round(expectedCompletedDays * 100.0 / today.getDayOfWeek().getValue());

        assertTrue(result.getHasActiveMedication());
        assertTrue(result.getCheckedInToday());
        assertEquals(3, result.getConsecutiveDays());
        assertEquals((int) expectedCompletedDays, result.getWeekCompletedDays());
        assertEquals(expectedRate, result.getWeekAchievementRate());
        assertTrue(result.getMedications().get(0).getCheckedInToday());
    }

    @Test
    void shouldKeepYesterdayStreakWhenTodayHasNotBeenCheckedInYet() {
        Fixture fixture = fixture();
        when(fixture.medicationRecordMapper.selectVoList(any())).thenReturn(List.of(medication(11L, "缬沙坦")));
        LocalDate today = LocalDate.now();
        when(fixture.checkinMapper.selectList(any())).thenReturn(List.of(
            checkin(11L, today.minusDays(1)),
            checkin(11L, today.minusDays(2))
        ));

        ChMedicationCheckinStatVo result = fixture.service.queryCheckinStat(1001L);

        assertFalse(result.getCheckedInToday());
        assertEquals(2, result.getConsecutiveDays());
        assertFalse(result.getMedications().get(0).getCheckedInToday());
    }

    private static ChMedicationRecordVo medication(Long medId, String drugName) {
        ChMedicationRecordVo medication = new ChMedicationRecordVo();
        medication.setMedId(medId);
        medication.setDrugName(drugName);
        medication.setFrequency("QD");
        return medication;
    }

    private static ChMedicationCheckin checkin(Long medId, LocalDate date) {
        ChMedicationCheckin checkin = new ChMedicationCheckin();
        checkin.setMedId(medId);
        checkin.setCheckinDate(date);
        return checkin;
    }

    private static Fixture fixture() {
        ChMedicationRecordMapper medicationRecordMapper = mock(ChMedicationRecordMapper.class);
        ChMedicationAdjustMapper medicationAdjustMapper = mock(ChMedicationAdjustMapper.class);
        ChMedicationCheckinMapper checkinMapper = mock(ChMedicationCheckinMapper.class);
        ChDrugInteractionMapper interactionMapper = mock(ChDrugInteractionMapper.class);
        ChPatientProfileMapper patientProfileMapper = mock(ChPatientProfileMapper.class);
        ChMedicationServiceImpl service = new ChMedicationServiceImpl(
            medicationRecordMapper,
            medicationAdjustMapper,
            checkinMapper,
            interactionMapper,
            patientProfileMapper
        );
        return new Fixture(service, medicationRecordMapper, checkinMapper);
    }

    private record Fixture(ChMedicationServiceImpl service,
                           ChMedicationRecordMapper medicationRecordMapper,
                           ChMedicationCheckinMapper checkinMapper) {
    }
}
