package com.seu.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
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
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
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

        UserForm userForm = new UserForm();

        if(user.getID()!=null){
            userForm.setId(user.getID());
        }
        if(user.getSpecificId()!=null){
            userForm.setSpecific_id(user.getSpecificId());
        }
        if(user.getRole()!=null){
            userForm.setRole(user.getRole());
            if(user.getRole().equals("1")){
                Mediator mediator = mediatorRepository.findByFatherId(user.getID());
                userForm.setProvince(mediator.getProvince());
                userForm.setCity(mediator.getCity());
                userForm.setMediateCenter(mediator.getMediateCenter());
                userForm.setName(mediator.getMediatorName());
            }
            if(user.getRole().equals("2")){
                Admin admin = adminRepository.findByFatherId(user.getID());
                userForm.setProvince(admin.getProvince());
                userForm.setCity(admin.getCity());
                userForm.setMediateCenter(admin.getMediateCenter());
                userForm.setLevel(admin.getLevel());
                userForm.setName(admin.getAdminName());
            }
            if (user.getRole().equals("0")){
                NormalUser normalUser = normalUserRepository.findByFatherId(user.getID());
                userForm.setName(normalUser.getUserName());
            }

        }
        return ServerResponse.createBySuccess("用户登录成功", userForm);
    }

    @Override
    @Transactional
    public int register(String phone, String password, String name) {
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
        normalUser.setUserName(name);
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
    public ResultVO registerMediator(String province, String city, String mediateCenter, JSONArray arr) {
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
//            String mediateCenter=map.getString("mediateCenter");
//            String province=map.getString("province");
//            String city=map.getString("city");
            String authorityConfirm=map.getString("authorityConfirm");
            String authorityJudiciary=map.getString("authorityJudiciary");
            String id=KeyUtil.genUniqueKey();
            String specificId=KeyUtil.genUniqueKey();
            String role="1";
            User registerUser=new User(id,phone,md5Password,role,specificId);
            Mediator registerMediator=new Mediator(specificId,id,mediatorName,idCard,mediateCenter,authorityConfirm,authorityJudiciary);
            registerMediator.setProvince(province);
            registerMediator.setCity(city);
            userRepository.save(registerUser);
            mediatorRepository.save(registerMediator);
            /** 在IM上进行注册，发送原密码 */
            registerIMUtil.registerIM(phone,password,mediatorName);

        }
        return ResultVOUtil.ReturnBack(RegisterEnum.REGISTERMEDIATOR_SUCCESS.getCode(),RegisterEnum.REGISTERMEDIATOR_SUCCESS.getMsg());





    }

    @Override
    public String getCode(String Phone) {

        String code = String.valueOf((int)((Math.random()*9+1)*100000));
        try{
            //设置超时时间-可自行调整
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
//初始化ascClient需要的几个参数
            final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
            final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
//替换成你的AK
            final String accessKeyId = "LTAIL1KePAlpKKvH";//你的accessKeyId,参考本文档步骤2
            final String accessKeySecret = "0ROlCLO3RFb5gWN38s7giQMySrcsn4";//你的accessKeySecret，参考本文档步骤2
//初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                    accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
            request.setPhoneNumbers(Phone);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName("江苏省医患纠人民调解系统");
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode("SMS_151766151");
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            JSONObject jsonObject=JSONObject.parseObject("{}");
            jsonObject.put("code",code);
            request.setTemplateParam(jsonObject.toString());
            //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者，短信查询等API需要用到这个参数
            //request.setOutId("yourOutId");
//请求失败这里会抛ClientException异常
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            System.out.println(sendSmsResponse.getCode());
        }catch (ClientException ce){
            ce.printStackTrace();
        }
        return code;
    }
}
