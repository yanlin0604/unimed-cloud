package org.dromara.dhcore.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 方言采集录音记录视图对象
 *
 * @author unimed
 */
@Data
@ExcelIgnoreUnannotated
public class DhDialectRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 录音记录ID
     */
    @ExcelProperty(value = "录音记录ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long recordId;

    /**
     * 关联提示文字ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long promptId;

    /**
     * 采集文字内容（关联查询）
     */
    @ExcelProperty(value = "采集文字内容")
    private String promptContent;

    /**
     * 提交用户ID
     */
    @ExcelProperty(value = "提交用户ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    /**
     * 方言名称
     */
    @ExcelProperty(value = "方言名称")
    private String dialectName;

    /**
     * 方言地区
     */
    @ExcelProperty(value = "方言地区")
    private String dialectArea;

    /**
     * 持久访问URL
     */
    @ExcelProperty(value = "录音地址")
    private String audioUrl;

    /**
     * OSS文件ID（不导出）
     */
    private String ossId;

    /**
     * 录音时长（秒）
     */
    @ExcelProperty(value = "录音时长(秒)")
    private Integer duration;

    /**
     * 审核状态 PENDING/APPROVED/REJECTED
     */
    @ExcelProperty(value = "审核状态")
    private String auditStatus;

    /**
     * 审核备注
     */
    @ExcelProperty(value = "审核备注")
    private String auditRemark;

    /**
     * 提交时间
     */
    @ExcelProperty(value = "提交时间")
    private Date createTime;
}
