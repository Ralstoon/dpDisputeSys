package com.seu.controller;

import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.Const;
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
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/user/")
@Slf4j
public class NormalUserController {

    @Autowired
    private INormalUserService iNormalUserService;
    @Autowired
    private NormalUserDetailService normalUserDetailService;

    @RequestMapping(value = "login", method = RequestMethod.GET)
    public ServerResponse<NormalUser> login(String phone, String password, HttpSession session) {
        //TODO 没有处理用户反复登陆以及换账户登录的问题
        session.getAttribute("NormalUser");
        ServerResponse<NormalUser> response = iNormalUserService.login(phone, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
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
    public ResultVO loginout(HttpSession session) throws Exception {
        return iNormalUserService.loginout(session);
    }
}
