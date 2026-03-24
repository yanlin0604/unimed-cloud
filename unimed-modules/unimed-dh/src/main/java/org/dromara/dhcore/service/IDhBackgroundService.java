package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.DhBackgroundBo;
import org.dromara.dhcore.domain.bo.DhBackgroundQueryBo;
import org.dromara.dhcore.domain.bo.DhConfigStatusBo;
import org.dromara.dhcore.domain.vo.DhBackgroundVo;
import org.dromara.dhcore.domain.vo.portal.PortalBackgroundVo;

import java.util.List;

/**
 * 数字人背景资源服务接口
 *
 * @author unimed
 */
public interface IDhBackgroundService {

    /**
     * 分页查询背景列表（B端）
     *
     * @param bo        查询参数
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<DhBackgroundVo> queryPage(DhBackgroundQueryBo bo, PageQuery pageQuery);

    /**
     * 新增背景（B端）
     *
     * @param bo 新增参数
     * @return 新增后的背景
     */
    DhBackgroundVo save(DhBackgroundBo bo);

    /**
     * 修改背景（B端）
     *
     * @param bo 修改参数
     * @return 修改后的背景
     */
    DhBackgroundVo update(DhBackgroundBo bo);

    /**
     * 删除背景（B端）
     *
     * @param backgroundId 背景ID
     * @return 是否成功
     */
    Boolean delete(Long backgroundId);

    /**
     * 切换背景状态（B端）
     *
     * @param bo 状态参数
     * @return 更新后的背景
     */
    DhBackgroundVo changeStatus(DhConfigStatusBo bo);

    /**
     * 查询启用状态的背景列表（C端）
     * 按 sort_order 升序排列，批量填充 OSS 预签名 URL
     *
     * @return 背景列表
     */
    List<PortalBackgroundVo> listEnabled();
}
