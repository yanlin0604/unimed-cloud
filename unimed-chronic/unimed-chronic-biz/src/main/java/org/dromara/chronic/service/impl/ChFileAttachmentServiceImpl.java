package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChFileAttachmentBo;
import org.dromara.chronic.domain.entity.ChFileAttachment;
import org.dromara.chronic.domain.vo.ChFileAttachmentVo;
import org.dromara.chronic.mapper.ChFileAttachmentMapper;
import org.dromara.chronic.service.IChFileAttachmentService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 附件服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChFileAttachmentServiceImpl implements IChFileAttachmentService {

    private static final Set<String> VALID_BIZ_TYPES = Set.of("REPORT_PDF", "SIGN_IMAGE", "FUNDUS_PHOTO", "ECG", "OTHER");

    private final ChFileAttachmentMapper fileAttachmentMapper;

    @Override
    public Long insertByBo(ChFileAttachmentBo bo) {
        if (!VALID_BIZ_TYPES.contains(bo.getBizType())) {
            throw new ServiceException("无效的业务类型: " + bo.getBizType());
        }
        ChFileAttachment entity = MapstructUtils.convert(bo, ChFileAttachment.class);
        fileAttachmentMapper.insert(entity);
        return entity.getFileId();
    }

    @Override
    public Boolean updateByBo(ChFileAttachmentBo bo) {
        ChFileAttachment entity = fileAttachmentMapper.selectById(bo.getFileId());
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("附件不存在");
        }
        ChFileAttachment update = MapstructUtils.convert(bo, ChFileAttachment.class);
        fileAttachmentMapper.updateById(update);
        return true;
    }

    @Override
    public ChFileAttachmentVo queryById(Long fileId) {
        return fileAttachmentMapper.selectVoById(fileId);
    }

    @Override
    public TableDataInfo<ChFileAttachmentVo> queryPageList(ChFileAttachmentBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChFileAttachment> lqw = Wrappers.lambdaQuery();
        lqw.eq(StringUtils.isNotBlank(bo.getBizType()), ChFileAttachment::getBizType, bo.getBizType());
        lqw.eq(ObjectUtil.isNotNull(bo.getBizId()), ChFileAttachment::getBizId, bo.getBizId());
        lqw.orderByDesc(ChFileAttachment::getCreateTime);
        Page<ChFileAttachmentVo> page = fileAttachmentMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChFileAttachmentVo> queryByBiz(String bizType, Long bizId) {
        return fileAttachmentMapper.selectVoList(
            Wrappers.<ChFileAttachment>lambdaQuery()
                .eq(StringUtils.isNotBlank(bizType), ChFileAttachment::getBizType, bizType)
                .eq(ObjectUtil.isNotNull(bizId), ChFileAttachment::getBizId, bizId)
                .orderByDesc(ChFileAttachment::getCreateTime)
        );
    }

    @Override
    public Boolean deleteById(Long fileId) {
        ChFileAttachment entity = fileAttachmentMapper.selectById(fileId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("附件不存在");
        }
        fileAttachmentMapper.deleteById(fileId);
        return true;
    }
}
