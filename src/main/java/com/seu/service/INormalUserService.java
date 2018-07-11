package com.seu.service;

import com.seu.common.ServerResponse;
import com.seu.domian.NormalUser;

public interface INormalUserService {
    /** 用户登录 */
    ServerResponse<NormalUser> login(String phone, String password);
    /** 用户注册 */
    int register(String phone,String password);
}
