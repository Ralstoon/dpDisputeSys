package com.seu.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.RedisConstant;
import com.seu.common.ServerResponse;
import com.seu.converter.User2UserForm;
import com.seu.domian.Admin;
import com.seu.domian.Mediator;
import com.seu.domian.NormalUser;
import com.seu.domian.User;
import com.seu.enums.LoginEnum;
import com.seu.enums.RegisterEnum;
import com.seu.exception.NormalUserException;
import com.seu.form.VOForm.RegisterMediatorForm;
import com.seu.form.VOForm.UserForm;
import com.seu.repository.AdminRepository;
import com.seu.repository.MediatorRepository;
import com.seu.repository.NormalUserRepository;
import com.seu.repository.UserRepository;
import com.seu.service.UserService;
import com.seu.util.MD5Util;
import com.seu.utils.KeyUtil;
import com.seu.utils.RegisterIMUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private NormalUserRepository normalUserRepository;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MediatorRepository mediatorRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private RegisterIMUtil registerIMUtil;




    @Override
    public ServerResponse<UserForm> login(String phone, String password) {
        User user=userRepository.findByPhone(phone);
        if(user == null){
            return ServerResponse.createByErrorMessage("手机号未注册");
        }
        /** 加密 */
        String md5Password = MD5Util.MD5EncodeUtf8(password);
//        md5Password=password;
        if(!user.getPassword().equals(md5Password)){
            return ServerResponse.createByErrorMessage("登录密码错误");
        }
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
        User user=new User(ID,phone,md5Password,"0",normalId);
        User saveUser=userRepository.save(user);
        if(saveUser==null)
            return -1;
        //2、其次对normal_user_detail表
//        NormalUser saveNormalUser=normalUserDetailService.registerWithNormalUserDetail(user_id);
        NormalUser normalUser=new NormalUser(normalId,ID);
        NormalUser saveNormalUser=normalUserRepository.save(normalUser);
        /** 注册IM */
        registerIMUtil.registerIM(phone,password);
        return (saveNormalUser==null)?-1:1;
    }

    @Override
    @Transactional
    public ResultVO loginout(String ID,String role) throws Exception{
//        Object currentUser=redisTemplate.opsForValue().get(String.format(RedisConstant.USER_RREFIX,role,ID));
        if(redisTemplate.opsForValue().get(String.format(RedisConstant.USER_RREFIX,role,ID))==null){
            throw new NormalUserException(LoginEnum.LOGINSESSION_NULL.getCode(),LoginEnum.LOGINSESSION_NULL.getMsg());
        }else{
            redisTemplate.opsForValue().getOperations().delete(String.format(RedisConstant.USER_RREFIX,role,ID));
            return ResultVOUtil.ReturnBack(LoginEnum.LOGINOUT_SUCCESS.getCode(),LoginEnum.LOGINOUT_SUCCESS.getMsg());
        }
    }


    @Override
    public String findNameById(String ID) {
        User user=userRepository.getOne(ID);
        String name=null;
        if(user.getRole().trim()=="0" || user.getRole().trim().equals("0")){
            NormalUser currentUser=normalUserRepository.getOne(user.getSpecificId());
            name=currentUser.getName();
        }else if(user.getRole().trim()=="1" || user.getRole().trim().equals("1")){
            Mediator currentUser=mediatorRepository.getOne(user.getSpecificId());
            name=currentUser.getMediatorName();
        }else if(user.getRole().trim()=="2" || user.getRole().trim().equals("2")){
            Admin currentUser=adminRepository.getOne(user.getSpecificId());
            name=currentUser.getAdminName();
        }
        return name;

    }

    @Override
    @Transactional
    public ResultVO registerMediator(JSONArray arr) {
        log.info("进入注册调解员");
        for(int i=0;i<arr.size();++i){
            JSONObject map=arr.getJSONObject(i);
            String phone=map.getString("phone");
            User user=userRepository.findByPhone(phone);
            if(user!=null)
                continue;
            String password=map.getString("password");
            String md5Password=MD5Util.MD5EncodeUtf8(password);
            String mediatorName=map.getString("mediatorName");
            String idCard=map.getString("idCard");
            String mediateCenter=map.getString("mediateCenter");
            String authorityConfirm=map.getString("authorityConfirm");
            String authorityJudiciary=map.getString("authorityJudiciary");
            String id=KeyUtil.genUniqueKey();
            String specificId=KeyUtil.genUniqueKey();
            String role="1";
            User registerUser=new User(id,phone,md5Password,role,specificId);
            Mediator registerMediator=new Mediator(specificId,id,mediatorName,idCard,mediateCenter,authorityConfirm,authorityJudiciary);
            userRepository.save(registerUser);
            mediatorRepository.save(registerMediator);
            /** 在IM上进行注册，发送原密码 */
            registerIMUtil.registerIM(phone,password,mediatorName);

        }
        return ResultVOUtil.ReturnBack(RegisterEnum.REGISTERMEDIATOR_SUCCESS.getCode(),RegisterEnum.REGISTERMEDIATOR_SUCCESS.getMsg());





    }
}
