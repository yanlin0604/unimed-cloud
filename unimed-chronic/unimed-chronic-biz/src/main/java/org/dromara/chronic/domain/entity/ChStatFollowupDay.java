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
 * 随访统计日表对象 ch_stat_followup_day
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_stat_followup_day")
public class ChStatFollowupDay extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    /** 统计日期 */
    private Date statDate;

    /** 总随访数 */
    private Long totalCount;

    /** 完成数 */
    private Long doneCount;

    /** 逾期数 */
    private Long overdueCount;

    /** 完成率 */
    private BigDecimal completionRate;

    @TableLogic
    private String delFlag;
}
