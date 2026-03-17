package org.dromara.dhcore.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.*;
import org.dromara.dhcore.domain.bo.*;
import org.dromara.dhcore.domain.vo.*;
import org.dromara.dhcore.mapper.*;
import org.dromara.dhcore.service.IDhConfigService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 数字人口播配置中心服务实�? */
@RequiredArgsConstructor
@Service
public class DhConfigServiceImpl implements IDhConfigService {

    private final DhMemberConfigMapper memberConfigMapper;
    private final DhPaymentPriceConfigMapper paymentPriceConfigMapper;
    private final DhVideoUploadConfigMapper videoUploadConfigMapper;
    private final DhQrUploadConfigMapper qrUploadConfigMapper;
    private final DhSensitiveWordMapper sensitiveWordMapper;
    private final DhNotifyTemplateMapper notifyTemplateMapper;

    @Override
    public TableDataInfo<DhMemberConfigVo> queryMemberConfigPage(DhMemberConfigQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhMemberConfig> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(bo.getKeyword())) {
            lqw.and(wrapper -> wrapper.like(DhMemberConfig::getLevelName, bo.getKeyword()).or().like(DhMemberConfig::getRemark, bo.getKeyword()));
        }
        lqw.eq(StringUtils.isNotBlank(bo.getLevel()), DhMemberConfig::getLevel, bo.getLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhMemberConfig::getStatus, bo.getStatus());
        lqw.ge(StringUtils.isNotBlank(bo.getBeginTime()), DhMemberConfig::getCreateTime, parseDateTime(bo.getBeginTime()));
        lqw.le(StringUtils.isNotBlank(bo.getEndTime()), DhMemberConfig::getCreateTime, parseDateTime(bo.getEndTime()));
        lqw.orderByDesc(DhMemberConfig::getUpdateTime);
        Page<DhMemberConfig> page = memberConfigMapper.selectPage(pageQuery.build(), lqw);
        List<DhMemberConfigVo> rows = page.getRecords().stream().map(this::toMemberConfigVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhMemberConfigVo saveMemberConfig(DhMemberConfigBo bo) {
        checkMemberLevelUnique(bo.getId(), bo.getLevel());
        DhMemberConfig entity = bo.getId() == null ? new DhMemberConfig() : requireMemberConfig(bo.getId());
        entity.setLevel(bo.getLevel());
        entity.setLevelName(bo.getLevelName());
        entity.setOrderPrice(bo.getOrderPrice());
        entity.setMonthlyLimit(bo.getMonthlyLimit());
        entity.setSpeedPriority(bo.getSpeedPriority());
        entity.setMinTopupAmount(bo.getMinTopupAmount());
        entity.setValidityDays(bo.getValidityDays());
        entity.setExpectDeliveryHours(bo.getExpectDeliveryHours());
        entity.setRedoLimit(bo.getRedoLimit());
        entity.setStatus(bo.getStatus());
        entity.setRemark(bo.getRemark());
        if (bo.getId() == null) {
            memberConfigMapper.insert(entity);
        } else {
            memberConfigMapper.updateById(entity);
        }
        return toMemberConfigVo(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteMemberConfig(Long id) {
        requireMemberConfig(id);
        memberConfigMapper.deleteById(id);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhMemberConfigVo changeMemberConfigStatus(DhConfigStatusBo bo) {
        DhMemberConfig entity = requireMemberConfig(bo.getId());
        assertStatusChanged(entity.getStatus(), bo.getStatus());
        entity.setStatus(bo.getStatus());
        memberConfigMapper.updateById(entity);
        return toMemberConfigVo(entity);
    }

    @Override
    public TableDataInfo<DhPaymentPriceConfigVo> queryPaymentPricePage(DhPaymentPriceQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhPaymentPriceConfig> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getKeyword()), DhPaymentPriceConfig::getConfigName, bo.getKeyword());
        lqw.eq(StringUtils.isNotBlank(bo.getMemberLevel()), DhPaymentPriceConfig::getMemberLevel, bo.getMemberLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getPayType()), DhPaymentPriceConfig::getPayType, bo.getPayType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhPaymentPriceConfig::getStatus, bo.getStatus());
        lqw.ge(StringUtils.isNotBlank(bo.getBeginTime()), DhPaymentPriceConfig::getCreateTime, parseDateTime(bo.getBeginTime()));
        lqw.le(StringUtils.isNotBlank(bo.getEndTime()), DhPaymentPriceConfig::getCreateTime, parseDateTime(bo.getEndTime()));
        lqw.orderByAsc(DhPaymentPriceConfig::getSort).orderByDesc(DhPaymentPriceConfig::getUpdateTime);
        Page<DhPaymentPriceConfig> page = paymentPriceConfigMapper.selectPage(pageQuery.build(), lqw);
        List<DhPaymentPriceConfigVo> rows = page.getRecords().stream().map(this::toPaymentPriceConfigVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhPaymentPriceConfigVo savePaymentPriceConfig(DhPaymentPriceConfigBo bo) {
        DhPaymentPriceConfig entity = bo.getId() == null ? new DhPaymentPriceConfig() : requirePaymentPriceConfig(bo.getId());
        entity.setConfigName(bo.getConfigName());
        entity.setMemberLevel(bo.getMemberLevel());
        entity.setPayType(bo.getPayType());
        entity.setAmount(bo.getAmount());
        entity.setBonusAmount(bo.getBonusAmount());
        entity.setSort(bo.getSort());
        entity.setStatus(bo.getStatus());
        entity.setRemark(bo.getRemark());
        if (bo.getId() == null) {
            paymentPriceConfigMapper.insert(entity);
        } else {
            paymentPriceConfigMapper.updateById(entity);
        }
        return toPaymentPriceConfigVo(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deletePaymentPriceConfig(Long id) {
        requirePaymentPriceConfig(id);
        paymentPriceConfigMapper.deleteById(id);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhPaymentPriceConfigVo changePaymentPriceConfigStatus(DhConfigStatusBo bo) {
        DhPaymentPriceConfig entity = requirePaymentPriceConfig(bo.getId());
        assertStatusChanged(entity.getStatus(), bo.getStatus());
        entity.setStatus(bo.getStatus());
        paymentPriceConfigMapper.updateById(entity);
        return toPaymentPriceConfigVo(entity);
    }

    @Override
    public TableDataInfo<DhVideoUploadConfigVo> queryVideoUploadConfigPage(DhVideoUploadConfigQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhVideoUploadConfig> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(bo.getKeyword())) {
            lqw.and(wrapper -> wrapper.like(DhVideoUploadConfig::getConfigName, bo.getKeyword()).or().like(DhVideoUploadConfig::getFormatDesc, bo.getKeyword()));
        }
        lqw.eq(StringUtils.isNotBlank(bo.getType()), DhVideoUploadConfig::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhVideoUploadConfig::getStatus, bo.getStatus());
        lqw.ge(StringUtils.isNotBlank(bo.getBeginTime()), DhVideoUploadConfig::getCreateTime, parseDateTime(bo.getBeginTime()));
        lqw.le(StringUtils.isNotBlank(bo.getEndTime()), DhVideoUploadConfig::getCreateTime, parseDateTime(bo.getEndTime()));
        lqw.orderByDesc(DhVideoUploadConfig::getUpdateTime);
        Page<DhVideoUploadConfig> page = videoUploadConfigMapper.selectPage(pageQuery.build(), lqw);
        List<DhVideoUploadConfigVo> rows = page.getRecords().stream().map(this::toVideoUploadConfigVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhVideoUploadConfigVo saveVideoUploadConfig(DhVideoUploadConfigBo bo) {
        DhVideoUploadConfig entity = bo.getId() == null ? new DhVideoUploadConfig() : requireVideoUploadConfig(bo.getId());
        entity.setConfigName(bo.getConfigName());
        entity.setType(bo.getType());
        entity.setVideoFileIds(bo.getVideoFileIds());
        entity.setMaxSizeMb(bo.getMaxSizeMb());
        entity.setFormatDesc(bo.getFormatDesc());
        entity.setStatus(bo.getStatus());
        entity.setRemark(bo.getRemark());
        if (bo.getId() == null) {
            videoUploadConfigMapper.insert(entity);
        } else {
            videoUploadConfigMapper.updateById(entity);
        }
        return toVideoUploadConfigVo(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteVideoUploadConfig(Long id) {
        requireVideoUploadConfig(id);
        videoUploadConfigMapper.deleteById(id);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhVideoUploadConfigVo changeVideoUploadConfigStatus(DhConfigStatusBo bo) {
        DhVideoUploadConfig entity = requireVideoUploadConfig(bo.getId());
        assertStatusChanged(entity.getStatus(), bo.getStatus());
        entity.setStatus(bo.getStatus());
        videoUploadConfigMapper.updateById(entity);
        return toVideoUploadConfigVo(entity);
    }

    @Override
    public TableDataInfo<DhQrUploadConfigVo> queryQrUploadConfigPage(DhQrUploadConfigQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhQrUploadConfig> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(bo.getKeyword())) {
            lqw.and(wrapper ->
                wrapper.like(DhQrUploadConfig::getConfigName, bo.getKeyword())
                    .or().like(DhQrUploadConfig::getAccountNo, bo.getKeyword())
                    .or().like(DhQrUploadConfig::getAccountName, bo.getKeyword())
            );
        }
        lqw.eq(StringUtils.isNotBlank(bo.getType()), DhQrUploadConfig::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhQrUploadConfig::getStatus, bo.getStatus());
        lqw.ge(StringUtils.isNotBlank(bo.getBeginTime()), DhQrUploadConfig::getCreateTime, parseDateTime(bo.getBeginTime()));
        lqw.le(StringUtils.isNotBlank(bo.getEndTime()), DhQrUploadConfig::getCreateTime, parseDateTime(bo.getEndTime()));
        lqw.orderByDesc(DhQrUploadConfig::getUpdateTime);
        Page<DhQrUploadConfig> page = qrUploadConfigMapper.selectPage(pageQuery.build(), lqw);
        List<DhQrUploadConfigVo> rows = page.getRecords().stream().map(this::toQrUploadConfigVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhQrUploadConfigVo saveQrUploadConfig(DhQrUploadConfigBo bo) {
        checkQrAccountUnique(bo.getId(), bo.getType(), bo.getAccountNo());
        DhQrUploadConfig entity = bo.getId() == null ? new DhQrUploadConfig() : requireQrUploadConfig(bo.getId());
        entity.setConfigName(bo.getConfigName());
        entity.setType(bo.getType());
        entity.setQrImageIds(bo.getQrImageIds());
        entity.setAccountName(bo.getAccountName());
        entity.setAccountNo(bo.getAccountNo());
        entity.setBankName(bo.getBankName());
        entity.setStatus(bo.getStatus());
        entity.setRemark(bo.getRemark());
        if (bo.getId() == null) {
            qrUploadConfigMapper.insert(entity);
        } else {
            qrUploadConfigMapper.updateById(entity);
        }
        return toQrUploadConfigVo(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteQrUploadConfig(Long id) {
        requireQrUploadConfig(id);
        qrUploadConfigMapper.deleteById(id);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhQrUploadConfigVo changeQrUploadConfigStatus(DhConfigStatusBo bo) {
        DhQrUploadConfig entity = requireQrUploadConfig(bo.getId());
        assertStatusChanged(entity.getStatus(), bo.getStatus());
        entity.setStatus(bo.getStatus());
        qrUploadConfigMapper.updateById(entity);
        return toQrUploadConfigVo(entity);
    }

    @Override
    public TableDataInfo<DhSensitiveWordVo> querySensitiveWordPage(DhSensitiveWordQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhSensitiveWord> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getKeyword()), DhSensitiveWord::getWord, bo.getKeyword());
        lqw.eq(StringUtils.isNotBlank(bo.getLevel()), DhSensitiveWord::getLevel, bo.getLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getCategory()), DhSensitiveWord::getCategory, bo.getCategory());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhSensitiveWord::getStatus, bo.getStatus());
        lqw.orderByDesc(DhSensitiveWord::getUpdateTime);
        Page<DhSensitiveWord> page = sensitiveWordMapper.selectPage(pageQuery.build(), lqw);
        List<DhSensitiveWordVo> rows = page.getRecords().stream().map(this::toSensitiveWordVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhSensitiveWordVo saveSensitiveWord(DhSensitiveWordBo bo) {
        checkSensitiveWordUnique(bo.getId(), bo.getWord());
        DhSensitiveWord entity = bo.getId() == null ? new DhSensitiveWord() : requireSensitiveWord(bo.getId());
        entity.setWord(bo.getWord());
        entity.setLevel(bo.getLevel());
        entity.setCategory(bo.getCategory());
        entity.setStatus(bo.getStatus());
        if (bo.getId() == null) {
            sensitiveWordMapper.insert(entity);
        } else {
            sensitiveWordMapper.updateById(entity);
        }
        return toSensitiveWordVo(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteSensitiveWord(Long id) {
        requireSensitiveWord(id);
        sensitiveWordMapper.deleteById(id);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhSensitiveWordVo changeSensitiveWordStatus(DhConfigStatusBo bo) {
        DhSensitiveWord entity = requireSensitiveWord(bo.getId());
        assertStatusChanged(entity.getStatus(), bo.getStatus());
        entity.setStatus(bo.getStatus());
        sensitiveWordMapper.updateById(entity);
        return toSensitiveWordVo(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Integer> batchImportSensitiveWords(DhSensitiveWordImportBo bo) {
        int imported = 0;
        int duplicated = 0;
        for (String word : bo.getWords()) {
            if (StringUtils.isBlank(word)) {
                continue;
            }
            String trimWord = word.trim();
            DhSensitiveWord exists = sensitiveWordMapper.selectOne(
                Wrappers.<DhSensitiveWord>lambdaQuery().eq(DhSensitiveWord::getWord, trimWord)
            );
            if (exists != null) {
                duplicated++;
                continue;
            }
            DhSensitiveWord entity = new DhSensitiveWord();
            entity.setWord(trimWord);
            entity.setLevel("WARNING");
            entity.setCategory("OTHER");
            entity.setStatus("0");
            sensitiveWordMapper.insert(entity);
            imported++;
        }
        Map<String, Integer> result = new HashMap<>(2);
        result.put("imported", imported);
        result.put("duplicated", duplicated);
        return result;
    }

    @Override
    public TableDataInfo<DhNotifyTemplateVo> queryNotifyTemplatePage(DhNotifyTemplateQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhNotifyTemplate> lqw = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(bo.getKeyword())) {
            lqw.and(wrapper ->
                wrapper.like(DhNotifyTemplate::getTemplateName, bo.getKeyword())
                    .or().like(DhNotifyTemplate::getContent, bo.getKeyword())
            );
        }
        lqw.eq(StringUtils.isNotBlank(bo.getScene()), DhNotifyTemplate::getScene, bo.getScene());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhNotifyTemplate::getStatus, bo.getStatus());
        lqw.orderByDesc(DhNotifyTemplate::getUpdateTime);
        Page<DhNotifyTemplate> page = notifyTemplateMapper.selectPage(pageQuery.build(), lqw);
        List<DhNotifyTemplateVo> rows = page.getRecords().stream().map(this::toNotifyTemplateVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhNotifyTemplateVo saveNotifyTemplate(DhNotifyTemplateBo bo) {
        DhNotifyTemplate entity = bo.getId() == null ? new DhNotifyTemplate() : requireNotifyTemplate(bo.getId());
        entity.setTemplateName(bo.getTemplateName());
        entity.setScene(bo.getScene());
        entity.setChannel(bo.getChannel());
        entity.setContent(bo.getContent());
        entity.setTimeoutHours(bo.getTimeoutHours());
        entity.setStatus(bo.getStatus());
        if (bo.getId() == null) {
            notifyTemplateMapper.insert(entity);
        } else {
            notifyTemplateMapper.updateById(entity);
        }
        return toNotifyTemplateVo(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhNotifyTemplateVo changeNotifyTemplateStatus(DhConfigStatusBo bo) {
        DhNotifyTemplate entity = requireNotifyTemplate(bo.getId());
        assertStatusChanged(entity.getStatus(), bo.getStatus());
        entity.setStatus(bo.getStatus());
        notifyTemplateMapper.updateById(entity);
        return toNotifyTemplateVo(entity);
    }

    private void checkMemberLevelUnique(Long id, String level) {
        DhMemberConfig exists = memberConfigMapper.selectOne(
            Wrappers.<DhMemberConfig>lambdaQuery()
                .eq(DhMemberConfig::getLevel, level)
                .ne(id != null, DhMemberConfig::getConfigId, id)
        );
        if (exists != null) {
            throw new ServiceException("该会员等级配置已存在");
        }
    }

    private void checkQrAccountUnique(Long id, String type, String accountNo) {
        DhQrUploadConfig exists = qrUploadConfigMapper.selectOne(
            Wrappers.<DhQrUploadConfig>lambdaQuery()
                .eq(DhQrUploadConfig::getType, type)
                .eq(DhQrUploadConfig::getAccountNo, accountNo)
                .ne(id != null, DhQrUploadConfig::getConfigId, id)
        );
        if (exists != null) {
            throw new ServiceException("相同类型与收款账号的二维码配置已存在");
        }
    }

    private void checkSensitiveWordUnique(Long id, String word) {
        DhSensitiveWord exists = sensitiveWordMapper.selectOne(
            Wrappers.<DhSensitiveWord>lambdaQuery()
                .eq(DhSensitiveWord::getWord, word)
                .ne(id != null, DhSensitiveWord::getWordId, id)
        );
        if (exists != null) {
            throw new ServiceException("该敏感词已存�?);
        }
    }

    private void assertStatusChanged(String current, String next) {
        if (StringUtils.equals(current, next)) {
            throw new ServiceException("当前已是目标状�?);
        }
    }

    private Date parseDateTime(String dateTime) {
        if (StringUtils.isBlank(dateTime)) {
            return null;
        }
        try {
            return DateUtil.parseDateTime(dateTime);
        } catch (Exception ex) {
            throw new ServiceException("时间参数格式错误");
        }
    }

    private DhMemberConfig requireMemberConfig(Long id) {
        DhMemberConfig entity = memberConfigMapper.selectById(id);
        if (entity == null) {
            throw new ServiceException("会员配置不存�?);
        }
        return entity;
    }

    private DhPaymentPriceConfig requirePaymentPriceConfig(Long id) {
        DhPaymentPriceConfig entity = paymentPriceConfigMapper.selectById(id);
        if (entity == null) {
            throw new ServiceException("充值档位配置不存在");
        }
        return entity;
    }

    private DhVideoUploadConfig requireVideoUploadConfig(Long id) {
        DhVideoUploadConfig entity = videoUploadConfigMapper.selectById(id);
        if (entity == null) {
            throw new ServiceException("视频上传配置不存�?);
        }
        return entity;
    }

    private DhQrUploadConfig requireQrUploadConfig(Long id) {
        DhQrUploadConfig entity = qrUploadConfigMapper.selectById(id);
        if (entity == null) {
            throw new ServiceException("二维码上传配置不存在");
        }
        return entity;
    }

    private DhSensitiveWord requireSensitiveWord(Long id) {
        DhSensitiveWord entity = sensitiveWordMapper.selectById(id);
        if (entity == null) {
            throw new ServiceException("敏感词不存在");
        }
        return entity;
    }

    private DhNotifyTemplate requireNotifyTemplate(Long id) {
        DhNotifyTemplate entity = notifyTemplateMapper.selectById(id);
        if (entity == null) {
            throw new ServiceException("通知模板不存�?);
        }
        return entity;
    }

    private DhMemberConfigVo toMemberConfigVo(DhMemberConfig entity) {
        DhMemberConfigVo vo = new DhMemberConfigVo();
        vo.setId(entity.getConfigId());
        vo.setLevel(entity.getLevel());
        vo.setLevelName(entity.getLevelName());
        vo.setOrderPrice(entity.getOrderPrice());
        vo.setMonthlyLimit(entity.getMonthlyLimit());
        vo.setSpeedPriority(entity.getSpeedPriority());
        vo.setMinTopupAmount(entity.getMinTopupAmount());
        vo.setValidityDays(entity.getValidityDays());
        vo.setExpectDeliveryHours(entity.getExpectDeliveryHours());
        vo.setRedoLimit(entity.getRedoLimit());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    private DhPaymentPriceConfigVo toPaymentPriceConfigVo(DhPaymentPriceConfig entity) {
        DhPaymentPriceConfigVo vo = new DhPaymentPriceConfigVo();
        vo.setId(entity.getConfigId());
        vo.setConfigName(entity.getConfigName());
        vo.setMemberLevel(entity.getMemberLevel());
        vo.setPayType(entity.getPayType());
        vo.setAmount(entity.getAmount());
        vo.setBonusAmount(entity.getBonusAmount());
        vo.setSort(entity.getSort());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    private DhVideoUploadConfigVo toVideoUploadConfigVo(DhVideoUploadConfig entity) {
        DhVideoUploadConfigVo vo = new DhVideoUploadConfigVo();
        vo.setId(entity.getConfigId());
        vo.setConfigName(entity.getConfigName());
        vo.setType(entity.getType());
        vo.setVideoFileIds(entity.getVideoFileIds());
        vo.setMaxSizeMb(entity.getMaxSizeMb());
        vo.setFormatDesc(entity.getFormatDesc());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    private DhQrUploadConfigVo toQrUploadConfigVo(DhQrUploadConfig entity) {
        DhQrUploadConfigVo vo = new DhQrUploadConfigVo();
        vo.setId(entity.getConfigId());
        vo.setConfigName(entity.getConfigName());
        vo.setType(entity.getType());
        vo.setQrImageIds(entity.getQrImageIds());
        vo.setAccountName(entity.getAccountName());
        vo.setAccountNo(entity.getAccountNo());
        vo.setBankName(entity.getBankName());
        vo.setStatus(entity.getStatus());
        vo.setRemark(entity.getRemark());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    private DhSensitiveWordVo toSensitiveWordVo(DhSensitiveWord entity) {
        DhSensitiveWordVo vo = new DhSensitiveWordVo();
        vo.setId(entity.getWordId());
        vo.setWord(entity.getWord());
        vo.setLevel(entity.getLevel());
        vo.setCategory(entity.getCategory());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    private DhNotifyTemplateVo toNotifyTemplateVo(DhNotifyTemplate entity) {
        DhNotifyTemplateVo vo = new DhNotifyTemplateVo();
        vo.setId(entity.getTemplateId());
        vo.setTemplateName(entity.getTemplateName());
        vo.setScene(entity.getScene());
        vo.setChannel(entity.getChannel());
        vo.setContent(entity.getContent());
        vo.setTimeoutHours(entity.getTimeoutHours());
        vo.setStatus(entity.getStatus());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
