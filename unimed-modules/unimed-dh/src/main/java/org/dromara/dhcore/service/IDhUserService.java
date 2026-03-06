package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.DhBalanceAdjustBo;
import org.dromara.dhcore.domain.bo.DhUserQueryBo;
import org.dromara.dhcore.domain.bo.DhUserStatusBo;
import org.dromara.dhcore.domain.bo.DhWalletLogQueryBo;
import org.dromara.dhcore.domain.vo.DhUserDetailVo;
import org.dromara.dhcore.domain.vo.DhUserItemVo;
import org.dromara.dhcore.domain.vo.DhWalletLogVo;

/**
 * 数字人口播用户管理服务接口
 */
public interface IDhUserService {

    /**
     * 分页查询用户
     */
    TableDataInfo<DhUserItemVo> queryUserPage(DhUserQueryBo bo, PageQuery pageQuery);

    /**
     * 查询用户详情
     */
    DhUserDetailVo queryUserDetail(Long userId);

    /**
     * 切换用户状态
     */
    DhUserItemVo changeUserStatus(DhUserStatusBo bo);

    /**
     * 调整余额
     */
    DhUserItemVo adjustBalance(DhBalanceAdjustBo bo);

    /**
     * 查询钱包流水
     */
    TableDataInfo<DhWalletLogVo> queryWalletLogPage(DhWalletLogQueryBo bo, PageQuery pageQuery);
}
