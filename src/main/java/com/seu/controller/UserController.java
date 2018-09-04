package com.seu.controller;

import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.RedisConstant;
import com.seu.common.ServerResponse;
import com.seu.enums.RegisterEnum;
import com.seu.enums.UpdateInfoEnum;
import com.seu.form.LoginForm;
import com.seu.form.NormalUserForm;
import com.seu.form.VOForm.UserForm;
import com.seu.service.DisputeProgressService;
import com.seu.service.DisputeRegisterService;
import com.seu.service.UserService;
import com.seu.service.NormalUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin
@RequestMapping("/user/")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private NormalUserService normalUserService;
    @Autowired
    private RedisTemplate redisTemplate;

    /*
     *@Author 吴宇航
     *@Description  处理所有用户统一登录的方法
     *@Date 21:10 2018/8/3
     *@Param [phone, password, httpServletResponse]
     *@return com.seu.common.ServerResponse<com.seu.domian.NormalUser>
     **/
    @RequestMapping(value = "login",method = RequestMethod.POST)
    public ServerResponse<UserForm> login(@RequestBody JSONObject map) {
        String phone=map.getString("phone");
        String password=map.getString("password");
//        System.out.println(password+"  "+phone);
        //TODO 没有处理用户反复登陆以及换账户登录,希望该功能由前端检查，若反复登陆和切换账户，则可以推荐用户先注销
//        session.getAttribute("NormalUser");
        ServerResponse<UserForm> response = userService.login(phone, password);

        if (response.isSuccess()) {
//            NormalUser currentUser=(NormalUser)response.getData();
//            session.setAttribute(Const.CURRENT_USER, response.getData());
            UserForm userForm=response.getData();
            // 先放入redis服务器，设置key为token_
            String ID=userForm.getId();
            String role=userForm.getRole();
            Integer expire=RedisConstant.EXPIRE;
//            redisTemplate.opsForValue().set(String.format(RedisConstant.USER_RREFIX,role,ID),userForm,expire,TimeUnit.SECONDS);
        }
        return response;
    }

    /*
     *@Author 吴宇航
     *@Description  注册似乎一定是普通用户
     *@Date 21:57 2018/8/3
     *@Param [phone, password]
     *@return com.seu.ViewObject.ResultVO
     **/
    @RequestMapping(value = "register",method = RequestMethod.POST)
    public ResultVO register(@RequestBody Map<String,String> map){
        String phone=map.get("phone");
        String password=map.get("password");
//        log.info("【注册phone为：】{}",phone);
        int resultNum=userService.register(phone,password);
        if(resultNum==1){
            return ResultVOUtil.ReturnBack(RegisterEnum.REGISTER_SUCCESS.getCode(),RegisterEnum.REGISTER_SUCCESS.getMsg());
        }else {
            return ResultVOUtil.ReturnBack(RegisterEnum.REGISTER_FAIL.getCode(),RegisterEnum.REGISTER_FAIL.getMsg());
        }
    }

    /*
     *@Author 吴宇航
     *@Description  普通用户个人信息修改
     *@Date 13:30 2018/8/4
     *@Param [normalUserDetailForm, bindingResult]
     *@return com.seu.ViewObject.ResultVO
     **/
    // TODO
    @RequestMapping(value = "updateInfo",method = RequestMethod.POST)
    public ResultVO updateUserInfo(@RequestBody Map<String,Object> map){
//        if(bindingResult.hasErrors()){
//            Map<String,Object> param=new HashMap<>();
//            param.put("data",bindingResult.getFieldError().getDefaultMessage());
//            return ResultVOUtil.ReturnBack(param,UpdateInfoEnum.UPDATE_FAIL.getCode(),UpdateInfoEnum.UPDATE_FAIL.getMsg());
//        }
//        NormalUser currentUser=(NormalUser)session.getAttribute("currentUser");
        NormalUserForm normalUserForm1=new NormalUserForm();
        NormalUserForm result= normalUserService.updateNormalUser(normalUserForm1);
        if(result!=null)
            return ResultVOUtil.ReturnBack(result,UpdateInfoEnum.UPDATE_SUCCESS.getCode(),UpdateInfoEnum.UPDATE_SUCCESS.getMsg());
        else
            return ResultVOUtil.ReturnBack(UpdateInfoEnum.UPDATE_FAIL.getCode(),UpdateInfoEnum.UPDATE_FAIL.getMsg());
    }


    @RequestMapping(value = "loginout",method = RequestMethod.GET)
    public ResultVO loginout(@RequestBody Map<String,String> map) throws Exception {
        String ID=map.get("ID");
        String role=map.get("role");
        return userService.loginout(ID,role);
    }

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//    @RequestMapping(value="test",method = RequestMethod.GET)
//    public Set<String> test(){
//        String pattern="123321";
//        Set<String> result=stringRedisTemplate.keys("*"+pattern);
//        return result;
//    }

    @Autowired
    DisputeProgressService disputeProgressService;

    @PostMapping(value = "getUserCaseList")
    public ResultVO getUserCaseList(@RequestBody Map<String, String> map){

        String usrId = map.get("userId");
        return disputeProgressService.getUserCaseList(usrId);
    }
}
