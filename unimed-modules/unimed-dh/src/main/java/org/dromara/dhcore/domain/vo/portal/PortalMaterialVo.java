package org.dromara.dhcore.domain.vo.portal;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * C端用户素材视图对象
 *
 * @author AI
 */
@Data
public class PortalMaterialVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 素材ID
     */
    private Long id;

    /**
     * 素材类型（IMAGE/VIDEO/AUDIO）
     */
    private String materialType;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件访问URL
     */
    private String fileUrl;

    /**
     * 缩略图URL
     */
    private String thumbnailUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 分辨率（如 1920x1080）
     */
    private String resolution;

    /**
     * 时长（秒，视频/音频适用）
     */
    private Integer duration;

    /**
     * 创建时间
     */
    private Date createTime;
}
