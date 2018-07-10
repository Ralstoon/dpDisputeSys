package com.seu.service.impl;

        import com.seu.common.ServerResponse;
        import com.seu.domian.NormalUser;
        import com.seu.repository.NormalUserRepository;
        import com.seu.service.INormalUserService;
        import com.seu.util.MD5Util;
        import org.apache.commons.lang3.StringUtils;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;

@Service
public class NormalUserImpl implements INormalUserService {

    @Autowired
    private NormalUserRepository normalUserRepository;

    @Override
    public ServerResponse<NormalUser> login(String phone, String password) {
        int resultCount = normalUserRepository.checkUser(phone);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("手机号未注册");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);

        NormalUser normalUser = normalUserRepository.selectLogin(phone, md5Password);
        if(normalUser == null){
            return ServerResponse.createByErrorMessage("登录密码错误");
        }

        normalUser.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("用户登录成功", normalUser);
    }
}
