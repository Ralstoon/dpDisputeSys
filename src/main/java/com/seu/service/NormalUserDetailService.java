package com.seu.service;

import com.seu.domian.NormalUserDetail;
import com.seu.form.NormalUserDetailForm;

public interface NormalUserDetailService {
    /** 根据user_id创建一张detail表 */
    int registerWithNormalUserDetail(String user_id);

    /** 修改普通用户信息 */
    NormalUserDetailForm updateNormalUserDetail(NormalUserDetailForm normalUserDetailForm,String userId);
}
