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
import org.dromara.chronic.domain.entity.ChPatientDisease;
import org.dromara.chronic.domain.entity.ChPatientProfile;
import org.dromara.chronic.domain.entity.ChPatientTag;
import org.dromara.chronic.domain.entity.ChPatientTimeline;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.domain.vo.ChPatientDiseaseVo;
import org.dromara.chronic.domain.vo.ChPatientProfileVo;
import org.dromara.chronic.domain.vo.ChPatientTimelineVo;
import org.dromara.chronic.mapper.ChConsentRecordMapper;
import org.dromara.chronic.mapper.ChPatientContractMapper;
import org.dromara.chronic.mapper.ChPatientDiseaseMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.mapper.ChPatientTagMapper;
import org.dromara.chronic.mapper.ChPatientTimelineMapper;
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
    private final ChPatientTimelineMapper patientTimelineMapper;
    private final ChPatientContractMapper patientContractMapper;
    private final ChConsentRecordMapper consentRecordMapper;
    private final DiseaseNameHelper diseaseNameHelper;

    @Override
    public List<ChPatientProfileVo> queryList(ChPatientProfileBo bo) {
        List<ChPatientProfileVo> result = baseMapper.selectVoList(buildQueryWrapper(bo));
        fillContractStatus(result);
        fillConsentStatus(result);
        return result;
    }

    public TableDataInfo<ChPatientProfileVo> queryPageList(ChPatientProfileBo bo, PageQuery pageQuery) {
        Page<ChPatientProfileVo> result = baseMapper.selectVoPage(pageQuery.build(), buildQueryWrapper(bo));
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
        detailVo.setTags(patientTagMapper.selectVoList(
            Wrappers.<ChPatientTag>lambdaQuery().eq(ChPatientTag::getPatientId, patientId)
        ));
        List<ChPatientTimelineVo> timelines = patientTimelineMapper.selectVoList(
            Wrappers.<ChPatientTimeline>lambdaQuery()
                .eq(ChPatientTimeline::getPatientId, patientId)
                .orderByDesc(ChPatientTimeline::getEventTime)
        );
        detailVo.setLatestTimeline(CollUtil.isEmpty(timelines) ? null : timelines.get(0));
        fillContractStatus(detailVo, patientId);
        fillConsentStatus(detailVo, patientId);
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
        String tagValue = ObjectUtil.defaultIfNull(params.get("tagValue"), "").toString();
        if (StringUtils.isAllBlank(diseaseCode, tagValue)) {
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
        if (StringUtils.isNotBlank(tagValue)) {
            patientIds.addAll(patientTagMapper.selectObjs(
                Wrappers.<ChPatientTag>lambdaQuery()
                    .select(ChPatientTag::getPatientId)
                    .eq(ChPatientTag::getTagValue, tagValue)
            ).stream().map(obj -> Long.valueOf(String.valueOf(obj))).toList());
        }
        return CollUtil.distinct(patientIds);
    }

    private void fillPatientDiseaseNames(List<ChPatientDiseaseVo> list) {
        if (CollUtil.isEmpty(list)) return;
        List<String> diseaseCodes = list.stream().map(ChPatientDiseaseVo::getDiseaseCode)
            .filter(StringUtils::isNotBlank).distinct().collect(Collectors.toList());
        if (!diseaseCodes.isEmpty()) {
            try {
                Map<String, String> diseaseNameMap = diseaseNameHelper.batchGetDiseaseName(diseaseCodes);
                list.forEach(v -> v.setDiseaseName(diseaseNameMap.get(v.getDiseaseCode())));
            } catch (Exception e) { /* ignore */ }
        }
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
}
