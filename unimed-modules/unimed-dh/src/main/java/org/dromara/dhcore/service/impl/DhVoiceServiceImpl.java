package org.dromara.dhcore.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.dhcore.domain.DhVoice;
import org.dromara.dhcore.domain.vo.DhVoiceVo;
import org.dromara.dhcore.mapper.DhVoiceMapper;
import org.dromara.dhcore.service.IDhVoiceService;
import org.dromara.dhcore.support.utils.DhConvertUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 音色服务实现类
 *
 * @author dhcore
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DhVoiceServiceImpl implements IDhVoiceService {

    private final DhVoiceMapper dhVoiceMapper;

    @Override
    public List<DhVoiceVo> listAvailableVoices(Long userId) {
        LambdaQueryWrapper<DhVoice> lqw = Wrappers.lambdaQuery();
        lqw.and(w -> w.eq(DhVoice::getIsSystem, 1).or().eq(DhVoice::getUserId, userId));
        lqw.eq(DhVoice::getStatus, "0");
        lqw.orderByDesc(DhVoice::getIsSystem).orderByDesc(DhVoice::getCreateTime);

        List<DhVoice> voices = dhVoiceMapper.selectList(lqw);
        List<DhVoiceVo> result = new ArrayList<>();
        for (DhVoice voice : voices) {
            result.add(DhConvertUtils.toVoiceVo(voice));
        }
        return result;
    }

    @Override
    public DhVoiceVo saveVoice(Long userId, String ossId, String name, String sampleUrl) {
        DhVoice voice = new DhVoice();
        voice.setUserId(userId);
        voice.setName(name != null ? name : "音色-" + DateUtil.format(new java.util.Date(), "yyyyMMddHHmmss"));
        voice.setOssId(ossId);
        voice.setSampleUrl(sampleUrl);
        voice.setSource("upload");
        voice.setIsSystem(0);
        voice.setStatus("0");
        dhVoiceMapper.insert(voice);
        return DhConvertUtils.toVoiceVo(voice);
    }

    @Override
    public void deleteVoice(Long userId, Long voiceId) {
        DhVoice voice = dhVoiceMapper.selectById(voiceId);
        if (voice == null) {
            throw new ServiceException("音色不存在");
        }
        if (!userId.equals(voice.getUserId())) {
            throw new ServiceException("无权删除该音色");
        }
        if (voice.getIsSystem() == 1) {
            throw new ServiceException("系统预设音色不可删除");
        }
        dhVoiceMapper.deleteById(voiceId);
    }

    @Override
    public DhVoice getById(Long voiceId) {
        return dhVoiceMapper.selectById(voiceId);
    }

    @Override
    public DhVoice save(DhVoice voice) {
        dhVoiceMapper.insert(voice);
        return voice;
    }
}
