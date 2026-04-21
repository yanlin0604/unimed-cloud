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
 * 患者时间线对象 ch_patient_timeline
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_patient_timeline")
public class ChPatientTimeline extends TenantEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 患者ID
     */
    private Long patientId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件标题
     */
    private String eventTitle;

    /**
     * 事件详情
     */
    private String eventDetail;

    /**
     * 事件时间
     */
    private Date eventTime;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
