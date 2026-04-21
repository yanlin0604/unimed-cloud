package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChHealthExamBo;
import org.dromara.chronic.domain.bo.ChHealthExamItemBo;
import org.dromara.chronic.domain.entity.ChHealthExam;
import org.dromara.chronic.domain.entity.ChHealthExamItem;
import org.dromara.chronic.domain.vo.ChHealthExamItemVo;
import org.dromara.chronic.domain.vo.ChHealthExamVo;
import org.dromara.chronic.mapper.ChHealthExamItemMapper;
import org.dromara.chronic.mapper.ChHealthExamMapper;
import org.dromara.chronic.service.IChHealthExamService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 体检检验服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChHealthExamServiceImpl implements IChHealthExamService {

    private final ChHealthExamMapper examMapper;
    private final ChHealthExamItemMapper itemMapper;

    @Override
    public Long createExam(ChHealthExamBo bo) {
        ChHealthExam entity = MapstructUtils.convert(bo, ChHealthExam.class);
        examMapper.insert(entity);
        return entity.getExamId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Void updateExam(ChHealthExamBo bo) {
        ChHealthExam existing = examMapper.selectById(bo.getExamId());
        if (ObjectUtil.isNull(existing)) {
            throw new ServiceException("体检报告不存在");
        }
        ChHealthExam entity = MapstructUtils.convert(bo, ChHealthExam.class);
        examMapper.updateById(entity);
        return null;
    }

    @Override
    public ChHealthExamVo queryById(Long examId) {
        ChHealthExamVo vo = examMapper.selectVoById(examId);
        if (vo != null) {
            vo.setItems(queryItemsByExamId(examId));
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChHealthExamVo> queryPageList(ChHealthExamBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChHealthExam> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChHealthExam::getPatientId, bo.getPatientId());
        lqw.eq(StringUtils.isNotBlank(bo.getExamType()), ChHealthExam::getExamType, bo.getExamType());
        lqw.eq(StringUtils.isNotBlank(bo.getSpecialCategory()), ChHealthExam::getSpecialCategory, bo.getSpecialCategory());
        lqw.orderByDesc(ChHealthExam::getExamDate);
        Page<ChHealthExamVo> page = examMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChHealthExamVo> queryByPatientId(Long patientId) {
        return examMapper.selectVoList(
            Wrappers.<ChHealthExam>lambdaQuery()
                .eq(ChHealthExam::getPatientId, patientId)
                .orderByDesc(ChHealthExam::getExamDate)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long syncLisExam(ChHealthExamBo bo) {
        if (StringUtils.isNotBlank(bo.getExternalSn())) {
            ChHealthExam existed = examMapper.selectOne(
                Wrappers.<ChHealthExam>lambdaQuery().eq(ChHealthExam::getExternalSn, bo.getExternalSn())
            );
            if (ObjectUtil.isNotNull(existed)) {
                log.info("LIS幂等命中 externalSn={}, 返回已有examId={}", bo.getExternalSn(), existed.getExamId());
                return existed.getExamId();
            }
        }
        return createExam(bo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long syncPacsExam(ChHealthExamBo bo) {
        return syncLisExam(bo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addItem(ChHealthExamItemBo bo) {
        ChHealthExamItem entity = MapstructUtils.convert(bo, ChHealthExamItem.class);
        itemMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public List<ChHealthExamItemVo> queryItemsByExamId(Long examId) {
        return itemMapper.selectVoList(
            Wrappers.<ChHealthExamItem>lambdaQuery()
                .eq(ChHealthExamItem::getExamId, examId)
                .orderByAsc(ChHealthExamItem::getId)
        );
    }
}
