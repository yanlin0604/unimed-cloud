package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChReportInstance;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 报告实例业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "报告实例业务对象")
@AutoMapper(target = ChReportInstance.class, reverseConvertGenerate = false)
public class ChReportInstanceBo extends BaseEntity {

    @Schema(description = "报告ID")
    private Long reportId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "模板ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "模板ID不能为空")
    private Long templateId;

    @Schema(description = "报告类型")
    private String reportType;

    @Schema(description = "报告状态")
    private String reportStatus;

    @Schema(description = "PDF文件ID")
    private Long pdfFileId;

    @Schema(description = "PDF文件OSS ID")
    private String pdfOssId;

    @Schema(description = "电子签名状态")
    private Boolean signStatus;

    @Schema(description = "二维码")
    private String qrCode;

    @Schema(description = "防伪二维码内容")
    private String qrCodeContent;

    @Schema(description = "推送状态")
    private String pushStatus;
}
