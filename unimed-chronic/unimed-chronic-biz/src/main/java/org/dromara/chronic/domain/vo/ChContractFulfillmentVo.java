package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChContractFulfillment;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 履约记录视图对象
 *
 * @author unimed
 */
@Schema(description = "履约记录视图对象")
@Data
@AutoMapper(target = ChContractFulfillment.class)
public class ChContractFulfillmentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "签约ID")
    private Long contractId;
    @Schema(description = "服务项目")
    private String serviceItem;
    @Schema(description = "计划日期")
    private Date planDate;
    @Schema(description = "实际日期")
    private Date actualDate;
    @Schema(description = "履约状态")
    private String fulfillmentStatus;
    @Schema(description = "是否违约")
    private Boolean slaViolation;

    @Schema(description = "履约状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "fulfillmentStatus", other = ChronicDictTypeConstant.CHRONIC_FULFILLMENT_STATUS)
    private String fulfillmentStatusName;
}
