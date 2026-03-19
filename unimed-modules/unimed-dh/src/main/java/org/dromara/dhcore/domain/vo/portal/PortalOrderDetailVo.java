package org.dromara.dhcore.domain.vo.portal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * C端订单详情视图对象
 *
 * @author unimed
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PortalOrderDetailVo extends PortalOrderVo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 脚本文案
     */
    private String scriptText;

    /**
     * 素材文件列表
     */
    private List<PortalOrderMaterialVo> materialFiles;

    /**
     * 进度节点列表
     */
    private List<PortalProgressNodeVo> progressNodes;

    /**
     * 成品视频URL
     */
    private String resultVideoUrl;

    /**
     * 制作人名称
     */
    private String assigneeName;

    /**
     * 完成时间
     */
    private Date completedTime;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 返工原因
     */
    private String redoReason;

    /**
     * 视频比例（如 16:9、9:16、1:1）
     */
    private String videoRatio;

    /**
     * 视频规格（如 1920x1080、1080x1920）
     */
    private String videoResolution;

    /**
     * 视频时长（秒）
     */
    private Integer videoDuration;

    /**
     * 实际金额
     */
    private BigDecimal actualAmount;

    /**
     * 形象信息
     */
    private PortalAvatarInfo avatarInfo;

    /**
     * 音色信息
     */
    private PortalVoiceInfo voiceInfo;

    /**
     * 形象信息内嵌视图对象
     */
    @Data
    public static class PortalAvatarInfo implements java.io.Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 形象ID
         */
        @JsonSerialize(using = ToStringSerializer.class)
        private Long avatarId;

        /**
         * 形象名称
         */
        private String name;

        /**
         * 图片URL
         */
        private String imageUrl;
    }

    /**
     * 音色信息内嵌视图对象
     */
    @Data
    public static class PortalVoiceInfo implements java.io.Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 音色ID
         */
        @JsonSerialize(using = ToStringSerializer.class)
        private Long voiceId;

        /**
         * 音色名称
         */
        private String name;

        /**
         * 试听URL
         */
        private String sampleUrl;
    }

    /**
     * 订单素材内嵌视图对象
     */
    @Data
    public static class PortalOrderMaterialVo implements java.io.Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 素材ID
         */
        @JsonSerialize(using = ToStringSerializer.class)
        private Long materialId;

        /**
         * 文件名称
         */
        private String fileName;

        /**
         * 文件地址
         */
        private String fileUrl;

        /**
         * 文件类型
         */
        private String fileType;

        /**
         * 缩略图地址
         */
        private String thumbnailUrl;
    }

    /**
     * 进度节点内嵌视图对象
     */
    @Data
    public static class PortalProgressNodeVo implements java.io.Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 节点名称
         */
        private String label;

        /**
         * 节点状态（pending/active/completed）
         */
        private String status;

        /**
         * 时间戳
         */
        private Date timestamp;

        /**
         * 操作说明
         */
        private String description;
    }
}
