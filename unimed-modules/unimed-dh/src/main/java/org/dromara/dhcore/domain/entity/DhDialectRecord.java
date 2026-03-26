package org.dromara.dhcore.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 方言采集录音记录对象 dh_dialect_record
 *
 * @author unimed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_dialect_record")
public class DhDialectRecord extends TenantEntity {

    /**
     * 录音记录ID
     */
    @TableId(value = "record_id")
    private Long recordId;

    /**
     * 关联提示文字ID
     */
    private Long promptId;

    /**
     * 提交用户ID
     */
    private Long userId;

    /**
     * 方言名称
     */
    private String dialectName;

    /**
     * 方言地区
     */
    private String dialectArea;

    /**
     * OSS持久访问URL
     */
    private String audioUrl;

    /**
     * OSS文件ID
     */
    private String ossId;

    /**
     * 录音时长（秒）
     */
    private Integer duration;

    /**
     * 审核状态 PENDING/APPROVED/REJECTED
     */
    private String auditStatus;

    /**
     * 审核备注
     */
    private String auditRemark;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
