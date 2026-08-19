package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 报告模板对象 ch_report_template
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_report_template")
public class ChReportTemplate extends TenantEntity {

    @TableId(value = "template_id")
    private Long templateId;

    private String templateName;

    /**
     * 模板内容 JSON
     */
    private String templateContent;

    private String diseaseCode;

    /** 模板类型(ANNUAL/FOLLOWUP/SPECIAL) */
    private String templateType;

    private Boolean isActive;

    @TableLogic
    private String delFlag;
}
