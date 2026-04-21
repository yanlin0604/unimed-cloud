package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChEducationRuleBo;
import org.dromara.chronic.domain.vo.ChEducationRuleVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 宣教规则服务
 *
 * @author unimed
 */
public interface IChEducationRuleService {

    Long add(ChEducationRuleBo bo);

    Void update(ChEducationRuleBo bo);

    ChEducationRuleVo queryById(Long ruleId);

    TableDataInfo<ChEducationRuleVo> queryPageList(ChEducationRuleBo bo, PageQuery pageQuery);

    List<ChEducationRuleVo> queryActiveRules();
}
