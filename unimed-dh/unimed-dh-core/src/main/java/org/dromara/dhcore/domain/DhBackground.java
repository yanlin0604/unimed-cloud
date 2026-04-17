package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 数字人背景资源对象 dh_background
 *
 * @author unimed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_background")
public class DhBackground extends TenantEntity {

    /**
     * 背景ID
     */
    @TableId(value = "background_id")
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
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
