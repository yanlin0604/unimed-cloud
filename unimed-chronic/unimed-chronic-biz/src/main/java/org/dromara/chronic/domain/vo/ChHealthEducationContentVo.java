package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChHealthEducationContent;

import java.io.Serial;
import java.io.Serializable;

@Schema(description = "宣教内容视图对象")
@Data
@AutoMapper(target = ChHealthEducationContent.class)
public class ChHealthEducationContentVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "宣教内容ID")
    private Long contentId;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容正文")
    private String contentBody;

    @Schema(description = "标签")
    private String tags;

}
