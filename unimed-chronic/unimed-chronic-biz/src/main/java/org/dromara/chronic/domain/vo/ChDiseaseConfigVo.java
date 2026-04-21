package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChDiseaseConfig;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 慢病病种配置视图对象
 *
 * @author unimed
 */
@Schema(description = "病种配置视图对象")
@Data
@AutoMapper(target = ChDiseaseConfig.class)
public class ChDiseaseConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "配置ID")
    private Long configId;
    @Schema(description = "租户ID")
    private String tenantId;
    @Schema(description = "病种编码")
    private String diseaseCode;
    @Schema(description = "病种名称")
    private String diseaseName;
    @Schema(description = "病种分类")
    private String diseaseCategory;
    @Schema(description = "是否主病种")
    private Boolean isPrimary;
    @Schema(description = "父级病种编码")
    private String parentDiseaseCode;
    @Schema(description = "随访模板ID")
    private Long followupTemplateId;
    @Schema(description = "评估策略ID")
    private Long assessmentStrategyId;
    @Schema(description = "监测项目")
    private String monitorItems;
    @Schema(description = "是否启用")
    private Boolean isActive;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "更新时间")
    private Date updateTime;
}
