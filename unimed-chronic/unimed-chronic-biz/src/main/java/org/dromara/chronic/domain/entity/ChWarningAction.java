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
 * 预警处置动作对象 ch_warning_action
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_warning_action")
public class ChWarningAction extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long warningId;

    /**
     * 动作类型: CONFIRM/HANDLE/ESCALATE/RESOLVE
     */
    private String actionType;

    private String actionDetail;

    private Long actionUserId;

    private Date actionTime;

    @TableLogic
    private String delFlag;
}
