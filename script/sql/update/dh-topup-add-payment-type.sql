-- 充值工单表新增支付方式字段
-- 对应需求：C端充值流程完善 - Requirement 5.1
ALTER TABLE dh_topup_ticket
    ADD COLUMN payment_type VARCHAR(20) NULL COMMENT '支付方式(WECHAT/ALIPAY/BANK_CARD)' AFTER voucher_desc;
