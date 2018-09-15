package com.seu.service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName KeyWordsSearchService
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/9 0009 下午 8:23
 * @Version 1.0
 **/


public interface KeyWordsSearchService {
    /** 通过单个关键词做通配符匹配 */
    List<String> getByOneWord(String keywords,String type) throws Exception;

    List<String> getByWords(String keywords, String type) throws Exception;

    // TODO 欠完善
    /** 类案推荐 */
    Map<String,Object> getSimilarCases(String caseId);

    /** 通过类案的名字查询案件具体内容 */
    Object getCaseDetails(String caseName,String type);
}
