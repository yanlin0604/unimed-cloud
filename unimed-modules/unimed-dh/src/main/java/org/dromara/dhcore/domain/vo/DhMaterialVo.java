package org.dromara.dhcore.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户素材视图对象
 *
 * @author unimed
 */
@Data
public class DhMaterialVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 素材ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long materialId;

    /**
     * 类型 IMAGE/VIDEO/AUDIO
     */
    private String materialType;

    /**
     * 素材名称
     */
    private String name;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 文件URL
     */
    private String fileUrl;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;

    /**
     * 文件大小(字节)
     */
    private Long fileSize;

    /**
     * 时长(秒)
     */
    private Integer duration;

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
     * OSS文件ID
     */
    private String ossId;

    /**
     * 分辨率（如 1920x1080）
     */
    private String resolution;

    /**
     * 上传者用户名
     */
    private String uploadUser;

    /**
     * 创建时间
     */
    private Date createTime;
}
