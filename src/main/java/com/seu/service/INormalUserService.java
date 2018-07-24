package com.seu.service;

import com.seu.ViewObject.ResultVO;
import com.seu.common.ServerResponse;
import com.seu.domian.NormalUser;

import javax.servlet.http.HttpSession;

public interface INormalUserService {
    /** 用户登录 */
    ServerResponse<NormalUser> login(String phone, String password);
    /** 用户注册 */
    int register(String phone,String password);

    String findPhoneByUserId(String userId);

    /** 用户注销 */
    ResultVO loginout(HttpSession session) throws Exception;

}
