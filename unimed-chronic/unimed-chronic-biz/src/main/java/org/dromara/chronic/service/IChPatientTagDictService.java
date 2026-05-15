package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChPatientTagDictBo;
import org.dromara.chronic.domain.vo.ChPatientTagDictVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 患者标签字典服务层
 *
 * @author unimed
 */
public interface IChPatientTagDictService {

    /**
     * 分页查询（含使用次数聚合）
     */
    TableDataInfo<ChPatientTagDictVo> queryPageList(ChPatientTagDictBo bo, PageQuery pageQuery);

    /**
     * 列表查询（下拉选择用，按 sortOrder 排序，过滤启用项）
     */
    List<ChPatientTagDictVo> queryList(ChPatientTagDictBo bo);

    /**
     * 详情
     */
    ChPatientTagDictVo queryById(Long id);

    /**
     * 新增
     */
    Boolean insertByBo(ChPatientTagDictBo bo);

    /**
     * 修改
     */
    Boolean updateByBo(ChPatientTagDictBo bo);

    /**
     * 切换状态
     */
    Boolean changeStatus(Long id, String status);

    /**
     * 批量删除（校验：被引用中的字典不允许删除）
     */
    Boolean deleteByIds(Collection<Long> ids);
}
