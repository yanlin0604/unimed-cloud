package org.dromara.dhcore.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 音色视图对象
 *
 * @author unimed
 */
@Data
public class DhVoiceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 音色ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long voiceId;

    /**
     * 音色名称
     */
    private String name;

    /**
     * 试听音频URL
     */
    private String sampleUrl;

    /**
     * OSS文件ID
     */
    private String ossId;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 来源 system/clone/upload
     */
    private String source;

    /**
     * 是否系统预设
     */
    private Boolean isSystem;

    /**
     * 创建时间
     */
    private Date createTime;
}
