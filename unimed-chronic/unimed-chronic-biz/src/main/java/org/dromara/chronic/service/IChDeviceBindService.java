package org.dromara.chronic.service;

import org.dromara.chronic.domain.bo.ChDeviceBindBo;
import org.dromara.chronic.domain.bo.ChDeviceRawRecordBo;
import org.dromara.chronic.domain.vo.ChDeviceBindVo;
import org.dromara.chronic.domain.vo.ChDeviceRawRecordVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 设备绑定服务
 *
 * @author unimed
 */
public interface IChDeviceBindService {

    Long bindDevice(ChDeviceBindBo bo);

    Void unbindDevice(Long bindId);

    List<ChDeviceBindVo> queryByPatientId(Long patientId);

    Void updateHeartbeat(String deviceId, Integer batteryLevel, String onlineStatus);

    ChDeviceRawRecordVo saveRawRecord(ChDeviceRawRecordBo bo);

    /**
     * 分页查询设备绑定记录（含 patientName 回填）
     *
     * @param bo        查询条件（patientId eq / deviceId like / deviceType eq / onlineStatus eq）
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<ChDeviceBindVo> queryPageList(ChDeviceBindBo bo, PageQuery pageQuery);

    /**
     * 设备绑定详情（含 patientName 回填）
     *
     * @param bindId 绑定ID
     * @return 设备绑定视图对象
     */
    ChDeviceBindVo queryById(Long bindId);

    /**
     * 分页查询设备原始上报数据
     *
     * @param deviceId  设备ID，可为空
     * @param patientId 患者ID，可为空
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<ChDeviceRawRecordVo> queryRawRecordPage(String deviceId, Long patientId, PageQuery pageQuery);

    /**
     * 患者端自助解绑：校验绑定记录归属当前患者后解绑
     *
     * @param bindId    绑定ID
     * @param patientId 当前登录患者ID
     */
    Void unbindByPatient(Long bindId, Long patientId);
}
