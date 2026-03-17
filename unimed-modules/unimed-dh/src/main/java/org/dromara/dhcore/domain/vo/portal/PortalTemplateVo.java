package org.dromara.dhcore.domain.vo.portal;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * C端公开模板视图对象
 *
 * @author unimed
 */
@Data
public class PortalTemplateVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    private Long id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板类型（VIDEO/AUDIO/IMAGE�?
     */
    private String type;

    /**
     * 封面图URL
     */
    private String coverUrl;

    /**
     * 预览URL
     */
    private String previewUrl;

    /**
     * 描述
     */
    private String description;

    /**
     * 格式说明
     */
    private String formatDesc;
}
