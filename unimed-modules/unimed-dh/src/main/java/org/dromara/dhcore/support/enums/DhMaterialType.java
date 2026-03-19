package org.dromara.dhcore.support.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 素材类型枚举
 *
 * @author dhcore
 */
@Getter
@AllArgsConstructor
public enum DhMaterialType {

    /**
     * 图片
     */
    IMAGE("IMAGE", "图片"),

    /**
     * 视频
     */
    VIDEO("VIDEO", "视频"),

    /**
     * 音频
     */
    AUDIO("AUDIO", "音频"),

    /**
     * 文档
     */
    DOCUMENT("DOCUMENT", "文档"),

    /**
     * 形象
     */
    AVATAR("AVATAR", "形象"),

    /**
     * 音色
     */
    VOICE("VOICE", "音色"),

    /**
     * 参考视频
     */
    REFERENCE_VIDEO("REFERENCE_VIDEO", "参考视频");

    /**
     * 类型值（存储到数据库）
     */
    @EnumValue
    private final String value;

    /**
     * 类型描述
     */
    private final String description;

    /**
     * 根据值获取枚举
     *
     * @param value 类型值
     * @return 枚举实例，未找到返回 null
     */
    public static DhMaterialType fromValue(String value) {
        if (value == null) {
            return null;
        }
        for (DhMaterialType type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }
}
