package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChScreeningRecord;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 义诊筛查记录视图对象
 *
 * @author unimed
 */
@Schema(description = "筛查记录视图对象")
@Data
@AutoMapper(target = ChScreeningRecord.class)
public class ChScreeningRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    private Long recordId;
    @Schema(description = "批次ID")
    private Long batchId;
    @Schema(description = "离线UUID")
    private String offlineUuid;
    @Schema(description = "患者姓名")
    private String patientName;
    @Schema(description = "身份证号")
    private String idCard;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "性别")
    private String gender;
    @Schema(description = "年龄")
    private Integer age;
    @Schema(description = "症状")
    private String symptoms;
    @Schema(description = "生命体征")
    private String vitals;
    @Schema(description = "风险等级")
    private String riskLevel;
    @Schema(description = "筛查结论")
    private String conclusion;
    @Schema(description = "入组状态")
    private String enrollStatus;
    @Schema(description = "入组患者ID")
    private Long enrolledPatientId;
    @Schema(description = "上传时间")
    private Date uploadTime;

    @Schema(description = "风险等级名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "riskLevel", other = ChronicDictTypeConstant.CHRONIC_RISK_LEVEL)
    private String riskLevelName;

    @Schema(description = "入组状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "enrollStatus", other = ChronicDictTypeConstant.CHRONIC_ENROLL_STATUS)
    private String enrollStatusName;

    @Schema(description = "性别名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "gender", other = ChronicDictTypeConstant.CHRONIC_GENDER)
    private String genderName;

    @Schema(description = "批次名称")
    private String batchName;
}
