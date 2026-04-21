package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 行政区划对象 ch_area_dict
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_area_dict")
public class ChAreaDict extends TenantEntity {

    @TableId(value = "area_code")
    private String areaCode;

    private String areaName;

    /**
     * 区划层级: 1省/2市/3县/4乡/5村
     */
    private Integer areaLevel;

    private String parentAreaCode;

    @TableLogic
    private String delFlag;
}
