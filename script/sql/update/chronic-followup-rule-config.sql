-- -------------------------------------------------------------
-- 慢病随访排期规则可配置化 DDL + 种子数据
-- 适用于 unimed-chronic 数据库
--
-- 背景: 随访排期规则(病种×风险等级 -> 建议间隔/首轮到期/随访方式/方案建议)
--       此前硬编码在 FollowupRuleEngine 的 switch 中,本次迁移为可运营配置表。
-- 约束: 零行为回归。引擎"查表优先、内置 switch 兜底",表无数据时行为与现状完全一致。
--       删除本脚本/清空 ch_followup_rule 不会影响历史随访计划与任务。
--
-- 本脚本为非破坏性幂等脚本:建表用 CREATE TABLE IF NOT EXISTS(表已存在则跳过),
-- 种子数据用 INSERT IGNORE(唯一键 (tenant_id, disease_code, risk_level) 冲突时保留
-- 已有配置、不重建不覆盖),不会删除或重置任何管理端已配置的数据。
--
-- 【2026-08-31 逐轮生成模型修订】
--   1) 随访任务不再按 total_rounds 一次性预生成：任何创建入口只生成首轮，
--      后续轮次仅由医生完成本轮随访时填写「下次随访日期」驱动。
--      因此 total_rounds 统一固定为 1（= 规则生成轮次），不再表达"年度随访总轮数"。
--      与 FollowupRuleEngine 内置兜底默认值保持一致（兜底同样恒为 1）。
--   2) cycle_days 语义为「建议间隔(天)」：仅用于计划展示与医生排期参考，
--      不再参与任务到期日计算（原 firstDueDate + cycle_days*(round-1) 公式已移除）。
--   3) 移除 require_face_to_face_rounds 列：面对面随访机制已于 2026-08-27 整体下线
--      （该列已在生产库 DROP）。此前本脚本仍保留该列，导致在已上线库上执行种子
--      INSERT 直接报 Unknown column 错误、新环境无法初始化规则数据 —— 本次一并修正。
--   4) summary_advice 文案统一使用「线下随访」口径，不再出现"面对面"。
-- -------------------------------------------------------------

