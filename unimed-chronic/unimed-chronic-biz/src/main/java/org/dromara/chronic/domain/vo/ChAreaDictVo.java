package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChAreaDict;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 行政区划视图对象
 *
 * @author unimed
 */
@Schema(description = "行政区划字典视图对象")
@Data
@AutoMapper(target = ChAreaDict.class)
public class ChAreaDictVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "区域编码")
    private String areaCode;
    @Schema(description = "区域名称")
    private String areaName;
    @Schema(description = "区域层级")
    private Integer areaLevel;
    @Schema(description = "父级区域编码")
    private String parentAreaCode;

    @Schema(description = "子级区域列表")
    private List<ChAreaDictVo> children;
}
