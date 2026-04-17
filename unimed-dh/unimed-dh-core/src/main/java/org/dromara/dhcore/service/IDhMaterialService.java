package org.dromara.dhcore.service;

import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.DhMaterial;
import org.dromara.dhcore.domain.bo.DhConfigStatusBo;
import org.dromara.dhcore.domain.bo.DhMaterialBo;
import org.dromara.dhcore.domain.bo.DhMaterialQueryBo;
import org.dromara.dhcore.domain.vo.DhMaterialVo;

import java.util.List;

/**
 * 素材服务接口
 *
 * @author dhcore
 */
public interface IDhMaterialService {

    /**
     * 获取用户的素材列表
     *
     * @param userId 用户ID
     * @return 素材列表
     */
    List<DhMaterialVo> listByUserId(Long userId);

    /**
     * 根据用户ID和素材类型获取素材列表
     *
     * @param userId       用户ID
     * @param materialType 素材类型
     * @return 素材列表
     */
    List<DhMaterialVo> listByUserIdAndType(Long userId, String materialType);

    /**
     * 保存用户上传的素材
     *
     * @param userId       用户ID
     * @param ossId        OSS文件ID
     * @param materialType 素材类型
     * @param fileName     文件名（可选）
     * @param fileUrl      文件URL（可选）
     * @return 保存后的素材
     */
    DhMaterialVo saveMaterial(Long userId, String ossId, String materialType, String fileName, String fileUrl);

    /**
     * 删除素材
     *
     * @param userId     用户ID
     * @param materialId 素材ID
     */
    void deleteMaterial(Long userId, Long materialId);

    /**
     * 根据ID获取素材
     *
     * @param materialId 素材ID
     * @return 素材实体
     */
    DhMaterial getById(Long materialId);

    /**
     * 保存素材（内部方法）
     *
     * @param material 素材实体
     * @return 保存后的素材
     */
    DhMaterial save(DhMaterial material);

    // ==================== B端管理方法 ====================

    /**
     * 分页查询系统素材（B端）
     *
     * @param bo        查询参数
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    TableDataInfo<DhMaterialVo> querySystemMaterialPage(DhMaterialQueryBo bo, PageQuery pageQuery);

    /**
     * 新增系统素材（B端，is_system=1）
     *
     * @param bo 新增参数
     * @return 新增后的素材
     */
    DhMaterialVo saveSystemMaterial(DhMaterialBo bo);

    /**
     * 修改系统素材（B端）
     *
     * @param bo 修改参数
     * @return 修改后的素材
     */
    DhMaterialVo updateSystemMaterial(DhMaterialBo bo);

    /**
     * 删除系统素材（B端，仅允许删除 is_system=1 的记录）
     *
     * @param materialId 素材ID
     * @return 是否成功
     */
    Boolean deleteSystemMaterial(Long materialId);

    /**
     * 切换系统素材状态（B端）
     *
     * @param bo 状态参数
     * @return 更新后的素材
     */
    DhMaterialVo changeSystemMaterialStatus(DhConfigStatusBo bo);
}
