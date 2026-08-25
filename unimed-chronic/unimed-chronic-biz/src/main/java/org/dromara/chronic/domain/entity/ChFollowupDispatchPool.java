package org.dromara.chronic.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;

/**
 * 随访任务自动分发人员池实体
 *
 * @author unimed
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ch_followup_dispatch_pool")
public class ChFollowupDispatchPool extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 执行人用户ID
     */
    private Long userId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 执行人姓名
     */
    private String nickName;

    /**
     * 联系电话
     */
    private String phonenumber;

    /**
     * 适用病种编码（逗号分隔，*表示全部）
     */
    private String diseaseCodes;

    /**
     * 适用随访方式（逗号分隔，*表示全部）
     */
    private String visitTypes;

    /**
     * 当前待办任务上限
     */
    private Integer maxPendingTasks;

    /**
     * 分发权重(1-100)
     */
    private Integer weight;

    /**
     * 是否启用接单(1启用 0暂停)
     */
    private Boolean isActive;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 机构ID
     */
    private Long orgId;

    /**
     * 删除标志
     */
    @TableLogic
    private String delFlag;
}
