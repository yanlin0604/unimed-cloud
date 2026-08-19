package org.dromara.chronic.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.chronic.domain.entity.ChSosRecord;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import io.github.linpeilie.annotations.AutoMapper;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 紧急求助记录业务对象 ch_sos_record
 *
 * @author unimed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ChSosRecord.class, reverseConvertGenerate = false)
public class ChSosRecordBo extends BaseEntity {

    /** 求助ID */
    @NotNull(message = "求助ID不能为空", groups = { EditGroup.class })
    private Long sosId;

    /** 患者ID */
    @NotNull(message = "患者ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long patientId;

    /** GPS经度 */
    private BigDecimal gpsLng;

    /** GPS纬度 */
    private BigDecimal gpsLat;

    /** 反向地理编码地址 */
    private String gpsAddress;

    /** 通知医生状态(PENDING/SENT/FAILED) */
    private String notifyDoctorStatus;

    /** 通知紧急联系人状态(PENDING/SENT/FAILED) */
    private String notifyEmergencyStatus;

    /** 通知渠道明细 */
    private String notifyChannelSummary;

    /** 事件状态(NEW/HANDLING/RESOLVED/FALSE_ALARM) */
    private String eventStatus;

    /** 处置人用户ID */
    private Long handlerUserId;

    /** 处置时间 */
    private Date handleTime;

    /** 处置备注 */
    private String handleRemark;
}
