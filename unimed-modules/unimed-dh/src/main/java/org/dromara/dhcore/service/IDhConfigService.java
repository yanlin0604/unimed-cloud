package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.*;
import org.dromara.dhcore.domain.vo.*;

import java.util.Map;

/**
 * 数字人口播配置中心服务接口
 */
public interface IDhConfigService {

    TableDataInfo<DhMemberConfigVo> queryMemberConfigPage(DhMemberConfigQueryBo bo, PageQuery pageQuery);

    DhMemberConfigVo saveMemberConfig(DhMemberConfigBo bo);

    Boolean deleteMemberConfig(Long id);

    DhMemberConfigVo changeMemberConfigStatus(DhConfigStatusBo bo);

    TableDataInfo<DhPaymentPriceConfigVo> queryPaymentPricePage(DhPaymentPriceQueryBo bo, PageQuery pageQuery);

    DhPaymentPriceConfigVo savePaymentPriceConfig(DhPaymentPriceConfigBo bo);

    Boolean deletePaymentPriceConfig(Long id);

    DhPaymentPriceConfigVo changePaymentPriceConfigStatus(DhConfigStatusBo bo);

    TableDataInfo<DhVideoUploadConfigVo> queryVideoUploadConfigPage(DhVideoUploadConfigQueryBo bo, PageQuery pageQuery);

    DhVideoUploadConfigVo saveVideoUploadConfig(DhVideoUploadConfigBo bo);

    Boolean deleteVideoUploadConfig(Long id);

    DhVideoUploadConfigVo changeVideoUploadConfigStatus(DhConfigStatusBo bo);

    TableDataInfo<DhQrUploadConfigVo> queryQrUploadConfigPage(DhQrUploadConfigQueryBo bo, PageQuery pageQuery);

    DhQrUploadConfigVo saveQrUploadConfig(DhQrUploadConfigBo bo);

    Boolean deleteQrUploadConfig(Long id);

    DhQrUploadConfigVo changeQrUploadConfigStatus(DhConfigStatusBo bo);

    TableDataInfo<DhSensitiveWordVo> querySensitiveWordPage(DhSensitiveWordQueryBo bo, PageQuery pageQuery);

    DhSensitiveWordVo saveSensitiveWord(DhSensitiveWordBo bo);

    Boolean deleteSensitiveWord(Long id);

    DhSensitiveWordVo changeSensitiveWordStatus(DhConfigStatusBo bo);

    Map<String, Integer> batchImportSensitiveWords(DhSensitiveWordImportBo bo);

    TableDataInfo<DhNotifyTemplateVo> queryNotifyTemplatePage(DhNotifyTemplateQueryBo bo, PageQuery pageQuery);

    DhNotifyTemplateVo saveNotifyTemplate(DhNotifyTemplateBo bo);

    DhNotifyTemplateVo changeNotifyTemplateStatus(DhConfigStatusBo bo);
}
