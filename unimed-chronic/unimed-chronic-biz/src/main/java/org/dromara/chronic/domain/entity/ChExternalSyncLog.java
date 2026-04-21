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
 * 外部同步日志对象 ch_external_sync_log
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_external_sync_log")
public class ChExternalSyncLog extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private String syncType;

    private String syncDirection;

    private String externalSystem;

    private String syncStatus;

    private String syncDetail;

    private Date syncTime;

    @TableLogic
    private String delFlag;
}
