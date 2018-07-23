package com.seu.service;

import com.seu.domian.NormalUserDetail;
import com.seu.form.NormalUserDetailForm;

public interface NormalUserDetailService {
    /** 根据user_id创建一张detail表 */
    int registerWithNormalUserDetail(String user_id);

    /** 修改普通用户信息 */
    NormalUserDetailForm updateNormalUserDetail(NormalUserDetailForm normalUserDetailForm,String userId);

    /** 根据用户id查找用户实际姓名 */
    String findNormalUserNameByUserId(String userId);

    /** 根据用户id查找用户email */
    String findEmailByUserId(String userId);
}
