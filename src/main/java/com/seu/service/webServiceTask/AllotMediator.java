package com.seu.service.webServiceTask;

import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 * @ClassName AllotMediator
 * @Description 实现流程图中调解员分配自动任务的功能
 * @Author 吴宇航
 * @Date 2018/7/20 15:57
 * @Version 1.0
 **/
@Slf4j
public class AllotMediator implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        // TODO 调解员分配自动任务，
        log.info("【....开始进行调解员分配流程....】");

    }




}
