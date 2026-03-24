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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<DhMaterialVo> listByUserId(Long userId) {
        LambdaQueryWrapper<DhMaterial> lqw = Wrappers.lambdaQuery();
        // 系统预设素材 + 用户自己上传的素材
        lqw.and(w -> w.eq(DhMaterial::getIsSystem, 1).or().eq(DhMaterial::getUserId, userId));
        lqw.eq(DhMaterial::getStatus, "0");
        lqw.orderByDesc(DhMaterial::getIsSystem).orderByDesc(DhMaterial::getCreateTime);

        List<DhMaterial> materials = dhMaterialMapper.selectList(lqw);
        List<DhMaterialVo> result = new ArrayList<>();
        for (DhMaterial material : materials) {
            result.add(DhConvertUtils.toMaterialVo(material));
        }
        return result;
    }

    @Override
    public List<DhMaterialVo> listByUserIdAndType(Long userId, String materialType) {
        LambdaQueryWrapper<DhMaterial> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhMaterial::getUserId, userId);
        lqw.eq(DhMaterial::getMaterialType, materialType);
        lqw.orderByDesc(DhMaterial::getCreateTime);

        List<DhMaterial> materials = dhMaterialMapper.selectList(lqw);
        List<DhMaterialVo> result = new ArrayList<>();
        for (DhMaterial material : materials) {
            result.add(DhConvertUtils.toMaterialVo(material));
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
        List<DhMaterialVo> voList = page.getRecords().stream().map(DhConvertUtils::toMaterialVo).toList();
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
