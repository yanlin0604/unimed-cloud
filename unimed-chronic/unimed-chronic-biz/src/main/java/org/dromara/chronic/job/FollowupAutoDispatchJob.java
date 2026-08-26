package org.dromara.chronic.job;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.aizuda.snailjob.model.dto.ExecuteResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.manager.FollowupAutoDispatchManager;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 随访任务池自动分发定时任务
 * <p>
 * 周期性扫描随访任务池中的待分配任务,按配置策略(负载均衡/轮询/随机/专病匹配等)自动分发给执行人员池
 * <p>
 * 策略与单次上限通过 SnailJob 控制台任务参数配置(JSON),无需发版即可调整:
 * <pre>
 * {"strategy":"LEAST_LOADED","maxCount":200}
 * </pre>
 * strategy 白名单: RANDOM / LEAST_LOADED / ROUND_ROBIN / DISEASE_MATCH
 * 解析失败或未配置时回退默认策略 LEAST_LOADED、上限 200,并记录 WARN 日志。
 *
 * @author unimed
 */
@Slf4j
@Component
@JobExecutor(name = "followupAutoDispatchJob")
@RequiredArgsConstructor
public class FollowupAutoDispatchJob {

    /**
     * 合法分发策略白名单
     */
    private static final Set<String> STRATEGY_WHITELIST = Set.of(
        "RANDOM", "LEAST_LOADED", "ROUND_ROBIN", "DISEASE_MATCH"
    );

    /**
     * 未配置/非法时的默认策略
     */
    private static final String DEFAULT_STRATEGY = "LEAST_LOADED";

    /**
     * 未配置/非法时默认单次上限
     */
    private static final int DEFAULT_MAX_COUNT = 200;

    private final FollowupAutoDispatchManager autoDispatchManager;

    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("随访任务自动分发跑批开始");

        // 从 SnailJob 控制台任务参数读取策略与上限,JSON: {"strategy":"DISEASE_MATCH","maxCount":300}
        Selected params = resolveParams(jobArgs);
        String strategy = params.strategy;
        int maxCount = params.maxCount;

        int dispatched = autoDispatchManager.autoDispatch(strategy, maxCount);

        SnailJobLog.REMOTE.info("随访任务自动分发跑批完成, 策略: {}, 上限: {}, 分发任务数: {}", strategy, maxCount, dispatched);
        return ExecuteResult.success("自动分发随访任务" + dispatched + "条");
    }

    /**
     * 解析并规范化任务参数。解析失败/空白/非法策略/非法上限均回退默认值并 WARN。
     */
    private Selected resolveParams(JobArgs jobArgs) {
        String strategy = DEFAULT_STRATEGY;
        int maxCount = DEFAULT_MAX_COUNT;
        Object raw = jobArgs == null ? null : jobArgs.getJobParams();
        String json = raw == null ? null : String.valueOf(raw);
        if (StrUtil.isBlank(json)) {
            log.warn("随访自动分发任务参数为空/未配置,使用默认策略 {} 上限 {}", strategy, maxCount);
            return new Selected(strategy, maxCount);
        }
        try {
            JSONObject obj = JSONUtil.parseObj(json);
            String configuredStrategy = obj.getStr("strategy");
            if (StrUtil.isNotBlank(configuredStrategy)) {
                String upper = configuredStrategy.trim().toUpperCase();
                if (STRATEGY_WHITELIST.contains(upper)) {
                    strategy = upper;
                } else {
                    log.warn("随访自动分发非法策略 {}, 回退默认策略 {} 上限 {}", configuredStrategy,
                        DEFAULT_STRATEGY, DEFAULT_MAX_COUNT);
                    return new Selected(DEFAULT_STRATEGY, DEFAULT_MAX_COUNT);
                }
            }
            Integer cfgMax = obj.getInt("maxCount");
            if (cfgMax != null) {
                maxCount = Math.min(1000, Math.max(1, cfgMax));
            }
        } catch (Exception e) {
            log.warn("随访自动分发任务参数解析失败 json={}, 回退默认策略 {} 上限 {}, err={}",
                json, DEFAULT_STRATEGY, DEFAULT_MAX_COUNT, e.getMessage());
        }
        return new Selected(strategy, maxCount);
    }

    /**
     * 已选参数
     */
    private record Selected(String strategy, int maxCount) {
    }
}
