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

    TableDataInfo<ChMessageSessionVo> queryPageList(ChMessageSessionBo bo, PageQuery pageQuery);

    List<ChMessageSessionVo> queryByPatientId(Long patientId);

    Long sendMessage(ChMessageContentBo bo);

    List<ChMessageContentVo> queryMessagesBySessionId(Long sessionId);
}
