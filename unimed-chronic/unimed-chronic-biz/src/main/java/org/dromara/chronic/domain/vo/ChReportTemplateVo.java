package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChReportTemplate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 报告模板视图对象
 *
 * @author unimed
 */
@Schema(description = "报告模板视图对象")
@Data
@AutoMapper(target = ChReportTemplate.class)
public class ChReportTemplateVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "模板ID")
    private Long templateId;
    @Schema(description = "模板名称")
    private String templateName;
    @Schema(description = "模板内容")
    private String templateContent;
    @Schema(description = "病种编码")
    private String diseaseCode;
    @Schema(description = "模板类型(ANNUAL/FOLLOWUP/SPECIAL)")
    private String templateType;
    @Schema(description = "是否启用")
    private Boolean isActive;
    @Schema(description = "创建时间")
    private Date createTime;
}
