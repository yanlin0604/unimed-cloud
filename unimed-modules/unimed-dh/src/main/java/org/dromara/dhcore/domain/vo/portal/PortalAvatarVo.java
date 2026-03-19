package org.dromara.dhcore.domain.vo.portal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * C端数字人形象视图对象
 *
 * @author unimed
 */
@Data
public class PortalAvatarVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 形象ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 形象名称
     */
    private String name;

    /**
     * 形象图片URL
     */
    private String imageUrl;

    /**
     * 是否系统预设
     */
    private Boolean isSystem;

    /**
     * 创建时间
     */
    private Date createTime;
}
