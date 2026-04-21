package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChConsentRecord;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 知情同意记录业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "知情同意记录业务对象")
@AutoMapper(target = ChConsentRecord.class, reverseConvertGenerate = false)
public class ChConsentRecordBo extends BaseEntity {

    @Schema(description = "同意记录ID")
    private Long consentId;

    @Schema(description = "患者ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "患者ID不能为空")
    private Long patientId;

    @Schema(description = "同意类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "同意类型不能为空")
    private String consentType;

    @Schema(description = "签名图片文件ID")
    private Long signImageFileId;

    @Schema(description = "签名时间")
    private Date signTime;
}
