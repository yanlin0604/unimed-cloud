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

    private static final String INPUT_TYPE_HINT = "，可选值：IMAGE_BASE64/IMAGE_URL/OSS_FILE/PDF_FILE";

    private final BaiduOcrProperties properties;

    private volatile MedicalAipOcr aipOcr;

    /**
     * 调用百度OCR识别
     * <p>
     * 识别通道由 inputType 显式分派（而非"猜哪个载荷字段非空"）：
     * IMAGE_BASE64 → recognizeByImage、IMAGE_URL / OSS_FILE → recognizeByImageUrl、PDF_FILE → recognizeByPdf。
     */
    public BaiduOcrResponse recognize(BaiduOcrRequest request) {
        ensureEnabled();
        MedicalAipOcr client = getClient();
        String apiUrl = resolveOcrUrl(request.getDocumentType());
        String inputType = request.getInputType() == null ? "" : request.getInputType();
        try {
            HashMap<String, String> options = new HashMap<>();
            JSONObject result = switch (inputType) {
                case "IMAGE_BASE64" -> {
                    requireNotBlank(request.getImageBase64(), inputType, "imageBase64");
                    yield client.recognizeByImage(apiUrl, decodeBase64(request.getImageBase64(), "imageBase64"), options);
                }
                case "IMAGE_URL", "OSS_FILE" -> {
                    requireNotBlank(request.getFileUrl(), inputType, "fileUrl");
                    yield client.recognizeByImageUrl(apiUrl, request.getFileUrl(), options);
                }
                case "PDF_FILE" -> {
                    requireNotBlank(request.getPdfBase64(), inputType, "pdfBase64");
                    yield client.recognizeByPdf(apiUrl, request.getPdfBase64(), options);
                }
                default -> throw new ServiceException(
                    "不支持的OCR输入类型 inputType=" + inputType + INPUT_TYPE_HINT);
            };
            return parseResponse(result);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.warn("百度OCR接口调用失败: {}", e.getMessage(), e);
            throw new ServiceException("百度OCR接口调用失败");
        }
    }

    // ---------- private ----------

    /**
     * 载荷字段校验：错误信息必须明确指向 inputType，方便调用方判断是入参搭配错了还是内容缺失。
     */
    private void requireNotBlank(String payload, String inputType, String fieldName) {
        if (StringUtils.isBlank(payload)) {
            throw new ServiceException("inputType=" + inputType + " 时 " + fieldName + " 不能为空");
        }
    }

    private byte[] decodeBase64(String base64, String fieldName) {
        try {
            return Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            throw new ServiceException(fieldName + " 不是合法的 Base64 内容");
        }
    }

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
