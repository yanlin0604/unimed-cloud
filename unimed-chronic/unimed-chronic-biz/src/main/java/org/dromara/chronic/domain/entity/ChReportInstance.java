package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 报告实例对象 ch_report_instance
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_report_instance")
public class ChReportInstance extends TenantEntity {

    @TableId(value = "report_id")
    private Long reportId;

    private Long patientId;

    private Long templateId;

    /**
     * 报告类型: ANNUAL/FOLLOWUP/SPECIAL
     */
    private String reportType;

    /**
     * PDF 文件 OSS ID
     */
    private String pdfOssId;

    /**
     * 电子签章时间
     */
    private Date signTime;

    /**
     * 防伪二维码内容
     */
    private String qrCodeContent;

    /**
     * 推送状态: PENDING/PUSHED/FAILED
     */
    private String pushStatus;

    @TableLogic
    private String delFlag;
}
