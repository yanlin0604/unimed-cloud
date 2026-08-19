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
 * 机构统计日表对象 ch_stat_org_day
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_stat_org_day")
public class ChStatOrgDay extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    /** 机构ID */
    private Long orgId;

    /** 统计日期 */
    private Date statDate;

    /** 患者数 */
    private Long patientCount;

    /** 随访完成数 */
    private Long followupDoneCount;

    /** 预警数 */
    private Long warningCount;

    @TableLogic
    private String delFlag;
}
