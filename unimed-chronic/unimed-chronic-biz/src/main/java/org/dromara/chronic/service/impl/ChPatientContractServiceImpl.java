package org.dromara.chronic.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChContractServicePackageBo;
import org.dromara.chronic.domain.bo.ChPatientContractBo;
import org.dromara.chronic.domain.entity.ChContractFulfillment;
import org.dromara.chronic.domain.entity.ChContractServicePackage;
import org.dromara.chronic.domain.entity.ChPatientContract;
import org.dromara.chronic.domain.vo.ChContractFulfillmentVo;
import org.dromara.chronic.domain.vo.ChContractServicePackageVo;
import org.dromara.chronic.domain.vo.ChPatientContractVo;
import org.dromara.chronic.mapper.ChContractFulfillmentMapper;
import org.dromara.chronic.mapper.ChContractServicePackageMapper;
import org.dromara.chronic.mapper.ChPatientContractMapper;
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

/**
 * 患者签约服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChPatientContractServiceImpl implements IChPatientContractService {

    private final ChPatientContractMapper contractMapper;
    private final ChContractServicePackageMapper packageMapper;
    private final ChContractFulfillmentMapper fulfillmentMapper;

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
        return packageMapper.insert(entity) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long signContract(ChPatientContractBo bo) {
        if (bo.getContractPeriodEnd().before(bo.getContractPeriodStart())) {
            throw new ServiceException("签约结束时间不能早于开始时间");
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
        }
        return vo;
    }

    @Override
    public ChPatientContractVo queryById(Long contractId) {
        ChPatientContractVo vo = contractMapper.selectVoById(contractId);
        if (vo != null) {
            refreshContractState(vo);
        }
        return vo;
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
}
