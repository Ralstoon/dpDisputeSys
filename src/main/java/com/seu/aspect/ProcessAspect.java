package com.seu.aspect;

import com.seu.ViewObject.ResultVO;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.proxy.UndeclaredThrowableException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ProcessAspect
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/7 20:51
 * @Version 1.0
 **/

@Aspect
@Component
public class ProcessAspect {

//    @Pointcut("execution(* com.seu.controller.TestInstances.*(..))"+
//               "&& args(..)")
//    public void performance(Object[] jp){
//    }

//    @Before("performance(jp)")
//    @Before("execution(* com.seu.controller.TestInstances.*(..))"+
//            "&& args(..)")
//    public void beforeProcess(JoinPoint jp){
//
//        System.out.println("this is before advice");
//        String args="[";
//        for(Object obj:jp.getArgs()){
//            args+=obj.toString()+",";
//        }
//        args+="]";
//        System.out.println("caseId:"+args);
//
//    }
//
//    @Around("execution(* com.seu.controller.TestInstances.run1(..))"+
//            "|| execution(* com.seu.controller.TestInstances.run2(..))")
//    public void verifyProcess(ProceedingJoinPoint jp){
//        // 前置通知
//        System.out.println("this is before advice");
//        try {
//            jp.proceed();
//        }catch (Throwable e){
//            e.printStackTrace();
//        }
//        //后置通知
//        System.out.println("this is after advice");
//    }
}
