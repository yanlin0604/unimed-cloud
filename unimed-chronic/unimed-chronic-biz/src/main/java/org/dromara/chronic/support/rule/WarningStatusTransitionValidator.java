package org.dromara.chronic.support.rule;

import org.dromara.common.core.exception.ServiceException;

import java.util.Map;
import java.util.Set;

/**
 * 预警事件状态迁移校验器
 * <p>
 * 合法迁移路径：
 * <pre>
 *   NEW → CONFIRMED
 *   CONFIRMED → PROCESSING, ESCALATED, RESOLVED
 *   PROCESSING → ESCALATED, RESOLVED
 *   ESCALATED → PROCESSING, RESOLVED
 *   RESOLVED → ARCHIVED
 * </pre>
 * 任何不在上述白名单中的迁移均视为非法，抛出 {@link ServiceException}。
 *
 * @author unimed
 */
public final class WarningStatusTransitionValidator {

    private static final Map<String, Set<String>> ALLOWED_TRANSITIONS = Map.of(
        "NEW", Set.of("CONFIRMED"),
        "CONFIRMED", Set.of("PROCESSING", "ESCALATED", "RESOLVED"),
        "PROCESSING", Set.of("ESCALATED", "RESOLVED"),
        "ESCALATED", Set.of("PROCESSING", "RESOLVED"),
        "RESOLVED", Set.of("ARCHIVED")
    );

    private WarningStatusTransitionValidator() {
    }

    /**
     * 判断状态迁移是否合法（不抛异常，返回布尔值）
     *
     * @param currentStatus 当前状态
     * @param newStatus     目标状态
     * @return true=合法，false=非法（含 null 入参、未知状态）
     */
    public static boolean isLegalTransition(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }
        if (currentStatus.equals(newStatus)) {
            // 同状态不变更，视为非法跳转（调用方应自行判断幂等场景）
            return false;
        }
        Set<String> allowedTargets = ALLOWED_TRANSITIONS.get(currentStatus);
        return allowedTargets != null && allowedTargets.contains(newStatus);
    }

    /**
     * 校验状态迁移是否合法
     *
     * @param currentStatus 当前状态
     * @param newStatus     目标状态
     * @throws ServiceException 迁移非法时抛出
     */
    public static void validate(String currentStatus, String newStatus) {
        if (currentStatus == null || newStatus == null) {
            throw new ServiceException("预警状态不能为空");
        }
        if (currentStatus.equals(newStatus)) {
            // 同状态不变更，允许（幂等）
            return;
        }
        Set<String> allowedTargets = ALLOWED_TRANSITIONS.get(currentStatus);
        if (allowedTargets == null || !allowedTargets.contains(newStatus)) {
            throw new ServiceException("非法的预警状态迁移: " + currentStatus + "→" + newStatus);
        }
    }
}
