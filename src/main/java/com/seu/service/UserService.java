package com.seu.service;

import com.seu.ViewObject.ResultVO;
import com.seu.common.ServerResponse;
import com.seu.domian.NormalUser;
import com.seu.form.UserForm;

import javax.servlet.http.HttpSession;

public interface UserService {
    /** 用户登录 */
    ServerResponse<UserForm> login(String phone, String password);
    /** 用户注册 */
    int register(String phone,String password);

//    String findPhoneByUserId(String userId);

    /** 用户注销 */
    ResultVO loginout(String ID,String role) throws Exception;

}
