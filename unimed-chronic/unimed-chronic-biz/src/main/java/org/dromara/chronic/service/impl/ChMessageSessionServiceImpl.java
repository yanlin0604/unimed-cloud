package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChMessageContentBo;
import org.dromara.chronic.domain.bo.ChMessageSessionBo;
import org.dromara.chronic.domain.entity.ChMessageContent;
import org.dromara.chronic.domain.entity.ChMessageSession;
import org.dromara.chronic.domain.vo.ChMessageContentVo;
import org.dromara.chronic.domain.vo.ChMessageSessionVo;
import org.dromara.chronic.mapper.ChMessageContentMapper;
import org.dromara.chronic.mapper.ChMessageSessionMapper;
import org.dromara.chronic.service.IChMessageSessionService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 消息会话服务实现
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChMessageSessionServiceImpl implements IChMessageSessionService {

    private final ChMessageSessionMapper sessionMapper;
    private final ChMessageContentMapper contentMapper;

    @Override
    public Long createSession(ChMessageSessionBo bo) {
        ChMessageSession entity = MapstructUtils.convert(bo, ChMessageSession.class);
        sessionMapper.insert(entity);
        return entity.getSessionId();
    }

    @Override
    public ChMessageSessionVo queryById(Long sessionId) {
        ChMessageSessionVo vo = sessionMapper.selectVoById(sessionId);
        if (vo != null) {
            vo.setRecentMessages(queryMessagesBySessionId(sessionId));
        }
        return vo;
    }

    @Override
    public TableDataInfo<ChMessageSessionVo> queryPageList(ChMessageSessionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChMessageSession> lqw = Wrappers.lambdaQuery();
        lqw.eq(ObjectUtil.isNotNull(bo.getPatientId()), ChMessageSession::getPatientId, bo.getPatientId());
        lqw.eq(ObjectUtil.isNotNull(bo.getDoctorUserId()), ChMessageSession::getDoctorUserId, bo.getDoctorUserId());
        lqw.orderByDesc(ChMessageSession::getLastMessageTime);
        Page<ChMessageSessionVo> page = sessionMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    @Override
    public List<ChMessageSessionVo> queryByPatientId(Long patientId) {
        return sessionMapper.selectVoList(
            Wrappers.<ChMessageSession>lambdaQuery()
                .eq(ChMessageSession::getPatientId, patientId)
                .orderByDesc(ChMessageSession::getLastMessageTime)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long sendMessage(ChMessageContentBo bo) {
        ChMessageContent entity = MapstructUtils.convert(bo, ChMessageContent.class);
        contentMapper.insert(entity);
        ChMessageSession session = sessionMapper.selectById(bo.getSessionId());
        if (session != null) {
            session.setLastMessageTime(new Date());
            sessionMapper.updateById(session);
        }
        return entity.getId();
    }

    @Override
    public List<ChMessageContentVo> queryMessagesBySessionId(Long sessionId) {
        return contentMapper.selectVoList(
            Wrappers.<ChMessageContent>lambdaQuery()
                .eq(ChMessageContent::getSessionId, sessionId)
                .orderByAsc(ChMessageContent::getCreateTime)
                .last("LIMIT 50")
        );
    }
}
