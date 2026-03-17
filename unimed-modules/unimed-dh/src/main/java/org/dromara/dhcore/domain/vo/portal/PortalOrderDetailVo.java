package org.dromara.dhcore.domain.vo.portal;

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
    private List<PortalOrderMaterialVo> materials;

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
     * 实际金额
     */
    private BigDecimal actualAmount;

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
