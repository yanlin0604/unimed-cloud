package org.dromara.dhcore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.DhMaterial;
import org.dromara.dhcore.domain.bo.DhConfigStatusBo;
import org.dromara.dhcore.domain.bo.DhMaterialBo;
import org.dromara.dhcore.domain.bo.DhMaterialQueryBo;
import org.dromara.dhcore.domain.vo.DhMaterialVo;
import org.dromara.dhcore.mapper.DhMaterialMapper;
import org.dromara.dhcore.service.IDhMaterialService;
import org.dromara.dhcore.support.utils.DhConvertUtils;
import org.dromara.resource.api.RemoteFileService;
import org.dromara.resource.api.domain.RemoteFile;
import org.dromara.system.api.RemoteUserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 素材服务实现类
 *
 * @author dhcore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DhMaterialServiceImpl implements IDhMaterialService {

    private final DhMaterialMapper dhMaterialMapper;

    @DubboReference
    private RemoteFileService remoteFileService;

    @DubboReference
    private RemoteUserService remoteUserService;

    @Override
    public List<DhMaterialVo> listByUserId(Long userId) {
        LambdaQueryWrapper<DhMaterial> lqw = Wrappers.lambdaQuery();
        // 系统预设素材 + 用户自己上传的素材
        lqw.and(w -> w.eq(DhMaterial::getIsSystem, 1).or().eq(DhMaterial::getUserId, userId));
        lqw.eq(DhMaterial::getStatus, "0");
        lqw.orderByDesc(DhMaterial::getIsSystem).orderByDesc(DhMaterial::getCreateTime);
        return convertWithOssUrl(dhMaterialMapper.selectList(lqw));
    }

    @Override
    public List<DhMaterialVo> listByUserIdAndType(Long userId, String materialType) {
        LambdaQueryWrapper<DhMaterial> lqw = Wrappers.lambdaQuery();
        // 系统预设素材 + 用户自己上传的素材
        lqw.and(w -> w.eq(DhMaterial::getIsSystem, 1).or().eq(DhMaterial::getUserId, userId));
        lqw.eq(DhMaterial::getStatus, "0");
        if (StringUtils.isNotBlank(materialType)) {
            lqw.eq(DhMaterial::getMaterialType, materialType);
        }
        lqw.orderByDesc(DhMaterial::getIsSystem).orderByDesc(DhMaterial::getCreateTime);
        return convertWithOssUrl(dhMaterialMapper.selectList(lqw));
    }

    /**
     * 批量转换素材列表并通过 ossId 重新生成最新 URL
     * <p>
     * 防止数据库存储的 URL 签名过期导致无法访问。
     */
    private List<DhMaterialVo> convertWithOssUrl(List<DhMaterial> materials) {
        List<String> ossIds = materials.stream()
            .map(DhMaterial::getOssId)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        Map<String, RemoteFile> ossMap = new HashMap<>();
        if (!ossIds.isEmpty()) {
            try {
                List<RemoteFile> files = remoteFileService.selectByIds(String.join(",", ossIds));
                if (files != null) {
                    for (RemoteFile f : files) {
                        ossMap.put(String.valueOf(f.getOssId()), f);
                    }
                }
            } catch (Exception e) {
                log.warn("批量获取素材OSS信息失败", e);
            }
        }
        List<DhMaterialVo> result = new ArrayList<>();
        for (DhMaterial material : materials) {
            DhMaterialVo vo = DhConvertUtils.toMaterialVo(material);
            if (StringUtils.isNotBlank(material.getOssId())) {
                RemoteFile f = ossMap.get(material.getOssId());
                if (f != null) {
                    vo.setFileUrl(f.getUrl());
                    vo.setFileName(f.getOriginalName());
                }
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public DhMaterialVo saveMaterial(Long userId, String ossId, String materialType, String fileName, String fileUrl) {
        DhMaterial material = new DhMaterial();
        material.setUserId(userId);
        material.setMaterialType(materialType);
        material.setOssId(ossId);
        material.setFileName(fileName);
        material.setFileUrl(fileUrl);
        dhMaterialMapper.insert(material);
        return DhConvertUtils.toMaterialVo(material);
    }

    @Override
    public void deleteMaterial(Long userId, Long materialId) {
        DhMaterial material = dhMaterialMapper.selectById(materialId);
        if (material == null) {
            throw new ServiceException("素材不存在");
        }
        if (material.getIsSystem() != null && material.getIsSystem() == 1) {
            throw new ServiceException("系统素材不可删除");
        }
        if (!userId.equals(material.getUserId())) {
            throw new ServiceException("无权删除该素材");
        }
        dhMaterialMapper.deleteById(materialId);
    }

    @Override
    public DhMaterial getById(Long materialId) {
        return dhMaterialMapper.selectById(materialId);
    }

    @Override
    public DhMaterial save(DhMaterial material) {
        dhMaterialMapper.insert(material);
        return material;
    }

    // ==================== B端管理方法 ====================

    @Override
    public TableDataInfo<DhMaterialVo> querySystemMaterialPage(DhMaterialQueryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhMaterial> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhMaterial::getIsSystem, 1);
        lqw.like(StringUtils.isNotBlank(bo.getName()), DhMaterial::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getMaterialType()), DhMaterial::getMaterialType, bo.getMaterialType());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), DhMaterial::getStatus, bo.getStatus());
        lqw.orderByAsc(DhMaterial::getSortOrder).orderByDesc(DhMaterial::getCreateTime);
        Page<DhMaterial> page = dhMaterialMapper.selectPage(pageQuery.build(), lqw);
        List<DhMaterial> records = page.getRecords();

        // 批量通过 ossId 获取最新临时 URL，防止签名过期
        List<String> ossIds = records.stream()
            .map(DhMaterial::getOssId)
            .filter(StringUtils::isNotBlank)
            .distinct()
            .toList();
        Map<String, RemoteFile> ossMap = new HashMap<>();
        if (!ossIds.isEmpty()) {
            try {
                List<RemoteFile> files = remoteFileService.selectByIds(String.join(",", ossIds));
                if (files != null) {
                    for (RemoteFile f : files) {
                        ossMap.put(String.valueOf(f.getOssId()), f);
                    }
                }
            } catch (Exception e) {
                log.warn("批量获取素材OSS信息失败", e);
            }
        }

        // 批量查询上传者用户名
        List<Long> createByIds = records.stream()
            .map(DhMaterial::getCreateBy)
            .filter(id -> id != null && id > 0)
            .distinct()
            .toList();
        Map<Long, String> userNameMap = new HashMap<>();
        if (!createByIds.isEmpty()) {
            try {
                Map<Long, String> result = remoteUserService.selectUserNicksByIds(createByIds);
                if (result != null) {
                    userNameMap.putAll(result);
                }
            } catch (Exception e) {
                log.warn("批量获取上传者用户名失败", e);
            }
        }

        List<DhMaterialVo> voList = new ArrayList<>();
        for (DhMaterial material : records) {
            DhMaterialVo vo = DhConvertUtils.toMaterialVo(material);
            if (StringUtils.isNotBlank(material.getOssId())) {
                RemoteFile f = ossMap.get(material.getOssId());
                if (f != null) {
                    vo.setFileUrl(f.getUrl());
                    vo.setFileName(f.getOriginalName());
                }
            }
            if (material.getCreateBy() != null) {
                vo.setUploadUser(userNameMap.getOrDefault(material.getCreateBy(), String.valueOf(material.getCreateBy())));
            }
            voList.add(vo);
        }
        return new TableDataInfo<>(voList, page.getTotal());
    }

    @Override
    public DhMaterialVo saveSystemMaterial(DhMaterialBo bo) {
        DhMaterial material = new DhMaterial();
        material.setName(bo.getName());
        material.setMaterialType(bo.getMaterialType());
        material.setOssId(bo.getOssId());
        material.setFileUrl(bo.getFileUrl());
        material.setThumbnailUrl(bo.getThumbnailUrl());
        material.setFileName(bo.getFileName());
        material.setIsSystem(1);
        material.setStatus("0");
        material.setSortOrder(bo.getSortOrder() != null ? bo.getSortOrder() : 0);
        material.setRemark(bo.getRemark());
        dhMaterialMapper.insert(material);
        return DhConvertUtils.toMaterialVo(material);
    }

    @Override
    public DhMaterialVo updateSystemMaterial(DhMaterialBo bo) {
        if (bo.getMaterialId() == null) {
            throw new ServiceException("素材ID不能为空");
        }
        DhMaterial material = dhMaterialMapper.selectById(bo.getMaterialId());
        if (material == null) {
            throw new ServiceException("素材不存在");
        }
        if (material.getIsSystem() != 1) {
            throw new ServiceException("该素材不是系统预设素材");
        }
        LambdaUpdateWrapper<DhMaterial> uw = Wrappers.lambdaUpdate();
        uw.eq(DhMaterial::getMaterialId, bo.getMaterialId());
        uw.set(StringUtils.isNotBlank(bo.getName()), DhMaterial::getName, bo.getName());
        uw.set(bo.getSortOrder() != null, DhMaterial::getSortOrder, bo.getSortOrder());
        uw.set(StringUtils.isNotBlank(bo.getRemark()), DhMaterial::getRemark, bo.getRemark());
        dhMaterialMapper.update(null, uw);
        return DhConvertUtils.toMaterialVo(dhMaterialMapper.selectById(bo.getMaterialId()));
    }

    @Override
    public Boolean deleteSystemMaterial(Long materialId) {
        DhMaterial material = dhMaterialMapper.selectById(materialId);
        if (material == null) {
            throw new ServiceException("素材不存在");
        }
        if (material.getIsSystem() != 1) {
            throw new ServiceException("该记录不是系统预设素材，无法从此接口删除");
        }
        return dhMaterialMapper.deleteById(materialId) > 0;
    }

    @Override
    public DhMaterialVo changeSystemMaterialStatus(DhConfigStatusBo bo) {
        DhMaterial material = dhMaterialMapper.selectById(bo.getId());
        if (material == null) {
            throw new ServiceException("素材不存在");
        }
        LambdaUpdateWrapper<DhMaterial> uw = Wrappers.lambdaUpdate();
        uw.eq(DhMaterial::getMaterialId, bo.getId());
        uw.set(DhMaterial::getStatus, bo.getStatus());
        dhMaterialMapper.update(null, uw);
        material.setStatus(bo.getStatus());
        return DhConvertUtils.toMaterialVo(material);
    }
}
