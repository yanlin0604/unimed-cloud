-- C端门户认证：添加密码字段
-- 执行此脚本前请备份数据

ALTER TABLE `dh_user_profile` 
ADD COLUMN `password` varchar(100) DEFAULT NULL COMMENT '密码（BCrypt加密）' AFTER `phone`;
