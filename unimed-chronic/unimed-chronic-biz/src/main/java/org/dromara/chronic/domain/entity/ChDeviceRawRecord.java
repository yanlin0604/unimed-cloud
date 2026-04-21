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
 * 设备原始数据记录对象 ch_device_raw_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_device_raw_record")
public class ChDeviceRawRecord extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private String deviceId;

    private Long patientId;

    /**
     * 设备原始数据 JSON
     */
    private String rawData;

    private Date parsedAt;

    @TableLogic
    private String delFlag;
}
