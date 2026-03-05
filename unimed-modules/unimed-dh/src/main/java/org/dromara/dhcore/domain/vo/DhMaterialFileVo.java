package org.dromara.dhcore.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.dhcore.domain.DhOrderMaterial;

import java.io.Serial;
import java.io.Serializable;

/**
 * 订单素材视图对象
 */
@Data
@AutoMapper(target = DhOrderMaterial.class)
public class DhMaterialFileVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 缩略图地址
     */
    private String thumbnailUrl;
}
