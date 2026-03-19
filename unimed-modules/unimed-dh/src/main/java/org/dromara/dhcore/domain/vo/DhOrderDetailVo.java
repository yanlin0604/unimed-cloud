package org.dromara.dhcore.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.List;

/**
 * 订单详情视图对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class DhOrderDetailVo extends DhOrderItemVo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 脚本文案
     */
    private String scriptText;

    /**
     * 素材摘要
     */
    private String materialSummary;

    /**
     * 处理日志列表
     */
    private List<DhProcessLogVo> processLogs;

    /**
     * 质检清单
     */
    private DhQcChecklistVo qcChecklist;

    /**
     * 生产资产
     */
    private DhProductionAssetVo productionAsset;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 语气风格
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
     * 订单金额
     */
    private java.math.BigDecimal orderAmount;

    /**
     * 折扣率
     */
    private java.math.BigDecimal discountRate;

    /**
     * 实付金额
     */
    private java.math.BigDecimal actualAmount;

    /**
     * 素材文件列表
     */
    private List<DhMaterialFileVo> materialFiles;

    /**
     * 是否版权声明
     */
    private Boolean copyrightDeclared;

    /**
     * 返工原因
     */
    private String redoReason;

    /**
     * 原订单ID
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long originalOrderId;

    /**
     * 返工次数
     */
    private Integer redoCount;

    /**
     * 成片视频地址
     */
    private String resultVideoUrl;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 驳回类型
     */
    private String rejectType;
}
