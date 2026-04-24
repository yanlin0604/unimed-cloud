package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChReferralRecord;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 转诊记录视图对象
 *
 * @author unimed
 */
@Schema(description = "转诊记录视图对象")
@Data
@AutoMapper(target = ChReferralRecord.class)
public class ChReferralRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "转诊ID")
    private Long referralId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "转出机构ID")
    private Long fromOrgId;
    @Schema(description = "转入机构ID")
    private Long toOrgId;
    @Schema(description = "转入区域编码")
    private String toAreaCode;
    @Schema(description = "转诊原因")
    private String referralReason;
    @Schema(description = "转诊类别")
    private String referralCategory;
    @Schema(description = "转诊状态")
    private String referralStatus;
    @Schema(description = "转诊类型")
    private String referralType;
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "转诊类别名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "referralCategory", other = ChronicDictTypeConstant.CHRONIC_REFERRAL_CATEGORY)
    private String referralCategoryName;

    @Schema(description = "转诊状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "referralStatus", other = ChronicDictTypeConstant.CHRONIC_REFERRAL_STATUS)
    private String referralStatusName;

    @Schema(description = "转诊类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "referralType", other = ChronicDictTypeConstant.CHRONIC_REFERRAL_TYPE)
    private String referralTypeName;

    @Schema(description = "转出机构名称")
    private String fromOrgName;

    @Schema(description = "转入机构名称")
    private String toOrgName;
}
