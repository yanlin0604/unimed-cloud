package org.dromara.dhcore.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 方言采集提示文字对象 dh_dialect_prompt
 *
 * @author unimed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_dialect_prompt")
public class DhDialectPrompt extends TenantEntity {

    /**
     * 提示文字ID
     */
    @TableId(value = "prompt_id")
    private Long promptId;

    /**
     * 采集文字内容
     */
    private String content;

    /**
     * 分类标签
     */
    private String category;

    /**
     * 状态 0正常 1禁用
     */
    private String status;

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
