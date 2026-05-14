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
 * 检验记录对象 ch_lab_test
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_lab_test")
public class ChLabTest extends TenantEntity {

    @TableId(value = "test_id")
    private Long testId;

    private Long patientId;

    private Date testDate;

    private String testType;

    private String testItems;

    private String reportImage;

    private String hospital;

    private String doctor;

    private String remark;

    @TableLogic
    private String delFlag;
}
