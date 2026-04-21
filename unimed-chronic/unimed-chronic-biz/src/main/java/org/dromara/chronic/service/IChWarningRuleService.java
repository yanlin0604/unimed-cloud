package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChWarningRuleBo;
import org.dromara.chronic.domain.vo.ChWarningRuleVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 预警规则服务
 *
 * @author unimed
 */
public interface IChWarningRuleService {

    Long add(ChWarningRuleBo bo);

    Void update(ChWarningRuleBo bo);

    ChWarningRuleVo queryById(Long ruleId);

    TableDataInfo<ChWarningRuleVo> queryPageList(ChWarningRuleBo bo, PageQuery pageQuery);

    List<ChWarningRuleVo> queryByDiseaseCode(String diseaseCode);
}
