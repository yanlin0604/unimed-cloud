package org.dromara.dhcore.support.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 来源类型枚举
 *
 * @author dhcore
 */
@Getter
@AllArgsConstructor
public enum DhSource {

    /**
     * 上传
     */
    UPLOAD("upload", "上传"),

    /**
     * 系统预设
     */
    SYSTEM("system", "系统预设"),

    /**
     * 克隆
     */
    CLONE("clone", "克隆");

    /**
     * 来源值（存储到数据库）
     */
    @EnumValue
    private final String value;

    /**
     * 来源描述
     */
    private final String description;

    /**
     * 根据值获取枚举
     *
     * @param value 来源值
     * @return 枚举实例，未找到返回 null
     */
    public static DhSource fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (DhSource source : values()) {
            if (source.getValue().equals(value)) {
                return source;
            }
        }
        return null;
    }
}
