-- =====================================================================
-- 慢病模块 - 历史诊疗记录对应时间线(ENCOUNTER)事件回填
-- 背景:
--   ch_patient_timeline 仅在 EncounterManager.submit() 中写入 ENCOUNTER 事件。
--   早期 mock / HIS 同步 / 历史导入直接 INSERT 进 ch_encounter_record 的数据,
--   绕过了 submit 流程,导致 ch_patient_timeline 缺失对应 ENCOUNTER 行。
--   表现:档案总览-最近诊疗卡片显示"暂无记录",但诊疗历史 Tab 有数据。
--
-- 当前修复:档案总览已切换为直接查 ch_encounter_record(代码改动),
--          本脚本用于回填历史时间线,使时间线维度保持完整。
--
-- 幂等性:NOT EXISTS 判定,重复执行不会产生重复行。
-- 创建时间: 2026-05-18
-- =====================================================================

-- ============== 1) 预览(可选,默认注释):统计将要补齐的行数 ==============
-- SELECT COUNT(*) AS will_insert
-- FROM ch_encounter_record e
-- WHERE e.submit_status = 'SUBMITTED'
--   AND e.del_flag = '0'
--   AND NOT EXISTS (
--       SELECT 1 FROM ch_patient_timeline t
--       WHERE t.patient_id = e.patient_id
--         AND t.event_type = 'ENCOUNTER'
--         AND t.event_time = e.encounter_time
--         AND t.del_flag = '0'
--   );

-- ============== 2) 正式回填 ==============
INSERT INTO `ch_patient_timeline`
    (`patient_id`, `event_type`, `event_title`, `event_detail`, `event_time`,
     `create_dept`, `tenant_id`, `create_by`, `create_time`, `del_flag`)
SELECT
    e.patient_id,
    'ENCOUNTER'                                                                       AS event_type,
    '门诊诊疗记录'                                                                      AS event_title,
    CONCAT('就诊类型: ', IFNULL(e.encounter_type, ''),
           ', 就诊时间: ', DATE_FORMAT(e.encounter_time, '%Y-%m-%dT%H:%i:%s'))           AS event_detail,
    COALESCE(e.submitted_time, e.encounter_time, e.create_time)                       AS event_time,
    e.create_dept                                                                     AS create_dept,
    CAST(IFNULL(e.tenant_id, 0) AS CHAR)                                              AS tenant_id,
    e.create_by                                                                       AS create_by,
    NOW()                                                                             AS create_time,
    '0'                                                                               AS del_flag
FROM ch_encounter_record e
WHERE e.submit_status = 'SUBMITTED'
  AND e.del_flag = '0'
  AND NOT EXISTS (
      SELECT 1
      FROM ch_patient_timeline t
      WHERE t.patient_id = e.patient_id
        AND t.event_type = 'ENCOUNTER'
        AND t.event_time = e.encounter_time
        AND t.del_flag = '0'
  );

-- ============== 3) 校验(可选,默认注释):确认每条 SUBMITTED 记录都有对应时间线 ==============
-- SELECT e.id AS encounter_id, e.patient_id, e.encounter_time
-- FROM ch_encounter_record e
-- WHERE e.submit_status = 'SUBMITTED'
--   AND e.del_flag = '0'
--   AND NOT EXISTS (
--       SELECT 1 FROM ch_patient_timeline t
--       WHERE t.patient_id = e.patient_id
--         AND t.event_type = 'ENCOUNTER'
--         AND t.event_time = e.encounter_time
--         AND t.del_flag = '0'
--   );
