package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChReportInstance;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 报告实例视图对象
 *
 * @author unimed
 */
@Schema(description = "报告实例视图对象")
@Data
@AutoMapper(target = ChReportInstance.class)
public class ChReportInstanceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "报告ID")
    private Long reportId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "模板ID")
    private Long templateId;
    @Schema(description = "报告类型")
    private String reportType;
    @Schema(description = "PDF文件OSS ID")
    private String pdfOssId;
    @Schema(description = "签名时间")
    private Date signTime;
    @Schema(description = "二维码内容")
    private String qrCodeContent;
    @Schema(description = "推送状态")
    private String pushStatus;
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "报告类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "reportType", other = ChronicDictTypeConstant.CHRONIC_REPORT_TYPE)
    private String reportTypeName;

    @Schema(description = "推送状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "pushStatus", other = ChronicDictTypeConstant.CHRONIC_PUSH_STATUS)
    private String pushStatusName;
}
