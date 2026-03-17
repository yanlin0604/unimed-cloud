package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 数字人口播视频上传配置对象 dh_video_upload_config
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_video_upload_config")
public class DhVideoUploadConfig extends TenantEntity {

    /**
     * 视频上传配置ID
     */
    @TableId(value = "config_id")
    private Long configId;

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
     * 状态（0启用 1停用）
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
