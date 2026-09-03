package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 医生与团队绩效考核评估对象 ch_performance_eval
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_performance_eval")
public class ChPerformanceEval extends TenantEntity {

    @TableId(value = "eval_id")
    private Long evalId;

    private Long doctorUserId;

    private String doctorName;

    private Long teamId;

    private String teamName;

    private String evalCycle;

    private Integer managedCount;

    private Integer followupCount;

    private BigDecimal controlRate;

    private BigDecimal satisfactionScore;

    private BigDecimal totalScore;

    private String grade;

    private LocalDate evalDate;

    private String delFlag;
}
