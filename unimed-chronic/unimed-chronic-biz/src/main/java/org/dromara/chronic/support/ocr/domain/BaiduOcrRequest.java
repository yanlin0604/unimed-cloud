package org.dromara.chronic.support.ocr.domain;

import lombok.Data;

/**
 * 百度OCR请求
 *
 * @author unimed
 */
@Data
public class BaiduOcrRequest {

    private String documentType;

    private String inputType;

    private String imageBase64;

    private String pdfBase64;

    private String fileUrl;
}
