package org.dromara.chronic.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 患者病种视图对象 ch_patient_disease
 *
 * @author unimed
 */
@Schema(description = "患者病种视图对象")
@Data
@AutoMapper(target = ChPatientDisease.class)
public class ChPatientDiseaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "病种编码")
    private String diseaseCode;

    @Schema(description = "ICD编码")
    private String icdCode;

    @Schema(description = "诊断依据")
    private String diagnosisBasis;

    @Schema(description = "确诊日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date confirmDate;

    @Schema(description = "是否并发症")
    private Boolean isComplication;

    @Schema(description = "父级病种编码")
    private String parentDiseaseCode;

    @Schema(description = "管理级别(字典 chronic_manage_level)")
    private String manageLevel;

    @Schema(description = "管理级别名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "manageLevel", other = ChronicDictTypeConstant.CHRONIC_MANAGE_LEVEL)
    private String manageLevelName;

    @Schema(description = "启用状态(true启用 false停用)")
    private Boolean enableStatus;

    @Schema(description = "确诊医生用户ID")
    private Long diagnosisDoctorUserId;

    @Schema(description = "确诊医生昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "diagnosisDoctorUserId")
    private String diagnosisDoctorName;

    @Schema(description = "确诊机构ID")
    private Long diagnosisOrgId;

    @Schema(description = "病种名称")
    private String diseaseName;

    @Schema(description = "主病名称（并发症时回填）")
    private String parentDiseaseName;
}
