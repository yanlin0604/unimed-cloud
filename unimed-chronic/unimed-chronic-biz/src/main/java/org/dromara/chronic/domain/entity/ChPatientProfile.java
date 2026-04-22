package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
    private String name;

    /**
     * 身份证号
     */
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
     * 联系电话
     */
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
     * 手术史
     */
    private String surgeryHistory;

    /**
     * 外伤史
     */
    private String traumaHistory;

    /**
     * 输血史
     */
    private String transfusionHistory;

    /**
     * 遗传病史
     */
    private String geneticHistory;

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
     * 归属机构ID
     */
    private Long orgId;

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
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
