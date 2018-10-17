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
//    private String
//
//    public JSONObject applyMixStream(String mediatorStream,String[] others){
//        String t= String.valueOf((new Date().getTime())/1000+60);
//        String sign= MD5Util.MD5EncodeUtf8(webrtcConstant.key+t).toLowerCase();
//        String postUrl=String.format("http://fcgi.video.qcloud.com/common_access?appid=%s&interface=Mix_StreamV2&t=%s&sign=%s",webrtcConstant.appid,sign,t,sign);
//        JSONObject content=JSONObject.parseObject("{}");
//        int event_id= new Random().nextInt();
//        content.put("timestamp",((int)(new Date().getTime()))/1000);
//        content.put("eventId",event_id);
//        JSONObject inter=JSONObject.parseObject("{}");
//        inter.put("interfaceName","Mix_StreamV2");
//        JSONObject para=JSONObject.parseObject("{}");
//        para.put("app_id",webrtcConstant.appid);
//        para.put("interface", "mix_streamv2.start_mix_stream_advanced");
//        para.put("mix_stream_session_id" ,"disputeweb_room");
//        para.put("output_stream_id",mediatorStream);
//        para.put("output_stream_type",0);
//        // input_stream_list
//
//
//
//    }
//}
