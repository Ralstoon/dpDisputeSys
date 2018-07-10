package com.seu.controller;

import com.seu.common.Const;
import com.seu.common.ServerResponse;
import com.seu.domian.NormalUser;
import com.seu.service.INormalUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
@CrossOrigin
@RequestMapping("/user/")
public class NormalUserController {
    @Autowired
    private INormalUserService iNormalUserService;

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<NormalUser> login(String phone, String password, HttpSession session) {
        ServerResponse<NormalUser> response = iNormalUserService.login(phone, password);
        if (response.isSuccess()) {
            session.setAttribute(Const.CURRENT_USER, response.getData());
        }
        return response;
    }


}
