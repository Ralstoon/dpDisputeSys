package com.seu.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.common.ServerResponse;
import com.seu.form.VOForm.UserForm;

public interface UserService {
    /** 用户登录 */
    ServerResponse<UserForm> login(String phone, String password);
    /** 用户注册 */
    int register(String phone,String password, String name);

//    String findPhoneByUserId(String userId);

    /** 用户注销 */
    ResultVO loginout(String ID,String role) throws Exception;

    /** 根据用户id查找名字*/
    String findNameById(String ID);


    /** 调解员账号注册 */
    ResultVO registerMediator(String province, String city, String mediate_center, JSONArray map);

    String getCode(String Phone);

}
