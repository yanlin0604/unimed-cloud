package org.dromara.dhcore.controller.portal;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.DhPaymentPriceConfig;
import org.dromara.dhcore.domain.DhQrUploadConfig;
import org.dromara.dhcore.domain.DhTopupTicket;
import org.dromara.dhcore.domain.bo.portal.PortalTopupApplyBo;
import org.dromara.dhcore.domain.bo.portal.PortalTopupSupplementBo;
import org.dromara.dhcore.domain.vo.portal.PortalTopupPlanVo;
import org.dromara.dhcore.domain.vo.portal.PortalTopupRecordVo;
import org.dromara.dhcore.mapper.DhPaymentPriceConfigMapper;
import org.dromara.dhcore.mapper.DhQrUploadConfigMapper;
import org.dromara.dhcore.mapper.DhTopupTicketMapper;
import org.dromara.resource.api.RemoteFileService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * C端充值控制器
 * <p>
 * 提供充值档位查询、支付渠道查询、充值申请提交、充值记录查询等接口。
 * 充值申请创建后状态为 PENDING，需后台管理员审核。
 *
 * @author unimed
 */
@Tag(name = "C端-充值管理")
@SaCheckLogin
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/portal/topup")
public class PortalTopupController extends BaseController {

    private final DhPaymentPriceConfigMapper paymentPriceConfigMapper;
    private final DhQrUploadConfigMapper qrUploadConfigMapper;
    private final DhTopupTicketMapper topupTicketMapper;

    @DubboReference
    private RemoteFileService remoteFileService;

