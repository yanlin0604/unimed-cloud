-- 订单表新增视频格式字段（比例、规格、时长）
-- 执行时间: 2026-03-19
-- 说明: 补全口播订单创建时用户填写的视频输出格式信息

ALTER TABLE `dh_order`
    ADD COLUMN `video_ratio`      VARCHAR(16) DEFAULT NULL COMMENT '视频比例（如 16:9、9:16、1:1）' AFTER `speech_speed`,
    ADD COLUMN `video_resolution` VARCHAR(32) DEFAULT NULL COMMENT '视频规格（如 1920x1080、1080x1920）' AFTER `video_ratio`,
    ADD COLUMN `video_duration`   INT(11)     DEFAULT NULL COMMENT '视频时长（秒）' AFTER `video_resolution`;
