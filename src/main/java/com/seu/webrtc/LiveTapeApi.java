package com.seu.webrtc;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seu.common.webrtcConstant;
import com.seu.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.python.antlr.ast.Str;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @ClassName liveTapeApi
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/10/13 0013 下午 8:21
 * @Version 1.0
 **/
@Component
@Slf4j
public class LiveTapeApi {
    public void isLiveChannel(String stream_id){
        String url="http://fcgi.video.qcloud.com/common_access?appid=%s&interface=Live_Channel_GetStatus&Param.s.channel_id=%s&t=%s&sign=%s";
        try{
            String t= String.valueOf((new Date().getTime())/1000+60);
            String sign= MD5Util.MD5EncodeUtf8(webrtcConstant.key+t);
            sign=sign.toLowerCase();
            url=String.format(url,webrtcConstant.appid,stream_id,t,sign);
            System.out.println(url);
            HttpURLConnection urlConnection=(HttpURLConnection)new URL(url).openConnection();
            urlConnection.connect();
            System.out.println(urlConnection.getResponseCode());
            InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while(br.read() != -1){
                sb.append(br.readLine());
            }
            String content = new String(sb);
            content = new String(content.getBytes("GBK"), "ISO-8859-1");
            System.out.println(content);
            br.close();
            JSONObject res=JSONObject.parseObject("{"+sb.toString());
            System.out.println("直播状态为："+res.toString());
        }catch (IOException ioe){
            log.error("[开始推直播流返回出错]："+ioe.getMessage());
            ioe.printStackTrace();
        }
    }

    public JSONObject startLiveChannel(Integer roomid,String userid){
        System.out.println("开启直播流中......");
//        String stream_id=webrtcConstant.bizid+"_"+userid;
        String stream_id="33601_7668f3e31a";
        JSONObject obj=JSONObject.parseObject("{}");
        Integer ret=-1;
        String url="http://fcgi.video.qcloud.com/common_access?appid=%s&interface=Live_Channel_SetStatus&Param.s.channel_id=%s&Param.n.status=1&t=%s&sign=%s";
        try{
            String t= String.valueOf((new Date().getTime())/1000+60);
            String sign= MD5Util.MD5EncodeUtf8(webrtcConstant.key+t);
            sign=sign.toLowerCase();
            url=String.format(url,webrtcConstant.appid,stream_id,t,sign);
            System.out.println(url);
            HttpURLConnection urlConnection=(HttpURLConnection)new URL(url).openConnection();
            urlConnection.connect();
            System.out.println(urlConnection.getResponseCode());
            InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while(br.read() != -1){
                sb.append(br.readLine());
            }
            String content = new String(sb);
            content = new String(content.getBytes("GBK"), "ISO-8859-1");
            System.out.println(content);
            br.close();
            JSONObject res=JSONObject.parseObject("{"+sb.toString());
            ret=res.getInteger("ret");
            if(ret!=0){
                ret=-1;  // 出错
                log.error(res.getString("message"));
                System.out.println(res.getString("message"));
            }else{
                obj.put("errorCode",0);
                obj.put("res",res.toString());
                System.out.println("开启直播流成功！");
                isLiveChannel(stream_id);
            }
            return obj;
        }catch (IOException ioe){
            log.error("[开始推直播流返回出错]："+ioe.getMessage());
            ioe.printStackTrace();
            return obj;
        }
    }
    public JSONObject endLiveChannel(Integer roomid,String userid){
        System.out.println("关闭直播流中......");
//        String stream_id=webrtcConstant.bizid+"_"+userid;
        String stream_id="33601_7668f3e31a";
        JSONObject obj=JSONObject.parseObject("{}");
        Integer ret=-1;
        String url="http://fcgi.video.qcloud.com/common_access?appid=%s&interface=Live_Channel_SetStatus&Param.s.channel_id=%s&Param.n.status=2&t=%s&sign=%s";
        try{
            String t= String.valueOf((new Date().getTime())/1000+60);
            String sign= MD5Util.MD5EncodeUtf8(webrtcConstant.key+t);
            sign=sign.toLowerCase();
            url=String.format(url,webrtcConstant.appid,stream_id,t,sign);
            System.out.println(url);
            HttpURLConnection urlConnection=(HttpURLConnection)new URL(url).openConnection();
            urlConnection.connect();
            System.out.println(urlConnection.getResponseCode());
            InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while(br.read() != -1){
                sb.append(br.readLine());
            }
            String content = new String(sb);
            content = new String(content.getBytes("GBK"), "ISO-8859-1");
            System.out.println(content);
            br.close();
            JSONObject res=JSONObject.parseObject("{"+sb.toString());
            ret=res.getInteger("ret");
            if(ret!=0){
                ret=-1;  // 出错
                log.error(res.getString("message"));
                System.out.println(res.getString("message"));

            }else{
                obj.put("errorCode",0);
                obj.put("res",res.toString());
                System.out.println("关闭直播流成功！");
            }
            return obj;
        }catch (IOException ioe) {
            log.error("[结束推直播流返回出错]：" + ioe.getMessage());
            ioe.printStackTrace();
            return obj;
        }
    }


