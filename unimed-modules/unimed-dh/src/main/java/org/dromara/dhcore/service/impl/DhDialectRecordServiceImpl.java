package org.dromara.dhcore.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.constant.TenantConstants;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.dhcore.domain.bo.DhDialectRecordAuditBo;
import org.dromara.dhcore.domain.bo.DhDialectRecordBo;
import org.dromara.dhcore.domain.bo.DhDialectRecordQueryBo;
import org.dromara.dhcore.domain.convert.DhDialectConvert;
import org.dromara.dhcore.domain.entity.DhDialectPrompt;
import org.dromara.dhcore.domain.entity.DhDialectRecord;
import org.dromara.dhcore.domain.vo.DhDialectRecordVo;
import org.dromara.dhcore.mapper.DhDialectPromptMapper;
import org.dromara.dhcore.mapper.DhDialectRecordMapper;
import org.dromara.dhcore.service.IDhDialectRecordService;
import org.dromara.resource.api.RemoteFileService;
import org.dromara.resource.api.domain.RemoteFile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 方言采集录音记录服务实现类
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DhDialectRecordServiceImpl implements IDhDialectRecordService {

    private final DhDialectRecordMapper dialectRecordMapper;
    private final DhDialectPromptMapper dialectPromptMapper;

    @DubboReference
    private RemoteFileService remoteFileService;

    @Override
    public DhDialectRecordVo submit(Long userId, DhDialectRecordBo bo) {
        // 通过 ossId 获取持久 URL
        String persistentUrl = null;
        if (StringUtils.isNotBlank(bo.getOssId())) {
            try {
                List<RemoteFile> files = remoteFileService.selectByIds(bo.getOssId());
                if (files != null && !files.isEmpty()) {
                    persistentUrl = files.get(0).getUrl();
                }
            } catch (Exception e) {
                log.error("通过ossId获取持久URL失败: {}", bo.getOssId(), e);
                throw new ServiceException("录音文件处理失败，请重试");
            }
        }
        if (StringUtils.isBlank(persistentUrl)) {
            throw new ServiceException("无法获取录音文件的访问地址，请重试");
        }

        DhDialectRecord record = new DhDialectRecord();
        record.setPromptId(bo.getPromptId());
        record.setTenantId(StringUtils.blankToDefault(LoginHelper.getTenantId(), TenantConstants.DEFAULT_TENANT_ID));
        record.setUserId(userId);
        record.setDialectName(bo.getDialectName());
        record.setDialectArea(bo.getDialectArea());
        record.setAudioUrl(persistentUrl);
        record.setOssId(bo.getOssId());
        record.setDuration(bo.getDuration());
        record.setInviteCode(bo.getInviteCode());
        record.setAuditStatus("PENDING");
        dialectRecordMapper.insert(record);

        DhDialectRecordVo vo = DhDialectConvert.toDialectRecordVo(record);
        // 填充关联文字内容
        fillPromptContent(List.of(vo));
        return vo;
    }

    @Override
    public TableDataInfo<DhDialectRecordVo> queryPage(DhDialectRecordQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhDialectRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getDialectName()), DhDialectRecord::getDialectName, bo.getDialectName());
        lqw.eq(StringUtils.isNotBlank(bo.getInviteCode()), DhDialectRecord::getInviteCode, bo.getInviteCode());
        lqw.eq(StringUtils.isNotBlank(bo.getAuditStatus()), DhDialectRecord::getAuditStatus, bo.getAuditStatus());
        lqw.ge(StringUtils.isNotBlank(bo.getBeginTime()), DhDialectRecord::getCreateTime, bo.getBeginTime());
        lqw.le(StringUtils.isNotBlank(bo.getEndTime()), DhDialectRecord::getCreateTime, bo.getEndTime());
        lqw.orderByDesc(DhDialectRecord::getCreateTime);

        Page<DhDialectRecord> page = dialectRecordMapper.selectPage(pageQuery.build(), lqw);
        List<DhDialectRecordVo> voList = page.getRecords().stream()
            .map(DhDialectConvert::toDialectRecordVo).toList();

        // 批量填充关联文字内容
        fillPromptContent(voList);
        // 通过 ossId 刷新录音地址，防止 URL 已过期
        refreshAudioUrl(voList);

        return new TableDataInfo<>(voList, page.getTotal());
    }

    @Override
    public Boolean audit(DhDialectRecordAuditBo bo) {
        DhDialectRecord record = dialectRecordMapper.selectById(bo.getRecordId());
        if (record == null) {
            throw new ServiceException("录音记录不存在");
        }
        if (!"APPROVED".equals(bo.getAuditStatus()) && !"REJECTED".equals(bo.getAuditStatus())) {
            throw new ServiceException("审核状态不合法，仅支持 APPROVED/REJECTED");
        }
        LambdaUpdateWrapper<DhDialectRecord> uw = Wrappers.lambdaUpdate();
        uw.eq(DhDialectRecord::getRecordId, bo.getRecordId());
        uw.set(DhDialectRecord::getAuditStatus, bo.getAuditStatus());
        uw.set(DhDialectRecord::getAuditRemark, bo.getAuditRemark());
        return dialectRecordMapper.update(null, uw) > 0;
    }

    @Override
    public Boolean delete(List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            throw new ServiceException("请选择要删除的录音记录");
        }
        return dialectRecordMapper.deleteByIds(recordIds) > 0;
    }

    @Override
    public void export(DhDialectRecordQueryBo bo, HttpServletResponse response) {
        LambdaQueryWrapper<DhDialectRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getDialectName()), DhDialectRecord::getDialectName, bo.getDialectName());
        lqw.eq(StringUtils.isNotBlank(bo.getInviteCode()), DhDialectRecord::getInviteCode, bo.getInviteCode());
        lqw.eq(StringUtils.isNotBlank(bo.getAuditStatus()), DhDialectRecord::getAuditStatus, bo.getAuditStatus());
        lqw.ge(StringUtils.isNotBlank(bo.getBeginTime()), DhDialectRecord::getCreateTime, bo.getBeginTime());
        lqw.le(StringUtils.isNotBlank(bo.getEndTime()), DhDialectRecord::getCreateTime, bo.getEndTime());
        lqw.orderByDesc(DhDialectRecord::getCreateTime);
        // 限制导出最多1000条
        lqw.last("LIMIT 1000");

        List<DhDialectRecord> records = dialectRecordMapper.selectList(lqw);
        List<DhDialectRecordVo> voList = records.stream()
            .map(DhDialectConvert::toDialectRecordVo).toList();
        fillPromptContent(voList);
        // 通过 ossId 刷新录音地址，防止导出的 URL 已过期
        refreshAudioUrl(voList);

        ExcelUtil.exportExcel(voList, "方言采集录音记录", DhDialectRecordVo.class, response);
    }

    /**
     * 通过 ossId 批量刷新录音访问地址（防止导出的 URL 已过期）
     */
    private void refreshAudioUrl(List<DhDialectRecordVo> voList) {
        if (voList == null || voList.isEmpty()) {
            return;
        }
        List<String> ossIds = voList.stream()
            .map(DhDialectRecordVo::getOssId)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        if (ossIds.isEmpty()) {
            return;
        }
        Map<String, RemoteFile> ossMap = new HashMap<>();
        try {
            List<RemoteFile> files = remoteFileService.selectByIds(String.join(",", ossIds));
            if (files != null) {
                for (RemoteFile f : files) {
                    ossMap.put(String.valueOf(f.getOssId()), f);
                }
            }
        } catch (Exception e) {
            log.warn("导出时批量刷新录音OSS地址失败: {}", e.getMessage());
            return;
        }
        for (DhDialectRecordVo vo : voList) {
            if (StringUtils.isNotBlank(vo.getOssId())) {
                RemoteFile file = ossMap.get(vo.getOssId());
                if (file != null && StringUtils.isNotBlank(file.getUrl())) {
                    vo.setAudioUrl(file.getUrl());
                }
            }
        }
    }

    /**
     * 批量填充关联的提示文字内容
     */
    private void fillPromptContent(List<DhDialectRecordVo> voList) {
        if (voList == null || voList.isEmpty()) {
            return;
        }
        List<Long> promptIds = voList.stream()
            .map(DhDialectRecordVo::getPromptId)
            .filter(id -> id != null)
            .distinct()
            .toList();
        if (promptIds.isEmpty()) {
            return;
        }
        List<DhDialectPrompt> prompts = dialectPromptMapper.selectByIds(promptIds);
        Map<Long, String> promptMap = new HashMap<>();
        if (prompts != null) {
            for (DhDialectPrompt p : prompts) {
                promptMap.put(p.getPromptId(), p.getContent());
            }
        }
        for (DhDialectRecordVo vo : voList) {
            if (vo.getPromptId() != null) {
                vo.setPromptContent(promptMap.getOrDefault(vo.getPromptId(), ""));
            }
        }
    }
}
