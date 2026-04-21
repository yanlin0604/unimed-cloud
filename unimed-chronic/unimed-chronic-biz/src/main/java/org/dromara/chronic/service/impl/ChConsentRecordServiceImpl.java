package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChConsentRecordBo;
import org.dromara.chronic.domain.entity.ChConsentRecord;
import org.dromara.chronic.domain.vo.ChConsentRecordVo;
import org.dromara.chronic.mapper.ChConsentRecordMapper;
import org.dromara.chronic.service.IChConsentRecordService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 知情同意记录服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChConsentRecordServiceImpl implements IChConsentRecordService {

    private static final Set<String> VALID_CONSENT_TYPES = Set.of("SIGN_CONTRACT", "DATA_SHARE", "REFERRAL");

    private final ChConsentRecordMapper consentMapper;

    @Override
    public Long insertByBo(ChConsentRecordBo bo) {
        if (!VALID_CONSENT_TYPES.contains(bo.getConsentType())) {
            throw new ServiceException("无效的同意类型: " + bo.getConsentType());
        }
        ChConsentRecord entity = MapstructUtils.convert(bo, ChConsentRecord.class);
        if (entity.getSignTime() == null) {
            entity.setSignTime(new Date());
        }
        consentMapper.insert(entity);
        return entity.getConsentId();
    }

    @Override
    public Boolean updateByBo(ChConsentRecordBo bo) {
        ChConsentRecord entity = consentMapper.selectById(bo.getConsentId());
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("知情同意记录不存在");
        }
        if (StringUtils.isNotBlank(bo.getConsentType()) && !VALID_CONSENT_TYPES.contains(bo.getConsentType())) {
            throw new ServiceException("无效的同意类型: " + bo.getConsentType());
        }
        ChConsentRecord update = MapstructUtils.convert(bo, ChConsentRecord.class);
        consentMapper.updateById(update);
        return true;
    }

    @Override
    public ChConsentRecordVo queryById(Long consentId) {
        return consentMapper.selectVoById(consentId);
    }

    @Override
    public TableDataInfo<ChConsentRecordVo> queryPageList(ChConsentRecordBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChConsentRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChConsentRecord::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getConsentType()), ChConsentRecord::getConsentType, bo.getConsentType());
        lqw.orderByDesc(ChConsentRecord::getSignTime);
        Page<ChConsentRecordVo> page = consentMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChConsentRecordVo> queryByPatientId(Long patientId) {
        return consentMapper.selectVoList(
            Wrappers.<ChConsentRecord>lambdaQuery()
                .eq(ChConsentRecord::getPatientId, patientId)
                .orderByDesc(ChConsentRecord::getSignTime)
        );
    }

    @Override
    public Boolean deleteById(Long consentId) {
        ChConsentRecord entity = consentMapper.selectById(consentId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("知情同意记录不存在");
        }
        consentMapper.deleteById(consentId);
        return true;
    }
}
