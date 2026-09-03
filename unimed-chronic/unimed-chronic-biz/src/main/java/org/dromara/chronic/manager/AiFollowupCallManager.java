package org.dromara.chronic.manager;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.bo.ChAiCallTaskBo;
import org.dromara.chronic.domain.entity.ChAiCallTask;
import org.dromara.chronic.domain.entity.ChFollowupRecord;
import org.dromara.chronic.domain.vo.ChAiCallTaskVo;
import org.dromara.chronic.mapper.ChAiCallTaskMapper;
import org.dromara.chronic.mapper.ChFollowupRecordMapper;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * AI 智能语音随访外呼管理器
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AiFollowupCallManager {

    private final ChAiCallTaskMapper aiCallTaskMapper;
    private final ChFollowupRecordMapper followupRecordMapper;

    /**
     * 创建单笔或批量外呼任务
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createCallTask(ChAiCallTaskBo bo) {
        if (bo.getPatientId() == null || StrUtil.isBlank(bo.getPatientPhone())) {
            throw new ServiceException("患者ID与外呼手机号不能为空");
        }
        ChAiCallTask task = MapstructUtils.convert(bo, ChAiCallTask.class);
        if (task.getTaskId() == null) {
            task.setTaskId(IdUtil.getSnowflakeNextId());
        }
        task.setCallStatus("PENDING");
        aiCallTaskMapper.insert(task);
        log.info("创建 AI 外呼随访任务成功, taskId={}, patientId={}", task.getTaskId(), bo.getPatientId());
        return task.getTaskId();
    }

    /**
     * 处理外部 AI 语音机器人完成回调
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean handleCallCallback(Long taskId, String status, String audioUrl, String transcript, String metricsJson, String feedback) {
        ChAiCallTask task = aiCallTaskMapper.selectById(taskId);
        if (task == null) {
            log.warn("AI 外呼回调未找到任务, taskId={}", taskId);
            return false;
        }
        task.setCallStatus(StrUtil.isNotBlank(status) ? status : "SUCCESS");
        task.setAudioRecordUrl(audioUrl);
        task.setTranscriptText(transcript);
        task.setExtractedMetrics(metricsJson);
        task.setPatientFeedback(feedback);
        aiCallTaskMapper.updateById(task);

        // 若成功通话，且有关联随访计划，自动沉淀至随访记录表
        if ("SUCCESS".equalsIgnoreCase(status) && task.getPlanId() != null) {
            try {
                ChFollowupRecord record = new ChFollowupRecord();
                record.setRecordId(IdUtil.getSnowflakeNextId());
                record.setTaskId(taskId);
                record.setPatientId(task.getPatientId());
                record.setVisitType("PHONE");
                record.setVisitDate(new Date());
                record.setVisitContent(feedback != null ? feedback : "AI外呼正常回访");
                record.setFeedbackAdvice("语音外呼完成，详情见转写记录");
                record.setFollowupResult("CONTROLLED");
                followupRecordMapper.insert(record);
                log.info("AI 外呼结果已自动沉淀至随访记录, recordId={}, taskId={}", record.getRecordId(), taskId);
            } catch (Exception e) {
                log.error("AI 外呼回填随访记录失败, taskId={}", taskId, e);
            }
        }
        return true;
    }

    /**
     * 分页查询外呼任务列表
     */
    public TableDataInfo<ChAiCallTaskVo> queryPageList(ChAiCallTaskBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ChAiCallTask> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getPatientId() != null, ChAiCallTask::getPatientId, bo.getPatientId());
        lqw.eq(StrUtil.isNotBlank(bo.getDiseaseCode()), ChAiCallTask::getDiseaseCode, bo.getDiseaseCode());
        lqw.eq(StrUtil.isNotBlank(bo.getCallStatus()), ChAiCallTask::getCallStatus, bo.getCallStatus());
        lqw.orderByDesc(ChAiCallTask::getTaskId);
        Page<ChAiCallTaskVo> page = aiCallTaskMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(page);
    }

    /**
     * 模拟执行 AI 语音外呼（用于无真实呼叫中心环境下的仿真测试）
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean simulateCall(Long taskId) {
        ChAiCallTask task = aiCallTaskMapper.selectById(taskId);
        if (task == null) {
            throw new ServiceException("外呼任务不存在: " + taskId);
        }
        String audioUrl = "https://mock-oss.unimed.cn/ai-calls/" + taskId + ".mp3";
        String transcript = "【AI随访助手】您好，我是社区卫生服务中心的慢病随访助手，请问您是患者本人吗？\n"
            + "【患者】是的，是我。\n"
            + "【AI随访助手】请问您最近血压测量情况如何？是否有按时服用降压药？\n"
            + "【患者】最近每天早上量都在130到135之间，药每天都在吃，没有漏服。\n"
            + "【AI随访助手】目前有没有感到头晕、胸闷、视物模糊等不适症状？\n"
            + "【患者】没有，身体感觉还可以。\n"
            + "【AI随访助手】好的，您的血压控制较为平稳，请继续保持规律服药与低盐饮食，我们已为您记录随访结果。祝您身体健康，再见！";
        String metricsJson = "{\"sbp\":132,\"dbp\":84,\"heart_rate\":72,\"compliance\":\"GOOD\",\"symptoms\":[]}";
        String feedback = "AI语音外呼成功：患者血压自测平稳（132/84mmHg），用药依从性良好，无靶器官损害急性症状。";

        return handleCallCallback(taskId, "SUCCESS", audioUrl, transcript, metricsJson, feedback);
    }
}
