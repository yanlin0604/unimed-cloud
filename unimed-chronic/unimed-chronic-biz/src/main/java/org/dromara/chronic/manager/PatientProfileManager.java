package org.dromara.chronic.manager;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChPatientTag;
import org.dromara.chronic.domain.entity.ChPatientTimeline;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.mapper.ChPatientDiseaseMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.mapper.ChPatientTagMapper;
import org.dromara.chronic.mapper.ChPatientTimelineMapper;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
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
    private final ChPatientProfileMapper patientProfileMapper;
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
     * 按身份证号查询已存在的患者档案
     * <p>
     * 用于 HIS 同步等幂等判定场景。idCard 为空时直接返回 null，避免误命中同租户下
     * 其他也没填 idCard 的档案。
     *
     * @param idCard 身份证号
     * @return 已存在的患者档案实体；若不存在则返回 null
     */
    public ChPatientProfile findByIdCard(String idCard) {
        if (StringUtils.isBlank(idCard)) {
            return null;
        }
        return patientProfileMapper.selectOne(
            Wrappers.<ChPatientProfile>lambdaQuery()
                .eq(ChPatientProfile::getIdCard, idCard)
                .orderByAsc(ChPatientProfile::getPatientId)
                .last("limit 1")
        );
    }

    /**
     * 更新已有患者档案（HIS 幂等同步场景使用）
     * <p>
     * 将已查得的 patientId 回填到 BO，然后交由底层 Service 走 updateByBo；
     * 同步失败抛 {@link ServiceException} 交给上层记录 FAIL 日志。
     *
     * @param bo        HIS 推送的最新档案信息
     * @param patientId 已存在的患者 ID（必填）
     * @return 同一个 patientId
     */
    @Transactional(rollbackFor = Exception.class)
    public Long updateArchive(ChPatientProfileBo bo, Long patientId) {
        if (patientId == null) {
            throw new ServiceException("更新患者档案时 patientId 不能为空");
        }
        bo.setPatientId(patientId);
        if (Boolean.FALSE.equals(patientProfileService.updateByBo(bo))) {
            throw new ServiceException("更新患者档案失败");
        }
        ChPatientTimeline timeline = new ChPatientTimeline();
        timeline.setPatientId(patientId);
        timeline.setEventType("ARCHIVE");
        timeline.setEventTitle("患者档案更新");
        timeline.setEventDetail("HIS 同步覆盖档案主信息");
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
