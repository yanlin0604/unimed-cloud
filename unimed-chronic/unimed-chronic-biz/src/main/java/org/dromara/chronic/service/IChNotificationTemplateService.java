package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChNotificationTemplateBo;
import org.dromara.chronic.domain.vo.ChNotificationTemplateVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Map;

/**
 * 通知模板服务层
 *
 * @author unimed
 */
public interface IChNotificationTemplateService {

    /**
     * 分页查询通知模板
     *
     * @param bo        查询条件（templateName like / templateCode like / channel eq / isActive eq）
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<ChNotificationTemplateVo> queryPageList(ChNotificationTemplateBo bo, PageQuery pageQuery);

    /**
     * 通知模板详情
     *
     * @param templateId 模板ID
     * @return 模板视图对象
     */
    ChNotificationTemplateVo queryById(Long templateId);

    /**
     * 新增通知模板（校验 templateCode + channel 唯一）
     *
     * @param bo 业务对象
     * @return 是否成功
     */
    Boolean insertByBo(ChNotificationTemplateBo bo);

    /**
     * 修改通知模板（校验 templateCode + channel 唯一）
     *
     * @param bo 业务对象
     * @return 是否成功
     */
    Boolean updateByBo(ChNotificationTemplateBo bo);

    /**
     * 删除通知模板（逻辑删除）
     *
     * @param templateId 模板ID
     * @return 是否成功
     */
    Boolean deleteById(Long templateId);

    /**
     * 启用/停用通知模板
     *
     * @param templateId 模板ID
     * @param isActive   1启用 0停用
     */
    Void updateStatus(Long templateId, String isActive);

    /**
     * 按模板编码 + 渠道查询模板（供 job / manager 渲染文案使用）
     *
     * @param templateCode 模板编码
     * @param channel      推送渠道，为空时不限渠道（取任意一条启用模板）
     * @return 模板视图对象，不存在返回 null
     */
    ChNotificationTemplateVo queryByCode(String templateCode, String channel);

    /**
     * 渲染模板文案：将模板内容中的 {key} 占位符替换为 params 中的值。
     * <p>
     * 模板不存在、已停用或内容为空时返回 null，由调用方自行兜底。
     *
     * @param templateCode 模板编码
     * @param channel      推送渠道，可为空
     * @param params       占位符参数
     * @return 渲染后的文案；不可用时返回 null
     */
    String render(String templateCode, String channel, Map<String, String> params);
}
