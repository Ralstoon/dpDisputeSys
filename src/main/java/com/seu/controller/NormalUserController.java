package com.seu.controller;

import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.Const;
import com.seu.common.RedisConstant;
import com.seu.common.ServerResponse;
import com.seu.domian.NormalUser;
import com.seu.domian.NormalUserDetail;
import com.seu.enums.LoginEnum;
import com.seu.enums.RegisterEnum;
import com.seu.enums.UpdateInfoEnum;
import com.seu.form.NormalUserDetailForm;
import com.seu.service.INormalUserService;
import com.seu.service.NormalUserDetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin
@RequestMapping("/user/")
@Slf4j
public class NormalUserController {

    @Autowired
    private INormalUserService iNormalUserService;
    @Autowired
    private NormalUserDetailService normalUserDetailService;
    @Autowired
    private RedisTemplate redisTemplate;


    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ServerResponse<NormalUser> login(String phone, String password, HttpServletResponse httpServletResponse) {
        //TODO 没有处理用户反复登陆以及换账户登录的问题
//        session.getAttribute("NormalUser");
        ServerResponse<NormalUser> response = iNormalUserService.login(phone, password);

        if (response.isSuccess()) {
            NormalUser currentUser=(NormalUser)response.getData();
//            session.setAttribute(Const.CURRENT_USER, response.getData());
            // 先放入redis服务器，设置key为token_
            String userId=currentUser.getUserId();
            Integer expire=RedisConstant.EXPIRE;
            redisTemplate.opsForValue().set(String.format(RedisConstant.USER_RREFIX,userId),currentUser,expire,TimeUnit.SECONDS);
            // TODO 暂且未设置cookie，问询前端需要后决定
//            Cookie cookie=new Cookie("currentUser",token);
        }
        return response;
    }

    @RequestMapping(value = "register",method = RequestMethod.POST)
    public ResultVO register(@RequestParam(value = "phone") String phone,
                             @RequestParam(value = "password") String password){
        log.info("【phone为：】{}",phone);
        int resultNum=iNormalUserService.register(phone,password);
        if(resultNum==1){
            return ResultVOUtil.ReturnBack(RegisterEnum.REGISTER_SUCCESS.getCode(),RegisterEnum.REGISTER_SUCCESS.getMsg());
        }else {
            return ResultVOUtil.ReturnBack(RegisterEnum.REGISTER_FAIL.getCode(),RegisterEnum.REGISTER_FAIL.getMsg());
        }
    }

    @RequestMapping(value = "updateInfo",method = RequestMethod.POST)
    public ResultVO updateUserInfo(@Valid NormalUserDetailForm normalUserDetailForm,
                                   BindingResult bindingResult,
                                   HttpSession session){
        if(bindingResult.hasErrors()){
            // bindingResult.getFieldError().getDefaultMessage()表示返回验证失败的地方，比如XX必填
            return ResultVOUtil.ReturnBack(UpdateInfoEnum.UPDATE_FAIL.getCode(),UpdateInfoEnum.UPDATE_FAIL.getMsg());
        }
        NormalUser currentUser=(NormalUser)session.getAttribute("currentUser");
        NormalUserDetailForm result=normalUserDetailService.updateNormalUserDetail(normalUserDetailForm,currentUser.getUserId());
        if(result!=null)
            return ResultVOUtil.ReturnBack(result,UpdateInfoEnum.UPDATE_SUCCESS.getCode(),UpdateInfoEnum.UPDATE_SUCCESS.getMsg());
        else
            return ResultVOUtil.ReturnBack(UpdateInfoEnum.UPDATE_FAIL.getCode(),UpdateInfoEnum.UPDATE_FAIL.getMsg());
    }


    @RequestMapping(value = "loginout",method = RequestMethod.GET)
    public ResultVO loginout(@RequestParam("userId") String userId) throws Exception {

        return iNormalUserService.loginout(userId);
    }
}
