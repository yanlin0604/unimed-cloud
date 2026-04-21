package org.dromara.chronic.manager;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChEducationRule;
import org.dromara.chronic.domain.entity.ChHealthEducationDelivery;
import org.dromara.chronic.mapper.ChEducationRuleMapper;
import org.dromara.chronic.mapper.ChHealthEducationDeliveryMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 宣教推送管理器：规则匹配→通道选择→投递→阅读回执
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EducationPushManager {

    private final ChEducationRuleMapper educationRuleMapper;
    private final ChHealthEducationDeliveryMapper deliveryMapper;

    /**
     * 按规则匹配推送宣教内容
     */
    @Transactional(rollbackFor = Exception.class)
    public Long pushToPatient(Long contentId, Long patientId, String triggerType, String pushChannel) {
        ChHealthEducationDelivery delivery = new ChHealthEducationDelivery();
        delivery.setContentId(contentId);
        delivery.setPatientId(patientId);
        delivery.setTriggerType(triggerType);
        delivery.setPushChannel(pushChannel);
        delivery.setDeliveryStatus("DELIVERED");
        delivery.setReadStatus(false);
        deliveryMapper.insert(delivery);
        return delivery.getDeliveryId();
    }

    /**
     * 批量推送宣教内容
     */
    @Transactional(rollbackFor = Exception.class)
    public Void broadcastToPatients(Long contentId, List<Long> patientIds, String triggerType, String pushChannel) {
        for (Long patientId : patientIds) {
            pushToPatient(contentId, patientId, triggerType, pushChannel);
        }
        return null;
    }

    /**
     * 获取所有激活的宣教规则
     */
    public List<ChEducationRule> getActiveRules() {
        return educationRuleMapper.selectList(
            Wrappers.<ChEducationRule>lambdaQuery().eq(ChEducationRule::getIsActive, true)
        );
    }
}
