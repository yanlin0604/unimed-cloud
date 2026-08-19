package org.dromara.chronic.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 慢病 openapi 层鉴权配置
 * <p>
 * 背景：{@code controller/openapi/} 下 8 个控制器（Device/His/Lis/Pacs/Phs/Referral/
 * RiskAssessment/Webhook）原先<b>零鉴权注解、零凭证校验</b>。网关白名单不含
 * {@code /chronic/**}，因此这些端点仅要求「登录了就行」——任意登录身份（<b>包括患者
 * token</b>）都能调用，可给任意 patientId 注入伪造血压/血糖并触发预警链路，或经
 * HIS/LIS/PACS 端点注入伪造临床数据。「openapi 用 API-Key 鉴权」此前只存在于文档里。
 *
 * @author unimed
 */
@Data
@Component
@ConfigurationProperties(prefix = "chronic.openapi")
public class ChronicOpenapiAuthProperties {

    /**
     * 鉴权模式
     * <ul>
     *   <li>{@code off}     —— 完全跳过校验，等同修复前行为，仅供排障</li>
     *   <li>{@code observe} —— 只告警不拦截：无有效凭证时打 WARN 日志但放行（默认）</li>
     *   <li>{@code enforce} —— 无有效凭证一律拒绝（401）</li>
     * </ul>
     * 默认 observe 是为了先观察日志、确认没有真实调用方会被打断。
     */
    private String authMode = "observe";

    /**
     * 需要校验的路径（Ant 风格）。
     * <p>
     * 用路径匹配而非逐个控制器加注解，是为了让将来新增的 openapi 控制器自动被覆盖，
     * 避免漏挂注解形成新的裸奔端点。
     */
    private List<String> pathPatterns = new ArrayList<>(List.of("/chronic/openapi/**"));

    /**
     * 免校验路径（Ant 风格），默认为空。
     */
    private List<String> excludePathPatterns = new ArrayList<>();

    /**
     * API Token 专用请求头。
     * <p>
     * <b>不能直接复用 {@code Authorization}</b>：Sa-Token 的 {@code token-name} 就是
     * {@code Authorization}（见 application-common.yml），而慢病 openapi 未加入网关白名单，
     * 网关会先用该请求头做 {@code StpUtil.checkLogin()}。两者抢同一个头会导致
     * 要么过不了网关、要么拿不到 API Token，因此这里用独立请求头承载 API Token。
     */
    private String tokenHeaderName = "X-Chronic-Api-Token";

    /**
     * 是否兼容从 {@code Authorization: Bearer xxx} 读取 API Token。
     * <p>
     * 仅在将来把 {@code /chronic/chronic/openapi/**} 加入网关白名单（真正对外暴露、
     * 不走 Sa-Token）后才有意义，届时可与数字人外部接口
     * {@code /dh/api/v1/dh/external/**} 的做法保持一致。默认开启，不影响独立请求头方式。
     */
    private Boolean allowAuthorizationHeader = true;

    /**
     * 是否校验 Token 的 allowedEndpoints 白名单。
     * <p>
     * API Key 可在 auth 侧配置允许访问的端点列表；开启后 openapi 会额外校验当前请求
     * 路径是否在该列表内，实现「一把钥匙只开一扇门」。
     */
    private Boolean checkAllowedEndpoints = true;

    /**
     * 每分钟最大请求数，0 表示不限流。
     * <p>
     * auth 侧 {@code validateToken} 的返回值里不携带该 Key 的限流阈值，只返回 keyName，
     * 所以这里由调用方（慢病）配置一个统一阈值传给 {@code checkRateLimit}。
     */
    private Integer rateLimit = 0;

    public boolean isOff() {
        return "off".equalsIgnoreCase(authMode);
    }

    public boolean isEnforce() {
        return "enforce".equalsIgnoreCase(authMode);
    }
}
