package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChConsentRecord;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 知情同意记录视图对象
 *
 * @author unimed
 */
@Schema(description = "知情同意记录视图对象")
@Data
@AutoMapper(target = ChConsentRecord.class)
public class ChConsentRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "知情同意ID")
    private Long consentId;

    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "同意类型")
    private String consentType;

    @Schema(description = "签名图片文件ID")
    private Long signImageFileId;

    @Schema(description = "签名时间")
    private Date signTime;

    @Schema(description = "签署方式: ELECTRONIC/PAPER")
    private String signMethod;

    @Schema(description = "操作人IP")
    private String operatorIp;

    @Schema(description = "设备信息")
    private String deviceInfo;

    @Schema(description = "关联业务类型")
    private String relatedBizType;

    @Schema(description = "关联业务ID")
    private Long relatedBizId;

    @Schema(description = "租户ID")
    private String tenantId;

    @Schema(description = "创建者")
    private Long createBy;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "同意类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "consentType", other = ChronicDictTypeConstant.CHRONIC_CONSENT_TYPE)
    private String consentTypeName;
}
