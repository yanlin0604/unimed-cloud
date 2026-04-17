package org.dromara.dhcore.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.dhcore.domain.bo.DhDialectInviteBo;
import org.dromara.dhcore.domain.bo.DhDialectInviteQueryBo;
import org.dromara.dhcore.domain.entity.DhDialectInvite;
import org.dromara.dhcore.domain.entity.DhDialectRecord;
import org.dromara.dhcore.domain.vo.DhDialectInviteVo;
import org.dromara.dhcore.mapper.DhDialectInviteMapper;
import org.dromara.dhcore.mapper.DhDialectRecordMapper;
import org.dromara.dhcore.service.IDhDialectInviteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 方言邀请码配置服务实现类
 *
 * @author unimed
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DhDialectInviteServiceImpl implements IDhDialectInviteService {

    private final DhDialectInviteMapper dialectInviteMapper;
    private final DhDialectRecordMapper dialectRecordMapper;

    @Value("${dialect.portal.url:}")
    private String dialectPortalUrl;

    @Override
    public TableDataInfo<DhDialectInviteVo> queryPage(DhDialectInviteQueryBo queryBo, PageQuery pageQuery) {
        LambdaQueryWrapper<DhDialectInvite> lqw = Wrappers.lambdaQuery();
        lqw.like(StringUtils.isNotBlank(queryBo.getDialectName()), DhDialectInvite::getDialectName, queryBo.getDialectName());
        lqw.eq(StringUtils.isNotBlank(queryBo.getInviteCode()), DhDialectInvite::getInviteCode, queryBo.getInviteCode());
        lqw.orderByDesc(DhDialectInvite::getCreateTime);

        Page<DhDialectInvite> page = dialectInviteMapper.selectPage(pageQuery.build(), lqw);
        List<DhDialectInvite> records = page.getRecords();

        // 批量填充关联记录数
        List<String> inviteCodes = records.stream().map(DhDialectInvite::getInviteCode).toList();
        Map<String, Long> countMap;
        if (!inviteCodes.isEmpty()) {
            List<DhDialectRecord> allRecords = dialectRecordMapper.selectList(
                Wrappers.<DhDialectRecord>lambdaQuery().in(DhDialectRecord::getInviteCode, inviteCodes)
            );
            countMap = allRecords.stream().collect(Collectors.groupingBy(DhDialectRecord::getInviteCode, Collectors.counting()));
        } else {
            countMap = Map.of();
        }

        List<DhDialectInviteVo> voList = records.stream().map(invite -> {
            DhDialectInviteVo vo = new DhDialectInviteVo();
            vo.setInviteId(invite.getInviteId());
            vo.setDialectName(invite.getDialectName());
            vo.setInviteCode(invite.getInviteCode());
            vo.setCollectionUrl(buildCollectionUrl(invite.getDialectName(), invite.getInviteCode()));
            vo.setExtInfo(invite.getExtInfo());
            vo.setStatus(invite.getStatus());
            vo.setCreateTime(invite.getCreateTime());
            vo.setRecordCount(countMap.getOrDefault(invite.getInviteCode(), 0L).intValue());
            return vo;
        }).toList();

        return new TableDataInfo<>(voList, page.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DhDialectInviteVo save(DhDialectInviteBo bo) {
        String inviteCode = generateUniqueInviteCode();

        DhDialectInvite invite = new DhDialectInvite();
        invite.setDialectName(bo.getDialectName());
        invite.setInviteCode(inviteCode);
        invite.setExtInfo(bo.getExtInfo());
        invite.setStatus("0");
        dialectInviteMapper.insert(invite);

        // 生成分享链接
        String url = generateCollectionUrl(bo.getDialectName(), inviteCode);
        invite.setCollectionUrl(url);
        dialectInviteMapper.updateById(invite);

        DhDialectInviteVo vo = new DhDialectInviteVo();
        vo.setInviteId(invite.getInviteId());
        vo.setDialectName(invite.getDialectName());
        vo.setInviteCode(invite.getInviteCode());
        vo.setCollectionUrl(buildCollectionUrl(invite.getDialectName(), invite.getInviteCode()));
        vo.setExtInfo(invite.getExtInfo());
        vo.setStatus(invite.getStatus());
        vo.setCreateTime(invite.getCreateTime());
        vo.setRecordCount(0);
        return vo;
    }

    @Override
    public DhDialectInviteVo update(DhDialectInviteBo bo) {
        if (bo.getInviteId() == null) {
            throw new ServiceException("邀请配置ID不能为空");
        }
        DhDialectInvite exist = dialectInviteMapper.selectById(bo.getInviteId());
        if (exist == null) {
            throw new ServiceException("邀请配置不存在");
        }

        DhDialectInvite update = new DhDialectInvite();
        update.setInviteId(bo.getInviteId());
        update.setDialectName(bo.getDialectName());
        update.setExtInfo(bo.getExtInfo());
        if (StringUtils.isNotBlank(bo.getStatus())) {
            update.setStatus(bo.getStatus());
        }
        dialectInviteMapper.updateById(update);

        DhDialectInviteVo vo = new DhDialectInviteVo();
        vo.setInviteId(bo.getInviteId());
        vo.setDialectName(StringUtils.isNotBlank(bo.getDialectName()) ? bo.getDialectName() : exist.getDialectName());
        vo.setInviteCode(exist.getInviteCode());
        vo.setCollectionUrl(buildCollectionUrl(exist.getDialectName(), exist.getInviteCode()));
        vo.setExtInfo(bo.getExtInfo());
        vo.setStatus(StringUtils.blankToDefault(bo.getStatus(), exist.getStatus()));
        vo.setCreateTime(exist.getCreateTime());
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByIds(List<Long> inviteIds) {
        if (inviteIds == null || inviteIds.isEmpty()) {
            throw new ServiceException("请选择要删除的邀请码配置");
        }

        for (Long inviteId : inviteIds) {
            DhDialectInvite invite = dialectInviteMapper.selectById(inviteId);
            if (invite != null) {
                long count = dialectRecordMapper.selectCount(
                    Wrappers.<DhDialectRecord>lambdaQuery().eq(DhDialectRecord::getInviteCode, invite.getInviteCode())
                );
                if (count > 0) {
                    throw new ServiceException("该邀请码下存在采集记录，无法删除");
                }
            }
        }

        dialectInviteMapper.deleteByIds(inviteIds);
    }

    @Override
    public String generateCollectionUrl(String dialectName, String inviteCode) {
        return buildCollectionUrl(dialectName, inviteCode);
    }

    @Override
    public boolean isValidInviteCode(String inviteCode) {
        if (StringUtils.isBlank(inviteCode)) {
            return false;
        }
        LambdaQueryWrapper<DhDialectInvite> lqw = Wrappers.lambdaQuery();
        lqw.eq(DhDialectInvite::getInviteCode, inviteCode);
        lqw.eq(DhDialectInvite::getStatus, "0");
        lqw.eq(DhDialectInvite::getDelFlag, "0");
        return dialectInviteMapper.exists(lqw);
    }

    /**
     * 生成8位唯一邀请码（大写字母+数字组合）
     */
    private String generateUniqueInviteCode() {
        for (int i = 0; i < 50; i++) {
            String inviteCode = RandomUtil.randomStringUpper(8);
            LambdaQueryWrapper<DhDialectInvite> lqw = Wrappers.lambdaQuery();
            lqw.eq(DhDialectInvite::getInviteCode, inviteCode);
            if (!dialectInviteMapper.exists(lqw)) {
                return inviteCode;
            }
        }
        throw new ServiceException("邀请码生成失败，已达最大重试次数");
    }

    private String buildCollectionUrl(String dialectName, String inviteCode) {
        String relativePath = buildDialectPath(dialectName, inviteCode);
        String baseUrl = resolvePortalBaseUrl();
        if (StringUtils.isBlank(baseUrl)) {
            return relativePath;
        }
        return baseUrl + (relativePath.startsWith("/") ? relativePath : "/" + relativePath);
    }

    private String buildDialectPath(String dialectName, String inviteCode) {
        return "/#/dialect?dialectName=" + urlEncode(dialectName) + "&invite=" + urlEncode(inviteCode);
    }

    private String resolvePortalBaseUrl() {
        if (StringUtils.isNotBlank(dialectPortalUrl)) {
            return trimTrailingSlash(dialectPortalUrl.trim());
        }
        HttpServletRequest request = ServletUtils.getRequest();
        if (request == null) {
            return "";
        }
        String scheme = firstForwardedHeader(request.getHeader("X-Forwarded-Proto"), request.getScheme());
        String host = firstForwardedHeader(request.getHeader("X-Forwarded-Host"), request.getHeader("Host"));
        if (StringUtils.isBlank(host)) {
            host = request.getServerName();
        }
        if (StringUtils.isBlank(host)) {
            return "";
        }
        String normalizedHost = host.trim();
        String port = firstForwardedHeader(request.getHeader("X-Forwarded-Port"), null);
        if (StringUtils.isBlank(port) && !normalizedHost.contains(":")) {
            int serverPort = request.getServerPort();
            if (!isDefaultPort(scheme, serverPort)) {
                normalizedHost = normalizedHost + ":" + serverPort;
            }
        } else if (StringUtils.isNotBlank(port) && !normalizedHost.contains(":")) {
            int forwardedPort;
            try {
                forwardedPort = Integer.parseInt(port.trim());
            } catch (NumberFormatException ignored) {
                forwardedPort = -1;
            }
            if (forwardedPort > 0 && !isDefaultPort(scheme, forwardedPort)) {
                normalizedHost = normalizedHost + ":" + forwardedPort;
            }
        }
        return trimTrailingSlash(scheme + "://" + normalizedHost);
    }

    private String firstForwardedHeader(String headerValue, String defaultValue) {
        if (StringUtils.isBlank(headerValue)) {
            return defaultValue;
        }
        String[] parts = headerValue.split(",");
        return parts.length == 0 ? defaultValue : parts[0].trim();
    }

    private boolean isDefaultPort(String scheme, int port) {
        return ("http".equalsIgnoreCase(scheme) && port == 80)
            || ("https".equalsIgnoreCase(scheme) && port == 443);
    }

    private String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private String urlEncode(String value) {
        return URLEncoder.encode(StringUtils.blankToDefault(value, ""), StandardCharsets.UTF_8);
    }
}
