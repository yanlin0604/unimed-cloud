package org.dromara.dhcore.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 数字人口播订单素材对象 dh_order_material
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("dh_order_material")
public class DhOrderMaterial extends TenantEntity {

    /**
     * 素材ID
     */
    @TableId(value = "material_id")
    private Long materialId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 文件ID
     */
    private String fileId;

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

    /**
     * 排序号
     */
    private Integer sort;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
