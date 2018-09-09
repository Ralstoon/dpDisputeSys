//package com.seu.aspect;
//
//import com.seu.ViewObject.ResultVO;
//import org.activiti.engine.TaskService;
//import org.activiti.engine.task.Task;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * @ClassName ProcessAspect
// * @Description TODO
// * @Author 吴宇航
// * @Date 2018/9/7 20:51
// * @Version 1.0
// **/
//
//@Aspect
//public class ProcessAspect {
//    @Autowired
//    private TaskService taskService;
//
//
//
//    @Pointcut("execution(* com.seu.service.impl.DisputecaseAccessoryServiceImpl.addInquireHospital(String))"+
//               "&& args(caseId)")
//    public void performance(String caseId){
//    }
//
//    @Around("performance(caseId)")
//    public ResultVO verifyProcess(String caseId){
//        // 前置通知
//        List<Task> tasks=new ArrayList<>();
//    }
//}
