package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.vo.ChPatientDetailVo;
import org.dromara.chronic.domain.vo.ChPatientProfileVo;
import org.dromara.chronic.domain.vo.ChPatientTimelineVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 患者主档案服务层
 *
 * @author unimed
 */
public interface IChPatientProfileService {

    /**
     * 分页查询患者档案
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<ChPatientProfileVo> queryPageList(ChPatientProfileBo bo, PageQuery pageQuery);

    /**
     * 查询患者档案列表
     *
     * @param bo 查询条件
     * @return 列表结果
     */
    List<ChPatientProfileVo> queryList(ChPatientProfileBo bo);

    /**
     * 查询患者详情
     *
     * @param patientId 患者ID
     * @return 患者详情
     */
    ChPatientDetailVo queryDetailById(Long patientId);

    /**
     * 查询患者时间线
     *
     * @param patientId 患者ID
     * @return 时间线列表
     */
    List<ChPatientTimelineVo> queryTimelineByPatientId(Long patientId);

    /**
     * 新增患者主档案
     *
     * @param bo 患者档案业务对象
     * @return 是否成功
     */
    Boolean insertByBo(ChPatientProfileBo bo);

    /**
     * 更新患者主档案
     *
     * @param bo 患者档案业务对象
     * @return 是否成功
     */
    Boolean updateByBo(ChPatientProfileBo bo);

    /**
     * 删除患者主档案（逻辑删除）
     *
     * @param patientIds 患者ID集合
     * @return 是否成功
     */
    Boolean deleteByIds(java.util.Collection<Long> patientIds);

    /**
     * 校验身份证号是否重复
     *
     * @param idCard 身份证号
     * @param excludeId 排除的患者ID
     * @return true 表示已存在重复记录，false 表示可用
     */
    Boolean checkIdCardUnique(String idCard, Long excludeId);
}
