package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.DhVoice;
import org.dromara.dhcore.domain.bo.DhConfigStatusBo;
import org.dromara.dhcore.domain.bo.DhVoiceBo;
import org.dromara.dhcore.domain.bo.DhVoiceQueryBo;
import org.dromara.dhcore.domain.vo.DhVoiceVo;

import java.util.List;

/**
 * 音色服务接口
 *
 * @author dhcore
 */
public interface IDhVoiceService {

    /**
     * 获取用户可用的音色列表
     * 包括系统预设音色和用户自定义音色
     *
     * @param userId 用户ID
     * @return 音色列表
     */
    List<DhVoiceVo> listAvailableVoices(Long userId);

    /**
     * 保存用户上传的音色
     *
     * @param userId    用户ID
     * @param ossId     OSS文件ID
     * @param name      音色名称（可选）
     * @param sampleUrl 音频URL（可选）
     * @return 保存后的音色
     */
    DhVoiceVo saveVoice(Long userId, String ossId, String name, String sampleUrl);

    /**
     * 删除用户自定义音色
     *
     * @param userId  用户ID
     * @param voiceId 音色ID
     */
    void deleteVoice(Long userId, Long voiceId);

    /**
     * 根据ID获取音色
     *
     * @param voiceId 音色ID
     * @return 音色实体
     */
    DhVoice getById(Long voiceId);

    /**
     * 保存音色（内部方法，用于临时上传资产处理）
     *
     * @param voice 音色实体
     * @return 保存后的音色
     */
    DhVoice save(DhVoice voice);

    // ==================== B端管理方法 ====================

    /**
     * 分页查询系统音色（B端）
     *
     * @param bo        查询参数
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<DhVoiceVo> querySystemVoicePage(DhVoiceQueryBo bo, PageQuery pageQuery);

    /**
     * 新增系统音色（B端，is_system=1, source=system）
     *
     * @param bo 新增参数
     * @return 新增后的音色
     */
    DhVoiceVo saveSystemVoice(DhVoiceBo bo);

    /**
     * 修改系统音色（B端）
     *
     * @param bo 修改参数
     * @return 修改后的音色
     */
    DhVoiceVo updateSystemVoice(DhVoiceBo bo);

    /**
     * 删除系统音色（B端，仅允许删除 is_system=1 的记录）
     *
     * @param voiceId 音色ID
     * @return 是否成功
     */
    Boolean deleteSystemVoice(Long voiceId);

    /**
     * 切换系统音色状态（B端）
     *
     * @param bo 状态参数
     * @return 更新后的音色
     */
    DhVoiceVo changeSystemVoiceStatus(DhConfigStatusBo bo);
}
