package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 随访问卷作答明细对象 ch_followup_answer
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_followup_answer")
public class ChFollowupAnswer extends TenantEntity {

    @TableId(value = "id")
    private Long id;

    private Long recordId;

    private Long questionnaireId;

    private String questionId;

    private String answerValue;

    @TableLogic
    private String delFlag;
}
