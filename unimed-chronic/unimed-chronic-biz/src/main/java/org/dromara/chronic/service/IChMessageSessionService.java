package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChMessageContentBo;
import org.dromara.chronic.domain.bo.ChMessageSessionBo;
import org.dromara.chronic.domain.vo.ChMessageContentVo;
import org.dromara.chronic.domain.vo.ChMessageSessionVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 消息会话服务
 *
 * @author unimed
 */
public interface IChMessageSessionService {

    Long createSession(ChMessageSessionBo bo);

    ChMessageSessionVo queryById(Long sessionId);

    /**
     * 获取(不存在则创建)基于随访任务的患者-医生会话(TASK_CHAT)。
     * 以(patientId, doctorUserId, taskId, TASK_CHAT)幂等唯一。
     *
     * @param patientId   患者ID
     * @param doctorUserId 医生用户ID
     * @param taskId      随访任务ID
     * @return 会话ID
     */
    Long getOrCreateTaskSession(Long patientId, Long doctorUserId, Long taskId);

    TableDataInfo<ChMessageSessionVo> queryPageList(ChMessageSessionBo bo, PageQuery pageQuery);

    List<ChMessageSessionVo> queryByPatientId(Long patientId);

    Long sendMessage(ChMessageContentBo bo);

    List<ChMessageContentVo> queryMessagesBySessionId(Long sessionId);

    /**
     * 增量查询会话消息(供前端轮询)。sinceId 为空时退化为查询最新 50 条。
     *
     * @param sessionId 会话ID
     * @param sinceId   已拉取到的最大消息ID, 仅返回其后的新消息
     * @return 按时间正序的消息列表
     */
    List<ChMessageContentVo> queryMessagesBySessionId(Long sessionId, Long sinceId);
}
