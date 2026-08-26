package org.dromara.chronic.manager;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.bo.ChPatientTagBo;
import org.dromara.chronic.domain.entity.ChPatientAccount;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChPatientTag;
import org.dromara.chronic.domain.entity.ChPatientTimeline;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.mapper.ChPatientAccountMapper;
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

import java.util.ArrayList;
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
    private final ChPatientAccountMapper patientAccountMapper;
    private final FollowupEnrollmentManager followupEnrollmentManager;

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

        // 如果该手机号已有注册的患者账号，自动同步绑定至新建的档案
        if (StringUtils.isNotBlank(bo.getPhone())) {
            patientAccountMapper.update(null,
                Wrappers.<ChPatientAccount>lambdaUpdate()
                    .eq(ChPatientAccount::getPhone, bo.getPhone())
                    .set(ChPatientAccount::getPatientId, patientId)
            );
        }

        // 自动入组触发：若建档时已包含确诊慢病，自动触发慢病随访计划生成流水线
        if (CollUtil.isNotEmpty(diseases)) {
            for (ChPatientDisease disease : diseases) {
                if (disease != null && StringUtils.isNotBlank(disease.getDiseaseCode())) {
                    try {
                        followupEnrollmentManager.autoEnrollAndGeneratePlan(patientId, disease.getDiseaseCode(), bo.getDoctorUserId());
                    } catch (Exception e) {
                        // 自动入组失败不阻断建档主流程
                    }
                }
            }
        }

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
        if (entity.getEnableStatus() == null) {
            entity.setEnableStatus(Boolean.TRUE);
        }
        patientDiseaseMapper.insert(entity);

        ChPatientTimeline timeline = new ChPatientTimeline();
        timeline.setPatientId(bo.getPatientId());
        timeline.setEventType("DISEASE_BIND");
        timeline.setEventTitle("确诊绑定");
        timeline.setEventDetail("新增病种: " + bo.getDiseaseCode() + ", ICD: " + bo.getIcdCode());
        timeline.setEventTime(new Date());
        patientTimelineMapper.insert(timeline);

        // 自动入组触发：绑定新病种后自动生成或合并随访计划
        try {
            followupEnrollmentManager.autoEnrollAndGeneratePlan(bo.getPatientId(), bo.getDiseaseCode(), null);
        } catch (Exception e) {
            // 自动入组失败不阻断绑定主流程
        }

        return entity.getId();
    }

    /**
     * 按主键更新单条病种
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDisease(ChPatientDiseaseBo bo) {
        if (bo.getId() == null) {
            throw new ServiceException("病种ID不能为空");
        }
        ChPatientDisease exists = patientDiseaseMapper.selectById(bo.getId());
        if (exists == null) {
            throw new ServiceException("病种记录不存在");
        }
        ChPatientDisease entity = MapstructUtils.convert(bo, ChPatientDisease.class);
        // 防止把 patientId 改成别人
        entity.setPatientId(exists.getPatientId());
        patientDiseaseMapper.updateById(entity);

        ChPatientTimeline timeline = new ChPatientTimeline();
        timeline.setPatientId(exists.getPatientId());
        timeline.setEventType("DISEASE_UPDATE");
        timeline.setEventTitle("病种信息变更");
        timeline.setEventDetail("更新病种: " + (bo.getDiseaseCode() != null ? bo.getDiseaseCode() : exists.getDiseaseCode()));
        timeline.setEventTime(new Date());
        patientTimelineMapper.insert(timeline);
    }

    /**
     * 仅切换病种启停状态（不联动随访任务）
     */
    @Transactional(rollbackFor = Exception.class)
    public void toggleDiseaseStatus(Long id, Boolean enableStatus) {
        if (id == null || enableStatus == null) {
            throw new ServiceException("参数缺失");
        }
        ChPatientDisease exists = patientDiseaseMapper.selectById(id);
        if (exists == null) {
            throw new ServiceException("病种记录不存在");
        }
        ChPatientDisease update = new ChPatientDisease();
        update.setId(id);
        update.setEnableStatus(enableStatus);
        patientDiseaseMapper.updateById(update);

        ChPatientTimeline timeline = new ChPatientTimeline();
        timeline.setPatientId(exists.getPatientId());
        timeline.setEventType("DISEASE_STATUS");
        timeline.setEventTitle(enableStatus ? "病种启用" : "病种停用");
        timeline.setEventDetail("病种 " + exists.getDiseaseCode() + " 状态切换为 " + (enableStatus ? "启用" : "停用"));
        timeline.setEventTime(new Date());
        patientTimelineMapper.insert(timeline);
    }

    /**
     * 移除单条病种（逻辑删除，由 {@link ChPatientDisease#getDelFlag()} 控制）
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeDisease(Long id) {
        if (id == null) {
            throw new ServiceException("病种ID不能为空");
        }
        ChPatientDisease exists = patientDiseaseMapper.selectById(id);
        if (exists == null) {
            throw new ServiceException("病种记录不存在");
        }
        patientDiseaseMapper.deleteById(id);

        ChPatientTimeline timeline = new ChPatientTimeline();
        timeline.setPatientId(exists.getPatientId());
        timeline.setEventType("DISEASE_REMOVE");
        timeline.setEventTitle("病种移除");
        timeline.setEventDetail("移除病种: " + exists.getDiseaseCode());
        timeline.setEventTime(new Date());
        patientTimelineMapper.insert(timeline);
    }

    /**
     * 查询患者档案
     */
    public ChPatientDetailVo queryProfile(Long patientId) {
        return patientProfileService.queryDetailById(patientId);
    }

    /**
     * 编辑患者档案（含病种与标签全量替换）
     * <p>
     * 主档案走 service.updateByBo；diseases/tags 走"先清空-再批量插入"的全量替换策略，
     * 与前端"表单一次性提交全部关联数据"的语义一致。
     *
     * @param bo 携带 diseases / tags 的患者 BO
     */
    @Transactional(rollbackFor = Exception.class)
    public void editArchive(ChPatientProfileBo bo) {
        if (bo.getPatientId() == null) {
            throw new ServiceException("患者ID不能为空");
        }
        if (Boolean.FALSE.equals(patientProfileService.updateByBo(bo))) {
            throw new ServiceException("更新患者档案失败");
        }
        replaceDiseases(bo.getPatientId(), convertDiseases(bo.getDiseases()));
        replaceTags(bo.getPatientId(), convertTags(bo.getTags()));

        ChPatientTimeline timeline = new ChPatientTimeline();
        timeline.setPatientId(bo.getPatientId());
        timeline.setEventType("ARCHIVE");
        timeline.setEventTitle("患者档案变更");
        timeline.setEventDetail("管理员更新档案信息");
        timeline.setEventTime(new Date());
        patientTimelineMapper.insert(timeline);
    }

    /**
     * BO → 实体批量转换（病种）
     */
    public List<ChPatientDisease> convertDiseases(List<ChPatientDiseaseBo> source) {
        if (CollUtil.isEmpty(source)) {
            return new ArrayList<>();
        }
        List<ChPatientDisease> list = new ArrayList<>(source.size());
        for (ChPatientDiseaseBo item : source) {
            ChPatientDisease entity = MapstructUtils.convert(item, ChPatientDisease.class);
            if (entity != null) {
                if (entity.getConfirmDate() == null) {
                    entity.setConfirmDate(new Date());
                }
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * BO → 实体批量转换（标签）
     */
    public List<ChPatientTag> convertTags(List<ChPatientTagBo> source) {
        if (CollUtil.isEmpty(source)) {
            return new ArrayList<>();
        }
        List<ChPatientTag> list = new ArrayList<>(source.size());
        for (ChPatientTagBo item : source) {
            ChPatientTag entity = MapstructUtils.convert(item, ChPatientTag.class);
            if (entity != null) {
                list.add(entity);
            }
        }
        return list;
    }

    private void replaceDiseases(Long patientId, List<ChPatientDisease> diseases) {
        patientDiseaseMapper.delete(
            Wrappers.<ChPatientDisease>lambdaQuery().eq(ChPatientDisease::getPatientId, patientId)
        );
        if (CollUtil.isNotEmpty(diseases)) {
            diseases.forEach(item -> item.setPatientId(patientId));
            patientDiseaseMapper.insertBatch(diseases);
        }
    }

    private void replaceTags(Long patientId, List<ChPatientTag> tags) {
        patientTagMapper.delete(
            Wrappers.<ChPatientTag>lambdaQuery().eq(ChPatientTag::getPatientId, patientId)
        );
        if (CollUtil.isNotEmpty(tags)) {
            tags.forEach(item -> item.setPatientId(patientId));
            patientTagMapper.insertBatch(tags);
        }
    }
}
