package org.dromara.chronic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChNotificationTemplateBo;
import org.dromara.chronic.domain.entity.ChNotificationTemplate;
import org.dromara.chronic.domain.vo.ChNotificationTemplateVo;
import org.dromara.chronic.mapper.ChNotificationTemplateMapper;
import org.dromara.chronic.service.IChNotificationTemplateService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 通知模板服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChNotificationTemplateServiceImpl implements IChNotificationTemplateService {

    /** 启用状态 */
    private static final String ACTIVE = "1";
    /** 停用状态 */
    private static final String INACTIVE = "0";

    private final ChNotificationTemplateMapper baseMapper;

    @Override
    public TableDataInfo<ChNotificationTemplateVo> queryPageList(ChNotificationTemplateBo bo, PageQuery pageQuery) {
        Page<ChNotificationTemplateVo> page = baseMapper.selectVoPage(pageQuery.build(), buildWrapper(bo));
        return TableDataInfo.build(page);
    }

    @Override
    public ChNotificationTemplateVo queryById(Long templateId) {
        if (templateId == null) {
            return null;
        }
        return baseMapper.selectVoById(templateId);
    }

    @Override
    public Boolean insertByBo(ChNotificationTemplateBo bo) {
        validateCodeChannelUnique(bo.getTemplateCode(), bo.getChannel(), null);
        ChNotificationTemplate entity = MapstructUtils.convert(bo, ChNotificationTemplate.class);
        if (entity == null) {
            throw new ServiceException("通知模板参数不能为空");
        }
        // 未显式指定时默认启用
        if (StringUtils.isBlank(entity.getIsActive())) {
            entity.setIsActive(ACTIVE);
        }
        entity.setTemplateId(null);
        return baseMapper.insert(entity) > 0;
    }

    @Override
    public Boolean updateByBo(ChNotificationTemplateBo bo) {
        if (bo.getTemplateId() == null) {
            throw new ServiceException("模板ID不能为空");
        }
        ChNotificationTemplate existing = baseMapper.selectById(bo.getTemplateId());
        if (existing == null) {
            throw new ServiceException("通知模板不存在");
        }
        validateCodeChannelUnique(bo.getTemplateCode(), bo.getChannel(), bo.getTemplateId());
        ChNotificationTemplate entity = MapstructUtils.convert(bo, ChNotificationTemplate.class);
        if (entity == null) {
            throw new ServiceException("通知模板参数不能为空");
        }
        return baseMapper.updateById(entity) > 0;
    }

    @Override
    public Boolean deleteById(Long templateId) {
        if (templateId == null) {
            throw new ServiceException("模板ID不能为空");
        }
        ChNotificationTemplate existing = baseMapper.selectById(templateId);
        if (existing == null) {
            throw new ServiceException("通知模板不存在");
        }
        return baseMapper.deleteById(templateId) > 0;
    }

    @Override
    public Void updateStatus(Long templateId, String isActive) {
        if (templateId == null) {
            throw new ServiceException("模板ID不能为空");
        }
        if (!ACTIVE.equals(isActive) && !INACTIVE.equals(isActive)) {
            throw new ServiceException("无效的启用状态: " + isActive + "（仅支持 1启用 / 0停用）");
        }
        ChNotificationTemplate entity = baseMapper.selectById(templateId);
        if (entity == null) {
            throw new ServiceException("通知模板不存在");
        }
        entity.setIsActive(isActive);
        baseMapper.updateById(entity);
        return null;
    }

    @Override
    public ChNotificationTemplateVo queryByCode(String templateCode, String channel) {
        if (StringUtils.isBlank(templateCode)) {
            return null;
        }
        LambdaQueryWrapper<ChNotificationTemplate> lqw = Wrappers.<ChNotificationTemplate>lambdaQuery()
            .eq(ChNotificationTemplate::getTemplateCode, templateCode)
            .eq(StringUtils.isNotBlank(channel), ChNotificationTemplate::getChannel, channel)
            .orderByDesc(ChNotificationTemplate::getTemplateId)
            .last("limit 1");
        return baseMapper.selectVoOne(lqw);
    }

    @Override
    public String render(String templateCode, String channel, Map<String, String> params) {
        ChNotificationTemplateVo vo;
        try {
            vo = queryByCode(templateCode, channel);
        } catch (Exception e) {
            // 渲染属于增强能力，任何异常均降级为 null，由调用方兜底
            log.warn("通知模板查询失败 templateCode={} channel={} msg={}", templateCode, channel, e.getMessage());
            return null;
        }
        if (vo == null) {
            return null;
        }
        // 停用模板不参与渲染（历史数据 is_active 为 null 时视为启用）
        if (INACTIVE.equals(vo.getIsActive())) {
            return null;
        }
        String content = vo.getTemplateContent();
        if (StringUtils.isBlank(content)) {
            return null;
        }
        if (params == null || params.isEmpty()) {
            return content;
        }
        String result = content;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            String value = entry.getValue() == null ? "" : entry.getValue();
            result = result.replace("{" + entry.getKey() + "}", value);
        }
        return result;
    }

    /**
     * 构建分页/列表查询条件
     */
    private LambdaQueryWrapper<ChNotificationTemplate> buildWrapper(ChNotificationTemplateBo bo) {
        LambdaQueryWrapper<ChNotificationTemplate> lqw = Wrappers.lambdaQuery();
        if (bo == null) {
            return lqw.orderByDesc(ChNotificationTemplate::getCreateTime);
        }
        lqw.like(StringUtils.isNotBlank(bo.getTemplateName()), ChNotificationTemplate::getTemplateName, bo.getTemplateName());
        lqw.like(StringUtils.isNotBlank(bo.getTemplateCode()), ChNotificationTemplate::getTemplateCode, bo.getTemplateCode());
        lqw.eq(StringUtils.isNotBlank(bo.getChannel()), ChNotificationTemplate::getChannel, bo.getChannel());
        lqw.eq(StringUtils.isNotBlank(bo.getIsActive()), ChNotificationTemplate::getIsActive, bo.getIsActive());
        lqw.and(StringUtils.isNotBlank(bo.getKeyword()), w -> w
            .like(ChNotificationTemplate::getTemplateName, bo.getKeyword())
            .or().like(ChNotificationTemplate::getTemplateCode, bo.getKeyword()));
        lqw.orderByDesc(ChNotificationTemplate::getCreateTime);
        return lqw;
    }

    /**
     * 校验 templateCode + channel 组合唯一
     */
    private void validateCodeChannelUnique(String templateCode, String channel, Long excludeId) {
        if (StringUtils.isBlank(templateCode) || StringUtils.isBlank(channel)) {
            return;
        }
        LambdaQueryWrapper<ChNotificationTemplate> lqw = Wrappers.<ChNotificationTemplate>lambdaQuery()
            .eq(ChNotificationTemplate::getTemplateCode, templateCode)
            .eq(ChNotificationTemplate::getChannel, channel);
        if (excludeId != null) {
            lqw.ne(ChNotificationTemplate::getTemplateId, excludeId);
        }
        if (baseMapper.exists(lqw)) {
            throw new ServiceException("同一渠道下模板编码已存在：" + templateCode + " / " + channel);
        }
    }
}
