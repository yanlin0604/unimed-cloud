package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 消息内容对象 ch_message_content
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_message_content")
public class ChMessageContent extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long sessionId;

    /**
     * 发送者类型: DOCTOR/PATIENT
     */
    private String senderType;

    /**
     * 内容类型: TEXT/IMAGE/VOICE
     */
    private String contentType;

    private String content;

    private Long fileId;

    private Integer voiceDuration;

    @TableLogic
    private String delFlag;
}