    public JSONObject startLiveTape(Integer roomid,String userid){

        JSONObject obj=JSONObject.parseObject("{}");
        String input_stream_id=webrtcConstant.bizid+"_"+MD5Util.MD5EncodeUtf8(roomid+"_"+userid+"_main").toLowerCase();
        String url="http://fcgi.video.qcloud.com/common_access?appid=%s&interface=Live_Tape_Start&Param.s.channel_id=%s&Param.s.start_time=%s&Param.s.end_time=%s&Param.n.task_sub_type=%s&t=%s&sign=%s&Param.s.file_format=%s";
        String startTime,endTime;
        try {
            Date currentTime=new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(currentTime);
            calendar.add(Calendar.SECOND,3);
            startTime=sdf.format(calendar.getTime()); // 当前之间的三秒后
            startTime= URLEncoder.encode(startTime,"utf-8");
            calendar.setTime(calendar.getTime());
            calendar.add(Calendar.SECOND,30); // startTime的30秒后
//            calendar.add(Calendar.HOUR_OF_DAY,2);  // 默认加两个小时
            endTime=sdf.format(calendar.getTime());
            endTime=URLEncoder.encode(endTime,"utf-8");

            String task_sub_type=String.valueOf(0); // 定时录制模式
            String file_format="mp4";
            String t= String.valueOf((new Date().getTime())/1000+60);
            String sign= MD5Util.MD5EncodeUtf8(webrtcConstant.key+t);
            sign=sign.toLowerCase();
            url=String.format(url,webrtcConstant.appid,input_stream_id,startTime,endTime,task_sub_type,t,sign,file_format);
            System.out.println(url);
            HttpURLConnection urlConnection=(HttpURLConnection)new URL(url).openConnection();
            urlConnection.connect();
            System.out.println(urlConnection.getResponseCode());
            InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while(br.read() != -1){
                sb.append(br.readLine());
            }
            String content = new String(sb);
            content = new String(content.getBytes("GBK"), "ISO-8859-1");
            System.out.println(content);
            br.close();
            JSONObject res=JSONObject.parseObject("{"+sb.toString());
            String taskId=res.getJSONObject("output").getString("task_id");
            if(res.getInteger("ret")==0)
                obj.put("errorCode",0);
            obj.put("channel_id",input_stream_id);
            obj.put("task_id",taskId);


        }catch (UnsupportedEncodingException uee){
            log.error("[设置开始录制时时间URL编码出错]："+uee.getMessage());
            uee.printStackTrace();
        }catch (MalformedURLException mfue){
            log.error("[对开始录制的URL包装出错]："+mfue.getMessage());
            mfue.printStackTrace();
        }catch (IOException ioe){
            log.error("[接收开始录制的URL返回出错]："+ioe.getMessage());
            ioe.printStackTrace();
        }
        return obj;
    }


    public JSONObject endLiveTape(String channel_id,String task_id){
        JSONObject obj=JSONObject.parseObject("{}");
        String url= "http://fcgi.video.qcloud.com/common_access?appid=%s&interface=Live_Tape_Stop&Param.s.channel_id=%s&Param.n.task_id=%s&t=%s&sign=%s";
        try {
            String t= String.valueOf((new Date().getTime())/1000+60);
            String sign= MD5Util.MD5EncodeUtf8(webrtcConstant.key+t);
            sign=sign.toLowerCase();
            url=String.format(url,webrtcConstant.appid,channel_id,task_id,t,sign);
            System.out.println(url);
            HttpURLConnection urlConnection=(HttpURLConnection)new URL(url).openConnection();
            urlConnection.connect();
            System.out.println(urlConnection.getResponseCode());
            InputStream is = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            while(br.read() != -1){
                sb.append(br.readLine());
            }
            String content = new String(sb);
            content = new String(content.getBytes("GBK"), "ISO-8859-1");
            System.out.println(content);
            br.close();

            JSONObject res=JSONObject.parseObject("{"+sb.toString());
            if(res.getInteger("ret")==0){
                obj.put("errorCode",0);
                System.out.println("结束视频录制成功！");
            }

        }catch (UnsupportedEncodingException uee){
            log.error("[设置结束录制时时间URL编码出错]："+uee.getMessage());
            uee.printStackTrace();
        }catch (MalformedURLException mfue){
            log.error("[对结束录制的URL包装出错]："+mfue.getMessage());
            mfue.printStackTrace();
        }catch (IOException ioe){
            log.error("[接收结束录制的URL返回出错]："+ioe.getMessage());
            ioe.printStackTrace();
        }
        return obj;
    }

    public static void main(String[] args){
        System.out.println(MD5Util.MD5EncodeUtf8("113355_video_11376186_main").toLowerCase());
    }


}
