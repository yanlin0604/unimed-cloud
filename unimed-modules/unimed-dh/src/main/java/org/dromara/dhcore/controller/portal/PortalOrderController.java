package org.dromara.dhcore.controller.portal;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.DhOrder;
import org.dromara.dhcore.domain.DhOrderMaterial;
import org.dromara.dhcore.domain.DhOrderProcessLog;
import org.dromara.dhcore.domain.DhUserProfile;
import org.dromara.dhcore.domain.DhWalletLog;
import org.dromara.dhcore.domain.bo.portal.PortalOrderCreateBo;
import org.dromara.dhcore.domain.vo.DhMemberConfigVo;
import org.dromara.dhcore.domain.vo.DhOrderDetailVo;
import org.dromara.dhcore.domain.vo.portal.PortalOrderDetailVo;
import org.dromara.dhcore.domain.vo.portal.PortalOrderVo;
import org.dromara.dhcore.mapper.DhOrderMapper;
import org.dromara.dhcore.mapper.DhOrderMaterialMapper;
import org.dromara.dhcore.mapper.DhOrderProcessLogMapper;
import org.dromara.dhcore.mapper.DhUserProfileMapper;
import org.dromara.dhcore.mapper.DhWalletLogMapper;
import org.dromara.dhcore.service.IDhConfigService;
import org.dromara.dhcore.service.IDhOrderService;
import org.dromara.dhcore.service.support.DhOrderStatus;
import org.dromara.dhcore.domain.bo.DhMemberConfigQueryBo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * C端订单控制器
 * <p>
 * 提供当前登录用户的订单创建、列表查询、详情查看、取消订单、统计等接口�?
 * 创建和取消操作涉及余额变动，使用事务保证一致性�?
 * 所有接口通过 LoginHelper.getUserId() 硬绑定当前用户，并验证订单归属权�?
 *
 * @author unimed
 */
@Tag(name = "C�?订单管理")
@SaCheckLogin
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/portal/order")
public class PortalOrderController extends BaseController {

    private final IDhOrderService dhOrderService;
    private final IDhConfigService configService;
    private final DhOrderMapper orderMapper;
    private final DhOrderMaterialMapper orderMaterialMapper;
    private final DhOrderProcessLogMapper orderProcessLogMapper;
    private final DhUserProfileMapper userProfileMapper;
    private final DhWalletLogMapper walletLogMapper;

    // ==================== 查询端点 ====================