CREATE TABLE IF NOT EXISTS `ch_followup_rule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `disease_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '病种编码(HTN/T2DM/COPD/CHD/STROKE/CKD/TUMOR/DYSLIPID 等，取值域为 ch_disease_config)',
  `risk_level` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '风险/管理等级: LOW/MEDIUM/HIGH/VERY_HIGH/ANY(通配)',
  `cycle_days` int NOT NULL COMMENT '建议随访间隔(天),仅用于展示与医生排期参考,不参与到期日计算',
  `total_rounds` int NOT NULL DEFAULT 1 COMMENT '规则生成轮次:逐轮模型下固定为1(仅首轮),后续轮次由医生决定',
  `first_due_days` int NULL DEFAULT 7 COMMENT '首轮到期天数(新建档/确诊后)',
  `default_visit_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PHONE' COMMENT '默认随访方式(首轮): PHONE/ONLINE/OFFLINE/VIDEO',
  `summary_advice` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '方案建议文案(展示用,含临床随访频次规范)',
  `is_active` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用: 0-否 1-是',
  `create_dept` bigint NULL DEFAULT NULL COMMENT '创建部门',
  `tenant_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '租户ID',
  `create_by` bigint NULL DEFAULT NULL COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` bigint NULL DEFAULT NULL COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0存在 1删除)',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_tenant_disease_level` (`tenant_id`, `disease_code`, `risk_level`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '慢病随访排期规则配置表' ROW_FORMAT = Dynamic;

-- -------------------------------------------------------------
-- 种子数据: 病种×风险等级的建议间隔与方案建议文案
-- (与 FollowupRuleEngine 内置兜底一致,即使配置被删也能由 switch 兜底保证一致性)
-- 统一档病种(COPD/TUMOR/DYSLIPID)用 ANY 行,顺带验证通配语义
-- total_rounds 全部为 1: 规则只负责生成首轮
-- -------------------------------------------------------------
INSERT IGNORE INTO `ch_followup_rule`
  (`disease_code`, `risk_level`, `cycle_days`, `total_rounds`, `first_due_days`, `default_visit_type`, `summary_advice`, `is_active`, `tenant_id`, `create_by`, `create_time`, `del_flag`) VALUES
('HTN', 'HIGH', 30, 1, 7, 'PHONE', '高血压三级管理(高危/极高危):至少每1个月随访1次,重点监测靶器官损害、血压达标情况及药物不良反应。', 1, '000000', 1, NOW(), '0'),
('HTN', 'VERY_HIGH', 30, 1, 7, 'PHONE', '高血压三级管理(高危/极高危):至少每1个月随访1次,重点监测靶器官损害、血压达标情况及药物不良反应。', 1, '000000', 1, NOW(), '0'),
('HTN', 'MEDIUM', 60, 1, 7, 'PHONE', '高血压二级管理(中危):至少每2个月随访1次,指导规律用药与生活方式干预。', 1, '000000', 1, NOW(), '0'),
('HTN', 'LOW', 90, 1, 7, 'PHONE', '高血压一级管理(低危):至少每3个月随访1次,其中线下随访结合电话/线上随访。', 1, '000000', 1, NOW(), '0'),
('T2DM', 'HIGH', 30, 1, 7, 'PHONE', '2型糖尿病强化管理(血糖不达标或伴并发症):每1个月随访1次,监测空腹/餐后血糖及胰岛素用药反应。', 1, '000000', 1, NOW(), '0'),
('T2DM', 'VERY_HIGH', 30, 1, 7, 'PHONE', '2型糖尿病强化管理(血糖不达标或伴并发症):每1个月随访1次,监测空腹/餐后血糖及胰岛素用药反应。', 1, '000000', 1, NOW(), '0'),
('T2DM', 'MEDIUM', 90, 1, 7, 'PHONE', '2型糖尿病常规管理(血糖达标且稳定):每3个月随访1次,线下随访结合电话/线上随访。', 1, '000000', 1, NOW(), '0'),
('T2DM', 'LOW', 90, 1, 7, 'PHONE', '2型糖尿病常规管理(血糖达标且稳定):每3个月随访1次,线下随访结合电话/线上随访。', 1, '000000', 1, NOW(), '0'),
('COPD', 'ANY', 90, 1, 7, 'PHONE', '慢阻肺患者管理:每3个月随访1次,评估CAT/mMRC呼吸困难分级与吸入剂依从性。', 1, '000000', 1, NOW(), '0'),
('CHD', 'HIGH', 30, 1, 7, 'PHONE', '心脑血管重症/急性发作恢复期强化随访:每月随访1次,评估神经缺损/心绞痛发作与抗栓药物依从性。', 1, '000000', 1, NOW(), '0'),
('CHD', 'VERY_HIGH', 30, 1, 7, 'PHONE', '心脑血管重症/急性发作恢复期强化随访:每月随访1次,评估神经缺损/心绞痛发作与抗栓药物依从性。', 1, '000000', 1, NOW(), '0'),
('CHD', 'MEDIUM', 60, 1, 7, 'PHONE', '心脑血管常规二级预防管理:每2个月随访1次,维持血压血脂达标。', 1, '000000', 1, NOW(), '0'),
('CHD', 'LOW', 60, 1, 7, 'PHONE', '心脑血管常规二级预防管理:每2个月随访1次,维持血压血脂达标。', 1, '000000', 1, NOW(), '0'),
('STROKE', 'HIGH', 30, 1, 7, 'PHONE', '心脑血管重症/急性发作恢复期强化随访:每月随访1次,评估神经缺损/心绞痛发作与抗栓药物依从性。', 1, '000000', 1, NOW(), '0'),
('STROKE', 'VERY_HIGH', 30, 1, 7, 'PHONE', '心脑血管重症/急性发作恢复期强化随访:每月随访1次,评估神经缺损/心绞痛发作与抗栓药物依从性。', 1, '000000', 1, NOW(), '0'),
('STROKE', 'MEDIUM', 60, 1, 7, 'PHONE', '心脑血管常规二级预防管理:每2个月随访1次,维持血压血脂达标。', 1, '000000', 1, NOW(), '0'),
('STROKE', 'LOW', 60, 1, 7, 'PHONE', '心脑血管常规二级预防管理:每2个月随访1次,维持血压血脂达标。', 1, '000000', 1, NOW(), '0'),
('CKD', 'HIGH', 30, 1, 7, 'PHONE', 'CKD 3~5期强化管理:每月随访1次,监测尿蛋白、肾功能与水肿情况。', 1, '000000', 1, NOW(), '0'),
('CKD', 'VERY_HIGH', 30, 1, 7, 'PHONE', 'CKD 3~5期强化管理:每月随访1次,监测尿蛋白、肾功能与水肿情况。', 1, '000000', 1, NOW(), '0'),
('CKD', 'MEDIUM', 90, 1, 7, 'PHONE', 'CKD 1~2期常规管理:每3个月随访1次,控制血压与低蛋白饮食指导。', 1, '000000', 1, NOW(), '0'),
('CKD', 'LOW', 90, 1, 7, 'PHONE', 'CKD 1~2期常规管理:每3个月随访1次,控制血压与低蛋白饮食指导。', 1, '000000', 1, NOW(), '0'),
('TUMOR', 'ANY', 60, 1, 7, 'PHONE', '肿瘤康复随访:每2个月随访1次,评估体能状态(ECOG)、癌痛评分与定期复查进度。', 1, '000000', 1, NOW(), '0'),
('DYSLIPID', 'ANY', 90, 1, 7, 'PHONE', '通用慢病规范化随访管理:每3个月随访1次。', 1, '000000', 1, NOW(), '0');

-- 备注: 未显式登记的等级(如 HTN 无 ANY)在未命中时仍由代码 switch 兜底,兜底 total_rounds 同样为 1。
-- DYSLIPID 未在 switch 中出现,系统走 default 内置默认档,此处显式登记为与 default 相同语义,便于运营查看。
-- 不设「跨病种通用规则」这一档: 未配规则的病种一律由 FollowupRuleEngine 内置 switch default 兜底。
