package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.*;
import org.dromara.dhcore.domain.vo.DhOrderDetailVo;
import org.dromara.dhcore.domain.vo.DhOrderItemVo;
import org.dromara.dhcore.domain.vo.DhProductionAssetVo;

/**
 * 数字人口播订单与生产服务接口
 */
public interface IDhOrderService {

    /**
     * 分页查询订单
     */
    TableDataInfo<DhOrderItemVo> queryOrderPage(DhOrderQueryBo bo, PageQuery pageQuery);

    /**
     * 查询订单详情
     */
    DhOrderDetailVo queryOrderDetail(Long orderId);

    /**
     * 领取订单
     */
    DhOrderDetailVo claimOrder(Long orderId, DhOrderClaimBo bo);

    /**
     * 取消订单
     */
    DhOrderDetailVo cancelOrder(DhOrderCancelBo bo);

    /**
     * 拒绝订单
     */
    DhOrderDetailVo rejectOrder(DhOrderRejectBo bo);

    /**
     * 开始制作
     */
    DhProductionAssetVo startProduction(Long orderId, DhProductionStartBo bo);

    /**
     * 查询生产资产
     */
    DhProductionAssetVo getProductionAsset(Long orderId);

    /**
     * 保存生成元数据
     */
    DhProductionAssetVo saveGenerationMeta(Long orderId, DhProductionMetaBo bo);

    /**
     * 上传成品元数据
     */
    DhProductionAssetVo uploadResultVideo(Long orderId, DhProductionVideoBo bo);

    /**
     * 提交交付
     */
    DhOrderDetailVo submitOrderResult(DhProductionSubmitBo bo);
}
