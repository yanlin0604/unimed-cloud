package org.dromara.dhcore.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 数字人形象视图对象
 *
 * @author unimed
 */
@Data
public class DhAvatarVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 形象ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long avatarId;

    /**
     * 形象名称
     */
    private String name;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * OSS文件ID
     */
    private String ossId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 是否系统预设
     */
    private Boolean isSystem;

    /**
     * 创建时间
     */
    private Date createTime;
}