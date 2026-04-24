package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.common.constant.ChronicDictTypeConstant;
import org.dromara.chronic.domain.entity.ChExternalSyncLog;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 外部同步日志视图对象
 *
 * @author unimed
 */
@Schema(description = "外部同步日志视图对象")
@Data
@AutoMapper(target = ChExternalSyncLog.class)
public class ChExternalSyncLogVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "同步类型")
    private String syncType;
    @Schema(description = "同步方向")
    private String syncDirection;
    @Schema(description = "外部系统")
    private String externalSystem;
    @Schema(description = "同步状态")
    private String syncStatus;
    @Schema(description = "同步详情")
    private String syncDetail;
    @Schema(description = "同步时间")
    private Date syncTime;

    @Schema(description = "同步类型名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "syncType", other = ChronicDictTypeConstant.CHRONIC_SYNC_TYPE)
    private String syncTypeName;

    @Schema(description = "同步方向名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "syncDirection", other = ChronicDictTypeConstant.CHRONIC_SYNC_DIRECTION)
    private String syncDirectionName;

    @Schema(description = "同步状态名称")
    @Translation(type = TransConstant.DICT_TYPE_TO_LABEL, mapper = "syncStatus", other = ChronicDictTypeConstant.CHRONIC_SYNC_STATUS)
    private String syncStatusName;
}
