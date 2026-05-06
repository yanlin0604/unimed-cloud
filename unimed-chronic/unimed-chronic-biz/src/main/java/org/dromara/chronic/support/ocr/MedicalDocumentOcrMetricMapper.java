package org.dromara.chronic.support.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.entity.ChMedicalDocumentOcrMetricItem;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 医疗文档OCR指标映射器
 *
 * @author unimed
 */
@Component
public class MedicalDocumentOcrMetricMapper {

    public List<ChMedicalDocumentOcrMetricItem> mapItems(JsonNode root) {
        List<ChMedicalDocumentOcrMetricItem> items = new ArrayList<>();
        collectMetricItems(root, items);
        return items;
    }

    public ChHealthMetricRecordBo toMetricBo(Long patientId, ChMedicalDocumentOcrMetricItem item) {
        ChHealthMetricRecordBo bo = new ChHealthMetricRecordBo();
        bo.setPatientId(patientId);
        bo.setMetricType(item.getMetricType());
        bo.setMetricValue(item.getMetricValue());
        bo.setUnit(item.getUnit());
        bo.setReferenceValueMin(item.getReferenceValueMin());
        bo.setReferenceValueMax(item.getReferenceValueMax());
        bo.setIsAbnormal(item.getIsAbnormal());
        bo.setDataSource("OCR");
        return bo;
    }

    private void collectMetricItems(JsonNode node, List<ChMedicalDocumentOcrMetricItem> items) {
        if (node == null) return;
        if (node.isArray()) {
            for (JsonNode item : node) {
                collectMetricItems(item, items);
            }
            return;
        }
        if (node.isObject()) {
            ChMedicalDocumentOcrMetricItem metric = tryBuildMetricItem(node);
            if (metric != null) {
                items.add(metric);
            }
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                collectMetricItems(fields.next().getValue(), items);
            }
        }
    }

    private ChMedicalDocumentOcrMetricItem tryBuildMetricItem(JsonNode node) {
        String name = text(node, "itemName", "item_name", "项目名称", "名称", "name", "word_name");
        String value = text(node, "result", "resultValue", "result_value", "结果", "值", "value", "word");
        if (StringUtils.isBlank(name) || StringUtils.isBlank(value)) {
            return null;
        }
        ChMedicalDocumentOcrMetricItem item = new ChMedicalDocumentOcrMetricItem();
        item.setOriginalName(name);
        item.setMetricType(resolveMetricType(name));
        item.setMetricValue(value);
        item.setUnit(text(node, "unit", "单位"));
        item.setReferenceRange(text(node, "referenceRange", "reference_range", "参考范围"));
        item.setIsAbnormal(parseAbnormal(text(node, "abnormal", "isAbnormal", "异常", "提示")));
        item.setNeedConfirm(StringUtils.isBlank(item.getMetricType()));
        item.setRawItemJson(node.toString());
        return item;
    }

    private String resolveMetricType(String name) {
        if (containsAny(name, "血糖", "葡萄糖", "GLU")) return "BLOOD_GLUCOSE";
        if (containsAny(name, "总胆固醇", "甘油三酯", "LDL", "HDL", "血脂", "TC", "TG")) return "LIPID";
        if (containsAny(name, "尿酸", "UA")) return "URIC_ACID";
        if (containsAny(name, "血压", "收缩压", "舒张压")) return "BLOOD_PRESSURE";
        if (containsAny(name, "心率", "脉搏")) return "HEART_RATE";
        if (containsAny(name, "体重")) return "WEIGHT";
        if (containsAny(name, "BMI")) return "BMI";
        if (containsAny(name, "血氧", "SPO2")) return "SPO2";
        return null;
    }

    private boolean containsAny(String text, String... keywords) {
        if (StringUtils.isBlank(text)) return false;
        for (String keyword : keywords) {
            if (text.toUpperCase().contains(keyword.toUpperCase())) return true;
        }
        return false;
    }

    private Boolean parseAbnormal(String text) {
        if (StringUtils.isBlank(text)) return false;
        return containsAny(text, "异常", "偏高", "偏低", "↑", "↓", "H", "L");
    }

    private String text(JsonNode node, String... keys) {
        for (String key : keys) {
            JsonNode value = node.get(key);
            if (value != null && value.isValueNode() && StringUtils.isNotBlank(value.asText())) {
                return value.asText();
            }
        }
        return null;
    }
}
