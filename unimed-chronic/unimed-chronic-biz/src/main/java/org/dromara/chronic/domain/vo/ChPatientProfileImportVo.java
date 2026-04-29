package org.dromara.chronic.domain.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 患者档案对象导入VO
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
public class ChPatientProfileImportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 患者姓名
     */
    @ExcelProperty(value = "患者姓名")
    private String name;

    /**
     * 身份证号
     */
    @ExcelProperty(value = "身份证号")
    private String idCard;

    /**
     * 性别
     */
    @ExcelProperty(value = "性别", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "chronic_gender")
    private String gender;

    /**
     * 出生日期
     */
    @ExcelProperty(value = "出生日期")
    private Date birthday;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话")
    private String phone;

    /**
     * 家庭住址
     */
    @ExcelProperty(value = "家庭住址")
    private String address;

    /**
     * 民族
     */
    @ExcelProperty(value = "民族", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "chronic_nation")
    private String nation;

    /**
     * 职业
     */
    @ExcelProperty(value = "职业", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "chronic_occupation")
    private String occupation;

    /**
     * 文化程度
     */
    @ExcelProperty(value = "文化程度", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "chronic_education_level")
    private String educationLevel;

    /**
     * 身高(cm)
     */
    @ExcelProperty(value = "身高(cm)")
    private BigDecimal height;

    /**
     * 体重(kg)
     */
    @ExcelProperty(value = "体重(kg)")
    private BigDecimal weight;

    /**
     * 血型
     */
    @ExcelProperty(value = "血型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "chronic_blood_type")
    private String bloodType;

    /**
     * 婚姻状况
     */
    @ExcelProperty(value = "婚姻状况", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "chronic_marital_status")
    private String maritalStatus;
    
    /**
     * 既往史
     */
    @ExcelProperty(value = "既往史")
    private String pastMedicalHistory;

    /**
     * 过敏史
     */
    @ExcelProperty(value = "过敏史")
    private String allergyHistory;

    /**
     * 家族病史
     */
    @ExcelProperty(value = "家族病史")
    private String familyHistory;

    /**
     * 紧急联系人姓名
     */
    @ExcelProperty(value = "紧急联系人姓名")
    private String emergencyContactName;

    /**
     * 紧急联系人电话
     */
    @ExcelProperty(value = "紧急联系人电话")
    private String emergencyContactPhone;

    /**
     * 户籍地址
     */
    @ExcelProperty(value = "户籍地址")
    private String permanentAddress;

    /**
     * 残疾类型
     */
    @ExcelProperty(value = "残疾类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "chronic_disability_type")
    private String disabilityType;

    /**
     * 残疾等级
     */
    @ExcelProperty(value = "残疾等级", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "chronic_disability_level")
    private String disabilityLevel;

    /**
     * 吸烟指数
     */
    @ExcelProperty(value = "吸烟指数")
    private Integer smokingIndex;

    /**
     * 饮酒量
     */
    @ExcelProperty(value = "饮酒量")
    private String drinkingAmount;

    /**
     * 归属科室ID
     */
    @ExcelProperty(value = "归属科室名称")
    private String deptName;

    /**
     * 责任医生ID
     */
    @ExcelProperty(value = "责任医生")
    private String doctorUserName;

    /**
     * 管理状态
     */
    @ExcelProperty(value = "管理状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "chronic_manage_status")
    private String manageStatus;

    /**
     * 档案来源
     */
    @ExcelProperty(value = "档案来源", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "chronic_patient_source")
    private String source;
}
