-- 清理 chronic-mock-data.sql 中写入的占位 openid/unionid，避免与真实微信 openid 冲突
-- 仅在生产 / 准生产首次接通真实微信登录前执行；演示环境若仍需 mock 命中可跳过
-- 幂等：多次执行结果相同

UPDATE ch_patient_account
   SET openid = NULL,
       unionid = NULL
 WHERE openid LIKE 'wx\_openid\_%' ESCAPE '\\'
    OR unionid LIKE 'wx\_unionid\_%' ESCAPE '\\';
