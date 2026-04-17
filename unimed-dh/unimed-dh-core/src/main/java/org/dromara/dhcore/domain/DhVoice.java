package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 音色对象 dh_voice
 *
 * @author unimed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_voice")
public class DhVoice extends TenantEntity {

    /**
     * 音色ID
     */
    @TableId(value = "voice_id")
    private Long voiceId;

    /**
     * 用户ID（系统预设为空）
     */
    private Long userId;

    /**
     * 音色名称
     */
    private String name;

    /**
     * OSS文件ID
     */
    private String ossId;

    /**
     * 试听音频URL
     */
    private String sampleUrl;

    /**
     * 来源 system/clone/upload
     */
    private String source;

    /**
     * 是否系统预设 0否 1是
     */
    private Integer isSystem;

    /**
     * 状态 0正常 1禁用
     */
    private String status;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
