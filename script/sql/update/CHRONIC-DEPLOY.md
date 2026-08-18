# 慢病模块部署清单（数据库脚本执行顺序）

> 更新时间：2026-08-18
> 说明：标记 ✅ 的脚本已在 `47.113.122.118` 环境执行完毕；其余按需执行。

## 一、系统库 `unimed-cloud`

| 顺序 | 脚本 | 作用 | 状态 |
|------|------|------|------|
| 1 | `sys_dept.sql` / `sys_user.sql` | 基础组织与账号（RuoYi 标准示例数据） | ✅ 环境已有 |
| 2 | `chronic-menu-import.sql` | 慢病菜单 183 行（7 根目录 / 34 页面 / 124 按钮），权限码与后端 `@SaCheckPermission` 逐一对齐 | ✅ 已执行 |
| 3 | `chronic-doctor-role-users.sql` | 医生端权限载体菜单（隐藏，36 权限码）+ `慢病医生` 角色（role_id=100）+ 医生账号 2001~2008（密码 666666） | ✅ 已执行 |

**注意事项**

- `chronic-menu-import.sql` 按精确 `menu_id` 列表先删后插，**不使用 `BETWEEN`**（区间会误伤 workflow 菜单 11616~11806）。
- 慢病看板根目录 path 为 `chronic-dashboard`，不能改回 `dashboard`——会与前端静态路由 `/dashboard` 冲突。
- 已剔除后端无端点的 4 个菜单：设备管理、管理路径进度、自定义分组（仅 doctor 层）、通知模板。
- 未被菜单承载的后端权限码（11 个）：`chronic:attachment:*`、`chronic:audit:*`、`chronic:ops:*`、`chronic:archive-share:callback`——均为无前端页面的后端专用 API，超管可直接调用，非超管如需使用要另建载体菜单。

## 二、慢病业务库 `unimed-chronic`

| 顺序 | 脚本 | 作用 | 状态 |
|------|------|------|------|
| 1 | `../unimed-chronic.sql` | 全量建表 | ✅ 环境已有 |
| 2 | `chronic-followup-questionnaire-is-active.sql` | 补 `ch_followup_questionnaire.is_active` 列（问卷启停依赖） | ✅ 已执行 |
| 3 | `../mock/chronic-mock-data.sql` | 演示数据（如需重建库时执行） | ✅ 环境已有 |

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
```

## 五、账号速查

| 端 | 账号 | 密码 | 说明 |
|----|------|------|------|
| 管理端 | `admin` | 见环境 | 超管，可见全部慢病菜单 |
| 医生端 | `doctor2001` ~ `doctor2008` | `666666` | 慢病医生角色；2001/2002/2003 为三个团队负责人 |
| 患者端 | 手机号 `13800000001` 等 8 个 | 微信/验证码登录 | `ch_patient_account` 已全部关联患者档案（含 2 个家属代理账号） |
