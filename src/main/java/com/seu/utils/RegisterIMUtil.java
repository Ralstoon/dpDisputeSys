package com.seu.utils;

/**
 * @ClassName RegisterIMUtil
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/10/9 0009 下午 9:54
 * @Version 1.0
 **/
import com.seu.controller.CheckSumBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.python.antlr.ast.Str;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class RegisterIMUtil {
    public void registerIM(String userId, String pwd, String name){
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String url = "https://api.netease.im/nimserver/user/create.action";
            HttpPost httpPost = new HttpPost(url);

            String appKey = "33bfe9c6b02262832e41cfbbeda0b86a";
            String appSecret = "9080641069fc";
            String nonce =  "12345";
            String curTime = String.valueOf((new Date()).getTime() / 1000L);
            String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码

            // 设置请求的header
            httpPost.addHeader("AppKey", appKey);
            httpPost.addHeader("Nonce", nonce);
            httpPost.addHeader("CurTime", curTime);
            httpPost.addHeader("CheckSum", checkSum);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("accid", userId));
            nvps.add(new BasicNameValuePair("name", name));
            nvps.add(new BasicNameValuePair("token", pwd));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);
            // 打印执行结果
            System.out.println("[自动注册IM开始]");
            System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
            System.out.println("[自动注册IM结束]");
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void registerIM(String userId,String pwd){
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String url = "https://api.netease.im/nimserver/user/create.action";
            HttpPost httpPost = new HttpPost(url);

            String appKey = "33bfe9c6b02262832e41cfbbeda0b86a";
            String appSecret = "9080641069fc";
            String nonce =  "12345";
            String curTime = String.valueOf((new Date()).getTime() / 1000L);
            String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码

            // 设置请求的header
            httpPost.addHeader("AppKey", appKey);
            httpPost.addHeader("Nonce", nonce);
            httpPost.addHeader("CurTime", curTime);
            httpPost.addHeader("CheckSum", checkSum);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

            // 设置请求的参数
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("accid", userId));
//        nvps.add(new BasicNameValuePair("name", "测试三号"));
            nvps.add(new BasicNameValuePair("token", pwd));
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            // 执行请求
            HttpResponse response = httpClient.execute(httpPost);
            // 打印执行结果
            System.out.println("[自动注册IM开始]");
            System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
            System.out.println("[自动注册IM结束]");
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
}
