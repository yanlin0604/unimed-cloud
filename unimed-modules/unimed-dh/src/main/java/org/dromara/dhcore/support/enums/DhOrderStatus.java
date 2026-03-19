package org.dromara.dhcore.support.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数字人口播订单状态枚举
 *
 * @author dhcore
 */
@Getter
@AllArgsConstructor
public enum DhOrderStatus {

    /**
     * 待处理
     */
    PENDING("PENDING", "待处理"),

    /**
     * 制作中
     */
    PROCESSING("PROCESSING", "制作中"),

    /**
     * 待上传结果
     */
    TO_UPLOAD("TO_UPLOAD", "待上传结果"),

    /**
     * 已完成
     */
    COMPLETED("COMPLETED", "已完成"),

    /**
     * 返工中
     */
    REDO("REDO", "返工中"),

    /**
     * 已取消
     */
    CANCELLED("CANCELLED", "已取消"),

    /**
     * 已驳回
     */
    REJECTED("REJECTED", "已驳回");

    /**
     * 状态值（存储到数据库）
     */
    @EnumValue
    private final String value;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据值获取枚举
     *
     * @param value 状态值
     * @return 枚举实例，未找到返回 null
     */
    public static DhOrderStatus fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (DhOrderStatus status : values()) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }
        return null;
    }
}
