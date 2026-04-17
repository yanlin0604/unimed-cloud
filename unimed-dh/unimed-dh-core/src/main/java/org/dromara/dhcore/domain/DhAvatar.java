package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 数字人形象对象 dh_avatar
 *
 * @author unimed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_avatar")
public class DhAvatar extends TenantEntity {

    /**
     * 形象ID
     */
    @TableId(value = "avatar_id")
    private Long avatarId;

    /**
     * 用户ID（系统预设为空）
     */
    private Long userId;

    /**
     * 形象名称
     */
    private String name;

    /**
     * OSS文件ID
     */
    private String ossId;

    /**
     * 图片URL
     */
    private String imageUrl;

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
