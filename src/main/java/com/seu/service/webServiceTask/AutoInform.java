package com.seu.service.webServiceTask;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.seu.repository.NormalUserDetailRepository;
import com.seu.service.NormalUserDetailService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

public class AutoInform implements JavaDelegate {


    @Override
    public void execute(DelegateExecution delegateExecution) {
        //先从流程中取出变量userId和disputeId
        String email=delegateExecution.getVariable("email").toString();
        String disputeId=delegateExecution.getVariable("disputeId").toString();
        //验证是否Email是否已填写,填写的话发送邮件
        if(!(email.equals("")||email==null)){
            sendEmail(disputeId,email);
        }else
            System.out.println("邮件未发送因为用户未提供邮箱地址");
        // TODO 短信发送

    }


    public void sendEmail(String disputeId,String Email) {
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAIL1KePAlpKKvH", "0ROlCLO3RFb5gWN38s7giQMySrcsn4");
        IAcsClient client = new DefaultAcsClient(profile);
        SingleSendMailRequest request = new SingleSendMailRequest();
        try {

            request.setAccountName("disputeweb@wangj1106.top");
            request.setFromAlias("医患纠纷平台");
            request.setAddressType(1);
            request.setTagName("医患纠纷自动通知标签");
            request.setReplyToAddress(true);
            request.setToAddress(Email);
            request.setSubject("纠纷id为:"+disputeId+" 的案件已成功暂存");
            request.setHtmlBody("请尽快携带身份证及相关病历至最近的调解中心进行正式确认。");
            SingleSendMailResponse httpResponse = client.getAcsResponse(request);
        } catch (ServerException e) {
            e.printStackTrace();
        }
        catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
