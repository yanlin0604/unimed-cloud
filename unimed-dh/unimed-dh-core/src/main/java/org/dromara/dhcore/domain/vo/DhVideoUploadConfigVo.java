package org.dromara.dhcore.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 视频上传配置视图对象
 */
@Data
public class DhVideoUploadConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    private Long id;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 上传类型
     */
    private String type;

    /**
     * 文件ID列表
     */
    private String videoFileIds;

    /**
     * 大小限制MB
     */
    private Integer maxSizeMb;

    /**
     * 格式描述
     */
    private String formatDesc;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
