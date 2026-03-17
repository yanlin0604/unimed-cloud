package org.dromara.dhcore.service.support;

/**
 * 数字人口播订单状态常�? *
 * <p>状态说明：</p>
 * <ul>
 *     <li>PENDING：待处理</li>
 *     <li>PROCESSING：制作中</li>
 *     <li>TO_UPLOAD：待上传结果</li>
 *     <li>COMPLETED：已完成</li>
 *     <li>REDO：返工中</li>
 *     <li>CANCELLED：已取消</li>
 *     <li>REJECTED：已驳回</li>
 * </ul>
 */
public final class DhOrderStatus {

    /** 待处�?*/
    public static final String PENDING = "PENDING";
    /** 制作�?*/
    public static final String PROCESSING = "PROCESSING";
    /** 待上传结�?*/
    public static final String TO_UPLOAD = "TO_UPLOAD";
    /** 已完�?*/
    public static final String COMPLETED = "COMPLETED";
    /** 返工�?*/
    public static final String REDO = "REDO";
    /** 已取�?*/
    public static final String CANCELLED = "CANCELLED";
    /** 已驳�?*/
    public static final String REJECTED = "REJECTED";

    private DhOrderStatus() {
    }
}
