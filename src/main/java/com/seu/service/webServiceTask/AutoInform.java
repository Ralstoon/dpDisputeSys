package com.seu.service.webServiceTask;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dm.model.v20151123.SingleSendMailRequest;
import com.aliyuncs.dm.model.v20151123.SingleSendMailResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.seu.common.Component.SpringUtil;
import com.seu.domian.Disputecase;
import com.seu.domian.DisputecaseApply;
import com.seu.domian.NormalUser;
import com.seu.domian.User;
import com.seu.repository.*;
import com.seu.service.CommentService;
import com.seu.service.DisputeRegisterService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


public class AutoInform implements JavaDelegate {


    // TODO 向用户，包括申请人和代理人发送告知书和代理人委托书，并告知于立案判断前登录系统并上传（未注册账号默认密码为111111）或在立案判断时亲自携带过来
    @Override
    public void execute(DelegateExecution delegateExecution) {
        DisputecaseRepository disputecaseRepository=SpringUtil.getBean(DisputecaseRepository.class);
        DisputecaseApplyRepository disputecaseApplyRepository=SpringUtil.getBean(DisputecaseApplyRepository.class);
        NormalUserRepository normalUserRepository=SpringUtil.getBean(NormalUserRepository.class);
        UserRepository userRepository=SpringUtil.getBean(UserRepository.class);

        String caseId=delegateExecution.getVariable("disputeId").toString();
        Disputecase disputecase=disputecaseRepository.getOne(caseId);
        String prosperId=disputecase.getProposerId();
        String[] temp=prosperId.trim().split(",");
        for(String s:temp){
            DisputecaseApply disputecaseApply=disputecaseApplyRepository.findAllById(s).get(0);
            String phone=disputecaseApply.getPhone();
            String name=disputecaseApply.getName();
            String specificId=userRepository.findByPhone(phone).getSpecificId();
            String email=normalUserRepository.getOne(specificId).getEmail();
            if(!(email==null ||email==""))
                sendEmail(caseId,email);
            sendSms(caseId,phone,name);
        }


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

    public void sendSms(String disputeId,String Phone,String name) {
        try {
            //设置超时时间-可自行调整
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
//初始化ascClient需要的几个参数
            final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
            final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
//替换成你的AK
            final String accessKeyId = "LTAIL1KePAlpKKvH";//你的accessKeyId,参考本文档步骤2
            final String accessKeySecret = "0ROlCLO3RFb5gWN38s7giQMySrcsn4";//你的accessKeySecret，参考本文档步骤2
//初始化ascClient,暂时不支持多region（请勿修改）
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                    accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
            //组装请求对象
            SendSmsRequest request = new SendSmsRequest();
            //使用post提交
            request.setMethod(MethodType.POST);
            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
            request.setPhoneNumbers(Phone);
            //必填:短信签名-可在短信控制台中找到
            request.setSignName("王节");
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode("SMS_140110193");
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            JSONObject temp=JSONObject.parseObject("{}");
            temp.put("name",name);
            request.setTemplateParam(temp.toString());
            //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者，短信查询等API需要用到这个参数
            //request.setOutId("yourOutId");
//请求失败这里会抛ClientException异常
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
            System.out.println(sendSmsResponse.getCode());
        }catch (ClientException e){
            e.printStackTrace();
        }
    }

    //
}
