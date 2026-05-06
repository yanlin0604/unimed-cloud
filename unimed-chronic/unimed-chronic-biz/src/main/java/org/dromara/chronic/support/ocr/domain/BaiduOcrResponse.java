package org.dromara.chronic.support.ocr.domain;

import lombok.Data;

/**
 * 百度OCR响应
 *
 * @author unimed
 */
@Data
public class BaiduOcrResponse {

    private boolean success;

    private String rawJson;

    private String errorCode;

    private String errorMsg;

    public static BaiduOcrResponse success(String rawJson) {
        BaiduOcrResponse response = new BaiduOcrResponse();
        response.setSuccess(true);
        response.setRawJson(rawJson);
        return response;
    }

    public static BaiduOcrResponse failure(String errorCode, String errorMsg, String rawJson) {
        BaiduOcrResponse response = new BaiduOcrResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setErrorMsg(errorMsg);
        response.setRawJson(rawJson);
        return response;
    }
}
