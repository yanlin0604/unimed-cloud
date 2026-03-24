package org.dromara.dhcore.domain.vo.portal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * C端背景资源视图对象
 *
 * @author unimed
 */
@Data
public class PortalBackgroundVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 背景ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long backgroundId;

    /**
     * 背景名称
     */
    private String name;

    /**
     * 背景类型 IMAGE/VIDEO
     */
    private String bgType;

    /**
     * 预览URL（OSS预签名URL）
     */
    private String previewUrl;

    /**
     * 排序号
     */
    private Integer sortOrder;
}
