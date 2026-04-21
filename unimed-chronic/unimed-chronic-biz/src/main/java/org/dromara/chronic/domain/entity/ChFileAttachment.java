package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 附件统一管理对象 ch_file_attachment
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_file_attachment")
public class ChFileAttachment extends TenantEntity {

    @TableId(value = "file_id")
    private Long fileId;

    /**
     * 业务类型: REPORT_PDF/SIGN_IMAGE/FUNDUS_PHOTO/ECG/OTHER
     */
    private String bizType;

    private Long bizId;

    private String fileName;

    private Long fileSize;

    private Long ossId;

    @TableLogic
    private String delFlag;
}
