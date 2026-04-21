package org.dromara.chronic.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientAccountBo;
import org.dromara.chronic.domain.entity.ChPatientAccount;
import org.dromara.chronic.domain.vo.ChPatientAccountVo;
import org.dromara.chronic.mapper.ChPatientAccountMapper;
import org.dromara.chronic.service.IChPatientAccountService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 患者账号服务实现
 *
 * @author unimed
 */
@Service
@RequiredArgsConstructor
public class ChPatientAccountServiceImpl implements IChPatientAccountService {

    private final ChPatientAccountMapper patientAccountMapper;

    @Override
    public Long register(ChPatientAccountBo bo) {
        // 手机号唯一校验
        if (StringUtils.isNotBlank(bo.getPhone())) {
            ChPatientAccountVo existing = queryByPhone(bo.getPhone());
            if (ObjectUtil.isNotNull(existing)) {
                throw new ServiceException("该手机号已注册");
            }
        }
        ChPatientAccount entity = MapstructUtils.convert(bo, ChPatientAccount.class);
        if (entity.getIsFamilyProxy() == null) {
            entity.setIsFamilyProxy(false);
        }
        patientAccountMapper.insert(entity);
        return entity.getAccountId();
    }

    @Override
    public ChPatientAccountVo queryByPhone(String phone) {
        return patientAccountMapper.selectVoOne(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getPhone, phone)
        );
    }

    @Override
    public ChPatientAccountVo queryByOpenid(String openid) {
        return patientAccountMapper.selectVoOne(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getOpenid, openid)
        );
    }

    @Override
    public ChPatientAccountVo queryByPatientId(Long patientId) {
        return patientAccountMapper.selectVoOne(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getPatientId, patientId)
                .eq(ChPatientAccount::getIsFamilyProxy, false)
                .last("LIMIT 1")
        );
    }

    @Override
    public List<ChPatientAccountVo> queryFamilyProxies(Long masterAccountId) {
        return patientAccountMapper.selectVoList(
            Wrappers.<ChPatientAccount>lambdaQuery()
                .eq(ChPatientAccount::getMasterAccountId, masterAccountId)
                .eq(ChPatientAccount::getIsFamilyProxy, true)
        );
    }

    @Override
    public Boolean bindFamilyProxy(ChPatientAccountBo bo) {
        bo.setIsFamilyProxy(true);
        if (bo.getMasterAccountId() == null) {
            throw new ServiceException("家属代管必须指定主账号");
        }
        // 校验主账号存在
        ChPatientAccount master = patientAccountMapper.selectById(bo.getMasterAccountId());
        if (ObjectUtil.isNull(master)) {
            throw new ServiceException("主账号不存在");
        }
        // 校验授权过期时间
        if (bo.getAuthExpireTime() != null && bo.getAuthExpireTime().before(new Date())) {
            throw new ServiceException("授权过期时间不能早于当前时间");
        }
        ChPatientAccount entity = MapstructUtils.convert(bo, ChPatientAccount.class);
        patientAccountMapper.insert(entity);
        return true;
    }

    @Override
    public Boolean unbindFamilyProxy(Long accountId) {
        ChPatientAccount entity = patientAccountMapper.selectById(accountId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("账号不存在");
        }
        if (!Boolean.TRUE.equals(entity.getIsFamilyProxy())) {
            throw new ServiceException("非家属代管账号不能解绑");
        }
        patientAccountMapper.deleteById(accountId);
        return true;
    }

    @Override
    public Boolean updateAuthScope(Long accountId, String authScope) {
        ChPatientAccount entity = patientAccountMapper.selectById(accountId);
        if (ObjectUtil.isNull(entity)) {
            throw new ServiceException("账号不存在");
        }
        entity.setAuthScope(authScope);
        patientAccountMapper.updateById(entity);
        return true;
    }
}
