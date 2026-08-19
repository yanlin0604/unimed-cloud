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
 * 紧急求助记录对象 ch_sos_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_sos_record")
public class ChSosRecord extends TenantEntity {

    /** 求助ID */
    @TableId(value = "sos_id")
    private Long sosId;

    /** 患者ID */
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

    /** 通知渠道明细 (JSON) */
    private String notifyChannelSummary;

    /** 事件状态(NEW/HANDLING/RESOLVED/FALSE_ALARM) */
    private String eventStatus;

    /** 处置人用户ID */
    private Long handlerUserId;

    /** 处置时间 */
    private Date handleTime;

    /** 处置备注 */
    private String handleRemark;

    /** 删除标志(0存在 1删除) */
    @TableLogic
    private String delFlag;
}
