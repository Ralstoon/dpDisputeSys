package com.seu.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName WebrtcService
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/10/12 0012 下午 8:51
 * @Version 1.0
 **/
public interface WebrtcService {

    JSONObject mixStream(Integer time);
}
