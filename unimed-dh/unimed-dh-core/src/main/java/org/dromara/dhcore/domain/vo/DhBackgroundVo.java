package org.dromara.dhcore.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 数字人背景资源视图对象（B端）
 *
 * @author unimed
 */
@Data
public class DhBackgroundVo implements Serializable {

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
     * OSS文件ID
     */
    private String ossId;

    /**
     * 预览URL
     */
    private String previewUrl;

    /**
     * 是否系统预设
     */
    private Boolean isSystem;

    /**
     * 状态 0正常 1禁用
     */
    private String status;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;
}
