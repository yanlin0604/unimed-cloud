package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChSosRecordBo;
import org.dromara.chronic.domain.vo.ChSosRecordVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 紧急求助记录 Service 接口
 *
 * @author unimed
 */
public interface IChSosRecordService {

    /**
     * 查询紧急求助记录
     */
    ChSosRecordVo queryById(Long sosId);

    /**
     * 分页查询紧急求助记录列表
     */
    TableDataInfo<ChSosRecordVo> queryPageList(ChSosRecordBo bo, PageQuery pageQuery);

    /**
     * 查询列表
     */
    List<ChSosRecordVo> queryList(ChSosRecordBo bo);

    /**
     * 新增紧急求助记录
     */
    Long insertByBo(ChSosRecordBo bo);

    /**
     * 修改紧急求助记录
     */
    Boolean updateByBo(ChSosRecordBo bo);

    /**
     * 处置紧急求助
     */
    Boolean handleSos(Long sosId, Long handlerUserId, String eventStatus, String handleRemark);

    /**
     * 校验并批量删除紧急求助记录
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
