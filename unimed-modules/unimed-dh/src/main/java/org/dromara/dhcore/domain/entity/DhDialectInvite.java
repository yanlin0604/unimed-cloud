package org.dromara.dhcore.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 方言邀请码配置对象 dh_dialect_invite
 *
 * @author unimed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_dialect_invite")
public class DhDialectInvite extends TenantEntity {

    /**
     * 邀请配置ID
     */
    @TableId(value = "invite_id")
    private Long inviteId;

    /**
     * 语种名（与C端方言名称一致）
     */
    private String dialectName;

    /**
     * 邀请码（8位唯一）
     */
    private String inviteCode;

    /**
     * 生成的分享链接
     */
    private String collectionUrl;

    /**
     * JSON扩展字段（邀请人、自定义等）
     */
    private String extInfo;

    /**
     * 状态（0正常 1禁用）
     */
    private String status;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
