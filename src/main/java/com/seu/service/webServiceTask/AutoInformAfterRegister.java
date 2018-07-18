package com.seu.service.webServiceTask;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class AutoInformAfterRegister implements JavaDelegate {
    @Override
    public void execute(DelegateExecution delegateExecution) {
        //TODO 这里要实现用户登记后的自动通知，主要有页面直接发送，邮件发送和短信发送(通知时间地点以及携带材料)
    }
}
