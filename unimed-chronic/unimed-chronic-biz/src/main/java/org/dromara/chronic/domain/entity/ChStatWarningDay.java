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
 * 预警统计日表对象 ch_stat_warning_day
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_stat_warning_day")
public class ChStatWarningDay extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    /** 统计日期 */
    private Date statDate;

    /** 总预警数 */
    private Long totalCount;

    /** 已解决数 */
    private Long resolvedCount;

    /** 已升级数 */
    private Long escalatedCount;

    /** 平均解决时长(分钟) */
    private Long avgResolveMinutes;

    @TableLogic
    private String delFlag;
}
