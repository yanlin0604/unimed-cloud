package org.dromara.chronic.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.chronic.domain.entity.ChFollowupDispatchPool;
import org.dromara.chronic.domain.vo.ChFollowupDispatchPoolVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 随访任务自动分发人员池 Mapper
 *
 * @author unimed
 */
@Mapper
public interface ChFollowupDispatchPoolMapper extends BaseMapperPlus<ChFollowupDispatchPool, ChFollowupDispatchPoolVo> {
}
