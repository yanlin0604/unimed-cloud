package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 用户素材对象 dh_material
 *
 * @author unimed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_material")
public class DhMaterial extends TenantEntity {

    /**
     * 素材ID
     */
    @TableId(value = "material_id")
    private Long materialId;

    /**
     * 用户ID
     */
    private Long userId;

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
     * OSS文件ID
     */
    private String ossId;

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
     * 分辨率
     */
    private String resolution;

    /**
     * 时长(秒)
     */
    private Integer duration;

    /**
     * 是否系统预设 0否 1是
     */
    private Integer isSystem;

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
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;
}
