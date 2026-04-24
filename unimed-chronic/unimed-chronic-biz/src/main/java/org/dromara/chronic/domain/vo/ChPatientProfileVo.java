package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 患者主档案视图对象 ch_patient_profile
 *
 * @author unimed
 */
@Schema(description = "患者主档案视图对象")
@Data
@AutoMapper(target = ChPatientProfile.class)
public class ChPatientProfileVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "身份证号")
    private String idCard;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "出生日期")
    private Date birthday;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "经度")
    private BigDecimal gisLng;

    @Schema(description = "纬度")
    private BigDecimal gisLat;

    @Schema(description = "民族")
    private String nation;

    @Schema(description = "职业")
    private String occupation;

    @Schema(description = "文化程度")
    private String educationLevel;

    @Schema(description = "手术史")
    private String surgeryHistory;

    @Schema(description = "外伤史")
    private String traumaHistory;

    @Schema(description = "输血史")
    private String transfusionHistory;

    @Schema(description = "遗传史")
    private String geneticHistory;

    @Schema(description = "残疾类型")
    private String disabilityType;

    @Schema(description = "残疾等级")
    private String disabilityLevel;

    @Schema(description = "辅助器具")
    private String assistiveDevice;

    @Schema(description = "吸烟指数")
    private Integer smokingIndex;

    @Schema(description = "饮酒量")
    private String drinkingAmount;

    @Schema(description = "机构ID")
    private Long orgId;

    @Schema(description = "科室ID")
    private Long deptId;

    @Schema(description = "医生用户ID")
    private Long doctorUserId;

    @Schema(description = "管理状态")
    private String manageStatus;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "性别名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "gender", other = ChronicDictTypeConstant.CHRONIC_GENDER)
    private String genderName;

    @Schema(description = "民族名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "nation", other = ChronicDictTypeConstant.CHRONIC_NATION)
    private String nationName;

    @Schema(description = "职业名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "occupation", other = ChronicDictTypeConstant.CHRONIC_OCCUPATION)
    private String occupationName;

    @Schema(description = "文化程度名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "educationLevel", other = ChronicDictTypeConstant.CHRONIC_EDUCATION_LEVEL)
    private String educationLevelName;

    @Schema(description = "管理状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "manageStatus", other = ChronicDictTypeConstant.CHRONIC_MANAGE_STATUS)
    private String manageStatusName;

    @Schema(description = "患者来源名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "source", other = ChronicDictTypeConstant.CHRONIC_PATIENT_SOURCE)
    private String sourceName;

    @Schema(description = "残疾类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "disabilityType", other = ChronicDictTypeConstant.CHRONIC_DISABILITY_TYPE)
    private String disabilityTypeName;

    @Schema(description = "残疾等级名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "disabilityLevel", other = ChronicDictTypeConstant.CHRONIC_DISABILITY_LEVEL)
    private String disabilityLevelName;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "部门名称")
    @Translation(type = TransConstant.DEPT_ID_TO_NAME, mapper = "deptId")
    private String deptName;

    @Schema(description = "管理医生昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "doctorUserId")
    private String doctorNickName;
}
