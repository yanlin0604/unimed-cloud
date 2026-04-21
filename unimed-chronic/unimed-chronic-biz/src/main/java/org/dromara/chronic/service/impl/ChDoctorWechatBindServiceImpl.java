package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChDoctorWechatBindBo;
import org.dromara.chronic.domain.entity.ChDoctorWechatBind;
import org.dromara.chronic.domain.vo.ChDoctorWechatBindVo;
import org.dromara.chronic.mapper.ChDoctorWechatBindMapper;
import org.dromara.chronic.service.IChDoctorWechatBindService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 医生微信绑定服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChDoctorWechatBindServiceImpl implements IChDoctorWechatBindService {

    private final ChDoctorWechatBindMapper wechatBindMapper;

    @Override
    public Long bind(ChDoctorWechatBindBo bo) {
        // 检查 openid 是否已绑定
        ChDoctorWechatBind existing = wechatBindMapper.selectOne(
            Wrappers.<ChDoctorWechatBind>lambdaQuery()
                .eq(ChDoctorWechatBind::getOpenid, bo.getOpenid())
        );
        if (ObjectUtil.isNotNull(existing)) {
            // openid 已绑定其他用户，更新绑定
            existing.setUserId(bo.getUserId());
            existing.setUnionid(bo.getUnionid());
            wechatBindMapper.updateById(existing);
            return existing.getId();
        }
        ChDoctorWechatBind entity = MapstructUtils.convert(bo, ChDoctorWechatBind.class);
        entity.setBindTime(new Date());
        wechatBindMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public ChDoctorWechatBindVo queryByOpenid(String openid) {
        return wechatBindMapper.selectVoOne(
            Wrappers.<ChDoctorWechatBind>lambdaQuery()
                .eq(ChDoctorWechatBind::getOpenid, openid)
        );
    }

    @Override
    public ChDoctorWechatBindVo queryByUserId(Long userId) {
        return wechatBindMapper.selectVoOne(
            Wrappers.<ChDoctorWechatBind>lambdaQuery()
                .eq(ChDoctorWechatBind::getUserId, userId)
                .last("LIMIT 1")
        );
    }

    @Override
    public Boolean unbind(Long id) {
        ChDoctorWechatBind entity = wechatBindMapper.selectById(id);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("绑定记录不存在");
        }
        wechatBindMapper.deleteById(id);
        return true;
    }
}
