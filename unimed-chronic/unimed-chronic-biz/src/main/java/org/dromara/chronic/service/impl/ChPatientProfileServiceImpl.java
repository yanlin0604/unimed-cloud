package org.dromara.chronic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.common.helper.DiseaseNameHelper;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.entity.ChConsentRecord;
import org.dromara.chronic.domain.entity.ChPatientContract;
import org.dromara.chronic.domain.entity.ChPatientCloseApply;
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChPatientTag;
import org.dromara.chronic.domain.entity.ChPatientTagDict;
import org.dromara.chronic.domain.entity.ChPatientTimeline;
import org.dromara.chronic.domain.entity.ChRiskAssessment;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.domain.vo.ChPatientDiseaseVo;
import org.dromara.chronic.domain.vo.ChPatientProfileVo;
import org.dromara.chronic.domain.vo.ChPatientTagVo;
import org.dromara.chronic.domain.vo.ChPatientTimelineVo;
import org.dromara.chronic.mapper.ChConsentRecordMapper;
import org.dromara.chronic.mapper.ChPatientCloseApplyMapper;
import org.dromara.chronic.mapper.ChPatientContractMapper;
import org.dromara.chronic.mapper.ChPatientDiseaseMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.mapper.ChPatientTagDictMapper;
import org.dromara.chronic.mapper.ChPatientTagMapper;
import org.dromara.chronic.mapper.ChPatientTimelineMapper;
import org.dromara.chronic.mapper.ChRiskAssessmentMapper;
import org.dromara.chronic.service.IChPatientProfileService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 患者主档案服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChPatientProfileServiceImpl implements IChPatientProfileService {

    private final ChPatientProfileMapper baseMapper;
    private final ChPatientDiseaseMapper patientDiseaseMapper;
    private final ChPatientTagMapper patientTagMapper;
    private final ChPatientTagDictMapper patientTagDictMapper;
    private final ChPatientTimelineMapper patientTimelineMapper;
    private final ChPatientContractMapper patientContractMapper;
    private final ChConsentRecordMapper consentRecordMapper;
    private final ChRiskAssessmentMapper riskAssessmentMapper;
    private final ChPatientCloseApplyMapper patientCloseApplyMapper;
    private final DiseaseNameHelper diseaseNameHelper;

    @Override
    public List<ChPatientProfileVo> queryList(ChPatientProfileBo bo) {
        List<ChPatientProfileVo> result = baseMapper.selectVoList(buildQueryWrapper(bo));
        fillContractStatus(result);
        fillConsentStatus(result);
        fillCloseApplyStatus(result);
        return result;
    }

    public TableDataInfo<ChPatientProfileVo> queryPageList(ChPatientProfileBo bo, PageQuery pageQuery) {
        Page<ChPatientProfileVo> result = baseMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo));
        fillContractStatus(result.getRecords());
        fillConsentStatus(result.getRecords());
        fillCloseApplyStatus(result.getRecords());
        return TableDataInfo.build(result);
    }

    @Override
    public ChPatientDetailVo queryDetailById(Long patientId) {
        ChPatientProfile profile = baseMapper.selectById(patientId);
        if (ObjectUtil.isNull(profile)) {
            throw new ServiceException("患者档案不存在");
        }
        ChPatientDetailVo detailVo = BeanUtil.copyProperties(profile, ChPatientDetailVo.class);
        List<ChPatientDiseaseVo> diseaseList = patientDiseaseMapper.selectVoList(
            Wrappers.<ChPatientDisease>lambdaQuery().eq(ChPatientDisease::getPatientId, patientId)
        );
        fillPatientDiseaseNames(diseaseList);
        detailVo.setDiseaseList(diseaseList);
        List<ChPatientTagVo> tagList = patientTagMapper.selectVoList(
            Wrappers.<ChPatientTag>lambdaQuery().eq(ChPatientTag::getPatientId, patientId)
        );
        fillTagInfo(tagList);
        detailVo.setTags(tagList);
        List<ChPatientTimelineVo> timelines = patientTimelineMapper.selectVoList(
            Wrappers.<ChPatientTimeline>lambdaQuery()
                .eq(ChPatientTimeline::getPatientId, patientId)
                .orderByDesc(ChPatientTimeline::getEventTime)
        );
        detailVo.setLatestTimeline(CollUtil.isEmpty(timelines) ? null : timelines.get(0));
        fillContractStatus(detailVo, patientId);
        fillConsentStatus(detailVo, patientId);
        fillRiskLevel(detailVo, patientId);
        return detailVo;
    }

    @Override
    public List<ChPatientTimelineVo> queryTimelineByPatientId(Long patientId) {
        return patientTimelineMapper.selectVoList(
            Wrappers.<ChPatientTimeline>lambdaQuery()
                .eq(ChPatientTimeline::getPatientId, patientId)
                .orderByDesc(ChPatientTimeline::getEventTime)
        );
    }

    @Override
    public Boolean insertByBo(ChPatientProfileBo bo) {
        ChPatientProfile profile = MapstructUtils.convert(bo, ChPatientProfile.class);
        boolean success = baseMapper.insert(profile) > 0;
        if (success) {
            bo.setPatientId(profile.getPatientId());
        }
        return success;
    }

    @Override
    public Boolean updateByBo(ChPatientProfileBo bo) {
        if (ObjectUtil.isNull(bo.getPatientId())) {
            throw new ServiceException("患者ID不能为空");
        }
        ChPatientProfile profile = baseMapper.selectById(bo.getPatientId());
        if (ObjectUtil.isNull(profile)) {
            throw new ServiceException("患者档案不存在");
        }
        ChPatientProfile updateEntity = MapstructUtils.convert(bo, ChPatientProfile.class);
        return baseMapper.updateById(updateEntity) > 0;
    }

    @Override
    public Boolean deleteByIds(java.util.Collection<Long> patientIds) {
        if (CollUtil.isEmpty(patientIds)) {
            return Boolean.FALSE;
        }
        // 主档案逻辑删除；关联表（病种 / 标签 / 时间线）保留为审计痕迹
        return baseMapper.deleteByIds(patientIds) > 0;
    }

    @Override
    public Boolean checkIdCardUnique(String idCard, Long excludeId) {
        if (StringUtils.isBlank(idCard)) {
            return Boolean.FALSE;
        }
        LambdaQueryWrapper<ChPatientProfile> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChPatientProfile::getIdCard, idCard);
        lqw.ne(excludeId != null, ChPatientProfile::getPatientId, excludeId);
        return baseMapper.exists(lqw);
    }

    private LambdaQueryWrapper<ChPatientProfile> buildQueryWrapper(ChPatientProfileBo bo) {
        Map<String, Object> params = bo.getParams();
        List<Long> filteredPatientIds = resolvePatientIds(params);
        LambdaQueryWrapper<ChPatientProfile> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChPatientProfile::getPatientId, bo.getPatientId());
        lqw.like(StringUtils.isNotBlank(bo.getName()), ChPatientProfile::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getIdCard()), ChPatientProfile::getIdCard, bo.getIdCard());
        lqw.like(StringUtils.isNotBlank(bo.getPhone()), ChPatientProfile::getPhone, bo.getPhone());
        lqw.eq(ObjectUtil.isNotNull(bo.getDeptId()), ChPatientProfile::getDeptId, bo.getDeptId());
        lqw.eq(ObjectUtil.isNotNull(bo.getDoctorUserId()), ChPatientProfile::getDoctorUserId, bo.getDoctorUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getManageStatus()), ChPatientProfile::getManageStatus, bo.getManageStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getSource()), ChPatientProfile::getSource, bo.getSource());
        lqw.in(CollUtil.isNotEmpty(filteredPatientIds), ChPatientProfile::getPatientId, filteredPatientIds);
        lqw.orderByDesc(ChPatientProfile::getCreateTime);
        return lqw;
    }

    private List<Long> resolvePatientIds(Map<String, Object> params) {
        String diseaseCode = ObjectUtil.defaultIfNull(params.get("diseaseCode"), "").toString();
        String tagCode = ObjectUtil.defaultIfNull(params.get("tagCode"), "").toString();
        if (StringUtils.isAllBlank(diseaseCode, tagCode)) {
            return new ArrayList<>();
        }
        List<Long> patientIds = new ArrayList<>();
        if (StringUtils.isNotBlank(diseaseCode)) {
            patientIds.addAll(patientDiseaseMapper.selectObjs(
                Wrappers.<ChPatientDisease>lambdaQuery()
                    .select(ChPatientDisease::getPatientId)
                    .eq(ChPatientDisease::getDiseaseCode, diseaseCode)
            ).stream().map(obj -> Long.valueOf(String.valueOf(obj))).toList());
        }
        if (StringUtils.isNotBlank(tagCode)) {
            patientIds.addAll(patientTagMapper.selectObjs(
                Wrappers.<ChPatientTag>lambdaQuery()
                    .select(ChPatientTag::getPatientId)
                    .eq(ChPatientTag::getTagCode, tagCode)
            ).stream().map(obj -> Long.valueOf(String.valueOf(obj))).toList());
        }
        return CollUtil.distinct(patientIds);
    }

    private void fillPatientDiseaseNames(List<ChPatientDiseaseVo> list) {
        if (CollUtil.isEmpty(list)) return;
        // 合并 diseaseCode 与 parentDiseaseCode 一次性查询字典，避免 N+1
        List<String> codes = new ArrayList<>();
        list.forEach(v -> {
            if (StringUtils.isNotBlank(v.getDiseaseCode())) codes.add(v.getDiseaseCode());
            if (StringUtils.isNotBlank(v.getParentDiseaseCode())) codes.add(v.getParentDiseaseCode());
        });
        List<String> diseaseCodes = codes.stream().distinct().collect(Collectors.toList());
        if (!diseaseCodes.isEmpty()) {
            try {
                Map<String, String> diseaseNameMap = diseaseNameHelper.batchGetDiseaseName(diseaseCodes);
                list.forEach(v -> {
                    v.setDiseaseName(diseaseNameMap.get(v.getDiseaseCode()));
                    if (StringUtils.isNotBlank(v.getParentDiseaseCode())) {
                        v.setParentDiseaseName(diseaseNameMap.get(v.getParentDiseaseCode()));
                    }
                });
            } catch (Exception e) { /* ignore */ }
        }
    }

    /**
     * 批量回填患者标签的 tagName / tagColor（按 tag_code 查 ch_patient_tag_dict）
     * 字典查不到时保留 tagCode 兜底，避免前端展示英文 code
     */
    private void fillTagInfo(List<ChPatientTagVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<String> tagCodes = list.stream().map(ChPatientTagVo::getTagCode)
            .filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        if (tagCodes.isEmpty()) return;
        try {
            List<ChPatientTagDict> dicts = patientTagDictMapper.selectList(
                Wrappers.<ChPatientTagDict>lambdaQuery().in(ChPatientTagDict::getTagCode, tagCodes)
            );
            Map<String, ChPatientTagDict> codeMap = dicts.stream()
                .collect(Collectors.toMap(ChPatientTagDict::getTagCode, d -> d, (a, b) -> a));
            list.forEach(v -> {
                ChPatientTagDict dict = codeMap.get(v.getTagCode());
                if (dict != null) {
                    v.setTagName(dict.getTagName());
                    v.setTagColor(dict.getColor());
                }
            });
        } catch (Exception e) { /* ignore */ }
    }

    private void fillContractStatus(List<ChPatientProfileVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> patientIds = list.stream()
            .map(ChPatientProfileVo::getPatientId)
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (patientIds.isEmpty()) return;
        try {
            List<ChPatientContract> contracts = patientContractMapper.selectList(
                Wrappers.<ChPatientContract>lambdaQuery()
                    .in(ChPatientContract::getPatientId, patientIds)
                    .eq(ChPatientContract::getContractStatus, "ACTIVE")
                    .orderByDesc(ChPatientContract::getContractPeriodStart)
            );
            Map<Long, String> activeMap = contracts.stream()
                .collect(Collectors.toMap(ChPatientContract::getPatientId, ChPatientContract::getContractStatus, (a, b) -> a));
            list.forEach(v -> v.setContractStatus(activeMap.getOrDefault(v.getPatientId(), "UNSIGNED")));
        } catch (Exception e) {
            list.forEach(v -> v.setContractStatus("UNSIGNED"));
        }
    }

    private void fillContractStatus(ChPatientDetailVo detailVo, Long patientId) {
        try {
            ChPatientContract contract = patientContractMapper.selectOne(
                Wrappers.<ChPatientContract>lambdaQuery()
                    .eq(ChPatientContract::getPatientId, patientId)
                    .eq(ChPatientContract::getContractStatus, "ACTIVE")
                    .orderByDesc(ChPatientContract::getContractPeriodStart)
                    .last("LIMIT 1")
            );
            detailVo.setContractStatus(contract != null ? contract.getContractStatus() : "UNSIGNED");
        } catch (Exception e) {
            detailVo.setContractStatus("UNSIGNED");
        }
    }

    private void fillConsentStatus(List<ChPatientProfileVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> patientIds = list.stream()
            .map(ChPatientProfileVo::getPatientId)
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (patientIds.isEmpty()) return;
        try {
            List<Long> signedPatientIds = consentRecordMapper.selectList(
                Wrappers.<ChConsentRecord>lambdaQuery()
                    .in(ChConsentRecord::getPatientId, patientIds)
                    .eq(ChConsentRecord::getConsentType, "SIGN_CONTRACT")
            ).stream().map(ChConsentRecord::getPatientId).distinct().collect(Collectors.toList());
            list.forEach(v -> v.setConsentStatus(signedPatientIds.contains(v.getPatientId()) ? "SIGNED" : "UNSIGNED"));
        } catch (Exception e) {
            list.forEach(v -> v.setConsentStatus("UNSIGNED"));
        }
    }

    private void fillConsentStatus(ChPatientDetailVo detailVo, Long patientId) {
        try {
            long count = consentRecordMapper.selectCount(
                Wrappers.<ChConsentRecord>lambdaQuery()
                    .eq(ChConsentRecord::getPatientId, patientId)
                    .eq(ChConsentRecord::getConsentType, "SIGN_CONTRACT")
            );
            detailVo.setConsentStatus(count > 0 ? "SIGNED" : "UNSIGNED");
        } catch (Exception e) {
            detailVo.setConsentStatus("UNSIGNED");
        }
    }

    /**
     * 聚合最新一次风险评估的 risk_level 到详情 VO
     * 用于侧边栏「风险等级」主行展示，避免显示"-"
     */
    private void fillRiskLevel(ChPatientDetailVo detailVo, Long patientId) {
        try {
            ChRiskAssessment latest = riskAssessmentMapper.selectOne(
                Wrappers.<ChRiskAssessment>lambdaQuery()
                    .eq(ChRiskAssessment::getPatientId, patientId)
                    .orderByDesc(ChRiskAssessment::getCreateTime)
                    .last("LIMIT 1")
            );
            if (latest != null) {
                detailVo.setRiskLevel(latest.getRiskLevel());
            }
        } catch (Exception e) { /* ignore */ }
    }

    /**
     * 批量回填每个患者最新一条结案申请信息（applyId/状态/类型/时间），
     * 用于列表行展示"待审核/已通过/已驳回"标签与按钮分流。
     */
    private void fillCloseApplyStatus(List<ChPatientProfileVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<Long> patientIds = list.stream()
            .map(ChPatientProfileVo::getPatientId)
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (patientIds.isEmpty()) return;
        try {
            // 一次拉取候选患者全部申请，按 create_time 倒序，内存中按 patientId 取首条（即最新）
            List<ChPatientCloseApply> applies = patientCloseApplyMapper.selectList(
                Wrappers.<ChPatientCloseApply>lambdaQuery()
                    .in(ChPatientCloseApply::getPatientId, patientIds)
                    .orderByDesc(ChPatientCloseApply::getCreateTime)
            );
            Map<Long, ChPatientCloseApply> latestMap = new java.util.HashMap<>();
            for (ChPatientCloseApply apply : applies) {
                latestMap.putIfAbsent(apply.getPatientId(), apply);
            }
            list.forEach(v -> {
                ChPatientCloseApply latest = latestMap.get(v.getPatientId());
                if (latest != null) {
                    v.setCloseApplyId(latest.getApplyId());
                    v.setCloseApplyStatus(latest.getAuditStatus());
                    v.setCloseType(latest.getCloseType());
                    v.setCloseApplyTime(latest.getCreateTime());
                }
            });
        } catch (Exception e) {
            // 兜底：不让"结案"非关键字段拖垮整个列表查询
        }
    }
}
