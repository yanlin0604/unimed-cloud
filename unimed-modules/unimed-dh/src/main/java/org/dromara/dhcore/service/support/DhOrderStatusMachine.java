package org.dromara.dhcore.service.support;

import org.dromara.common.core.exception.ServiceException;
import org.dromara.dhcore.support.enums.DhOrderStatus;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * 数字人口播订单状态机
 */
public final class DhOrderStatusMachine {

    /**
     * 状态流转规则
     */
    private static final Map<DhOrderStatus, Set<DhOrderStatus>> TRANSITION_MAP = Map.of(
        DhOrderStatus.PENDING, EnumSet.of(DhOrderStatus.PROCESSING, DhOrderStatus.CANCELLED),
        DhOrderStatus.PROCESSING, EnumSet.of(DhOrderStatus.COMPLETED, DhOrderStatus.REDO, DhOrderStatus.CANCELLED, DhOrderStatus.REJECTED),
        DhOrderStatus.TO_UPLOAD, EnumSet.of(DhOrderStatus.COMPLETED, DhOrderStatus.REDO, DhOrderStatus.CANCELLED, DhOrderStatus.REJECTED),
        DhOrderStatus.REDO, EnumSet.of(DhOrderStatus.PROCESSING, DhOrderStatus.COMPLETED, DhOrderStatus.CANCELLED),
        DhOrderStatus.COMPLETED, EnumSet.noneOf(DhOrderStatus.class),
        DhOrderStatus.CANCELLED, EnumSet.noneOf(DhOrderStatus.class),
        DhOrderStatus.REJECTED, EnumSet.noneOf(DhOrderStatus.class)
    );

    private DhOrderStatusMachine() {
    }

    /**
     * 校验状态流转是否合法
     *
     * @param currentStatus 当前状态
     * @param targetStatus  目标状态
     * @throws ServiceException 如果状态流转不合法
     */
    public static void assertTransition(DhOrderStatus currentStatus, DhOrderStatus targetStatus) {
        Set<DhOrderStatus> allowedTargets = TRANSITION_MAP.get(currentStatus);
        if (allowedTargets == null || !allowedTargets.contains(targetStatus)) {
            throw new ServiceException("当前状态不可执行该操作，请刷新后重试");
        }
    }
}