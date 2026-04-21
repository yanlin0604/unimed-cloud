package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

import java.util.Date;

/**
 * 管理级别变更记录对象 ch_manage_level_record
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_manage_level_record")
public class ChManageLevelRecord extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long patientId;

    private String diseaseCode;

    private String oldLevel;

    private String newLevel;

    private String changeReason;

    private Date changeTime;

    @TableLogic
    private String delFlag;
}
