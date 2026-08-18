package org.dromara.chronic.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.chronic.domain.entity.ChLifestyleRecord;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * 生活方式记录业务对象
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "生活方式记录业务对象")
@AutoMapper(target = ChLifestyleRecord.class, reverseConvertGenerate = false)
public class ChLifestyleRecordBo extends BaseEntity {

    @Schema(description = "主键ID")
    private Long id;

    /**
     * 患者端提交时由服务端从登录上下文注入，管理端提交时由控制器显式赋值，故不做 NotNull 校验
     */
    @Schema(description = "患者ID")
    private Long patientId;

    @Schema(description = "吸烟状态")
    private String smokingStatus;

    @Schema(description = "饮酒状态")
    private String drinkingStatus;

    @Schema(description = "运动频率")
    private String exerciseFreq;

    @Schema(description = "饮食习惯")
    private String dietHabit;

    @Schema(description = "心理状态")
    private String psychologicalStatus;

    @Schema(description = "依从性等级")
    private String complianceLevel;
}
