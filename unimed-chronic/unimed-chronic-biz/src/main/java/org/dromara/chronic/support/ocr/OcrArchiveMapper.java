package org.dromara.chronic.support.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.dromara.chronic.domain.bo.ChPatientDiseaseBo;
import org.dromara.chronic.domain.bo.ChPatientProfileBo;
import org.dromara.chronic.domain.entity.ChOcrArchiveDraft;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 医疗文档OCR建档映射器
 *
 * @author unimed
 */
@Component
@RequiredArgsConstructor
public class OcrArchiveMapper {

    private final ObjectMapper objectMapper;

    public ChOcrArchiveDraft buildArchiveDraft(JsonNode root, ChPatientProfileBo profile, List<ChPatientDiseaseBo> diseases) {
        ChOcrArchiveDraft draft = new ChOcrArchiveDraft();
        // 身份证为空时无法匹配已有档案，只能新建；身份证非空时由确认环节根据是否命中库内档案再决定 CREATE/UPDATE
        draft.setActionType(StringUtils.isBlank(profile.getIdCard()) ? "CREATE_ARCHIVE" : "MATCH_EXISTING");
        draft.setNeedConfirm(true);
        draft.setRawItemJson(root == null ? null : root.toString());
        try {
            draft.setProfileDraftJson(objectMapper.writeValueAsString(profile));
            draft.setDiseaseDraftJson(objectMapper.writeValueAsString(diseases));
            draft.setUnmappedFieldJson("[]");
        } catch (Exception ignored) {
        }
        return draft;
    }

    public ChPatientProfileBo mapProfile(JsonNode root) {
        ChPatientProfileBo profile = new ChPatientProfileBo();
        profile.setName(firstValue(root, "姓名", "患者姓名", "name", "patient_name"));
        profile.setIdCard(firstValue(root, "身份证", "身份证号", "证件号码", "idCard", "id_card"));
        profile.setGender(normalizeGender(firstValue(root, "性别", "gender")));
        profile.setPhone(firstValue(root, "联系电话", "电话", "手机号", "phone"));
        profile.setAddress(firstValue(root, "地址", "现住址", "家庭住址", "address"));
        profile.setPermanentAddress(firstValue(root, "户籍地址", "户口地址", "permanentAddress"));
        profile.setInsuranceType(firstValue(root, "医保类型", "医疗付费方式", "insuranceType"));
        profile.setSource("OCR");
        return profile;
    }

    public List<ChPatientDiseaseBo> mapDiseases(JsonNode root) {
        List<ChPatientDiseaseBo> diseases = new ArrayList<>();
        String diagnosis = firstValue(root, "主要诊断", "出院诊断", "诊断", "diagnosis");
        if (StringUtils.isNotBlank(diagnosis)) {
            ChPatientDiseaseBo disease = new ChPatientDiseaseBo();
            disease.setDiseaseCode(diagnosis);
            disease.setDiagnosisBasis(diagnosis);
            disease.setIcdCode(firstValue(root, "ICD", "ICD编码", "icdCode"));
            disease.setIsComplication(false);
            diseases.add(disease);
        }
        return diseases;
    }

    private String firstValue(JsonNode root, String... keys) {
        if (root == null) return null;
        for (String key : keys) {
            String value = findByKey(root, key);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }

    private String findByKey(JsonNode node, String key) {
        if (node == null) return null;
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                if (entry.getKey().contains(key) && entry.getValue().isValueNode()) {
                    return entry.getValue().asText();
                }
                String nested = findByKey(entry.getValue(), key);
                if (StringUtils.isNotBlank(nested)) return nested;
            }
        } else if (node.isArray()) {
            for (JsonNode item : node) {
                String nested = findByKey(item, key);
                if (StringUtils.isNotBlank(nested)) return nested;
            }
        }
        return null;
    }

    private String normalizeGender(String gender) {
        if (StringUtils.isBlank(gender)) return null;
        if (gender.contains("男")) return "1";
        if (gender.contains("女")) return "0";
        return gender;
    }
}
