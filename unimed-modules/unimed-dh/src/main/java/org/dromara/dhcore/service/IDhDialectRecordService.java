package org.dromara.dhcore.service;

import jakarta.servlet.http.HttpServletResponse;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.DhDialectRecordAuditBo;
import org.dromara.dhcore.domain.bo.DhDialectRecordBo;
import org.dromara.dhcore.domain.bo.DhDialectRecordQueryBo;
import org.dromara.dhcore.domain.vo.DhDialectRecordVo;

import java.util.List;

/**
 * 方言采集录音记录服务接口
 *
 * @author unimed
 */
public interface IDhDialectRecordService {

    /**
     * C端提交录音记录（通过 ossId 获取持久URL后写库）
     */
    DhDialectRecordVo submit(Long userId, DhDialectRecordBo bo);

    /**
     * B端分页查询录音记录
     */
    TableDataInfo<DhDialectRecordVo> queryPage(DhDialectRecordQueryBo bo, PageQuery pageQuery);

    /**
     * B端审核录音记录
     */
    Boolean audit(DhDialectRecordAuditBo bo);

    /**
     * B端批量删除录音记录
     */
    Boolean delete(List<Long> recordIds);

    /**
     * B端导出录音记录
     */
    void export(DhDialectRecordQueryBo bo, HttpServletResponse response);
}
