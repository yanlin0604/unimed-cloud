package org.dromara.dhcore.domain.convert;

import org.dromara.dhcore.domain.entity.DhDialectPrompt;
import org.dromara.dhcore.domain.entity.DhDialectRecord;
import org.dromara.dhcore.domain.vo.DhDialectPromptVo;
import org.dromara.dhcore.domain.vo.DhDialectRecordVo;

/**
 * 方言采集实体转换工具类
 *
 * @author unimed
 */
public final class DhDialectConvert {

    private DhDialectConvert() {
    }

    /**
     * DhDialectPrompt 转 DhDialectPromptVo
     */
    public static DhDialectPromptVo toDialectPromptVo(DhDialectPrompt prompt) {
        if (prompt == null) {
            return null;
        }
        DhDialectPromptVo vo = new DhDialectPromptVo();
        vo.setPromptId(prompt.getPromptId());
        vo.setContent(prompt.getContent());
        vo.setCategory(prompt.getCategory());
        vo.setStatus(prompt.getStatus());
        vo.setRemark(prompt.getRemark());
        vo.setCreateTime(prompt.getCreateTime());
        return vo;
    }

    /**
     * DhDialectRecord 转 DhDialectRecordVo
     */
    public static DhDialectRecordVo toDialectRecordVo(DhDialectRecord record) {
        if (record == null) {
            return null;
        }
        DhDialectRecordVo vo = new DhDialectRecordVo();
        vo.setRecordId(record.getRecordId());
        vo.setPromptId(record.getPromptId());
        vo.setUserId(record.getUserId());
        vo.setDialectName(record.getDialectName());
        vo.setDialectArea(record.getDialectArea());
        vo.setAudioUrl(record.getAudioUrl());
        vo.setOssId(record.getOssId());
        vo.setDuration(record.getDuration());
        vo.setAuditStatus(record.getAuditStatus());
        vo.setAuditRemark(record.getAuditRemark());
        vo.setCreateTime(record.getCreateTime());
        return vo;
    }
}
