package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChOcrTask;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 医疗文档OCR任务业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "医疗文档OCR任务业务对象")
@AutoMapper(target = ChOcrTask.class, reverseConvertGenerate = false)
public class OcrTaskBo extends BaseEntity {

    @Schema(description = "OCR任务ID")
    private Long taskId;

    @Schema(description = "患者ID，建档前可为空")
    private Long patientId;

    @Schema(description = "来源: ADMIN/DOCTOR/PATIENT")
    private String sourceType;

    @Schema(description = "文档类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "文档类型不能为空")
    private String documentType;

    @Schema(description = "输入类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "输入类型不能为空")
    private String inputType;

    @Schema(description = "资源文件ID")
    private Long ossId;

    @Schema(description = "文件访问地址")
    private String fileUrl;

    @Schema(description = "图片Base64")
    private String imageBase64;

    @Schema(description = "PDF Base64")
    private String pdfBase64;

    @Schema(description = "文件MD5")
    private String fileMd5;

    @Schema(description = "任务状态")
    private String status;

    @Schema(description = "创建时间开始")
    private Date beginCreateTime;

    @Schema(description = "创建时间结束")
    private Date endCreateTime;
}
