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
 * 疾病统计日表对象 ch_stat_disease_day
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_stat_disease_day")
public class ChStatDiseaseDay extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    /** 疾病编码 */
    private String diseaseCode;

    /** 统计日期 */
    private Date statDate;

    /** 患者数 */
    private Long patientCount;

    /** 新增数 */
    private Long newCount;

    /** 高风险数 */
    private Long riskHighCount;

    @TableLogic
    private String delFlag;
}
