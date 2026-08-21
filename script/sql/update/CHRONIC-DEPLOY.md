# 慢病模块部署清单（数据库脚本执行顺序）

> 更新时间：2026-08-21
> 说明：标记 ✅ 的脚本已在 `47.113.122.118` 环境执行完毕；其余按需执行。

## 一、系统库 `unimed-cloud`

| 顺序 | 脚本 | 作用 | 状态 |
|------|------|------|------|
| 1 | `sys_dept.sql` / `sys_user.sql` | 基础组织与账号（RuoYi 标准示例数据） | ✅ 环境已有 |
| 2 | `chronic-menu-import.sql` | 慢病菜单 184 行，权限码与后端 `@SaCheckPermission` 逐一对齐 | ✅ 已执行旧版本 |
| 3 | `chronic-doctor-role-users.sql` | 医生端权限载体菜单（隐藏，36 权限码）+ `慢病医生` 角色（role_id=100）+ 医生账号 2001~2008（密码 666666） | ✅ 已执行 |
| 4 | `chronic-menu-increment-device-notification.sql` | 增量 12 行：设备管理页（200306）+ 通知模板页（200805）+ 筛查批次 list/edit 按钮（20060404/20060405） | ✅ 已执行 |
| 5 | `chronic-followup-plan-edit-menu.sql` | 增加随访计划完整修改权限 `chronic:followup-plan:edit` | ✅ 已执行 |

**注意事项**

- `chronic-menu-import.sql` 按精确 `menu_id` 列表先删后插，**不使用 `BETWEEN`**（区间会误伤 workflow 菜单 11616~11806）。
- 慢病看板根目录 path 为 `chronic-dashboard`，不能改回 `dashboard`——会与前端静态路由 `/dashboard` 冲突。
- 剩余无菜单承载的菜单只有 2 个：**管理路径进度**（后端无 `ch_clinical_pathway` 相关表与接口）、**自定义分组**（仅 doctor 层有接口）。对应前端 `views/assessment/pathway/`、`views/team/custom-group/` 是诚实空态页，因无菜单实际不可达，属预期。
- 未被菜单承载的后端权限码（11 个）：`chronic:attachment:*`（5）、`chronic:audit:*`（2）、`chronic:ops:*`（3）、`chronic:archive-share:callback`（1）——均为无前端页面的后端专用 API，超管可直接调用，非超管如需使用要另建载体菜单。
- **设备管理 / 通知模板原先被剔除的理由（"后端无 admin 端点"）已失效**：`DeviceController`、`NotificationTemplateController` 已补齐，菜单与前端页面同步于 2026-08-18 接通。

## 二、慢病业务库 `unimed-chronic`

| 顺序 | 脚本 | 作用 | 状态 |
|------|------|------|------|
| 1 | `../unimed-chronic.sql` | 全量建表 | ✅ 环境已有 |
| 2 | `chronic-followup-questionnaire-is-active.sql` | 补 `ch_followup_questionnaire.is_active` 列（问卷启停依赖） | ✅ 已执行 |
| 3 | `chronic-followup-plan-assignee.sql` | 补 `ch_followup_plan.assignee_user_id` 并从现有任务回填 | ✅ 已执行 |
| 4 | `../mock/chronic-mock-data.sql` | 演示数据（如需重建库时执行） | ✅ 环境已有 |

**注意事项**

- `chronic-mock-data.sql` 中 `ch_followup_plan_item.item_config` 的问卷关联键必须是驼峰 `questionnaireId`（后端按驼峰解析），此前的 `questionnaire_id` 已修正。
- 演示数据的医生 `user_id` 为 2001~2008，与系统库 `chronic-doctor-role-users.sql` 建的账号一一对应，否则医生昵称翻译为空且无法登录医生端。

## 三、SnailJob 调度库（未部署）

| 脚本 | 作用 | 状态 |
|------|------|------|
| `../unimed-job.sql` | SnailJob 服务端建表 | ⏳ 环境未部署（远端仅存遗留 `xxl_job` 库） |
| `chronic-snailjob-jobs.sql` | 注册 4 个慢病定时任务（随访任务生成 01:00 / 签约SLA 02:00 / 随访提醒 08:00 / 统计日报 23:50） | ⏳ 待 SnailJob 部署后执行 |

**降级保障**：调度器缺位时随访逾期状态不会自动刷新，因此后端在随访列表查询入口加了 `FollowupOverdueRefresher`（Redis 节流 5 分钟的批量刷新），保证 `OVERDUE` 状态与日期一致；SnailJob 上线后仍由 `FollowupRemindJob` 负责提醒推送。

## 四、验证脚本（只读，可反复执行）

```bash
# 三端前端 API 调用点 vs 后端路由（应为 0 打不通）
python script/sql/update/_check_api_contract.py

# 后端各层权限码 vs 线上菜单承载（doctor 应 36/36，patient/openapi 为 0）
python script/sql/update/_check_perm_coverage.py

# 菜单一致性三查：component 死链 / 死权限码 / 按钮码前端承载率
python script/sql/update/_check_menu_frontend.py

# 生成「页面目录 -> 应挂载的按钮权限码」映射（补按钮鉴权时用）
python script/sql/update/_gen_perm_map.py

# 库表活跃度盘点（识别废表 / 只读不写表），结论见 CHRONIC-DEAD-TABLES.md
python script/sql/update/_check_dead_tables.py
```

**按钮级鉴权约定**：管理端页面的操作按钮必须挂 `v-access:code="['<权限码>']"`，权限码取自本页面菜单（`menu_type='F'`）声明、且等于后端 `@SaCheckPermission` 原文。开关类（Switch）用 `:disabled="!hasAccessByCodes([...])"` 而不是隐藏——隐藏会让状态列变空，看不出当前状态。超管 `*:*:*` 自动全放行（`packages/effects/access/src/use-access.ts:38`）。

## 五、待清理的废表（未执行）

盘点结论见 `CHRONIC-DEAD-TABLES.md`，清理脚本见 `chronic-drop-dead-tables.sql`（**分阶段、默认未执行**）。

摘要：77 张表中 8 张代码零引用（`ch_sos_record`、`ch_webhook_subscription`、`ch_ops_health_check`、`ch_ops_rerun_ticket`、4 张 `ch_stat_*_day`），另有 `ch_clinical_pathway_status` 属"有接口零写入"的未完成功能，需产品决策。统计日表族的去留取决于看板走预聚合还是实时查询，**清理前必须先定这个技术方案**。

## 六、账号速查

| 端 | 账号 | 密码 | 说明 |
|----|------|------|------|
| 管理端 | `admin` | 见环境 | 超管，可见全部慢病菜单 |
| 医生端 | `doctor2001` ~ `doctor2008` | `666666` | 慢病医生角色；2001/2002/2003 为三个团队负责人 |
| 患者端 | 手机号 `13800000001` 等 8 个 | 微信/验证码登录 | `ch_patient_account` 已全部关联患者档案（含 2 个家属代理账号） |
