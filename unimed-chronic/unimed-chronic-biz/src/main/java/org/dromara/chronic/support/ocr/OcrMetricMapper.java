package org.dromara.chronic.support.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import org.dromara.chronic.domain.bo.ChHealthMetricRecordBo;
import org.dromara.chronic.domain.entity.ChOcrMetricItem;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 医疗文档OCR指标映射器
 *
 * @author unimed
 */
@Component
public class OcrMetricMapper {

    /** 血压字面量识别：120/80、120 / 80 mmHg、收缩压120 舒张压80 等常见格式 */
    private static final Pattern BP_SLASH_PATTERN = Pattern.compile("(\\d{2,3})\\s*/\\s*(\\d{2,3})");
    private static final Pattern BP_SYSTOLIC_NAMED = Pattern.compile("(?:收缩压|高压|SBP)\\D{0,5}(\\d{2,3})");
    private static final Pattern BP_DIASTOLIC_NAMED = Pattern.compile("(?:舒张压|低压|DBP)\\D{0,5}(\\d{2,3})");

    public List<ChOcrMetricItem> mapItems(JsonNode root) {
        List<ChOcrMetricItem> items = new ArrayList<>();
        collectMetricItems(root, items);
        return items;
    }

    public ChHealthMetricRecordBo toMetricBo(Long patientId, ChOcrMetricItem item) {
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

    private void collectMetricItems(JsonNode node, List<ChOcrMetricItem> items) {
        if (node == null) return;
        if (node.isArray()) {
            for (JsonNode item : node) {
                collectMetricItems(item, items);
            }
            return;
        }
        if (node.isObject()) {
            List<ChOcrMetricItem> built = tryBuildMetricItems(node);
            if (built != null && !built.isEmpty()) {
                items.addAll(built);
            }
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                collectMetricItems(fields.next().getValue(), items);
            }
        }
    }

    /**
     * 一个 OCR 节点可能产生 0 / 1 / 2 条指标项。
     * 血压节点会被拆为 BP_SYSTOLIC + BP_DIASTOLIC 两条独立 item，与字典/数据库格式保持一致。
     */
    private List<ChOcrMetricItem> tryBuildMetricItems(JsonNode node) {
        String name = text(node, "itemName", "item_name", "项目名称", "名称", "name", "word_name");
        String value = text(node, "result", "resultValue", "result_value", "结果", "值", "value", "word");
        if (StringUtils.isBlank(name) || StringUtils.isBlank(value)) {
            return List.of();
        }

        String unit = text(node, "unit", "单位");
        String reference = text(node, "referenceRange", "reference_range", "参考范围");
        Boolean abnormal = parseAbnormal(text(node, "abnormal", "isAbnormal", "异常", "提示"));
        String rawJson = node.toString();

        // 血压：拆为收缩压 + 舒张压
        if (containsAny(name, "血压", "收缩压", "舒张压", "BP")) {
            return splitBloodPressure(name, value, unit, reference, abnormal, rawJson);
        }

        // 普通指标
        ChOcrMetricItem item = new ChOcrMetricItem();
        item.setOriginalName(name);
        item.setMetricType(resolveMetricType(name));
        item.setMetricValue(value);
        item.setUnit(unit);
        item.setReferenceRange(reference);
        item.setIsAbnormal(abnormal);
        item.setNeedConfirm(StringUtils.isBlank(item.getMetricType()));
        item.setRawItemJson(rawJson);
        return List.of(item);
    }

    /**
     * 解析血压原始值为收缩压 + 舒张压两条记录。
     * 优先匹配"120/80"，其次匹配"收缩压 120 舒张压 80"型；都失败时仅产出一条 BP_SYSTOLIC 留待人工确认。
     */
    private List<ChOcrMetricItem> splitBloodPressure(String name, String value, String unit,
                                                      String reference, Boolean abnormal, String rawJson) {
        Integer systolic = null;
        Integer diastolic = null;

        Matcher slash = BP_SLASH_PATTERN.matcher(value);
        if (slash.find()) {
            systolic = Integer.parseInt(slash.group(1));
            diastolic = Integer.parseInt(slash.group(2));
        } else {
            Matcher sys = BP_SYSTOLIC_NAMED.matcher(name + " " + value);
            if (sys.find()) systolic = Integer.parseInt(sys.group(1));
            Matcher dia = BP_DIASTOLIC_NAMED.matcher(name + " " + value);
            if (dia.find()) diastolic = Integer.parseInt(dia.group(1));
        }

        // 仅根据名字判定收缩压/舒张压（条目仅含其中一项时）
        if (systolic == null && diastolic == null) {
            if (containsAny(name, "收缩压", "高压", "SBP")) {
                Matcher m = Pattern.compile("(\\d{2,3})").matcher(value);
                if (m.find()) systolic = Integer.parseInt(m.group(1));
            } else if (containsAny(name, "舒张压", "低压", "DBP")) {
                Matcher m = Pattern.compile("(\\d{2,3})").matcher(value);
                if (m.find()) diastolic = Integer.parseInt(m.group(1));
            }
        }

        List<ChOcrMetricItem> items = new ArrayList<>(2);
        String resolvedUnit = StringUtils.isBlank(unit) ? "mmHg" : unit;

        if (systolic != null) {
            items.add(buildBpItem(name, "BP_SYSTOLIC", String.valueOf(systolic), resolvedUnit, reference, abnormal, rawJson));
        }
        if (diastolic != null) {
            items.add(buildBpItem(name, "BP_DIASTOLIC", String.valueOf(diastolic), resolvedUnit, reference, abnormal, rawJson));
        }

        // 兜底：解析失败但原始字符串仍要保留供人工确认
        if (items.isEmpty()) {
            ChOcrMetricItem fallback = new ChOcrMetricItem();
            fallback.setOriginalName(name);
            fallback.setMetricType(null);
            fallback.setMetricValue(value);
            fallback.setUnit(resolvedUnit);
            fallback.setReferenceRange(reference);
            fallback.setIsAbnormal(abnormal);
            fallback.setNeedConfirm(true);
            fallback.setRawItemJson(rawJson);
            items.add(fallback);
        }
        return items;
    }

    private ChOcrMetricItem buildBpItem(String originalName, String metricType, String value, String unit,
                                         String reference, Boolean abnormal, String rawJson) {
        ChOcrMetricItem item = new ChOcrMetricItem();
        item.setOriginalName(originalName);
        item.setMetricType(metricType);
        item.setMetricValue(value);
        item.setUnit(unit);
        item.setReferenceRange(reference);
        item.setIsAbnormal(abnormal);
        item.setNeedConfirm(false);
        item.setRawItemJson(rawJson);
        return item;
    }

    private String resolveMetricType(String name) {
        if (containsAny(name, "血糖", "葡萄糖", "GLU")) return "BLOOD_GLUCOSE";
        if (containsAny(name, "总胆固醇", "甘油三酯", "LDL", "HDL", "血脂", "TC", "TG")) return "LIPID";
        if (containsAny(name, "尿酸", "UA")) return "URIC_ACID";
        // 血压由 splitBloodPressure 处理，不再返回单一类型
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
