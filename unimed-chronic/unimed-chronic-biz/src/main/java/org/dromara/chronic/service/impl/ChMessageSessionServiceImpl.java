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
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
    @Transactional(rollbackFor = Exception.class)
    public Long getOrCreateTaskSession(Long patientId, Long doctorUserId, Long taskId) {
        if (patientId == null || taskId == null) {
            throw new ServiceException("患者与任务ID不能为空");
        }
        ChMessageSession existing = sessionMapper.selectOne(
            Wrappers.<ChMessageSession>lambdaQuery()
                .eq(ChMessageSession::getPatientId, patientId)
                .eq(doctorUserId != null, ChMessageSession::getDoctorUserId, doctorUserId)
                .eq(ChMessageSession::getTaskId, taskId)
                .eq(ChMessageSession::getSessionType, "TASK_CHAT")
                .last("limit 1"));
        if (existing != null) {
            return existing.getSessionId();
        }
        ChMessageSession session = new ChMessageSession();
        session.setPatientId(patientId);
        session.setDoctorUserId(doctorUserId);
        session.setTaskId(taskId);
        session.setSessionType("TASK_CHAT");
        session.setLastMessageTime(new Date());
        sessionMapper.insert(session);
        return session.getSessionId();
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
        // 倒序取最新 50 条后反转为正序: 原 orderByAsc + LIMIT 50 取到的是最早 50 条,
        // 会话超过 50 条后新消息永远查不出来(刷新也无效)
        List<ChMessageContentVo> list = contentMapper.selectVoList(
            Wrappers.<ChMessageContent>lambdaQuery()
                .eq(ChMessageContent::getSessionId, sessionId)
                .orderByDesc(ChMessageContent::getId)
                .last("LIMIT 50")
        );
        Collections.reverse(list);
        return list;
    }

    @Override
    public List<ChMessageContentVo> queryMessagesBySessionId(Long sessionId, Long sinceId) {
        if (sinceId == null || sinceId <= 0) {
            return queryMessagesBySessionId(sessionId);
        }
        // 增量拉取: 消息主键为雪花ID(随时间单调递增), 仅返回 sinceId 之后的新消息, 供前端轮询使用
        return contentMapper.selectVoList(
            Wrappers.<ChMessageContent>lambdaQuery()
                .eq(ChMessageContent::getSessionId, sessionId)
                .gt(ChMessageContent::getId, sinceId)
                .orderByAsc(ChMessageContent::getId)
                .last("LIMIT 200")
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long getOrCreateConsultationSession(Long patientId, Long doctorUserId) {
        if (patientId == null) {
            throw new ServiceException("患者ID不能为空");
        }
        ChMessageSession existing = sessionMapper.selectOne(
            Wrappers.<ChMessageSession>lambdaQuery()
                .eq(ChMessageSession::getPatientId, patientId)
                .eq(doctorUserId != null, ChMessageSession::getDoctorUserId, doctorUserId)
                .eq(ChMessageSession::getSessionType, "DOCTOR_PATIENT")
                .isNull(ChMessageSession::getTaskId)
                .orderByDesc(ChMessageSession::getLastMessageTime)
                .last("limit 1"));
        if (existing != null) {
            return existing.getSessionId();
        }
        ChMessageSession session = new ChMessageSession();
        session.setPatientId(patientId);
        session.setDoctorUserId(doctorUserId);
        session.setSessionType("DOCTOR_PATIENT");
        session.setLastMessageTime(new Date());
        sessionMapper.insert(session);
        return session.getSessionId();
    }
}
