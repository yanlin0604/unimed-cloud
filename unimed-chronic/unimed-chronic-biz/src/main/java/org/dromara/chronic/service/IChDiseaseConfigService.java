package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChDiseaseConfigBo;
import org.dromara.chronic.domain.vo.ChDiseaseConfigVo;
import org.dromara.chronic.domain.vo.ChIcdDictVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 病种配置服务层
 *
 * @author unimed
 */
public interface IChDiseaseConfigService {

    TableDataInfo<ChDiseaseConfigVo> queryPageList(ChDiseaseConfigBo bo, PageQuery pageQuery);

    ChDiseaseConfigVo queryById(Long configId);

    Boolean insertByBo(ChDiseaseConfigBo bo);

    Boolean updateByBo(ChDiseaseConfigBo bo);

    Boolean disableById(Long configId);

    List<ChIcdDictVo> queryIcdList(String keyword);
}