    /**
     * 分页查询当前用户订单列表
     *
     * @param status    可选状态筛�?
     * @param keyword   可选关键词搜索（订单号/标题�?
     * @param pageQuery 分页参数
     */
    @Operation(summary = "查询订单列表")
    @GetMapping("/list")
    public TableDataInfo<PortalOrderVo> list(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String keyword,
        PageQuery pageQuery) {
        Long userId = LoginHelper.getUserId();
        LambdaQueryWrapper<DhOrder> lqw = Wrappers.lambdaQuery();
        // 硬绑定当前用户（通过 createBy 审计字段�?
        lqw.eq(DhOrder::getCreateBy, userId);
        lqw.eq(StringUtils.isNotBlank(status), DhOrder::getStatus, status);
        if (StringUtils.isNotBlank(keyword)) {
            lqw.and(w -> w.like(DhOrder::getOrderNo, keyword).or().like(DhOrder::getTitle, keyword));
        }
        lqw.orderByDesc(DhOrder::getCreateTime);

        Page<DhOrder> page = orderMapper.selectPage(pageQuery.build(), lqw);
        List<PortalOrderVo> rows = page.getRecords().stream().map(this::toPortalOrderVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    /**
     * 查询订单详情（验证归属权�?
     */
    @Operation(summary = "查询订单详情")
    @GetMapping("/{orderId}")
    public R<PortalOrderDetailVo> detail(@PathVariable Long orderId) {
        Long userId = LoginHelper.getUserId();
        DhOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            return R.fail("订单不存�?);
        }
        // 归属权校�?
        if (!userId.equals(order.getCreateBy())) {
            return R.fail("无权查看该订�?);
        }
        // 复用 B�?详情查询获取完整关联数据
        DhOrderDetailVo detailVo = dhOrderService.queryOrderDetail(orderId);
        return R.ok(toPortalOrderDetailVo(order, detailVo));
    }

    /**
     * 获取当前用户订单统计
     */
    @Operation(summary = "订单统计")
    @GetMapping("/stats")
    public R<Map<String, Long>> stats() {
        Long userId = LoginHelper.getUserId();
        Map<String, Long> statsMap = new LinkedHashMap<>();
        statsMap.put("total", countByUserAndStatus(userId, null));
        statsMap.put("pending", countByUserAndStatus(userId, DhOrderStatus.PENDING));
        statsMap.put("processing", countByUserAndStatus(userId, DhOrderStatus.PROCESSING)
            + countByUserAndStatus(userId, DhOrderStatus.TO_UPLOAD)
            + countByUserAndStatus(userId, DhOrderStatus.REDO));
        statsMap.put("completed", countByUserAndStatus(userId, DhOrderStatus.COMPLETED));
        statsMap.put("cancelled", countByUserAndStatus(userId, DhOrderStatus.CANCELLED));
        statsMap.put("rejected", countByUserAndStatus(userId, DhOrderStatus.REJECTED));
        return R.ok(statsMap);
    }

    // ==================== 写入端点 ====================

    /**
     * C端用户创建订�?
     * <p>
     * 流程：校验余�?�?创建订单 �?关联素材 �?扣减余额 �?记录流水 �?记录日志
     */
    @Operation(summary = "创建订单")
    @PostMapping("/create")
    @Transactional(rollbackFor = Exception.class)
    public R<PortalOrderVo> create(@Validated @RequestBody PortalOrderCreateBo bo) {
        Long userId = LoginHelper.getUserId();
        String userName = LoginHelper.getUsername();

        // 1. 获取用户画像
        DhUserProfile profile = userProfileMapper.selectById(userId);
        if (profile == null) {
            throw new ServiceException("用户信息不存�?);
        }

        // 2. 查询会员等级定价
        BigDecimal orderPrice = resolveMemberPrice(profile.getMemberLevel());

        // 3. 校验余额
        BigDecimal balance = profile.getWalletBalance() != null ? profile.getWalletBalance() : BigDecimal.ZERO;
        if (balance.compareTo(orderPrice) < 0) {
            throw new ServiceException("余额不足，当前余额：" + balance + "，订单单价：" + orderPrice);
        }

        // 4. 创建订单
        Date now = new Date();
        DhOrder order = new DhOrder();
        order.setOrderNo(generateOrderNo());
        order.setTitle(bo.getTitle());
        order.setApplicantName(userName);
        order.setMemberLevel(profile.getMemberLevel());
        order.setStatus(DhOrderStatus.PENDING);
        order.setIsRedo(0);
        order.setPriority(0);
        order.setScriptText(bo.getScriptText());
        order.setToneStyle(bo.getToneStyle());
        order.setSceneType(bo.getSceneType());
        order.setSpeechSpeed(bo.getSpeechSpeed());
        order.setContactInfo(bo.getContactInfo());
        order.setCopyrightDeclared(bo.getCopyrightDeclared());
        order.setOrderAmount(orderPrice);
        order.setActualAmount(orderPrice);
        order.setDiscountRate(BigDecimal.ONE);
        orderMapper.insert(order);

        // 5. 关联素材（如有）
        if (bo.getMaterialIds() != null && !bo.getMaterialIds().isEmpty()) {
            int sort = 0;
            for (Long materialId : bo.getMaterialIds()) {
                DhOrderMaterial om = new DhOrderMaterial();
                om.setOrderId(order.getOrderId());
                om.setFileId(String.valueOf(materialId));
                om.setSort(sort++);
                orderMaterialMapper.insert(om);
            }
        }

        // 6. 扣减余额
        BigDecimal newBalance = balance.subtract(orderPrice);
        profile.setWalletBalance(newBalance);
        profile.setTotalConsume(
            (profile.getTotalConsume() != null ? profile.getTotalConsume() : BigDecimal.ZERO).add(orderPrice)
        );
        profile.setOrderCount(
            (profile.getOrderCount() != null ? profile.getOrderCount() : 0) + 1
        );
        userProfileMapper.updateById(profile);

        // 7. 记录钱包流水
        DhWalletLog walletLog = new DhWalletLog();
        walletLog.setUserId(userId);
        walletLog.setUserName(userName);
        walletLog.setType("CONSUME");
        walletLog.setAmount(orderPrice.negate()); // 消费为负�?
        walletLog.setBalanceAfter(newBalance);
        walletLog.setRelatedOrderId(order.getOrderId());
        walletLog.setOperatorName(userName);
        walletLog.setRemark("创建订单�? + order.getOrderNo());
        walletLogMapper.insert(walletLog);

        // 8. 记录处理日志
        DhOrderProcessLog processLog = new DhOrderProcessLog();
        processLog.setOrderId(order.getOrderId());
        processLog.setActionText("用户提交订单");
        processLog.setOperatorName(userName);
        processLog.setOperateTime(now);
        orderProcessLogMapper.insert(processLog);

        return R.ok(toPortalOrderVo(order));
    }

    /**
     * C端用户取消订�?
     * <p>
     * �?PENDING 状态可取消，取消后退还余额�?
     */
    @Operation(summary = "取消订单")
    @PostMapping("/{orderId}/cancel")
    @Transactional(rollbackFor = Exception.class)
    public R<Void> cancel(@PathVariable Long orderId,
                          @RequestParam(required = false) String reason) {
        Long userId = LoginHelper.getUserId();
        String userName = LoginHelper.getUsername();

        // 1. 验证订单存在且归属当前用�?
        DhOrder order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new ServiceException("订单不存�?);
        }
        if (!userId.equals(order.getCreateBy())) {
            throw new ServiceException("无权操作该订�?);
        }

        // 2. 校验状�?
        if (!DhOrderStatus.PENDING.equals(order.getStatus())) {
            throw new ServiceException("仅待处理状态的订单可取�?);
        }

        // 3. 取消订单
        Date now = new Date();
        order.setStatus(DhOrderStatus.CANCELLED);
        order.setCancelReason(StringUtils.isNotBlank(reason) ? reason : "用户主动取消");
        orderMapper.updateById(order);

        // 4. 退还余�?
        BigDecimal refundAmount = order.getActualAmount() != null ? order.getActualAmount() : BigDecimal.ZERO;
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            DhUserProfile profile = userProfileMapper.selectById(userId);
            if (profile != null) {
                BigDecimal newBalance = (profile.getWalletBalance() != null ? profile.getWalletBalance() : BigDecimal.ZERO)
                    .add(refundAmount);
                profile.setWalletBalance(newBalance);
                profile.setTotalConsume(
                    (profile.getTotalConsume() != null ? profile.getTotalConsume() : BigDecimal.ZERO).subtract(refundAmount)
                );
                userProfileMapper.updateById(profile);

                // 5. 记录退款流�?
                DhWalletLog walletLog = new DhWalletLog();
                walletLog.setUserId(userId);
                walletLog.setUserName(userName);
                walletLog.setType("REFUND");
                walletLog.setAmount(refundAmount); // 退款为正数
                walletLog.setBalanceAfter(newBalance);
                walletLog.setRelatedOrderId(orderId);
                walletLog.setOperatorName(userName);
                walletLog.setRemark("取消订单退款：" + order.getOrderNo());
                walletLogMapper.insert(walletLog);
            }
        }

        // 6. 记录处理日志
        DhOrderProcessLog processLog = new DhOrderProcessLog();
        processLog.setOrderId(orderId);
        processLog.setActionText("用户取消订单" + (StringUtils.isNotBlank(reason) ? "�? + reason : ""));
        processLog.setOperatorName(userName);
        processLog.setOperateTime(now);
        orderProcessLogMapper.insert(processLog);

        return R.ok();
    }

    // ==================== 私有辅助方法 ====================

    /**
     * �?DhOrder 映射�?C�?PortalOrderVo
     */
    private PortalOrderVo toPortalOrderVo(DhOrder order) {
        PortalOrderVo vo = new PortalOrderVo();
        vo.setOrderId(order.getOrderId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTitle(order.getTitle());
        vo.setStatus(order.getStatus());
        vo.setUserStatus(mapUserStatus(order.getStatus()));
        vo.setIsRedo(order.getIsRedo());
        vo.setCreateTime(order.getCreateTime());
        vo.setOrderAmount(order.getOrderAmount());
        // 预计交付：根�?expectDeliveryHours 计算
        if (order.getExpectDeliveryHours() != null && order.getExpectDeliveryHours() > 0) {
            vo.setExpectedDelivery(order.getExpectDeliveryHours() + "小时�?);
        }
        return vo;
    }

    /**
     * �?B�?DhOrderDetailVo 映射�?C�?PortalOrderDetailVo
     */
    private PortalOrderDetailVo toPortalOrderDetailVo(DhOrder order, DhOrderDetailVo detailVo) {
        PortalOrderDetailVo vo = new PortalOrderDetailVo();
        // 基础字段
        vo.setOrderId(order.getOrderId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTitle(order.getTitle());
        vo.setStatus(order.getStatus());
        vo.setUserStatus(mapUserStatus(order.getStatus()));
        vo.setIsRedo(order.getIsRedo());
        vo.setCreateTime(order.getCreateTime());
        vo.setOrderAmount(order.getOrderAmount());
        if (order.getExpectDeliveryHours() != null && order.getExpectDeliveryHours() > 0) {
            vo.setExpectedDelivery(order.getExpectDeliveryHours() + "小时�?);
        }

        // 详情字段
        vo.setScriptText(detailVo.getScriptText());
        vo.setResultVideoUrl(detailVo.getResultVideoUrl());
        vo.setAssigneeName(detailVo.getAssigneeName());
        vo.setCompletedTime(order.getCompletedTime());
        vo.setCancelReason(detailVo.getCancelReason());
        vo.setRejectReason(detailVo.getRejectReason());
        vo.setRedoReason(detailVo.getRedoReason());
        vo.setActualAmount(detailVo.getActualAmount());

        // 素材列表
        if (detailVo.getMaterialFiles() != null) {
            vo.setMaterials(detailVo.getMaterialFiles().stream().map(mf -> {
                PortalOrderDetailVo.PortalOrderMaterialVo mv = new PortalOrderDetailVo.PortalOrderMaterialVo();
                mv.setFileName(mf.getFileName());
                mv.setFileUrl(mf.getFileUrl());
                mv.setFileType(mf.getFileType());
                mv.setThumbnailUrl(mf.getThumbnailUrl());
                return mv;
            }).toList());
        }

        // 进度节点（从 processLogs 映射�?
        if (detailVo.getProcessLogs() != null) {
            vo.setProgressNodes(detailVo.getProcessLogs().stream().map(log -> {
                PortalOrderDetailVo.PortalProgressNodeVo node = new PortalOrderDetailVo.PortalProgressNodeVo();
                node.setLabel(log.getAction());
                node.setStatus("completed");
                node.setTimestamp(log.getTime());
                node.setDescription(log.getOperator());
                return node;
            }).toList());
        }
        return vo;
    }

    /**
     * 后端状�?�?用户端简化状�?
     */
    private String mapUserStatus(String status) {
        if (status == null) {
            return "pending";
        }
        return switch (status) {
            case DhOrderStatus.PENDING -> "pending";
            case DhOrderStatus.PROCESSING, DhOrderStatus.TO_UPLOAD, DhOrderStatus.REDO -> "processing";
            case DhOrderStatus.COMPLETED -> "completed";
            case DhOrderStatus.CANCELLED -> "cancelled";
            case DhOrderStatus.REJECTED -> "rejected";
            default -> "pending";
        };
    }

    /**
     * 统计指定状态的订单�?
     */
    private long countByUserAndStatus(Long userId, String status) {
        LambdaQueryWrapper<DhOrder> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhOrder::getCreateBy, userId);
        lqw.eq(StringUtils.isNotBlank(status), DhOrder::getStatus, status);
        return orderMapper.selectCount(lqw);
    }

    /**
     * 查询会员等级对应的订单单�?
     */
    private BigDecimal resolveMemberPrice(String memberLevel) {
        if (StringUtils.isBlank(memberLevel)) {
            throw new ServiceException("会员等级信息缺失，请联系客服");
        }
        DhMemberConfigQueryBo queryBo = new DhMemberConfigQueryBo();
        queryBo.setLevel(memberLevel);
        queryBo.setStatus("0");
        PageQuery pq = new PageQuery();
        pq.setPageNum(1);
        pq.setPageSize(1);
        TableDataInfo<DhMemberConfigVo> result = configService.queryMemberConfigPage(queryBo, pq);
        if (result == null || result.getRows() == null || result.getRows().isEmpty()) {
            throw new ServiceException("未找到会员等级定价配�?);
        }
        BigDecimal price = result.getRows().get(0).getOrderPrice();
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("会员等级定价配置异常");
        }
        return price;
    }

    /**
     * 生成订单编号：DH + 日期时间 + 4位随机数
     */
    private String generateOrderNo() {
        return "DH" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + RandomUtil.randomNumbers(4);
    }
}
