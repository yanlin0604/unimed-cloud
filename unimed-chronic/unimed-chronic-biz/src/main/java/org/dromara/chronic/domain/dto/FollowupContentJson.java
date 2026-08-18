package org.dromara.chronic.domain.dto;

import lombok.Data;

import java.util.Map;

/**
 * 随访记录结构化内容。数据库仍使用既有 visit_content 字段，答案单独存储。
 *
 * @author unimed
 */
@Data
public class FollowupContentJson {

    private String summary;

    private Map<String, Object> vitalSigns;

    private Map<String, Object> medicationStatus;

    private Map<String, Object> adherence;

    private Map<String, Object> lifestyle;

    private String advice;

    private String nextFollowupDate;
}
