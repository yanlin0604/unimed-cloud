package org.dromara.dhcore.domain.vo.portal;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * C端音色视图对�?
 *
 * @author unimed
 */
@Data
public class PortalVoiceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 音色ID
     */
    private Long id;

    /**
     * 音色名称
     */
    private String name;

    /**
     * 试听音频URL
     */
    private String sampleUrl;

    /**
     * 是否系统预设
     */
    private Boolean isSystem;

    /**
     * 来源（clone/upload/system�?
     */
    private String source;

    /**
     * 创建时间
     */
    private Date createTime;
}
