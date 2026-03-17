package org.dromara.dhcore.service.support;

import org.dromara.common.core.exception.ServiceException;

import java.util.Map;
import java.util.Set;

/**
 * 数字人口播订单状态机
 */
public final class DhOrderStatusMachine {

    /**
     * 状态流转规则：
     * PENDING -> PROCESSING/CANCELLED
     * PROCESSING �?TO_UPLOAD -> COMPLETED/REDO/CANCELLED/REJECTED
     * REDO -> PROCESSING/COMPLETED/CANCELLED
     * COMPLETED/CANCELLED/REJECTED 为终态，不可继续流转
     */
    private static final Map<String, Set<String>> TRANSITION_MAP = Map.of(
        DhOrderStatus.PENDING, Set.of(DhOrderStatus.PROCESSING, DhOrderStatus.CANCELLED),
        DhOrderStatus.PROCESSING, Set.of(DhOrderStatus.COMPLETED, DhOrderStatus.REDO, DhOrderStatus.CANCELLED, DhOrderStatus.REJECTED),
        DhOrderStatus.TO_UPLOAD, Set.of(DhOrderStatus.COMPLETED, DhOrderStatus.REDO, DhOrderStatus.CANCELLED, DhOrderStatus.REJECTED),
        DhOrderStatus.REDO, Set.of(DhOrderStatus.PROCESSING, DhOrderStatus.COMPLETED, DhOrderStatus.CANCELLED),
        DhOrderStatus.COMPLETED, Set.of(),
        DhOrderStatus.CANCELLED, Set.of(),
        DhOrderStatus.REJECTED, Set.of()
    );

    private DhOrderStatusMachine() {
    }

    public static void assertTransition(String currentStatus, String targetStatus) {
        Set<String> targetSet = TRANSITION_MAP.get(currentStatus);
        if (targetSet == null || !targetSet.contains(targetStatus)) {
            throw new ServiceException("当前状态不可执行该操作，请刷新后重�?);
        }
    }
}
