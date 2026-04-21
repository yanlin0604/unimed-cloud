package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChReportInstanceBo;
import org.dromara.chronic.domain.bo.ChReportTemplateBo;
import org.dromara.chronic.domain.entity.ChReportInstance;
import org.dromara.chronic.domain.entity.ChReportTemplate;
import org.dromara.chronic.domain.vo.ChReportInstanceVo;
import org.dromara.chronic.domain.vo.ChReportTemplateVo;
import org.dromara.chronic.mapper.ChReportInstanceMapper;
import org.dromara.chronic.mapper.ChReportTemplateMapper;
import org.dromara.chronic.service.IChReportService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 健康报告服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChReportServiceImpl implements IChReportService {

    private final ChReportTemplateMapper templateMapper;
    private final ChReportInstanceMapper instanceMapper;

    @Override
    public Long createTemplate(ChReportTemplateBo bo) {
        ChReportTemplate entity = MapstructUtils.convert(bo, ChReportTemplate.class);
        templateMapper.insert(entity);
        return entity.getTemplateId();
    }

    @Override
    public Void updateTemplate(ChReportTemplateBo bo) {
        ChReportTemplate existing = templateMapper.selectById(bo.getTemplateId());
        if (ObjectUtil.isNull(existing)) {
            throw new ServiceException("报告模板不存在");
        }
        ChReportTemplate entity = MapstructUtils.convert(bo, ChReportTemplate.class);
        templateMapper.updateById(entity);
        return null;
    }

    @Override
    public ChReportTemplateVo queryTemplateById(Long templateId) {
        return templateMapper.selectVoById(templateId);
    }

    @Override
    public TableDataInfo<ChReportTemplateVo> queryTemplatePageList(ChReportTemplateBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChReportTemplate> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(bo.getTemplateName()), ChReportTemplate::getTemplateName, bo.getTemplateName());
        lqw.eq(StringUtils.isNotBlank(bo.getDiseaseCode()), ChReportTemplate::getDiseaseCode, bo.getDiseaseCode());
        lqw.orderByDesc(ChReportTemplate::getCreateTime);
        Page<ChReportTemplateVo> page = templateMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public Long generateReport(ChReportInstanceBo bo) {
        ChReportInstance entity = MapstructUtils.convert(bo, ChReportInstance.class);
        entity.setQrCodeContent(UUID.randomUUID().toString());
        entity.setPushStatus("PENDING");
        instanceMapper.insert(entity);
        log.info("报告生成: reportId={}, patientId={}", entity.getReportId(), bo.getPatientId());
        return entity.getReportId();
    }

    @Override
    public ChReportInstanceVo queryReportById(Long reportId) {
        return instanceMapper.selectVoById(reportId);
    }

    @Override
    public TableDataInfo<ChReportInstanceVo> queryReportPageList(ChReportInstanceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChReportInstance> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChReportInstance::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getReportType()), ChReportInstance::getReportType, bo.getReportType());
        lqw.orderByDesc(ChReportInstance::getCreateTime);
        Page<ChReportInstanceVo> page = instanceMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChReportInstanceVo> queryByPatientId(Long patientId) {
        return instanceMapper.selectVoList(
            Wrappers.<ChReportInstance>lambdaQuery()
                .eq(ChReportInstance::getPatientId, patientId)
                .orderByDesc(ChReportInstance::getCreateTime)
        );
    }

    @Override
    public Void pushReport(Long reportId, String channel) {
        ChReportInstance entity = instanceMapper.selectById(reportId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("报告实例不存在");
        }
        entity.setPushStatus("PUSHED");
        instanceMapper.updateById(entity);
        log.info("报告推送: reportId={}, channel={}", reportId, channel);
        return null;
    }
}
