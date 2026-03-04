package org.dromara.dh.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数字人信息
 *
 * @author unimed
 * @since 2.5.1
 */
@Data
@Schema(description = "数字人信息")
public class DigitalHumanInfo {

    @Schema(description = "数字人ID", example = "312")
    private Long id;

    @Schema(description = "数字人唯一标识", example = "7856875483760")
    private String digitalId;

    @Schema(description = "背景替换设置")
    private String replaceBg;

    @Schema(description = "声音文件", example = "BV700_V2_streaming")
    private String voiceFile;

    @Schema(description = "背景替换状态")
    private String replaceBgState;

    @Schema(description = "动作视频URL")
    private String actionVideoUrl;

    @Schema(description = "静默视频URL")
    private String silentVideoUrl;

    @Schema(description = "合成视频URL")
    private String composeVideoUrl;

    @Schema(description = "视频合成状态", example = "1")
    private Integer videoComposeState;

    @Schema(description = "性别", example = "female", allowableValues = {"male", "female"})
    private String sex;

    @Schema(description = "数字人标题", example = "护士灿灿")
    private String figureTitle;

    @Schema(description = "数字人介绍")
    private String figureIntroduction;

    @Schema(description = "分组类别")
    private String groupCategory;

    @Schema(description = "标签词")
    private String labelWords;

    @Schema(description = "训练人物ID", example = "护士灿灿_20250925_7856875483760_avatar")
    private String trainHumanId;

    @Schema(description = "创建用户")
    private String createUser;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "肖像抠图")
    private String portraitMatting;

    @Schema(description = "封面图片URL")
    private String coverImage;
}