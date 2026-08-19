package org.dromara.chronic.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.common.sensitive.annotation.Sensitive;
import org.dromara.common.sensitive.core.SensitiveStrategy;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 患者档案详情视图对象
 *
 * @author unimed
 */
@Schema(description = "患者详情视图对象")
@Data
public class ChPatientDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "姓名")
    @Sensitive(strategy = SensitiveStrategy.CHINESE_NAME, perms = "chronic:patient:edit")
    private String name;

    @Schema(description = "身份证号")
    @Sensitive(strategy = SensitiveStrategy.ID_CARD, perms = "chronic:patient:edit")
    private String idCard;

    @Schema(description = "性别")
    private String gender;

    @Schema(description = "出生日期")
    private Date birthday;

    @Schema(description = "手机号")
    @Sensitive(strategy = SensitiveStrategy.PHONE, perms = "chronic:patient:edit")
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

    @Schema(description = "年龄")
    private Integer age;

    @Schema(description = "身高(cm)")
    private BigDecimal height;

    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    @Schema(description = "血型")
    private String bloodType;

    @Schema(description = "婚姻状况")
    private String maritalStatus;

    @Schema(description = "既往史")
    private String pastMedicalHistory;

    @Schema(description = "过敏史")
    private String allergyHistory;

    @Schema(description = "家族病史")
    private String familyHistory;

    @Schema(description = "医保类型")
    private String insuranceType;

    @Schema(description = "紧急联系人姓名")
    @Sensitive(strategy = SensitiveStrategy.CHINESE_NAME, perms = "chronic:patient:edit")
    private String emergencyContactName;

    @Schema(description = "紧急联系人电话")
    @Sensitive(strategy = SensitiveStrategy.PHONE, perms = "chronic:patient:edit")
    private String emergencyContactPhone;

    @Schema(description = "户籍地址")
    private String permanentAddress;

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
    @Schema(description = "科室ID")
    private Long deptId;

    @Schema(description = "医生用户ID")
    private Long doctorUserId;

    @Schema(description = "管理状态")
    private String manageStatus;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "机构ID")
    private Long orgId;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "病种列表")
    private List<ChPatientDiseaseVo> diseaseList;

    @Schema(description = "标签")
    private List<ChPatientTagVo> tags;

    @Schema(description = "最新时间线")
    private ChPatientTimelineVo latestTimeline;

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

    @Schema(description = "风险等级（取最新一次风险评估）")
    private String riskLevel;

    @Schema(description = "风险等级名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "riskLevel", other = ChronicDictTypeConstant.CHRONIC_RISK_LEVEL)
    private String riskLevelName;

    @Schema(description = "患者来源名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "source", other = ChronicDictTypeConstant.CHRONIC_PATIENT_SOURCE)
    private String sourceName;

    @Schema(description = "残疾类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "disabilityType", other = ChronicDictTypeConstant.CHRONIC_DISABILITY_TYPE)
    private String disabilityTypeName;

    @Schema(description = "残疾等级名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "disabilityLevel", other = ChronicDictTypeConstant.CHRONIC_DISABILITY_LEVEL)
    private String disabilityLevelName;

    @Schema(description = "血型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "bloodType", other = ChronicDictTypeConstant.CHRONIC_BLOOD_TYPE)
    private String bloodTypeName;

    @Schema(description = "婚姻状况名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "maritalStatus", other = ChronicDictTypeConstant.CHRONIC_MARITAL_STATUS)
    private String maritalStatusName;

    @Schema(description = "签约状态")
    private String contractStatus;

    @Schema(description = "签约状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "contractStatus", other = ChronicDictTypeConstant.CHRONIC_CONTRACT_STATUS)
    private String contractStatusName;

    @Schema(description = "知情同意状态")
    private String consentStatus;

    @Schema(description = "知情同意状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "consentStatus", other = ChronicDictTypeConstant.CHRONIC_CONSENT_STATUS)
    private String consentStatusName;

    @Schema(description = "部门名称")
    @Translation(type = TransConstant.DEPT_ID_TO_NAME, mapper = "deptId")
    private String deptName;

    @Schema(description = "管理医生昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "doctorUserId")
    private String doctorNickName;
}
