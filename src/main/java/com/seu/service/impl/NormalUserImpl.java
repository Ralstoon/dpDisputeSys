package com.seu.service.impl;

        import com.seu.ViewObject.ResultVO;
        import com.seu.ViewObject.ResultVOUtil;
        import com.seu.common.ServerResponse;
        import com.seu.domian.NormalUser;
        import com.seu.enums.LoginEnum;
        import com.seu.exception.NormalUserException;
        import com.seu.repository.NormalUserRepository;
        import com.seu.service.INormalUserService;
        import com.seu.service.NormalUserDetailService;
        import com.seu.util.MD5Util;
        import com.seu.utils.KeyUtil;
        import org.apache.commons.lang3.StringUtils;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;
        import org.springframework.transaction.annotation.Transactional;

        import javax.servlet.http.HttpSession;
        import java.util.Collections;
        import java.util.List;

@Service
public class NormalUserImpl implements INormalUserService {

    @Autowired
    private NormalUserRepository normalUserRepository;
    @Autowired
    private NormalUserDetailService normalUserDetailService;

    @Override
    public ServerResponse<NormalUser> login(String phone, String password) {
        int resultCount = normalUserRepository.checkUser(phone);
        if(resultCount == 0){
            return ServerResponse.createByErrorMessage("手机号未注册");
        }
        /** 加密 */
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        NormalUser normalUser = normalUserRepository.selectLogin(phone, md5Password);
        if(normalUser == null){
            return ServerResponse.createByErrorMessage("登录密码错误");
        }

        normalUser.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("用户登录成功", normalUser);
    }

    @Override
    @Transactional
    public int register(String phone, String password) {
        //验证手机号是否已存在
        int resultCount = normalUserRepository.checkUser(phone);
        if(resultCount != 0){
            return -1;
        }
        //进行注册操作
        //1、首先对normal_user表
        String user_id=KeyUtil.genUniqueKey();
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        int resultNum1=normalUserRepository.register(user_id,phone,md5Password);
        //2、其次对normal_user_detail表
        int resultNum2=normalUserDetailService.registerWithNormalUserDetail(user_id);
        return (resultNum1==resultNum2)?1:-1;
    }

    @Override
    public String findPhoneByUserId(String userId) {
        String phone = normalUserRepository.findNormalUserByUserId(userId).getPhone();
        return phone;
    }

    @Override
    public ResultVO loginout(HttpSession session) throws Exception{
        if(session==null ||(Collections.list(session.getAttributeNames()).size()==0)){
            throw new NormalUserException(LoginEnum.LOGINSESSION_NULL.getCode(),LoginEnum.LOGINSESSION_NULL.getMsg());
        }
        List<String> params= Collections.list(session.getAttributeNames());
        for(String param:params){
            session.removeAttribute(param);
        }
        return ResultVOUtil.ReturnBack(LoginEnum.LOGINOUT_SUCCESS.getCode(),LoginEnum.LOGINOUT_SUCCESS.getMsg());
    }
}
