package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChContractServicePackageBo;
import org.dromara.chronic.domain.bo.ChPatientContractBo;
import org.dromara.chronic.domain.vo.ChContractFulfillmentVo;
import org.dromara.chronic.domain.vo.ChContractServicePackageVo;
import org.dromara.chronic.domain.vo.ChPatientContractVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 患者签约服务
 *
 * @author unimed
 */
public interface IChPatientContractService {

    TableDataInfo<ChPatientContractVo> queryContractPageList(ChPatientContractBo bo, PageQuery pageQuery);

    TableDataInfo<ChContractServicePackageVo> queryPackagePageList(ChContractServicePackageBo bo, PageQuery pageQuery);

    Boolean createPackage(ChContractServicePackageBo bo);

    Long signContract(ChPatientContractBo bo);

    List<ChContractFulfillmentVo> queryFulfillmentList(Long contractId);

    ChPatientContractVo queryCurrentContract(Long patientId);

    ChPatientContractVo queryById(Long contractId);
}
