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
 * 宣教投递对象 ch_health_education_delivery
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_health_education_delivery")
public class ChHealthEducationDelivery extends TenantEntity {

    @TableId(value = "delivery_id")
    private Long deliveryId;

    private Long contentId;

    private Long patientId;

    /**
     * 触发类型: RULE_ENGINE/MANUAL/WEATHER/SEASONAL
     */
    private String triggerType;

    /**
     * 推送通道: WECHAT/SMS/IVR/PAPER
     */
    private String pushChannel;

    private String deliveryStatus;

    private Boolean readStatus;

    private Date readTime;

    private Integer stayDuration;

    @TableLogic
    private String delFlag;
}
