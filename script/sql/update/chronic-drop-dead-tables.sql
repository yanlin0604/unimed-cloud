-- ============================================================
-- 慢病库废表清理脚本（盘点依据：CHRONIC-DEAD-TABLES.md）
-- 执行库：unimed-chronic
--
-- ⚠️ 本脚本【尚未执行】。不要整个文件一次性跑，按下面的阶段分批执行。
--
-- 设计原则：先 RENAME 归档、后 DROP。
--   RENAME 的好处：数据完整保留、任何隐藏依赖（漏检的原生 SQL / 外部系统直连）
--   会在下一次调用时立刻报「表不存在」暴露出来，且一条语句就能回滚。
--   直接 DROP 一旦漏检就只能从备份恢复。
--
-- 盘点结论：以下表的表名在整个 unimed-chronic-biz 的 java/xml 中零出现，
--   无 @TableName 实体、无 Mapper、无任何业务代码引用。
-- ============================================================


-- ============================================================
-- 阶段 0：执行前置检查（只读，务必先跑）
-- ============================================================

-- 0.1 确认这 8 张表当前的数据量，留档
SELECT TABLE_NAME, TABLE_ROWS, TABLE_COMMENT
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'unimed-chronic'
  AND TABLE_NAME IN (
    'ch_sos_record', 'ch_webhook_subscription',
    'ch_ops_health_check', 'ch_ops_rerun_ticket',
    'ch_stat_disease_day', 'ch_stat_followup_day',
    'ch_stat_org_day', 'ch_stat_warning_day'
  )
ORDER BY TABLE_NAME;

-- 0.2 确认没有外键指向这 8 张表（本库建表未使用外键，预期返回 0 行）
SELECT TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_SCHEMA = 'unimed-chronic'
  AND REFERENCED_TABLE_NAME IN (
    'ch_sos_record', 'ch_webhook_subscription',
    'ch_ops_health_check', 'ch_ops_rerun_ticket',
    'ch_stat_disease_day', 'ch_stat_followup_day',
    'ch_stat_org_day', 'ch_stat_warning_day'
  );

-- 0.3 确认没有视图引用它们（预期返回 0 行）
SELECT TABLE_NAME
FROM information_schema.VIEWS
WHERE TABLE_SCHEMA = 'unimed-chronic'
  AND (VIEW_DEFINITION LIKE '%ch_sos_record%'
    OR VIEW_DEFINITION LIKE '%ch_webhook_subscription%'
    OR VIEW_DEFINITION LIKE '%ch_ops_%'
    OR VIEW_DEFINITION LIKE '%ch_stat_%');


-- ============================================================
-- 阶段 1A：清理确定无争议的 4 张（推荐先只做这批）
--
--   ch_sos_record            SOS 已改用 ch_warning_event 实现，此表被架空
--   ch_webhook_subscription  /webhook/subscribe 端点不落库，订阅能力不存在
--   ch_ops_health_check      /ops/health-check 实时计算，不落库
--   ch_ops_rerun_ticket      /ops/task-rerun 走工作流审批，不落库
--
-- 这 4 张与「看板是否走预聚合」的技术选型无关，可直接归档。
-- ============================================================

RENAME TABLE `ch_sos_record`           TO `zz_dead_20260819_ch_sos_record`;
RENAME TABLE `ch_webhook_subscription` TO `zz_dead_20260819_ch_webhook_subscription`;
RENAME TABLE `ch_ops_health_check`     TO `zz_dead_20260819_ch_ops_health_check`;
RENAME TABLE `ch_ops_rerun_ticket`     TO `zz_dead_20260819_ch_ops_rerun_ticket`;


-- ============================================================
-- 阶段 1B：统计日表族 —— 【先定技术方案再执行，默认不要跑】
--
-- 这 4 张表是「预聚合看板」方案的预留设计，不是垃圾。
-- 详见 CHRONIC-DEAD-TABLES.md 第三节「与看板问题的关联」。
--
--   若选择【预聚合方案】（部署 SnailJob + 跑统计日报任务）
--       → 这 4 张表要保留并接上代码，本段【不要执行】
--
--   若选择【实时 count 查询方案】（放弃预聚合）
--       → 执行本段；并且 ch_stat_area_day 也要一并处理，
--         但它有实体和 Mapper（被 DashboardManager 使用），
--         必须先改完 Java 代码再动表，否则服务启动即报错。
-- ============================================================

