-- ============================================================
-- 修复：ch_area_dict / ch_audit_log 缺 tenant_id 列导致查询报错
-- 执行库：unimed-chronic
--
-- 问题：多租户插件 PlusTenantLineHandler.ignoreTable() **按表名**判断是否过滤，
--       只有落在 tenant.excludes 或 gen_table/gen_table_column 里的表才跳过。
--       慢病表全部不在 excludes 中，因此插件会给这两张表的 SQL 强行拼上
--       `tenant_id = '000000'` 条件 → MySQL 报 Unknown column 'tenant_id'。
--       ch_area_dict 还额外因为 ChAreaDict extends TenantEntity（实体自带 tenantId 字段），
--       MyBatis-Plus 生成的 SELECT 列清单里也会带 tenant_id，两条路径都会失败。
--
-- 影响：ch_area_dict —— 行政区划字典全部查询 500（AreaController 的 tree/mapping/stats）
--       ch_audit_log —— 审计日志写入报错。线上现有 7 行是 mock 原生 INSERT 灌的
--                       （create_time 全为 2026-04-28 15:35:20），从未经应用写入，故尚未暴露。
--
-- 修法选择：补列，而不是加进 tenant.excludes。
--   理由：同类字典表 ch_icd_dict / ch_disease_config **都有** tenant_id 列（实测），
--         补列与项目既有约定一致，且不需要改 nacos 配置或实体继承关系。
--
-- 列定义与回填值均对齐现有表实测结果：bigint NULL DEFAULT NULL，存量数据 tenant_id = 0。
-- 幂等性：MySQL 不支持 ADD COLUMN IF NOT EXISTS，重复执行报 1060 Duplicate column，属预期可忽略。
-- ============================================================

-- ---------- 1. 补列 ----------
ALTER TABLE `ch_area_dict` ADD COLUMN `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID' AFTER `create_dept`;
ALTER TABLE `ch_audit_log` ADD COLUMN `tenant_id` bigint NULL DEFAULT NULL COMMENT '租户ID' AFTER `create_dept`;

-- ---------- 2. 回填存量数据（与其它表一致，默认租户为 0） ----------
UPDATE `ch_area_dict` SET `tenant_id` = 0 WHERE `tenant_id` IS NULL;
UPDATE `ch_audit_log` SET `tenant_id` = 0 WHERE `tenant_id` IS NULL;

-- ---------- 3. 补索引（与其它表的 idx_*_tenant_id 约定一致） ----------
ALTER TABLE `ch_area_dict` ADD INDEX `idx_area_dict_tenant_id`(`tenant_id`);
ALTER TABLE `ch_audit_log` ADD INDEX `idx_audit_log_tenant_id`(`tenant_id`);

-- ---------- 4. 校验（只读，应输出 2 行且 has_col=1、null_rows=0） ----------
SELECT 'ch_area_dict' AS tbl,
       (SELECT COUNT(*) FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA='unimed-chronic' AND TABLE_NAME='ch_area_dict' AND COLUMN_NAME='tenant_id') AS has_col,
       (SELECT COUNT(*) FROM `ch_area_dict` WHERE `tenant_id` IS NULL) AS null_rows
UNION ALL
SELECT 'ch_audit_log',
       (SELECT COUNT(*) FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA='unimed-chronic' AND TABLE_NAME='ch_audit_log' AND COLUMN_NAME='tenant_id'),
       (SELECT COUNT(*) FROM `ch_audit_log` WHERE `tenant_id` IS NULL);

-- ============================================================
-- 备注：ch_ops_health_check 同样缺 tenant_id，但它无实体、无 Mapper、代码零引用
-- （属 CHRONIC-DEAD-TABLES.md 列出的 8 张废表之一），不会被插件命中，无需处理。
--
-- 遗留隐患（本脚本不处理，需另行决策）：
-- 插件生成的是 StringValue('000000') 对 bigint 列比较，靠 MySQL 隐式转换恰好等于 0。
-- 单租户下可用，一旦新增真实租户或修改列类型即失效。根治需统一 tenant_id 的类型约定。
-- ============================================================
