package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChHealthEducationContentBo;
import org.dromara.chronic.domain.vo.ChHealthEducationContentVo;
import org.dromara.chronic.domain.vo.ChHealthEducationDeliveryVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 宣教服务
 *
 * @author unimed
 */
public interface IChHealthEducationService {

    Long createContent(ChHealthEducationContentBo bo);

    ChHealthEducationContentVo queryContentById(Long contentId);

    TableDataInfo<ChHealthEducationContentVo> queryContentPageList(ChHealthEducationContentBo bo, PageQuery pageQuery);

    List<ChHealthEducationDeliveryVo> queryDeliveriesByPatientId(Long patientId);

    Void markRead(Long deliveryId, Integer stayDuration);
}
