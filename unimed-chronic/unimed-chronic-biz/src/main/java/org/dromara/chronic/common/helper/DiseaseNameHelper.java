package org.dromara.chronic.common.helper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.entity.ChDiseaseConfig;
import org.dromara.chronic.mapper.ChDiseaseConfigMapper;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 病种名称批量查询工具
 *
 * @author unimed
 */
@Component
@RequiredArgsConstructor
public class DiseaseNameHelper {

    private final ChDiseaseConfigMapper diseaseConfigMapper;

    public Map<String, String> batchGetDiseaseName(List<String> diseaseCodes) {
        if (diseaseCodes == null || diseaseCodes.isEmpty()) {
            return Collections.emptyMap();
        }
        List<String> codes = diseaseCodes.stream()
            .filter(StringUtils::isNotBlank)
            .distinct()
            .collect(Collectors.toList());
        if (codes.isEmpty()) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<ChDiseaseConfig> lqw = Wrappers.<ChDiseaseConfig>lambdaQuery()
            .in(ChDiseaseConfig::getDiseaseCode, codes)
            .select(ChDiseaseConfig::getDiseaseCode, ChDiseaseConfig::getDiseaseName);
        List<ChDiseaseConfig> list = diseaseConfigMapper.selectList(lqw);
        return list.stream().collect(
            Collectors.toMap(ChDiseaseConfig::getDiseaseCode, ChDiseaseConfig::getDiseaseName, (a, b) -> a));
    }
}
