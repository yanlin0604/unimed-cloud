package org.dromara.chronic.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.dromara.chronic.domain.entity.ChHealthMetricRecord;
import org.dromara.chronic.domain.vo.ChHealthMetricRecordVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 健康指标记录 Mapper
 *
 * @author unimed
 */
public interface ChHealthMetricRecordMapper extends BaseMapperPlus<ChHealthMetricRecord, ChHealthMetricRecordVo> {

    @Select("SELECT m1.* " +
            "FROM ch_health_metric_record m1 " +
            "INNER JOIN (" +
            "    SELECT metric_type, MAX(create_time) as max_time " +
            "    FROM ch_health_metric_record " +
            "    WHERE patient_id = #{patientId} " +
            "    GROUP BY metric_type" +
            ") m2 ON m1.metric_type = m2.metric_type AND m1.create_time = m2.max_time " +
            "WHERE m1.patient_id = #{patientId}")
    List<ChHealthMetricRecordVo> selectLatestByPatientId(@Param("patientId") Long patientId);
}
