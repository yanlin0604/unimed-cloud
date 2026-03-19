package org.dromara.dhcore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.dhcore.domain.DhMaterial;
import org.dromara.dhcore.domain.vo.DhMaterialVo;
import org.dromara.dhcore.mapper.DhMaterialMapper;
import org.dromara.dhcore.service.IDhMaterialService;
import org.dromara.dhcore.support.utils.DhConvertUtils;
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

    @Override
    public List<DhMaterialVo> listByUserId(Long userId) {
        LambdaQueryWrapper<DhMaterial> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhMaterial::getUserId, userId);
        lqw.orderByDesc(DhMaterial::getCreateTime);

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
}
