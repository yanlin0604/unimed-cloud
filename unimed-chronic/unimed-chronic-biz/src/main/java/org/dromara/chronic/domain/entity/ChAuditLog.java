package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 敏感操作审计日志对象 ch_audit_log
 * <p>
 * 独立于通用 @Log 框架，由业务逻辑显式写入
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@TableName("ch_audit_log")
public class ChAuditLog extends BaseEntity {

    @TableId(value = "id")
    private Long id;

    private String operationType;

    private String operationTarget;

    private String operationDetail;

    private Long operatorId;

    private String operatorName;

    private String operatorIp;

    private Date operationTime;
}
