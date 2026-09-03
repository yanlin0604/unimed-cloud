package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.encrypt.annotation.EncryptField;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 患者主档案对象 ch_patient_profile
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_patient_profile")
public class ChPatientProfile extends TenantEntity {

    /**
     * 患者ID
     */
    @TableId(value = "patient_id")
    private Long patientId;

    /**
     * 患者姓名
     */
    @EncryptField
    private String name;

    /**
     * 身份证号
     */
    @EncryptField
    private String idCard;

    /**
     * 性别
     */
    private String gender;

    /**
     * 出生日期
     */
    private Date birthday;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 联系电话
     */
    @EncryptField
    private String phone;

    /**
     * 家庭住址
     */
    private String address;

    /**
     * GIS 经度
     */
    private BigDecimal gisLng;

    /**
     * GIS 纬度
     */
    private BigDecimal gisLat;

    /**
     * 民族
     */
    private String nation;

    /**
     * 职业
     */
    private String occupation;

    /**
     * 文化程度
     */
    private String educationLevel;

    /**
     * 残疾类型
     */
    private String disabilityType;

    /**
     * 残疾等级
     */
    private String disabilityLevel;

    /**
     * 辅助器具
     */
    private String assistiveDevice;

    /**
     * 吸烟指数
     */
    private Integer smokingIndex;

    /**
     * 饮酒量
     */
    private String drinkingAmount;

    /**
     * 身高(cm)
     */
    private BigDecimal height;

    /**
     * 体重(kg)
     */
    private BigDecimal weight;

    /**
     * 血型
     */
    private String bloodType;

    /**
     * 婚姻状况
     */
    private String maritalStatus;

    /**
     * 既往史（JSON 数组，存储既往疾病诊断列表）
     */
    private String pastMedicalHistory;

    /**
     * 过敏史（JSON 数组，存储过敏原及反应描述）
     */
    private String allergyHistory;

    /**
     * 家族病史（JSON 数组，存储家族成员患病情况）
     */
    private String familyHistory;
    /**
     * 归属科室ID
     */
    private Long deptId;

    /**
     * 责任医生ID
     */
    private Long doctorUserId;

    /**
     * 管理状态
     */
    private String manageStatus;

    /**
     * 档案来源
     */
    private String source;

    /**
     * 医保类型
     */
    private String insuranceType;

    /**
     * 紧急联系人姓名
     */
    @EncryptField
    private String emergencyContactName;

    /**
     * 紧急联系人电话
     */
    @EncryptField
    private String emergencyContactPhone;

    /**
     * 户籍地址
     */
    private String permanentAddress;

    /**
     * 机构ID
     */
    private Long orgId;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;

    /**
     * 归档/删除原因
     */
    private String deletionReason;
}