    /**
     * 获取充值档位列表
     */
    @Operation(summary = "获取充值档位列表")
    @GetMapping("/plans")
    public R<List<PortalTopupPlanVo>> getPlans() {
        LambdaQueryWrapper<DhPaymentPriceConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhPaymentPriceConfig::getStatus, "0"); // 0=启用
        lqw.orderByAsc(DhPaymentPriceConfig::getSort);
        List<DhPaymentPriceConfig> configs = paymentPriceConfigMapper.selectList(lqw);
        List<PortalTopupPlanVo> plans = configs.stream().map(this::toTopupPlanVo).toList();
        return R.ok(plans);
    }

    /**
     * 获取支付渠道（收款码配置）
     */
    @Operation(summary = "获取支付渠道")
    @GetMapping("/payment-channels")
    public R<List<PaymentChannelVo>> getPaymentChannels() {
        LambdaQueryWrapper<DhQrUploadConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhQrUploadConfig::getStatus, "0");
        List<DhQrUploadConfig> configs = qrUploadConfigMapper.selectList(lqw);
        List<PaymentChannelVo> channels = configs.stream().map(this::toPaymentChannelVo).toList();
        return R.ok(channels);
    }

    /**
     * 提交充值申请
     */
    @Operation(summary = "提交充值申请")
    @PostMapping("/apply")
    public R<PortalTopupRecordVo> apply(@Validated @RequestBody PortalTopupApplyBo bo) {
        Long userId = LoginHelper.getUserId();
        String userName = LoginHelper.getUsername();

        DhTopupTicket ticket = new DhTopupTicket();
        ticket.setUserId(userId);
        ticket.setUserName(userName);
        ticket.setAmount(bo.getAmount());
        ticket.setPaymentType(bo.getPaymentType());
        ticket.setStatus("PENDING");
        ticket.setVoucherImageIds(String.join(",", bo.getVoucherOssIds()));
        ticket.setRemark(bo.getRemark());
        topupTicketMapper.insert(ticket);

        return R.ok(toTopupRecordVo(ticket));
    }

    /**
     * 分页查询当前用户充值记录
     */
    @Operation(summary = "查询充值记录")
    @GetMapping("/records")
    public TableDataInfo<PortalTopupRecordVo> getRecords(
        @RequestParam(required = false) String status,
        PageQuery pageQuery) {
        Long userId = LoginHelper.getUserId();
        LambdaQueryWrapper<DhTopupTicket> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhTopupTicket::getUserId, userId);
        lqw.eq(StringUtils.isNotBlank(status), DhTopupTicket::getStatus, status);
        lqw.orderByDesc(DhTopupTicket::getCreateTime);

        Page<DhTopupTicket> page = topupTicketMapper.selectPage(pageQuery.build(), lqw);
        List<PortalTopupRecordVo> rows = page.getRecords().stream().map(this::toTopupRecordVo).toList();
        return new TableDataInfo<>(rows, page.getTotal());
    }

    // ==================== 私有辅助 ====================

    private PortalTopupPlanVo toTopupPlanVo(DhPaymentPriceConfig config) {
        PortalTopupPlanVo vo = new PortalTopupPlanVo();
        vo.setId(config.getConfigId());
        vo.setConfigName(config.getConfigName());
        vo.setMemberLevel(config.getMemberLevel());
        vo.setPayType(config.getPayType());
        vo.setAmount(config.getAmount());
        vo.setBonusAmount(config.getBonusAmount());
        vo.setSort(config.getSort());
        return vo;
    }

    private PortalTopupRecordVo toTopupRecordVo(DhTopupTicket ticket) {
        PortalTopupRecordVo vo = new PortalTopupRecordVo();
        vo.setId(ticket.getTicketId());
        vo.setAmount(ticket.getAmount());
        vo.setActualAmount(ticket.getActualAmount());
        vo.setStatus(ticket.getStatus());
        vo.setVoucherDesc(ticket.getVoucherDesc());
        vo.setVoucherImageIds(ticket.getVoucherImageIds());
        vo.setCreateTime(ticket.getCreateTime());
        vo.setApprovedAt(ticket.getApprovedAt());
        vo.setRemark(ticket.getRemark());
        vo.setRejectReason(ticket.getRejectReason());
        return vo;
    }

    private PaymentChannelVo toPaymentChannelVo(DhQrUploadConfig config) {
        PaymentChannelVo vo = new PaymentChannelVo();
        vo.setId(config.getConfigId());
        vo.setName(config.getConfigName());
        vo.setType(config.getType());
        vo.setQrImageIds(config.getQrImageIds());
        vo.setQrImageUrl(resolveFirstOssUrl(config.getQrImageIds()));
        vo.setAccountName(config.getAccountName());
        vo.setAccountNo(config.getAccountNo());
        vo.setBankName(config.getBankName());
        return vo;
    }

    /**
     * 将 ossIds（逗号分隔）转换为第一张图片的访问 URL
     */
    private String resolveFirstOssUrl(String ossIds) {
        if (StringUtils.isBlank(ossIds)) {
            return null;
        }
        String firstId = ossIds.split(",")[0].trim();
        if (StringUtils.isBlank(firstId)) {
            return null;
        }
        try {
            String url = remoteFileService.selectUrlByIds(firstId);
            return StringUtils.isBlank(url) ? null : url.split(",")[0].trim();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 补充凭证
     */
    @Operation(summary = "补充支付凭证")
    @PostMapping("/{id}/supplement")
    public R<PortalTopupRecordVo> supplement(@PathVariable Long id,
                                             @Validated @RequestBody PortalTopupSupplementBo bo) {
        Long userId = LoginHelper.getUserId();
        DhTopupTicket ticket = topupTicketMapper.selectById(id);
        if (ticket == null || !userId.equals(ticket.getUserId())) {
            return R.fail("工单不存在或无权操作");
        }
        if (!"NEED_MORE".equals(ticket.getStatus())) {
            return R.fail("当前工单状态不允许补充凭证");
        }
        ticket.setVoucherImageIds(String.join(",", bo.getVoucherOssIds()));
        if (StringUtils.isNotBlank(bo.getVoucherDesc())) {
            ticket.setVoucherDesc(bo.getVoucherDesc());
        }
        ticket.setStatus("PENDING");
        topupTicketMapper.updateById(ticket);
        return R.ok(toTopupRecordVo(ticket));
    }

    /**
     * 支付渠道视图对象（内嵌类）
     */
    @Data
    public static class PaymentChannelVo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;
        private Long id;
        private String name;
        private String type;
        private String qrImageIds;
        /** OSS 图片访问 URL（第一张） */
        private String qrImageUrl;
        private String accountName;
        private String accountNo;
        private String bankName;
    }
}
