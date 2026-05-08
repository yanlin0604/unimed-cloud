-- 患者账户表：新增微信昵称和头像OSS ID字段
ALTER TABLE `ch_patient_account` ADD COLUMN `nickname`       VARCHAR(64) DEFAULT NULL COMMENT '微信昵称' AFTER `auth_expire_time`;
ALTER TABLE `ch_patient_account` ADD COLUMN `avatar_oss_id`  VARCHAR(64) DEFAULT NULL COMMENT '头像OSS ID' AFTER `nickname`;
