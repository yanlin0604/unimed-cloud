package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.portal.PortalOrderCreateBo;
import org.dromara.dhcore.domain.vo.portal.PortalOrderDetailVo;
import org.dromara.dhcore.domain.vo.portal.PortalOrderDownloadVo;
import org.dromara.dhcore.domain.vo.portal.PortalOrderVo;

import java.util.Map;

/**
 * C端订单服务接口
 *
 * @author dhcore
 */
public interface IPortalOrderService {

    /**
     * 分页查询用户订单列表
     *
     * @param userId    用户ID
     * @param status    状态筛选（可选）
     * @param keyword   关键词搜索（可选）
     * @param pageQuery 分页参数
     * @return 订单列表
     */
    TableDataInfo<PortalOrderVo> listOrders(Long userId, String status, String keyword, PageQuery pageQuery);

    /**
     * 查询订单详情
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    PortalOrderDetailVo getOrderDetail(Long userId, Long orderId);

    /**
     * 获取用户订单统计
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    Map<String, Long> getOrderStats(Long userId);

    /**
     * 获取用户已完成订单数
     *
     * @param userId 用户ID
     * @return 已完成订单数
     */
    Long countCompletedOrders(Long userId);

    /**
     * 创建订单
     *
     * @param userId   用户ID
     * @param userName 用户名
     * @param bo       订单创建参数
     * @return 创建后的订单
     */
    PortalOrderVo createOrder(Long userId, String userName, PortalOrderCreateBo bo);

    /**
     * 取消订单
     *
     * @param userId   用户ID
     * @param userName 用户名
     * @param orderId  订单ID
     * @param reason   取消原因（可选）
     */
    void cancelOrder(Long userId, String userName, Long orderId, String reason);

    /**
     * 获取作品下载信息
     *
     * @param userId  当前用户ID
     * @param orderId 订单ID
     * @return 下载信息
     */
    PortalOrderDownloadVo getDownloadInfo(Long userId, Long orderId);

    /**
     * 删除作品（软删除）
     *
     * @param userId   当前用户ID
     * @param userName 当前用户名
     * @param orderId  订单ID
     */
    void deleteOrder(Long userId, String userName, Long orderId);
}
