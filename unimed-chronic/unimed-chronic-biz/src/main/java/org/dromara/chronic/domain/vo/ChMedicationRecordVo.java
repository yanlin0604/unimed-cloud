package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChMedicationRecord;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用药记录视图对象
 *
 * @author unimed
 */
@Schema(description = "用药记录视图对象")
@Data
@AutoMapper(target = ChMedicationRecord.class)
public class ChMedicationRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用药记录ID")
    private Long medId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "药品名称")
    private String drugName;
    @Schema(description = "药品编码")
    private String drugCode;
    @Schema(description = "剂量")
    private String dosage;
    @Schema(description = "用药频率")
    private String frequency;
    @Schema(description = "给药途径")
    private String route;
    @Schema(description = "开始日期")
    private Date startDate;
    @Schema(description = "停止日期")
    private Date stopDate;
    @Schema(description = "配药量")
    private String dispenseQuantity;
    @Schema(description = "处方周期")
    private String prescriptionPeriod;
    @Schema(description = "处方医生用户ID")
    private Long prescriberUserId;
    @Schema(description = "处方已验证")
    private Boolean prescriberVerified;
    @Schema(description = "状态")
    private String status;

    @Schema(description = "用药依从性 GOOD/FAIR/POOR")
    private String compliance;

    @Schema(description = "处方依据")
    private String prescriptionBasis;

    @Schema(description = "用药备注")
    private String remark;

    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "用药状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "status", other = ChronicDictTypeConstant.CHRONIC_MEDICATION_STATUS)
    private String statusName;

    @Schema(description = "用药频次名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "frequency", other = ChronicDictTypeConstant.CHRONIC_FREQUENCY)
    private String frequencyName;

    @Schema(description = "给药途径名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "route", other = ChronicDictTypeConstant.CHRONIC_ROUTE)
    private String routeName;

    @Schema(description = "依从性名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "compliance", other = ChronicDictTypeConstant.CHRONIC_COMPLIANCE_LEVEL)
    private String complianceName;

    @Schema(description = "开方医生昵称")
    @Translation(type = TransConstant.USER_ID_TO_NICKNAME, mapper = "prescriberUserId")
    private String prescriberNickName;
}
