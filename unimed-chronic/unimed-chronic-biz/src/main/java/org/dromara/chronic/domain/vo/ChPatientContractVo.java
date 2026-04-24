package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChPatientContract;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 患者签约视图对象
 *
 * @author unimed
 */
@Schema(description = "患者签约视图对象")
@Data
@AutoMapper(target = ChPatientContract.class)
public class ChPatientContractVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "签约ID")
    private Long contractId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "团队ID")
    private Long teamId;
    @Schema(description = "服务包ID")
    private Long packageId;
    @Schema(description = "签约类型")
    private String contractType;
    @Schema(description = "签约开始日期")
    private Date contractPeriodStart;
    @Schema(description = "签约结束日期")
    private Date contractPeriodEnd;
    @Schema(description = "续约状态")
    private String renewalStatus;
    @Schema(description = "到期提醒状态")
    private Boolean expiryRemindStatus;
    @Schema(description = "签约状态")
    private String contractStatus;
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "签约类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "contractType", other = ChronicDictTypeConstant.CHRONIC_CONTRACT_TYPE)
    private String contractTypeName;

    @Schema(description = "续约状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "renewalStatus", other = ChronicDictTypeConstant.CHRONIC_RENEWAL_STATUS)
    private String renewalStatusName;

    @Schema(description = "合同状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "contractStatus", other = ChronicDictTypeConstant.CHRONIC_CONTRACT_STATUS)
    private String contractStatusName;

    @Schema(description = "团队名称")
    private String teamName;

    @Schema(description = "服务包名称")
    private String packageName;
}
