package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChOcrTask;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 医疗文档OCR任务视图对象
 *
 * @author unimed
 */
@Data
@Schema(description = "医疗文档OCR任务视图对象")
@AutoMapper(target = ChOcrTask.class)
public class OcrTaskVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "OCR任务ID")
    private Long taskId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "患者姓名")
    private String patientName;
    @Schema(description = "来源")
    private String sourceType;
    @Schema(description = "来源名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "sourceType", other = ChronicDictTypeConstant.CHRONIC_OCR_SOURCE_TYPE)
    private String sourceTypeName;
    @Schema(description = "文档类型")
    private String documentType;
    @Schema(description = "文档类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "documentType", other = ChronicDictTypeConstant.CHRONIC_OCR_DOCUMENT_TYPE)
    private String documentTypeName;
    @Schema(description = "输入类型")
    private String inputType;
    @Schema(description = "输入类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "inputType", other = ChronicDictTypeConstant.CHRONIC_OCR_INPUT_TYPE)
    private String inputTypeName;
    @Schema(description = "资源文件ID")
    private Long ossId;

    @Schema(description = "文件访问地址")
    @Translation(type = TransConstant.OSS_ID_TO_URL, mapper = "ossId")
    private String fileUrl;
    @Schema(description = "文件MD5")
    private String fileMd5;
    @Schema(description = "状态")
    private String status;
    @Schema(description = "状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "status", other = ChronicDictTypeConstant.CHRONIC_OCR_STATUS)
    private String statusName;
    @Schema(description = "报告主信息草稿JSON")
    private String reportDraftJson;
    @Schema(description = "原始OCR JSON")
    private String rawOcrJson;
    @Schema(description = "错误码")
    private String errorCode;
    @Schema(description = "错误信息")
    private String errorMsg;
    @Schema(description = "确认后的患者ID")
    private Long confirmedPatientId;
    @Schema(description = "确认入库指标数量")
    private Integer confirmedMetricCount;
    @Schema(description = "确认后的检查检验报告ID")
    private Long confirmedExamId;
    @Schema(description = "确认人")
    private Long confirmedBy;
    @Schema(description = "确认人昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "confirmedBy")
    private String confirmedByName;
    @Schema(description = "确认时间")
    private Date confirmedTime;
    @Schema(description = "创建时间")
    private Date createTime;
    @Schema(description = "建档草稿")
    private OcrArchiveDraftVo archiveDraft;
    @Schema(description = "指标草稿")
    private List<OcrMetricItemVo> metricItems;
    @Schema(description = "报告项目草稿")
    private List<OcrReportItemVo> reportItems;
}
