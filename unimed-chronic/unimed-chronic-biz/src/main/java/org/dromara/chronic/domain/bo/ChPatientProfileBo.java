package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.common.core.xss.Xss;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 患者主档案业务对象 ch_patient_profile
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "患者主档案业务对象")
@AutoMapper(target = ChPatientProfile.class, reverseConvertGenerate = false)
public class ChPatientProfileBo extends BaseEntity {

    /**
     * 患者ID
     */
    @Schema(description = "患者ID")
    private Long patientId;

    /**
     * 患者姓名
     */
    @Schema(description = "患者姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @Xss(message = "患者姓名不能包含脚本字符")
    @NotBlank(message = "患者姓名不能为空")
    @Size(max = 50, message = "患者姓名长度不能超过{max}个字符")
    private String name;

    /**
     * 身份证号
     */
    @Schema(description = "身份证号")
    @Size(max = 18, message = "身份证号长度不能超过{max}个字符")
    private String idCard;

    /**
     * 性别
     */
    @Schema(description = "性别")
    private String gender;

    /**
     * 出生日期
     */
    @Schema(description = "出生日期")
    private Date birthday;

    /**
     * 联系电话
     */
    @Schema(description = "联系电话")
    @Size(max = 20, message = "联系电话长度不能超过{max}个字符")
    private String phone;

    /**
     * 家庭住址
     */
    @Schema(description = "家庭住址")
    @Size(max = 255, message = "家庭住址长度不能超过{max}个字符")
    private String address;

    /**
     * GIS 经度
     */
    @Schema(description = "GIS 经度")
    private BigDecimal gisLng;

    /**
     * GIS 纬度
     */
    @Schema(description = "GIS 纬度")
    private BigDecimal gisLat;

    /**
     * 民族
     */
    @Schema(description = "民族")
    @Size(max = 32, message = "民族长度不能超过{max}个字符")
    private String nation;

    /**
     * 职业
     */
    @Schema(description = "职业")
    @Size(max = 64, message = "职业长度不能超过{max}个字符")
    private String occupation;

    /**
     * 文化程度
     */
    @Schema(description = "文化程度")
    @Size(max = 32, message = "文化程度长度不能超过{max}个字符")
    private String educationLevel;

    /**
     * 年龄
     */
    @Schema(description = "年龄")
    private Integer age;

    /**
     * 身高(cm)
     */
    @Schema(description = "身高(cm)")
    private BigDecimal height;

    /**
     * 体重(kg)
     */
    @Schema(description = "体重(kg)")
    private BigDecimal weight;

    /**
     * 血型
     */
    @Schema(description = "血型")
    private String bloodType;

    /**
     * 婚姻状况
     */
    @Schema(description = "婚姻状况")
    private String maritalStatus;

    /**
     * 既往史
     */
    @Schema(description = "既往史")
    private String pastMedicalHistory;

    /**
     * 过敏史
     */
    @Schema(description = "过敏史")
    private String allergyHistory;

    /**
     * 家族病史
     */
    @Schema(description = "家族病史")
    private String familyHistory;

    /**
     * 医保类型
     */
    @Schema(description = "医保类型")
    private String insuranceType;

    /**
     * 紧急联系人姓名
     */
    @Schema(description = "紧急联系人姓名")
    private String emergencyContactName;

    /**
     * 紧急联系人电话
     */
    @Schema(description = "紧急联系人电话")
    private String emergencyContactPhone;

    /**
     * 户籍地址
     */
    @Schema(description = "户籍地址")
    private String permanentAddress;

    /**
     * 残疾类型
     */
    @Schema(description = "残疾类型")
    private String disabilityType;

    /**
     * 残疾等级
     */
    @Schema(description = "残疾等级")
    private String disabilityLevel;

    /**
     * 辅助器具
     */
    @Schema(description = "辅助器具")
    private String assistiveDevice;

    /**
     * 吸烟指数
     */
    @Schema(description = "吸烟指数")
    private Integer smokingIndex;

    /**
     * 饮酒量
     */
    @Schema(description = "饮酒量")
    private String drinkingAmount;
    /**
     * 归属科室ID
     */
    @Schema(description = "归属科室ID")
    private Long deptId;

    /**
     * 责任医生ID
     */
    @Schema(description = "责任医生ID")
    private Long doctorUserId;

    /**
     * 管理状态
     */
    @Schema(description = "管理状态")
    private String manageStatus;

    /**
     * 档案来源
     */
    @Schema(description = "档案来源")
    private String source;

    /**
     * 关联病种列表（新建/编辑档案时一并落库）
     */
    @Schema(description = "关联病种列表")
    private List<ChPatientDiseaseBo> diseases;

    /**
     * 患者标签列表（新建/编辑档案时一并落库）
     */
    @Schema(description = "患者标签列表")
    private List<ChPatientTagBo> tags;
}
