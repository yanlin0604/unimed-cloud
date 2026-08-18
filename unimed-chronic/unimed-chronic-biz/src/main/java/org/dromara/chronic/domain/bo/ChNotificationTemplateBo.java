package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChNotificationTemplate;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 通知模板业务对象 ch_notification_template
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "通知模板业务对象")
@AutoMapper(target = ChNotificationTemplate.class, reverseConvertGenerate = false)
public class ChNotificationTemplateBo extends BaseEntity {

    @Schema(description = "模板ID（修改时必填）")
    @NotNull(message = "模板ID不能为空", groups = {EditGroup.class})
    private Long templateId;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "模板名称不能为空", groups = {AddGroup.class, EditGroup.class})
    private String templateName;

    @Schema(description = "模板编码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "模板编码不能为空", groups = {AddGroup.class, EditGroup.class})
    private String templateCode;

    @Schema(description = "推送渠道 WECHAT/SMS/IVR/PAPER", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "推送渠道不能为空", groups = {AddGroup.class, EditGroup.class})
    private String channel;

    @Schema(description = "模板内容（支持 {name} 形式占位符）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "模板内容不能为空", groups = {AddGroup.class, EditGroup.class})
    private String templateContent;

    @Schema(description = "是否启用: 1启用 0停用")
    private String isActive;

    // ==================== 以下为查询条件字段（不参与写入） ====================

    @Schema(description = "查询：模板名称/模板编码模糊关键字")
    private String keyword;
}
