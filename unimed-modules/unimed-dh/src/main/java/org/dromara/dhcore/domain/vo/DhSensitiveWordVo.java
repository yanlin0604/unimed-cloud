package org.dromara.dhcore.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 敏感词配置视图对象
 */
@Data
public class DhSensitiveWordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 敏感词ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    /**
     * 敏感词
     */
    private String word;

    /**
     * 风险等级
     */
    private String level;

    /**
     * 分类
     */
    private String category;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
