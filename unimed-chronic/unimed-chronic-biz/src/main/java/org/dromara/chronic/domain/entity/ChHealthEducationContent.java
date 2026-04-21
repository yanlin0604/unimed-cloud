package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.tenant.core.TenantEntity;

/**
 * 宣教内容对象 ch_health_education_content
 *
 * @author unimed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("ch_health_education_content")
public class ChHealthEducationContent extends TenantEntity {

    @TableId(value = "content_id")
    private Long contentId;

    private String title;

    private String contentBody;

    /**
     * 标签 JSON（按病种/画像/用药/体检/天气节气）
     */
    private String tags;

    @TableLogic
    private String delFlag;
}
