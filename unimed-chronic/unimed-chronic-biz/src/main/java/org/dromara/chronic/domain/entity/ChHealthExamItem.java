package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.math.BigDecimal;

/**
 * 体检检验项对象 ch_health_exam_item
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_health_exam_item")
public class ChHealthExamItem extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long examId;

    private String itemName;

    private String itemCode;

    private String resultValue;

    private String referenceRange;

    private Boolean isAbnormal;

    /**
     * 眼底DR分级(0-4)
     */
    private Integer drGrade;

    /**
     * TCSS神经病变评分
     */
    private Integer tcssScore;

    /**
     * MRS改良Rankin量表
     */
    private Integer mrsScore;

    /**
     * NIHSS卒中量表
     */
    private Integer nihssScore;

    /**
     * eGFR估算肾小球滤过率
     */
    private BigDecimal egfrValue;

    @TableLogic
    private String delFlag;
}
