package org.dromara.chronic.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChContractServicePackageBo;
import org.dromara.chronic.domain.bo.ChPatientContractBo;
import org.dromara.chronic.domain.entity.ChConsentRecord;
import org.dromara.chronic.domain.entity.ChContractFulfillment;
import org.dromara.chronic.domain.entity.ChContractServicePackage;
import org.dromara.chronic.domain.entity.ChDoctorTeam;
import org.dromara.chronic.domain.entity.ChPatientContract;
import org.dromara.chronic.domain.vo.ChContractFulfillmentVo;
import org.dromara.chronic.domain.vo.ChContractServicePackageVo;
import org.dromara.chronic.domain.vo.ChPatientContractVo;
import org.dromara.chronic.mapper.ChConsentRecordMapper;
import org.dromara.chronic.mapper.ChContractFulfillmentMapper;
import org.dromara.chronic.mapper.ChContractServicePackageMapper;
import org.dromara.chronic.mapper.ChDoctorTeamMapper;
import org.dromara.chronic.mapper.ChPatientContractMapper;
import org.dromara.chronic.mapper.ChPatientProfileMapper;
import org.dromara.chronic.service.IChPatientContractService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 患者签约服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChPatientContractServiceImpl implements IChPatientContractService {

    private final ChPatientContractMapper contractMapper;
    private final ChDoctorTeamMapper teamMapper;
    private final ChContractServicePackageMapper packageMapper;
    private final ChContractFulfillmentMapper fulfillmentMapper;
    private final ChPatientProfileMapper profileMapper;
    private final ChConsentRecordMapper consentRecordMapper;

    @Override
    public TableDataInfo<ChPatientContractVo> queryContractPageList(ChPatientContractBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChPatientContract> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChPatientContract::getPatientId, bo.getPatientId());
        lqw.eq(ObjectUtil.isNotNull(bo.getTeamId()), ChPatientContract::getTeamId, bo.getTeamId());
        lqw.eq(ObjectUtil.isNotNull(bo.getPackageId()), ChPatientContract::getPackageId, bo.getPackageId());
        lqw.eq(StringUtils.isNotBlank(bo.getRenewalStatus()), ChPatientContract::getRenewalStatus, bo.getRenewalStatus());
        lqw.eq(StringUtils.isNotBlank(bo.getContractType()), ChPatientContract::getContractType, bo.getContractType());
        lqw.orderByDesc(ChPatientContract::getCreateTime);
        Page<ChPatientContractVo> page = contractMapper.selectVoPage(pageQuery.build(), lqw);
        page.getRecords().forEach(this::refreshContractState);
        fillContractNames(page.getRecords());
        return TableDataInfo.build(page);
    }

    @Override
    public TableDataInfo<ChContractServicePackageVo> queryPackagePageList(ChContractServicePackageBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChContractServicePackage> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getPackageName()), ChContractServicePackage::getPackageName, bo.getPackageName());
        lqw.eq(StringUtils.isNotBlank(bo.getPackageType()), ChContractServicePackage::getPackageType, bo.getPackageType());
        lqw.eq(ObjectUtil.isNotNull(bo.getIsActive()), ChContractServicePackage::getIsActive, bo.getIsActive());
        lqw.orderByAsc(ChContractServicePackage::getPackageId);
        Page<ChContractServicePackageVo> page = packageMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public Boolean createPackage(ChContractServicePackageBo bo) {
        ChContractServicePackage entity = MapstructUtils.convert(bo, ChContractServicePackage.class);
        if (entity.getIsActive() == null) {
            entity.setIsActive(Boolean.TRUE);
        }
        // 确保 serviceItems 为有效的 JSON 数组格式
        entity.setServiceItems(resolveServiceItemsForStorage(bo.getServiceItems()));
        return packageMapper.insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long signContract(ChPatientContractBo bo) {
        if (bo.getContractPeriodEnd().before(bo.getContractPeriodStart())) {
            throw new ServiceException("签约结束时间不能早于开始时间");
        }
        if (ObjectUtil.isNotNull(bo.getPatientId())) {
            long consentCount = consentRecordMapper.selectCount(
                Wrappers.<ChConsentRecord>lambdaQuery()
                    .eq(ChConsentRecord::getPatientId, bo.getPatientId())
                    .eq(ChConsentRecord::getConsentType, "SIGN_CONTRACT")
            );
            if (consentCount == 0) {
                throw new ServiceException("患者未签署知情同意书，请先完成知情同意签署");
            }
        }
        ChContractServicePackage servicePackage = packageMapper.selectById(bo.getPackageId());
        if (servicePackage == null) {
            throw new ServiceException("签约服务包不存在");
        }
        ChPatientContract entity = MapstructUtils.convert(bo, ChPatientContract.class);
        entity.setContractStatus("ACTIVE");
        entity.setRenewalStatus(resolveRenewalStatus(bo.getContractPeriodEnd()));
        entity.setExpiryRemindStatus(isExpiring(bo.getContractPeriodEnd()));
        contractMapper.insert(entity);
        createFulfillmentPlan(entity.getContractId(), servicePackage.getServiceItems(), bo.getContractPeriodStart(), bo.getContractPeriodEnd());
        return entity.getContractId();
    }

    @Override
    public ChPatientContractVo queryCurrentContract(Long patientId) {
        LambdaQueryWrapper<ChPatientContract> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChPatientContract::getPatientId, patientId);
        lqw.eq(ChPatientContract::getContractStatus, "ACTIVE");
        lqw.orderByDesc(ChPatientContract::getContractPeriodStart);
        lqw.last("LIMIT 1");
        ChPatientContractVo vo = contractMapper.selectVoOne(lqw);
        if (vo != null) {
            refreshContractState(vo);
            fillContractName(vo);
        }
        return vo;
    }

    @Override
    public ChPatientContractVo queryById(Long contractId) {
        ChPatientContractVo vo = contractMapper.selectVoById(contractId);
        if (vo != null) {
            refreshContractState(vo);
            fillContractName(vo);
        }
        return vo;
    }

    @Override
    public Boolean updateLastRemindTime(Long contractId) {
        ChPatientContract update = new ChPatientContract();
        update.setContractId(contractId);
        update.setLastRemindTime(new Date());
        return contractMapper.updateById(update) > 0;
    }

    @Override
    public List<ChContractFulfillmentVo> queryFulfillmentList(Long contractId) {
        List<ChContractFulfillmentVo> list = fulfillmentMapper.selectVoList(
            Wrappers.<ChContractFulfillment>lambdaQuery()
                .eq(ChContractFulfillment::getContractId, contractId)
                .orderByAsc(ChContractFulfillment::getPlanDate)
        );
        Date now = new Date();
        list.forEach(item -> {
            if ("PLANNED".equals(item.getFulfillmentStatus()) && item.getPlanDate() != null && item.getPlanDate().before(now)) {
                item.setSlaViolation(Boolean.TRUE);
            }
        });
        return list;
    }

    private void refreshContractState(ChPatientContractVo vo) {
        vo.setRenewalStatus(resolveRenewalStatus(vo.getContractPeriodEnd()));
        vo.setExpiryRemindStatus(isExpiring(vo.getContractPeriodEnd()));
    }

    private String resolveRenewalStatus(Date endDate) {
        if (endDate == null) {
            return "ACTIVE";
        }
        Date now = new Date();
        if (endDate.before(now)) {
            return "EXPIRED";
        }
        return isExpiring(endDate) ? "EXPIRING" : "ACTIVE";
    }

    private boolean isExpiring(Date endDate) {
        if (endDate == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 30);
        return !endDate.before(new Date()) && !endDate.after(calendar.getTime());
    }

    private void createFulfillmentPlan(Long contractId, String serviceItems, Date startDate, Date endDate) {
        List<String> items = resolveServiceItems(serviceItems);
        if (items.isEmpty()) {
            items = List.of("签约服务");
        }
        Calendar cursor = Calendar.getInstance();
        cursor.setTime(startDate == null ? new Date() : startDate);
        int interval = Math.max(1, items.size());
        List<ChContractFulfillment> fulfillments = new ArrayList<>();
        for (String item : items) {
            ChContractFulfillment fulfillment = new ChContractFulfillment();
            fulfillment.setContractId(contractId);
            fulfillment.setServiceItem(item);
            fulfillment.setPlanDate(cursor.getTime());
            fulfillment.setFulfillmentStatus("PLANNED");
            fulfillment.setSlaViolation(Boolean.FALSE);
            fulfillments.add(fulfillment);
            cursor.add(Calendar.MONTH, Math.max(1, 12 / interval));
            if (endDate != null && cursor.getTime().after(endDate)) {
                cursor.setTime(endDate);
            }
        }
        fulfillmentMapper.insertBatch(fulfillments);
    }

    private List<String> resolveServiceItems(String serviceItems) {
        if (StringUtils.isBlank(serviceItems)) {
            return new ArrayList<>();
        }
        if (serviceItems.trim().startsWith("[")) {
            try {
                List<Dict> dictList = JsonUtils.parseObject(serviceItems, new TypeReference<List<Dict>>() {
                });
                if (dictList != null && !dictList.isEmpty()) {
                    List<String> items = new ArrayList<>();
                    for (Dict dict : dictList) {
                        Object itemName = ObjectUtil.defaultIfNull(dict.get("name"), dict.get("serviceItem"));
                        if (itemName != null) {
                            items.add(String.valueOf(itemName));
                        }
                    }
                    if (!items.isEmpty()) {
                        return items;
                    }
                }
            } catch (RuntimeException ignored) {
                // 回退到逗号切分
            }
        }
        return new ArrayList<>(Arrays.asList(StringUtils.split(serviceItems, ",")));
    }

    /**
     * 确保服务项目以有效 JSON 数组格式存储
     * 
     * @param serviceItems 输入的服务项目（可能是逗号分隔文本或 JSON 数组）
     * @return 有效 JSON 数组字符串，如 ["体检","咨询"]
     */
    private String resolveServiceItemsForStorage(String serviceItems) {
        if (StringUtils.isBlank(serviceItems)) {
            return "[]";
        }
        String trimmed = serviceItems.trim();
        // 已经是有效的 JSON 数组，直接返回
        if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
            try {
                JsonUtils.parseObject(trimmed, Object.class);
                return trimmed;
            } catch (Exception e) {
                // JSON 格式无效，转为逗号切分处理
            }
        }
        // 逗号分隔或其他格式，转为 JSON 数组
        List<String> items = new ArrayList<>();
        for (String part : StringUtils.split(serviceItems, ",")) {
            String item = part.trim();
            if (StringUtils.isNotBlank(item)) {
                items.add(item);
            }
        }
        return "[" + items.stream().map(s -> "\"" + s + "\"").collect(Collectors.joining(",")) + "]";
    }

    /**
     * 批量回填签约VO的团队名称和服务包名称
     */
    private void fillContractNames(List<ChPatientContractVo> list) {
        if (CollUtil.isEmpty(list)) {
            return;
        }
        // patientName
        List<Long> patientIds = list.stream().map(ChPatientContractVo::getPatientId)
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (!patientIds.isEmpty()) {
            try {
                profileMapper.selectBatchIds(patientIds).forEach(p ->
                    list.stream().filter(v -> p.getPatientId().equals(v.getPatientId()))
                        .forEach(v -> v.setPatientName(p.getName())));
            } catch (Exception e) {
                // 查询失败不影响主流程
            }
        }
        // teamName
        List<Long> teamIds = list.stream().map(ChPatientContractVo::getTeamId)
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (!teamIds.isEmpty()) {
            try {
                teamMapper.selectBatchIds(teamIds).forEach(t ->
                    list.stream().filter(v -> t.getTeamId().equals(v.getTeamId()))
                        .forEach(v -> v.setTeamName(t.getTeamName())));
            } catch (Exception e) {
                // 查询失败不影响主流程
            }
        }
        // packageName
        List<Long> packageIds = list.stream().map(ChPatientContractVo::getPackageId)
            .filter(ObjectUtil::isNotNull).distinct().collect(Collectors.toList());
        if (!packageIds.isEmpty()) {
            try {
                packageMapper.selectBatchIds(packageIds).forEach(p ->
                    list.stream().filter(v -> p.getPackageId().equals(v.getPackageId()))
                        .forEach(v -> v.setPackageName(p.getPackageName())));
            } catch (Exception e) {
                // 查询失败不影响主流程
            }
        }
    }

    /**
     * 单条签约VO回填团队名称和服务包名称
     */
    private void fillContractName(ChPatientContractVo vo) {
        if (vo == null) {
            return;
        }
        fillContractNames(List.of(vo));
    }

    @Override
    public TableDataInfo<ChContractFulfillmentVo> queryFulfillmentPage(Long contractId, PageQuery pageQuery) {
        LambdaQueryWrapper<ChContractFulfillment> lqw = Wrappers.lambdaQuery();
        lqw.eq(ChContractFulfillment::getContractId, contractId);
        lqw.orderByAsc(ChContractFulfillment::getPlanDate);
        Page<ChContractFulfillmentVo> page = fulfillmentMapper.selectVoPage(pageQuery.build(), lqw);
        Date now = new Date();
        page.getRecords().forEach(item -> {
            if ("PLANNED".equals(item.getFulfillmentStatus()) && item.getPlanDate() != null && item.getPlanDate().before(now)) {
                item.setSlaViolation(Boolean.TRUE);
            }
        });
        return TableDataInfo.build(page);
    }
}
