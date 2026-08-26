package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChFollowupRuleBo;
import org.dromara.chronic.domain.vo.ChFollowupRuleVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;

/**
 * 慢病随访排期规则配置服务
 *
 * @author unimed
 */
public interface IChFollowupRuleService {

    Long createRule(ChFollowupRuleBo bo);

    Void updateRule(ChFollowupRuleBo bo);

    ChFollowupRuleVo queryById(Long id);

    TableDataInfo<ChFollowupRuleVo> queryPageList(ChFollowupRuleBo bo, PageQuery pageQuery);

    Void toggleActive(Long id, Boolean isActive);

    Void deleteRules(Collection<Long> ids);
}