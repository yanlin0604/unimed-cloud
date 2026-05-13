package org.dromara.chronic.support.ocr;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.domain.entity.ChOcrArchiveDraft;
import org.dromara.chronic.domain.vo.OcrArchiveDraftVo;
import org.dromara.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * OCR 草稿 JSON 字段打包/解包工具
 * <p>
 * 历史 ch_medical_document_ocr_archive_draft 表持有 3 个 JSON 字段：
 *   profile_draft_json / disease_draft_json / raw_item_json
 * <p>
 * 设计书 ch_ocr_draft 表统一为一个 JSON 字段 draft_data，结构为：
 *   {"profile": ..., "disease": ..., "raw": ...}
 * <p>
 * 本工具负责在持久化前后转换两种形态，保持业务字段语义不变。
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OcrDraftDataConverter {

    private static final String KEY_PROFILE = "profile";
    private static final String KEY_DISEASE = "disease";
    private static final String KEY_RAW = "raw";

    private final ObjectMapper objectMapper;

    /**
     * 写入前：把 3 个业务字段打包为 draft_data JSON
     */
    public void packArchive(ChOcrArchiveDraft draft) {
        if (draft == null) {
            return;
        }
        Map<String, String> map = new LinkedHashMap<>(3);
        map.put(KEY_PROFILE, draft.getProfileDraftJson());
        map.put(KEY_DISEASE, draft.getDiseaseDraftJson());
        map.put(KEY_RAW, draft.getRawItemJson());
        try {
            draft.setDraftData(objectMapper.writeValueAsString(map));
        } catch (Exception e) {
            log.warn("打包OCR建档草稿draft_data失败", e);
        }
    }

    /**
     * 读取后：把 VO 的 draftData JSON 拆解回 3 个业务字段
     */
    public void unpackArchive(OcrArchiveDraftVo vo) {
        if (vo == null || StringUtils.isBlank(vo.getDraftData())) {
            return;
        }
        try {
            Map<String, String> map = objectMapper.readValue(
                vo.getDraftData(), new TypeReference<Map<String, String>>() {});
            if (vo.getProfileDraftJson() == null) {
                vo.setProfileDraftJson(map.get(KEY_PROFILE));
            }
            if (vo.getDiseaseDraftJson() == null) {
                vo.setDiseaseDraftJson(map.get(KEY_DISEASE));
            }
            if (vo.getRawItemJson() == null) {
                vo.setRawItemJson(map.get(KEY_RAW));
            }
        } catch (Exception e) {
            log.warn("解包OCR建档草稿draft_data失败, draftId={}", vo.getId(), e);
        }
    }
}
