//package com.seu.webrtc;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.seu.common.webrtcConstant;
//import com.seu.util.MD5Util;
//import org.python.antlr.ast.Str;
//import org.springframework.stereotype.Component;
//
//import java.util.Date;
//import java.util.Random;
//
///**
// * @ClassName mixStreamApi
// * @Description TODO
// * @Author 吴宇航
// * @Date 2018/10/13 0013 上午 10:43
// * @Version 1.0
// **/
//@Component
//public class mixStreamApi {
//    public JSONObject applyMixStream(String s){
//        String[] srr=s.split(",");
//        String t= String.valueOf((new Date().getTime())/1000+60);
//        String sign= MD5Util.MD5EncodeUtf8(webrtcConstant.key+t);
//        int event_id= new Random().nextInt();
//        String postUrl=String.format("http://fcgi.video.qcloud.com/common_access?appid=%s&interface=Mix_StreamV2&sign=%s",webrtcConstant.appid,sign);
//        JSONObject content=JSONObject.parseObject("{}");
//        content.put("timestamp",((int)(new Date().getTime()))/1000);
//        content.put("eventId",event_id);
//        JSONObject interface=JSONObject.parseObject("{}");
//
//    }
//}
