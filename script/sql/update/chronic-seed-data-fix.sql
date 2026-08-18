-- ============================================================
-- 慢病模块种子数据修正（库 unimed-chronic）
-- 背景：mock/chronic-mock-data.sql 中这几张表的 INSERT 早先因表不存在而未执行，
--       补建表后灌入的数据存在三处与当前代码不一致：
--   1. ch_ocr_task.doc_type / input_type 用的是已废弃的短枚举
--      （LAB/EXAM/DISCHARGE/MEDICAL_HOME、OSS/PDF），
--      后端 BaiduOcrClient.resolveOcrUrl 与字典 chronic_ocr_document_type
--      认的是长枚举，短值会全部落到 default 分支且翻译不出标签。
--   2. patient_id 引用 1011/1014/1019，而 ch_patient_profile 只有 1001-1010，
--      导致列表能查出行、点进详情却没有患者。
--   3. report_draft_json / raw_ocr_json 全为 NULL，
--      患者端 ocr-detail.vue 会对每条任务都显示「暂无结构化识别结果」。
-- 幂等：全部是带 WHERE 的 UPDATE，可重复执行。
-- ============================================================

-- ---------- 1. OCR 枚举对齐当前代码 ----------
UPDATE `ch_ocr_task` SET `doc_type` = 'LAB_REPORT'          WHERE `doc_type` = 'LAB';
UPDATE `ch_ocr_task` SET `doc_type` = 'EXAM_REPORT'         WHERE `doc_type` = 'EXAM';
UPDATE `ch_ocr_task` SET `doc_type` = 'DISCHARGE_SUMMARY'   WHERE `doc_type` = 'DISCHARGE';
UPDATE `ch_ocr_task` SET `doc_type` = 'MEDICAL_RECORD_HOME' WHERE `doc_type` = 'MEDICAL_HOME';

UPDATE `ch_ocr_task` SET `input_type` = 'OSS_FILE' WHERE `input_type` = 'OSS';
UPDATE `ch_ocr_task` SET `input_type` = 'PDF_FILE' WHERE `input_type` = 'PDF';

-- ---------- 2. 悬空 patient_id 收敛到真实患者 1001-1010 ----------
UPDATE `ch_ocr_task`     SET `patient_id` = 1005 WHERE `patient_id` = 1011;
UPDATE `ch_ocr_task`     SET `patient_id` = 1006 WHERE `patient_id` = 1014;
UPDATE `ch_ocr_task`     SET `patient_id` = 1007 WHERE `patient_id` = 1019;
UPDATE `ch_lab_test`     SET `patient_id` = 1005 WHERE `patient_id` = 1011;
UPDATE `ch_lab_test`     SET `patient_id` = 1006 WHERE `patient_id` = 1014;
UPDATE `ch_lab_test`     SET `patient_id` = 1007 WHERE `patient_id` = 1019;
UPDATE `ch_medical_exam` SET `patient_id` = 1005 WHERE `patient_id` = 1011;
UPDATE `ch_medical_exam` SET `patient_id` = 1006 WHERE `patient_id` = 1014;
UPDATE `ch_medical_exam` SET `patient_id` = 1007 WHERE `patient_id` = 1019;
UPDATE `ch_sos_record`   SET `patient_id` = 1006 WHERE `patient_id` = 1014;
UPDATE `ch_sos_record`   SET `patient_id` = 1007 WHERE `patient_id` = 1019;

-- FAILED 任务的 patient_id 为 NULL 属正常（识别失败未匹配到患者），保留不动。

-- ---------- 3. 识别成功/已确认的任务补草稿 JSON ----------
-- 结构与患者端 ocr-detail.vue 的 parseIndicators 约定一致：
-- {"reportItems":[{itemName,resultValue,unit,referenceRange,isAbnormal}]}
UPDATE `ch_ocr_task` SET `report_draft_json` = JSON_OBJECT('reportItems', JSON_ARRAY(
    JSON_OBJECT('itemName','空腹血糖','resultValue','8.6','unit','mmol/L','referenceRange','3.9-6.1','isAbnormal',true),
    JSON_OBJECT('itemName','糖化血红蛋白','resultValue','7.9','unit','%','referenceRange','4.0-6.0','isAbnormal',true),
    JSON_OBJECT('itemName','总胆固醇','resultValue','5.1','unit','mmol/L','referenceRange','3.1-5.2','isAbnormal',false),
    JSON_OBJECT('itemName','甘油三酯','resultValue','2.4','unit','mmol/L','referenceRange','0.4-1.7','isAbnormal',true),
    JSON_OBJECT('itemName','低密度脂蛋白','resultValue','3.0','unit','mmol/L','referenceRange','0-3.4','isAbnormal',false)
))
WHERE `doc_type` = 'LAB_REPORT' AND `task_status` IN ('SUCCESS','CONFIRMED') AND `report_draft_json` IS NULL;

UPDATE `ch_ocr_task` SET `report_draft_json` = JSON_OBJECT('reportItems', JSON_ARRAY(
    JSON_OBJECT('itemName','检查部位','resultValue','双眼底','unit','','referenceRange','','isAbnormal',false),
    JSON_OBJECT('itemName','影像所见','resultValue','视网膜微血管瘤，散在点状出血','unit','','referenceRange','','isAbnormal',true),
    JSON_OBJECT('itemName','诊断结论','resultValue','糖尿病视网膜病变Ⅱ期','unit','','referenceRange','','isAbnormal',true)
))
WHERE `doc_type` = 'EXAM_REPORT' AND `task_status` IN ('SUCCESS','CONFIRMED') AND `report_draft_json` IS NULL;

-- 原始 OCR JSON：按百度 OCR words_result 结构，供「原始识别文本」区块展示
UPDATE `ch_ocr_task` SET `raw_ocr_json` = JSON_OBJECT(
    'words_result_num', 6,
    'words_result', JSON_ARRAY(
        JSON_OBJECT('words','检验报告单'),
        JSON_OBJECT('words','姓名：*** 性别：* 年龄：**'),
        JSON_OBJECT('words','空腹血糖 8.6 mmol/L ↑ 参考值 3.9-6.1'),
        JSON_OBJECT('words','糖化血红蛋白 7.9 % ↑ 参考值 4.0-6.0'),
        JSON_OBJECT('words','总胆固醇 5.1 mmol/L 参考值 3.1-5.2'),
        JSON_OBJECT('words','报告日期：2026-04-20')
    ))
WHERE `task_status` IN ('SUCCESS','CONFIRMED') AND `raw_ocr_json` IS NULL;

-- 失败任务补错误信息，便于管理端排错列表有内容可看
UPDATE `ch_ocr_task`
   SET `error_code` = 'OCR_IMAGE_UNRECOGNIZED',
       `error_msg`  = '图片模糊或非医疗文档，未能识别出有效字段'
 WHERE `task_status` = 'FAILED' AND (`error_code` IS NULL OR `error_code` = '');

-- ---------- 4. 识别完成时间补齐（recognized_at 为空会让耗时统计为 NULL） ----------
UPDATE `ch_ocr_task` SET `recognized_at` = DATE_ADD(`create_time`, INTERVAL 8 SECOND)
 WHERE `task_status` IN ('SUCCESS','CONFIRMED','FAILED') AND `recognized_at` IS NULL AND `create_time` IS NOT NULL;
