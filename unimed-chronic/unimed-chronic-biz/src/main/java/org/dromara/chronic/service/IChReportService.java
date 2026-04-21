package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChReportInstanceBo;
import org.dromara.chronic.domain.bo.ChReportTemplateBo;
import org.dromara.chronic.domain.vo.ChReportInstanceVo;
import org.dromara.chronic.domain.vo.ChReportTemplateVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 健康报告服务
 *
 * @author unimed
 */
public interface IChReportService {

    Long createTemplate(ChReportTemplateBo bo);

    Void updateTemplate(ChReportTemplateBo bo);

    ChReportTemplateVo queryTemplateById(Long templateId);

    TableDataInfo<ChReportTemplateVo> queryTemplatePageList(ChReportTemplateBo bo, PageQuery pageQuery);

    Long generateReport(ChReportInstanceBo bo);

    ChReportInstanceVo queryReportById(Long reportId);

    TableDataInfo<ChReportInstanceVo> queryReportPageList(ChReportInstanceBo bo, PageQuery pageQuery);

    List<ChReportInstanceVo> queryByPatientId(Long patientId);

    Void pushReport(Long reportId, String channel);
}
