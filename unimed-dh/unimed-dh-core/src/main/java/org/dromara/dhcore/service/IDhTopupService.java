package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.DhTopupApproveBo;
import org.dromara.dhcore.domain.bo.DhTopupNeedMoreBo;
import org.dromara.dhcore.domain.bo.DhTopupQueryBo;
import org.dromara.dhcore.domain.bo.DhTopupRejectBo;
import org.dromara.dhcore.domain.vo.DhTopupTicketVo;

/**
 * 数字人口播充值审核服务接口
 */
public interface IDhTopupService {

    /**
     * 分页查询充值工单
     */
    TableDataInfo<DhTopupTicketVo> queryTopupPage(DhTopupQueryBo bo, PageQuery pageQuery);

    /**
     * 审核通过
     */
    DhTopupTicketVo approveTopup(DhTopupApproveBo bo);

    /**
     * 标记待补充
     */
    DhTopupTicketVo markTopupNeedMore(DhTopupNeedMoreBo bo);

    /**
     * 驳回工单
     */
    DhTopupTicketVo rejectTopup(DhTopupRejectBo bo);
}
