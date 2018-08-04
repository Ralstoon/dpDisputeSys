package com.seu.service.impl;

import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.RedisConstant;
import com.seu.common.ServerResponse;
import com.seu.converter.User2UserForm;
import com.seu.domian.NormalUser;
import com.seu.domian.User;
import com.seu.enums.LoginEnum;
import com.seu.exception.NormalUserException;
import com.seu.form.UserForm;
import com.seu.repository.NormalUserRepository;
import com.seu.repository.UserRepository;
import com.seu.service.UserService;
import com.seu.util.MD5Util;
import com.seu.utils.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private NormalUserRepository normalUserRepository;
//    @Autowired
//    private NormalUserService normalUserDetailService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserRepository userRepository;




    @Override
    public ServerResponse<UserForm> login(String phone, String password) {
//        int resultCount = normalUserRepository.checkUser(phone);
        User user=userRepository.findByPhone(phone);
        if(user == null){
            return ServerResponse.createByErrorMessage("手机号未注册");
        }
        /** 加密 */
        String md5Password = MD5Util.MD5EncodeUtf8(password);

//        NormalUser normalUser = normalUserRepository.selectLogin(phone, md5Password);
        if(!user.getPassword().equals(md5Password)){
            return ServerResponse.createByErrorMessage("登录密码错误");
        }

//        normalUser.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess("用户登录成功", User2UserForm.convert(user));
    }

    @Override
    @Transactional
    public int register(String phone, String password) {
        //验证手机号是否已存在
//        int resultCount = normalUserRepository.checkUser(phone);
        if(userRepository.findByPhone(phone)!=null){
            return -1;
        }
        //进行注册操作
        //1、首先对user表
        String normalId=KeyUtil.genUniqueKey();
        String ID=KeyUtil.genUniqueKey();
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user=new User(ID,phone,md5Password,"普通用户",normalId);
        User saveUser=userRepository.save(user);
        if(saveUser==null)
            return -1;
        //2、其次对normal_user_detail表
//        NormalUser saveNormalUser=normalUserDetailService.registerWithNormalUserDetail(user_id);
        NormalUser normalUser=new NormalUser(normalId,ID);
        NormalUser saveNormalUser=normalUserRepository.save(normalUser);
        return (saveNormalUser==null)?-1:1;
    }

//    @Override
//    public String findPhoneByUserId(String userId) {
//        String phone = normalUserRepository.findNormalUserByUserId(userId).getPhone();
//        return phone;
//    }

    @Override
    public ResultVO loginout(String ID,String role) throws Exception{
//        Object currentUser=redisTemplate.opsForValue().get(String.format(RedisConstant.USER_RREFIX,role,ID));
        if(redisTemplate.opsForValue().get(String.format(RedisConstant.USER_RREFIX,role,ID))==null){
            throw new NormalUserException(LoginEnum.LOGINSESSION_NULL.getCode(),LoginEnum.LOGINSESSION_NULL.getMsg());
        }else{
            redisTemplate.opsForValue().getOperations().delete(String.format(RedisConstant.USER_RREFIX,role,ID));
            return ResultVOUtil.ReturnBack(LoginEnum.LOGINOUT_SUCCESS.getCode(),LoginEnum.LOGINOUT_SUCCESS.getMsg());
        }

//        if(session==null ||(Collections.list(session.getAttributeNames()).size()==0)){
//            throw new NormalUserException(LoginEnum.LOGINSESSION_NULL.getCode(),LoginEnum.LOGINSESSION_NULL.getMsg());
//        }
//        List<String> params= Collections.list(session.getAttributeNames());
//        for(String param:params){
//            session.removeAttribute(param);
//        }
//        return ResultVOUtil.ReturnBack(LoginEnum.LOGINOUT_SUCCESS.getCode(),LoginEnum.LOGINOUT_SUCCESS.getMsg());
    }
}
