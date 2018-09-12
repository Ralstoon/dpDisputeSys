package com.seu.service;

import java.util.List;

/**
 * @ClassName KeyWordsSearchService
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/9 0009 下午 8:23
 * @Version 1.0
 **/


public interface KeyWordsSearchService {
    /** 通过单个关键词做通配符匹配 */
    List<String> getByOneWord(String keywords) throws Exception;

    List<String> getByWords(String keywords) throws Exception;
}
