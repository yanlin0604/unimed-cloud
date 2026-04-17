package org.dromara.dhcore.mapper;

import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.dhcore.domain.DhWalletLog;
import org.dromara.dhcore.domain.vo.DhWalletLogVo;

import java.math.BigDecimal;

/**
 * 数字人口播钱包流水Mapper
 */
public interface DhWalletLogMapper extends BaseMapperPlus<DhWalletLog, DhWalletLogVo> {

    /**
     * 统计指定用户退款流水金额绝对值之和
     *
     * @param userId 用户ID
     * @return 累计退款金额，无记录时返回0
     */
    @Select("SELECT COALESCE(SUM(ABS(amount)), 0) FROM dh_wallet_log " +
            "WHERE user_id = #{userId} AND type = 'REFUND' AND del_flag = '0'")
    BigDecimal sumRefundAmount(@Param("userId") Long userId);
}
