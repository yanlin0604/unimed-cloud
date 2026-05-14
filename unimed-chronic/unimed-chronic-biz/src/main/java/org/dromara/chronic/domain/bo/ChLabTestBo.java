package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChLabTest;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 检验记录业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "检验记录业务对象")
@AutoMapper(target = ChLabTest.class, reverseConvertGenerate = false)
public class ChLabTestBo extends BaseEntity {

    @Schema(description = "检验ID")
    private Long testId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "检验日期")
    private Date testDate;

    @Schema(description = "检验类型")
    private String testType;

    @Schema(description = "检验项目明细JSON")
    private String testItems;

    @Schema(description = "报告图片URL")
    private String reportImage;

    @Schema(description = "检验医院")
    private String hospital;

    @Schema(description = "检验医生")
    private String doctor;

    @Schema(description = "备注")
    private String remark;
}
