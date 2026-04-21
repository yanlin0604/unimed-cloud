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
 * 区域日统计对象 ch_stat_area_day
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_stat_area_day")
public class ChStatAreaDay extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private String areaCode;

    private Date statDate;

    private Long patientCount;

    private Long managedCount;

    private Long warningCount;

    private Long followupCount;

    @TableLogic
    private String delFlag;
}
