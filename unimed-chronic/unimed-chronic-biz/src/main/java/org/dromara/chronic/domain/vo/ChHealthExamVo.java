package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChHealthExam;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 体检报告视图对象
 *
 * @author unimed
 */
@Schema(description = "体检报告视图对象")
@Data
@AutoMapper(target = ChHealthExam.class)
public class ChHealthExamVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "体检ID")
    private Long examId;
    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "外部流水号")
    private String externalSn;
    @Schema(description = "体检类型")
    private String examType;
    @Schema(description = "体检日期")
    private Date examDate;
    @Schema(description = "体检机构ID")
    private Long examOrgId;
    @Schema(description = "特殊分类")
    private String specialCategory;
    @Schema(description = "创建时间")
    private Date createTime;

    /**
     * 检验项列表（聚合视图）
     */
    @Schema(description = "检验项列表（聚合视图）")
    private List<ChHealthExamItemVo> items;
}
