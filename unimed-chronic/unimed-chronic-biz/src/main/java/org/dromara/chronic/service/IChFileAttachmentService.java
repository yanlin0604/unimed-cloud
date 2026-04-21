package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChFileAttachmentBo;
import org.dromara.chronic.domain.vo.ChFileAttachmentVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 附件服务
 *
 * @author unimed
 */
public interface IChFileAttachmentService {

    Long insertByBo(ChFileAttachmentBo bo);

    Boolean updateByBo(ChFileAttachmentBo bo);

    ChFileAttachmentVo queryById(Long fileId);

    TableDataInfo<ChFileAttachmentVo> queryPageList(ChFileAttachmentBo bo, PageQuery pageQuery);

    List<ChFileAttachmentVo> queryByBiz(String bizType, Long bizId);

    Boolean deleteById(Long fileId);
}
