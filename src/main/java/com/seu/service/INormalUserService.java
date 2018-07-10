package com.seu.service;

import com.seu.common.ServerResponse;
import com.seu.domian.NormalUser;

public interface INormalUserService {
    ServerResponse<NormalUser> login(String phone, String password);
}
