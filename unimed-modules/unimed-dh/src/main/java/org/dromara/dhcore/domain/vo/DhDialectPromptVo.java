package org.dromara.dhcore.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 方言采集提示文字视图对象
 *
 * @author unimed
 */
@Data
public class DhDialectPromptVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 提示文字ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long promptId;

    /**
     * 采集文字内容
     */
    private String content;

    /**
     * 分类标签
     */
    private String category;

    /**
     * 状态 0正常 1禁用
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
}
