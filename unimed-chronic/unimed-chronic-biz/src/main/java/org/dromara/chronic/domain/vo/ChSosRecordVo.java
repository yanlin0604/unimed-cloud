package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChSosRecord;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 紧急求助记录视图对象 ch_sos_record
 *
 * @author unimed
 */
@Data
@Schema(description = "紧急求助记录视图对象")
@AutoMapper(target = ChSosRecord.class)
public class ChSosRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 求助ID */
    @Schema(description = "求助ID")
    private Long sosId;

    /** 患者ID */
    @Schema(description = "患者ID")
    private Long patientId;

    /** 患者姓名 (由服务层富化) */
    @Schema(description = "患者姓名")
    private String patientName;

    /** 患者手机号 (由服务层富化) */
    @Schema(description = "患者手机号")
    private String patientPhone;

    /** GPS经度 */
    @Schema(description = "GPS经度")
    private BigDecimal gpsLng;

    /** GPS纬度 */
    @Schema(description = "GPS纬度")
    private BigDecimal gpsLat;

    /** 反向地理编码地址 */
    @Schema(description = "反向地理编码地址")
    private String gpsAddress;

    /** 通知医生状态(PENDING/SENT/FAILED) */
    @Schema(description = "通知医生状态")
    private String notifyDoctorStatus;

    /** 通知紧急联系人状态(PENDING/SENT/FAILED) */
    @Schema(description = "通知紧急联系人状态")
    private String notifyEmergencyStatus;

    /** 通知渠道明细 */
    @Schema(description = "通知渠道明细")
    private String notifyChannelSummary;

    /** 事件状态(NEW/HANDLING/RESOLVED/FALSE_ALARM) */
    @Schema(description = "事件状态")
    private String eventStatus;

    /** 处置人用户ID */
    @Schema(description = "处置人用户ID")
    private Long handlerUserId;

    /** 处置人昵称 */
    @Schema(description = "处置人昵称")
    private String handlerNickName;

    /** 处置时间 */
    @Schema(description = "处置时间")
    private Date handleTime;

    /** 处置备注 */
    @Schema(description = "处置备注")
    private String handleRemark;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private Date createTime;
}
