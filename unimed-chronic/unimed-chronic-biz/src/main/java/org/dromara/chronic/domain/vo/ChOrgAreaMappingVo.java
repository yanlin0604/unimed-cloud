package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChOrgAreaMapping;

import java.io.Serial;
import java.io.Serializable;

/**
 * 机构区域映射视图对象
 *
 * @author unimed
 */
@Schema(description = "机构区域映射视图对象")
@Data
@AutoMapper(target = ChOrgAreaMapping.class)
public class ChOrgAreaMappingVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    private Long id;
    @Schema(description = "机构ID")
    private Long orgId;
    @Schema(description = "区域编码")
    private String areaCode;
}
