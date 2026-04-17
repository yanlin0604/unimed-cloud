package org.dromara.dhcore.domain.bo.portal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * C端订单创建业务对象
 *
 * @author unimed
 */
@Data
public class PortalOrderCreateBo {

    /**
     * 订单标题
     */
    @NotBlank(message = "订单标题不能为空")
    @Size(max = 100, message = "订单标题不能超过100个字符")
    private String title;

    /**
     * 脚本文案
     */
    @NotBlank(message = "脚本文案不能为空")
    @Size(max = 5000, message = "脚本文案不能超过5000个字符")
    private String scriptText;

    /**
     * 数字人形象ID（已保存的形象）
     */
    private Long avatarId;

    /**
     * 临时上传的形象OSS ID（优先于 avatarId）
     */
    private String avatarUploadOssId;

    /**
     * 音色ID（已保存的音色）
     */
    private Long voiceId;

    /**
     * 临时上传的音色OSS ID（优先于 voiceId）
     */
    private String voiceUploadOssId;

    /**
     * 素材文件ID列表
     */
    private List<Long> materialIds;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 参考视频OSS ID
     */
    private String referenceVideoOssId;

    /**
     * 口播风格
     */
    private String toneStyle;

    /**
     * 场景类型
     */
    private String sceneType;

    /**
     * 语速
     */
    private String speechSpeed;

    /**
     * 视频比例（如 16:9、9:16、1:1）
     */
    @Size(max = 16, message = "视频比例不能超过16个字符")
    private String videoRatio;

    /**
     * 视频规格（如 1920x1080、1080x1920）
     */
    @Size(max = 32, message = "视频规格不能超过32个字符")
    private String videoResolution;

    /**
     * 视频时长（秒）
     */
    private Integer videoDuration;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 版权声明（0未声明 1已声明）
     */
    @NotNull(message = "请确认版权声明")
    private Integer copyrightDeclared;
}
