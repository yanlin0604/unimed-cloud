package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.DhDialectInviteBo;
import org.dromara.dhcore.domain.bo.DhDialectInviteQueryBo;
import org.dromara.dhcore.domain.vo.DhDialectInviteVo;

import java.util.List;

/**
 * 方言邀请码配置 Service 接口
 *
 * @author unimed
 */
public interface IDhDialectInviteService {

    /**
     * 分页查询邀请码配置
     */
    TableDataInfo<DhDialectInviteVo> queryPage(DhDialectInviteQueryBo queryBo, PageQuery pageQuery);

    /**
     * 新增邀请码配置
     */
    DhDialectInviteVo save(DhDialectInviteBo bo);

    /**
     * 修改邀请码配置
     */
    DhDialectInviteVo update(DhDialectInviteBo bo);

    /**
     * 删除邀请码配置（校验关联记录）
     */
    void deleteByIds(List<Long> inviteIds);

    /**
     * 生成并返回分享链接
     */
    String generateCollectionUrl(String dialectName, String inviteCode);

    /**
     * 校验邀请码是否有效
     */
    boolean isValidInviteCode(String inviteCode);
}
