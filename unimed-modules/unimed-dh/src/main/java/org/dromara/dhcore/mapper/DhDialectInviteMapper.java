package org.dromara.dhcore.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.dhcore.domain.entity.DhDialectInvite;
import org.dromara.dhcore.domain.vo.DhDialectInviteVo;

/**
 * 方言邀请码配置 Mapper
 *
 * @author unimed
 */
@Mapper
public interface DhDialectInviteMapper extends BaseMapperPlus<DhDialectInvite, DhDialectInviteVo> {

}