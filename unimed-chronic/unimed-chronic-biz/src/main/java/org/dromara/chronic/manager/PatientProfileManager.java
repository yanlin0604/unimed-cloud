package org.dromara.chronic.manager;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.domain.entity.ChPatientTag;
import org.dromara.chronic.domain.entity.ChPatientTimeline;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.mapper.ChPatientDiseaseMapper;
import org.dromara.chronic.mapper.ChPatientTagMapper;
import org.dromara.chronic.mapper.ChPatientTimelineMapper;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 患者档案管理编排层
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class PatientProfileManager {

    private final IChPatientProfileService patientProfileService;
    private final ChPatientDiseaseMapper patientDiseaseMapper;
    private final ChPatientTagMapper patientTagMapper;
    private final ChPatientTimelineMapper patientTimelineMapper;

    /**
     * 创建患者档案
     *
     * @param bo       患者档案
     * @param diseases 患者病种列表
     * @param tags     患者标签列表
     * @return 患者ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createArchive(ChPatientProfileBo bo, List<ChPatientDisease> diseases, List<ChPatientTag> tags) {
        if (!patientProfileService.insertByBo(bo)) {
            throw new ServiceException("创建患者档案失败");
        }
        Long patientId = bo.getPatientId();
        if (CollUtil.isNotEmpty(diseases)) {
            diseases.forEach(item -> item.setPatientId(patientId));
            patientDiseaseMapper.insertBatch(diseases);
        }
        if (CollUtil.isNotEmpty(tags)) {
            tags.forEach(item -> item.setPatientId(patientId));
            patientTagMapper.insertBatch(tags);
        }
        ChPatientTimeline timeline = new ChPatientTimeline();
        timeline.setPatientId(patientId);
        timeline.setEventType("ARCHIVE");
        timeline.setEventTitle("患者建档");
        timeline.setEventDetail("完成患者主档、病种及标签初始化");
        timeline.setEventTime(new Date());
        patientTimelineMapper.insert(timeline);
        return patientId;
    }

    /**
     * 绑定病种到已有患者
     */
    @Transactional(rollbackFor = Exception.class)
    public Long bindDisease(ChPatientDiseaseBo bo) {
        ChPatientDisease entity = MapstructUtils.convert(bo, ChPatientDisease.class);
        if (entity.getConfirmDate() == null) {
            entity.setConfirmDate(new Date());
        }
        patientDiseaseMapper.insert(entity);

        ChPatientTimeline timeline = new ChPatientTimeline();
        timeline.setPatientId(bo.getPatientId());
        timeline.setEventType("DISEASE_BIND");
        timeline.setEventTitle("确诊绑定");
        timeline.setEventDetail("新增病种: " + bo.getDiseaseCode() + ", ICD: " + bo.getIcdCode());
        timeline.setEventTime(new Date());
        patientTimelineMapper.insert(timeline);

        return entity.getId();
    }

    /**
     * 查询患者档案
     */
    public ChPatientDetailVo queryProfile(Long patientId) {
        return patientProfileService.queryDetailById(patientId);
    }
}
