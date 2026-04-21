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
 * 义诊筛查批次对象 ch_screening_batch
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_screening_batch")
public class ChScreeningBatch extends TenantEntity {

    @TableId(value = "batch_id")
    private Long batchId;

    private String batchName;

    private Date activityDate;

    private Long orgId;

    private Long doctorUserId;

    private String location;

    private String notes;

    private String status;

    @TableLogic
    private String delFlag;
}
