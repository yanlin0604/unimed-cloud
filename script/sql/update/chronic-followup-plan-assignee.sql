-- 随访计划补充执行医生字段
ALTER TABLE `ch_followup_plan`
    ADD COLUMN `assignee_user_id` bigint NULL DEFAULT NULL COMMENT '执行医生用户ID' AFTER `disease_code`;

CREATE INDEX `idx_fp_assignee_user_id` ON `ch_followup_plan` (`assignee_user_id`);

UPDATE `ch_followup_plan` p
JOIN (
    SELECT `plan_id`, MIN(`assignee_user_id`) AS `assignee_user_id`
    FROM `ch_followup_task`
    WHERE `assignee_user_id` IS NOT NULL
    GROUP BY `plan_id`
) t ON t.`plan_id` = p.`plan_id`
SET p.`assignee_user_id` = t.`assignee_user_id`
WHERE p.`assignee_user_id` IS NULL;
