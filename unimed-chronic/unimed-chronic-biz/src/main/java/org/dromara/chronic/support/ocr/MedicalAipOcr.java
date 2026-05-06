package org.dromara.chronic.support.ocr;

import com.baidu.aip.http.AipRequest;
import com.baidu.aip.http.EBodyFormat;
import com.baidu.aip.ocr.AipOcr;
import com.baidu.aip.util.Base64Util;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * 扩展百度 AipOcr，补充医疗文档 OCR 接口
 * <p>
 * 官方 SDK 未内置医疗类接口，此处按照 SDK 内部调用模式
 * （preOperation → 组装 body → setUri → postOperation → requestServer）
 * 添加病案首页、检验报告、检查报告、诊断报告等端点。
 *
 * @author unimed
 */
public class MedicalAipOcr extends AipOcr {

    public MedicalAipOcr(String appId, String apiKey, String secretKey) {
        super(appId, apiKey, secretKey);
    }

    // ---------- 通用调用：按指定 URL 发起 OCR 请求 ----------

    /**
     * 以图片字节数组调用指定 OCR 接口
     */
    public JSONObject recognizeByImage(String apiUrl, byte[] image, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);
        request.addBody("image", Base64Util.encode(image));
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(apiUrl);
        request.setBodyFormat(EBodyFormat.FORM_KV);
        postOperation(request);
        return requestServer(request);
    }

    /**
     * 以图片 URL 调用指定 OCR 接口
     */
    public JSONObject recognizeByImageUrl(String apiUrl, String imageUrl, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);
        request.addBody("url", imageUrl);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(apiUrl);
        request.setBodyFormat(EBodyFormat.FORM_KV);
        postOperation(request);
        return requestServer(request);
    }


    /**
     * 以 PDF base64 调用指定 OCR 接口
     */
    public JSONObject recognizeByPdf(String apiUrl, String pdfBase64, HashMap<String, String> options) {
        AipRequest request = new AipRequest();
        preOperation(request);
        request.addBody("pdf_file", pdfBase64);
        if (options != null) {
            request.addBody(options);
        }
        request.setUri(apiUrl);
        request.setBodyFormat(EBodyFormat.FORM_KV);
        postOperation(request);
        return requestServer(request);
    }
}
