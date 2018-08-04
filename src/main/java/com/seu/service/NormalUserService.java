package com.seu.service;

import com.seu.form.NormalUserForm;

public interface NormalUserService {
    /** 根据user_id创建一张detail表 */
//    int registerWithNormalUserDetail(String user_id);

    /** 修改普通用户信息 */
    NormalUserForm updateNormalUser(NormalUserForm normalUserForm);

    /** 根据主id查找用户实际姓名 */
    String findNormalUserNameByFatherId(String fatherId);

    /** 根据主id查找用户email */
    String findEmailByUserId(String fatherId);

    /** 根据主id查找用户phone */
    String findPhoneByUserId(String fatherId);
}
