package org.dromara.chronic.support.ocr;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.chronic.config.BaiduOcrProperties;
import org.dromara.chronic.support.ocr.domain.BaiduOcrRequest;
import org.dromara.chronic.support.ocr.domain.BaiduOcrResponse;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.HashMap;

/**
 * 百度OCR客户端（基于官方 aip-java-sdk）
 * <p>
 * SDK 自动管理 access_token 的获取与刷新，无需手动维护。
 *
 * @author unimed
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BaiduOcrClient {

    private final BaiduOcrProperties properties;

    private volatile MedicalAipOcr aipOcr;

    /**
     * 调用百度OCR识别
     */
    public BaiduOcrResponse recognize(BaiduOcrRequest request) {
        ensureEnabled();
        MedicalAipOcr client = getClient();
        String apiUrl = resolveOcrUrl(request.getDocumentType());
        try {
            HashMap<String, String> options = new HashMap<>();
            JSONObject result;
            if (StringUtils.isNotBlank(request.getImageBase64())) {
                byte[] imageBytes = Base64.getDecoder().decode(request.getImageBase64());
                result = client.recognizeByImage(apiUrl, imageBytes, options);
            } else if (StringUtils.isNotBlank(request.getFileUrl())) {
                result = client.recognizeByImageUrl(apiUrl, request.getFileUrl(), options);
            } else if (StringUtils.isNotBlank(request.getPdfBase64())) {
                result = client.recognizeByPdf(apiUrl, request.getPdfBase64(), options);
            } else {
                throw new ServiceException("OCR识别内容不能为空");
            }
            return parseResponse(result);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.warn("百度OCR接口调用失败: {}", e.getMessage(), e);
            throw new ServiceException("百度OCR接口调用失败");
        }
    }

    // ---------- private ----------

    private MedicalAipOcr getClient() {
        if (aipOcr == null) {
            synchronized (this) {
                if (aipOcr == null) {
                    if (StringUtils.isBlank(properties.getAppId())
                        || StringUtils.isBlank(properties.getApiKey())
                        || StringUtils.isBlank(properties.getSecretKey())) {
                        throw new ServiceException("百度OCR配置不完整，请检查 appId / apiKey / secretKey");
                    }
                    MedicalAipOcr client = new MedicalAipOcr(
                        properties.getAppId(),
                        properties.getApiKey(),
                        properties.getSecretKey()
                    );
                    client.setConnectionTimeoutInMillis(properties.getConnectTimeout());
                    client.setSocketTimeoutInMillis(properties.getReadTimeout());
                    aipOcr = client;
                }
            }
        }
        return aipOcr;
    }

    private BaiduOcrResponse parseResponse(JSONObject result) {
        String rawJson = result.toString();
        if (result.has("error_code")) {
            return BaiduOcrResponse.failure(
                String.valueOf(result.get("error_code")),
                result.optString("error_msg", "百度OCR识别失败"),
                rawJson
            );
        }
        return BaiduOcrResponse.success(rawJson);
    }

    private String resolveOcrUrl(String documentType) {
        return switch (documentType == null ? "" : documentType) {
            case "MEDICAL_RECORD_HOME", "DISCHARGE_SUMMARY" -> BaiduOcrEndpoints.MEDICAL_RECORD_HOME;
            case "LAB_REPORT" -> BaiduOcrEndpoints.LAB_REPORT;
            case "EXAM_REPORT" -> BaiduOcrEndpoints.EXAM_REPORT;
            case "DIAGNOSIS_REPORT" -> BaiduOcrEndpoints.DIAGNOSIS_REPORT;
            default -> BaiduOcrEndpoints.LAB_REPORT;
        };
    }

    private void ensureEnabled() {
        if (!Boolean.TRUE.equals(properties.getEnabled())) {
            throw new ServiceException("OCR识别功能暂未启用");
        }
    }
}
