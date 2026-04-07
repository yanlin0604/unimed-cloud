package org.dromara.dhcore.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 方言邀请码配置视图对象
 *
 * @author unimed
 */
@Data
public class DhDialectInviteVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 邀请配置ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long inviteId;

    /**
     * 语种名（与C端方言名称一致）
     */
    private String dialectName;

    /**
     * 邀请码
     */
    private String inviteCode;

    /**
     * 生成的分享链接
     */
    private String collectionUrl;

    /**
     * 扩展信息
     */
    private String extInfo;

    /**
     * 状态
     */
    private String status;

    /**
     * 关联采集记录数
     */
    private Integer recordCount;

    /**
     * 创建时间
     */
    private Date createTime;
}
