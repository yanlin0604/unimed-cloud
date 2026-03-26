package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.DhConfigStatusBo;
import org.dromara.dhcore.domain.bo.DhDialectPromptBo;
import org.dromara.dhcore.domain.bo.DhDialectPromptQueryBo;
import org.dromara.dhcore.domain.vo.DhDialectPromptVo;

import java.util.List;

/**
 * 方言采集提示文字服务接口
 *
 * @author unimed
 */
public interface IDhDialectPromptService {

    /**
     * 分页查询提示文字（B端）
     */
    TableDataInfo<DhDialectPromptVo> queryPage(DhDialectPromptQueryBo bo, PageQuery pageQuery);

    /**
     * 新增提示文字
     */
    DhDialectPromptVo save(DhDialectPromptBo bo);

    /**
     * 修改提示文字
     */
    DhDialectPromptVo update(DhDialectPromptBo bo);

    /**
     * 批量删除提示文字
     */
    Boolean delete(List<Long> promptIds);

    /**
     * 切换状态
     */
    DhDialectPromptVo changeStatus(DhConfigStatusBo bo);

    /**
     * 查询启用的提示文字列表（C端）
     */
    List<DhDialectPromptVo> listEnabled();
}
