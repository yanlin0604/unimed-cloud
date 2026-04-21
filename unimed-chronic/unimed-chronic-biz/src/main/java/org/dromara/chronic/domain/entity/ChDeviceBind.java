package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 设备绑定对象 ch_device_bind
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_device_bind")
public class ChDeviceBind extends TenantEntity {

    @TableId(value = "bind_id")
    private Long bindId;

    private Long patientId;

    private String deviceId;

    private String deviceType;

    private Integer batteryLevel;

    private String onlineStatus;

    private Date lastCommTime;

    @TableLogic
    private String delFlag;
}
