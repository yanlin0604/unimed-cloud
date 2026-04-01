package org.dromara.dhcore.controller.portal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.constant.TenantConstants;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.common.web.core.BaseController;
import org.dromara.dhcore.domain.bo.DhDialectRecordBo;
import org.dromara.dhcore.domain.vo.DhDialectPromptVo;
import org.dromara.dhcore.domain.vo.DhDialectRecordVo;
import org.dromara.dhcore.domain.vo.portal.PortalFileUploadVo;
import org.dromara.dhcore.service.IDhDialectPromptService;
import org.dromara.dhcore.service.IDhDialectRecordService;
import org.dromara.resource.api.RemoteFileService;
import org.dromara.resource.api.domain.RemoteFile;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.function.Supplier;

/**
 * C端方言采集控制器
 *
 * @author unimed
 */
@Tag(name = "C端-方言采集")
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/dh/portal/dialect")
public class PortalDialectController extends BaseController {

    private final IDhDialectPromptService dialectPromptService;
    private final IDhDialectRecordService dialectRecordService;

    @DubboReference
    private RemoteFileService remoteFileService;

    /**
     * 获取启用的提示文字列表
     */
    @Operation(summary = "获取启用的提示文字列表")
    @GetMapping("/prompts")
    public R<List<DhDialectPromptVo>> getPrompts() {
        return executeWithPortalTenant(() -> R.ok(dialectPromptService.listEnabled()));
    }

    /**
     * 上传方言录音文件
     */
    @Operation(summary = "上传方言录音文件")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R<PortalFileUploadVo> uploadAudio(@RequestPart("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return R.fail("上传文件不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            return R.fail("上传文件名不能为空");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("audio/")) {
            return R.fail("仅支持音频文件上传");
        }

        try {
            RemoteFile uploadedFile = remoteFileService.upload(originalFilename, originalFilename, contentType, file.getBytes());
            PortalFileUploadVo vo = new PortalFileUploadVo();
            vo.setOssId(String.valueOf(uploadedFile.getOssId()));
            vo.setUrl(uploadedFile.getUrl());
            vo.setFileName(uploadedFile.getOriginalName());
            return R.ok(vo);
        } catch (Exception e) {
            return R.fail("录音上传失败，请重试");
        }
    }

    /**
     * 提交录音记录
     */
    @Operation(summary = "提交录音记录")
    @PostMapping("/record")
    public R<DhDialectRecordVo> submitRecord(@Validated @RequestBody DhDialectRecordBo bo) {
        return executeWithPortalTenant(() -> {
            Long userId = LoginHelper.isLogin() ? LoginHelper.getUserId() : null;
            return R.ok(dialectRecordService.submit(userId, bo));
        });
    }

    private <T> R<T> executeWithPortalTenant(Supplier<R<T>> action) {
        return TenantHelper.dynamic(resolvePortalTenantId(), action);
    }

    private String resolvePortalTenantId() {
        return StringUtils.blankToDefault(LoginHelper.getTenantId(), TenantConstants.DEFAULT_TENANT_ID);
    }
}
