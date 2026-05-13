package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChHealthExamBo;
import org.dromara.chronic.domain.bo.ChHealthExamItemBo;
import org.dromara.chronic.domain.bo.EgfrCalcBo;
import org.dromara.chronic.domain.vo.ChHealthExamItemVo;
import org.dromara.chronic.domain.vo.ChHealthExamVo;
import org.dromara.chronic.domain.vo.EgfrCalcVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 体检检验服务
 *
 * @author unimed
 */
public interface IChHealthExamService {

    Long createExam(ChHealthExamBo bo);

    Void updateExam(ChHealthExamBo bo);

    ChHealthExamVo queryById(Long examId);

    TableDataInfo<ChHealthExamVo> queryPageList(ChHealthExamBo bo, PageQuery pageQuery);

    List<ChHealthExamVo> queryByPatientId(Long patientId);

    Long syncLisExam(ChHealthExamBo bo);

    Long syncPacsExam(ChHealthExamBo bo);

    Long addItem(ChHealthExamItemBo bo);

    List<ChHealthExamItemVo> queryItemsByExamId(Long examId);

    /**
     * 删除体检报告（同时级联删除项目）
     */
    Void removeExam(Long examId);

    /**
     * 计算 eGFR，并返回 CKD 分期信息
     *
     * @param bo 入参（patientId 提供时自动读取年龄/性别）
     */
    EgfrCalcVo calcEgfr(EgfrCalcBo bo);
}
