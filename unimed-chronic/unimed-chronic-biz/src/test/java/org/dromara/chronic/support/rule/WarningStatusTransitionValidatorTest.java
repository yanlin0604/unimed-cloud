package org.dromara.chronic.support.rule;

import org.dromara.common.core.exception.ServiceException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * WarningStatusTransitionValidator 测试
 * 验证预警状态机合法/非法跳转
 * <p>
 * 该 Validator 是纯逻辑类（静态方法，无依赖注入）。
 *
 * @author unimed
 */
class WarningStatusTransitionValidatorTest {

    // ========== 合法跳转 ==========

    @Test
    void shouldAllowNewToConfirmed() {
        assertTrue(WarningStatusTransitionValidator.isLegalTransition("NEW", "CONFIRMED"));
    }

    @Test
    void shouldAllowConfirmedToProcessing() {
        assertTrue(WarningStatusTransitionValidator.isLegalTransition("CONFIRMED", "PROCESSING"));
    }

    @Test
    void shouldAllowConfirmedToEscalated() {
        assertTrue(WarningStatusTransitionValidator.isLegalTransition("CONFIRMED", "ESCALATED"));
    }

    @Test
    void shouldAllowProcessingToEscalated() {
        assertTrue(WarningStatusTransitionValidator.isLegalTransition("PROCESSING", "ESCALATED"));
    }

    @Test
    void shouldAllowProcessingToResolved() {
        assertTrue(WarningStatusTransitionValidator.isLegalTransition("PROCESSING", "RESOLVED"));
    }

    @Test
    void shouldAllowEscalatedToProcessing() {
        assertTrue(WarningStatusTransitionValidator.isLegalTransition("ESCALATED", "PROCESSING"));
    }

    @Test
    void shouldAllowEscalatedToResolved() {
        assertTrue(WarningStatusTransitionValidator.isLegalTransition("ESCALATED", "RESOLVED"));
    }

    @Test
    void shouldAllowResolvedToArchived() {
        assertTrue(WarningStatusTransitionValidator.isLegalTransition("RESOLVED", "ARCHIVED"));
    }

    // ========== 非法跳转 ==========

    @Test
    void shouldRejectNewToResolved() {
        // NEW 不能直接跳 RESOLVED，需先 CONFIRMED/PROCESSING
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("NEW", "RESOLVED"));
    }

    @Test
    void shouldRejectNewToArchived() {
        // NEW 不能直接归档，需先 CONFIRMED→...→RESOLVED→ARCHIVED
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("NEW", "ARCHIVED"));
    }

    @Test
    void shouldRejectNewToProcessing() {
        // NEW 不能直接跳 PROCESSING，需先 CONFIRMED
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("NEW", "PROCESSING"));
    }

    @Test
    void shouldRejectResolvedToNew() {
        // 已解决不能回退到 NEW
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("RESOLVED", "NEW"));
    }

    @Test
    void shouldRejectArchivedToAny() {
        // 已归档不能跳到任何状态（ARCHIVED 不在 ALLOWED_TRANSITIONS 中）
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("ARCHIVED", "NEW"));
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("ARCHIVED", "CONFIRMED"));
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("ARCHIVED", "PROCESSING"));
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("ARCHIVED", "RESOLVED"));
    }

    @Test
    void shouldRejectConfirmedToNew() {
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("CONFIRMED", "NEW"));
    }

    @Test
    void shouldRejectSameStatusTransition() {
        // isLegalTransition: 相同状态不算合法跳转（调用方应自行判断幂等）
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("NEW", "NEW"));
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("PROCESSING", "PROCESSING"));
    }

    // ========== 边界情况 ==========

    @Test
    void shouldRejectUnknownStatus() {
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("UNKNOWN", "CONFIRMED"));
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("NEW", "UNKNOWN"));
    }

    @Test
    void shouldRejectNullStatus() {
        assertFalse(WarningStatusTransitionValidator.isLegalTransition(null, "CONFIRMED"));
        assertFalse(WarningStatusTransitionValidator.isLegalTransition("NEW", null));
        assertFalse(WarningStatusTransitionValidator.isLegalTransition(null, null));
    }

    // ========== validate 方法 ==========

    @Test
    void shouldThrowOnIllegalTransition() {
        assertThrows(ServiceException.class, () -> {
            WarningStatusTransitionValidator.validate("NEW", "RESOLVED");
        });
    }

    @Test
    void shouldNotThrowOnLegalTransition() {
        assertDoesNotThrow(() -> {
            WarningStatusTransitionValidator.validate("NEW", "CONFIRMED");
        });
    }

    @Test
    void shouldAllowSameStatusInValidate() {
        // validate: 同状态不变更，允许（幂等）
        assertDoesNotThrow(() -> {
            WarningStatusTransitionValidator.validate("NEW", "NEW");
        });
    }

    @Test
    void shouldThrowOnNullStatusInValidate() {
        assertThrows(ServiceException.class, () -> {
            WarningStatusTransitionValidator.validate(null, "CONFIRMED");
        });
    }
}
