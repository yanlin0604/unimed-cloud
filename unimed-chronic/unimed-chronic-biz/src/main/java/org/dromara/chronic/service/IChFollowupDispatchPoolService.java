package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChFollowupDispatchPoolBo;
import org.dromara.chronic.domain.bo.ChFollowupDispatchRunBo;
import org.dromara.chronic.domain.vo.ChFollowupDispatchPoolVo;
import org.dromara.chronic.domain.vo.ChFollowupDispatchResultVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 随访任务自动分发人员池服务接口
 *
 * @author unimed
 */
public interface IChFollowupDispatchPoolService {

    /**
     * 分页查询分发人员池
     */
    TableDataInfo<ChFollowupDispatchPoolVo> queryPageList(ChFollowupDispatchPoolBo bo, PageQuery pageQuery);

    /**
     * 查询所有启用的分发人员列表
     */
    List<ChFollowupDispatchPoolVo> queryActiveList();

    /**
     * 单个或批量添加用户到分发人员池
     */
    Boolean addUsersToPool(ChFollowupDispatchPoolBo bo);

    /**
     * 更新分发人员配置（权重、上限、病种、状态等）
     */
    Boolean updatePoolMember(ChFollowupDispatchPoolBo bo);

    /**
     * 切换分发人员接单状态（启用/暂停）
     */
    Boolean toggleActive(Long id, Boolean isActive);

    /**
     * 从分发池中移除人员
     */
    Boolean removeFromPool(Collection<Long> ids);

    /**
     * 核心算法：执行自动跑批/随机分发随访任务池中的任务
     */
    ChFollowupDispatchResultVo executeBatchDispatch(ChFollowupDispatchRunBo bo);
}
