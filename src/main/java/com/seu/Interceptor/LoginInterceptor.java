package com.seu.Interceptor;

import com.seu.domian.NormalUser;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @ClassName LoginInterceptor
 * @Description 该类用于拦截所有需要登录才可进行操作的功能，验证是否已登陆
 * @Author 吴宇航
 * @Date 2018/7/20 20:05
 * @Version 1.0
 **/
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        System.out.println("拦截器进行验证：");
//        HttpSession session=httpServletRequest.getSession();
//
//        if(session.getAttribute("ID")==null){
//            System.out.println("该用户尚未登录");
//        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