-- RENAME TABLE `ch_stat_disease_day`  TO `zz_dead_20260819_ch_stat_disease_day`;
-- RENAME TABLE `ch_stat_followup_day` TO `zz_dead_20260819_ch_stat_followup_day`;
-- RENAME TABLE `ch_stat_org_day`      TO `zz_dead_20260819_ch_stat_org_day`;
-- RENAME TABLE `ch_stat_warning_day`  TO `zz_dead_20260819_ch_stat_warning_day`;


-- ============================================================
-- 阶段 2：观察期结束后物理删除（建议观察 ≥ 2 周，且至少覆盖一个完整业务周期）
--
-- 前置条件：
--   1. 慢病服务在观察期内正常启停过，无「表不存在」类报错
--   2. 已确认 zz_dead_ 表在观察期内行数无增长（说明确实没人写）
--   3. 已做整库备份
-- ============================================================

-- 2.1 观察期核对：确认归档表行数未增长（与阶段 0.1 留档的数字比对）
-- SELECT TABLE_NAME, TABLE_ROWS FROM information_schema.TABLES
--  WHERE TABLE_SCHEMA='unimed-chronic' AND TABLE_NAME LIKE 'zz_dead_20260819_%'
--  ORDER BY TABLE_NAME;

-- 2.2 物理删除
-- DROP TABLE IF EXISTS `zz_dead_20260819_ch_sos_record`;
-- DROP TABLE IF EXISTS `zz_dead_20260819_ch_webhook_subscription`;
-- DROP TABLE IF EXISTS `zz_dead_20260819_ch_ops_health_check`;
-- DROP TABLE IF EXISTS `zz_dead_20260819_ch_ops_rerun_ticket`;
-- DROP TABLE IF EXISTS `zz_dead_20260819_ch_stat_disease_day`;
-- DROP TABLE IF EXISTS `zz_dead_20260819_ch_stat_followup_day`;
-- DROP TABLE IF EXISTS `zz_dead_20260819_ch_stat_org_day`;
-- DROP TABLE IF EXISTS `zz_dead_20260819_ch_stat_warning_day`;


-- ============================================================
-- 回滚：把归档表改回原名即可，数据无损
-- ============================================================

-- RENAME TABLE `zz_dead_20260819_ch_sos_record`           TO `ch_sos_record`;
-- RENAME TABLE `zz_dead_20260819_ch_webhook_subscription` TO `ch_webhook_subscription`;
-- RENAME TABLE `zz_dead_20260819_ch_ops_health_check`     TO `ch_ops_health_check`;
-- RENAME TABLE `zz_dead_20260819_ch_ops_rerun_ticket`     TO `ch_ops_rerun_ticket`;
-- RENAME TABLE `zz_dead_20260819_ch_stat_disease_day`     TO `ch_stat_disease_day`;
-- RENAME TABLE `zz_dead_20260819_ch_stat_followup_day`    TO `ch_stat_followup_day`;
-- RENAME TABLE `zz_dead_20260819_ch_stat_org_day`         TO `ch_stat_org_day`;
-- RENAME TABLE `zz_dead_20260819_ch_stat_warning_day`     TO `ch_stat_warning_day`;


-- ============================================================
-- 建表脚本同步（清理落地后再改，避免重建库时废表又回来）
--
-- script/sql/unimed-chronic.sql 里对应的 CREATE TABLE 段落需一并删除：
--   ch_sos_record            约 1760 行起
--   ch_webhook_subscription  约 1983 行起
--   ch_ops_health_check      约 1251 行起
--   ch_ops_rerun_ticket      约 1275 行起
--   ch_stat_disease_day      约 1814 行起
--   ch_stat_followup_day     约 1837 行起
--   ch_stat_org_day          约 1859 行起
--   ch_stat_warning_day      约 1883 行起
-- ============================================================


-- ============================================================
-- 不在本脚本范围内：ch_clinical_pathway_status
--
-- 该表有完整代码骨架（实体/Mapper/Service/VO/doctor 层端点）但零写入逻辑，
-- 属「功能没做完」而非「废表」，需先做产品决策（做完 / 砍掉 / 维持现状）。
-- 详见 CHRONIC-DEAD-TABLES.md 第四节。砍掉时除删表外还需同步删除：
--   domain/entity/ChClinicalPathwayStatus.java
--   mapper/ChClinicalPathwayStatusMapper.java
--   service/IClinicalPathwayService.java
--   service/impl/ClinicalPathwayServiceImpl.java
--   domain/vo/PathwayProgressVo.java
--   controller/doctor/DoctorPatientController.java 中 getPathwayProgress 端点
--   unimed-cloud.sys_menu 中 menu_id=200905（chronic:doctor:patient:pathway）
--   前端 web/chronic-admin-vue .../views/assessment/pathway/
-- ============================================================
