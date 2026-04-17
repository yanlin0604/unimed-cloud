package org.dromara.dhcore.domain.vo.portal;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * C端文件上传响应视图对象
 *
 * @author unimed
 */
@Data
public class PortalFileUploadVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文件访问地址
     */
    private String url;

    /**
     * 原始文件名
     */
    private String fileName;

    /**
     * 对象存储主键
     */
    private String ossId;
}
