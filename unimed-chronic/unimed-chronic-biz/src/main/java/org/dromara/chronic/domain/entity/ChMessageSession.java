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
 * 消息会话对象 ch_message_session
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_message_session")
public class ChMessageSession extends TenantEntity {

    @TableId(value = "session_id")
    private Long sessionId;

    private Long patientId;

    private Long doctorUserId;

    /**
     * 会话类型: DOCTOR_PATIENT/TEAM_PATIENT/TASK_CHAT
     */
    private String sessionType;

    /**
     * 关联随访任务ID(TASK_CHAT 会话用, 其余类型为空)
     */
    private Long taskId;

    private Date lastMessageTime;

    @TableLogic
    private String delFlag;
}
