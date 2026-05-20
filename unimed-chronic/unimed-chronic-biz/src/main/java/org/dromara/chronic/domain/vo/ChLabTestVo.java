package org.dromara.chronic.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.chronic.domain.entity.ChLabTest;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 检验记录视图对象
 *
 * @author unimed
 */
@Schema(description = "检验记录视图对象")
@Data
@AutoMapper(target = ChLabTest.class)
public class ChLabTestVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "检验ID")
    private Long testId;

    @Schema(description = "患者ID")
    private Long patientId;
    @Schema(description = "患者姓名")
    private String patientName;

    @Schema(description = "检验日期")
    private Date testDate;

    @Schema(description = "检验类型")
    private String testType;

    @Schema(description = "检验项目明细JSON")
    private String testItems;

    @Schema(description = "报告图片URL")
    private String reportImage;

    @Schema(description = "检验医院")
    private String hospital;

    @Schema(description = "检验医生")
    private String doctor;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private Date createTime;
}
