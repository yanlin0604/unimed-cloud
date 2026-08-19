package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChHealthExam;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 体检报告业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "体检报告业务对象")
@AutoMapper(target = ChHealthExam.class, reverseConvertGenerate = false)
public class ChHealthExamBo extends BaseEntity {

    @Schema(description = "体检ID")
    private Long examId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "外部序号")
    private String externalSn;

    @Schema(description = "体检类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "体检类型不能为空")
    private String examType;

    @Schema(description = "体检日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "体检日期不能为空")
    private Date examDate;

    @Schema(description = "体检机构ID")
    private Long examOrgId;

    @Schema(description = "特殊分类")
    private String specialCategory;

    @Schema(description = "体检结论")
    private String conclusion;
}
