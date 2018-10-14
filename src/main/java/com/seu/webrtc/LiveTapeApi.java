package com.seu.webrtc;

import com.alibaba.fastjson.JSONObject;
import com.seu.common.webrtcConstant;
import com.seu.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
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
    public JSONObject startLiveTape(Integer roomid,String userid){
        JSONObject obj=JSONObject.parseObject("{}");
        String input_stream_id= webrtcConstant.bizid+"_"+ MD5Util.MD5EncodeUtf8(String.valueOf(roomid)+"_"+userid+"_main");
        String url="http://fcgi.video.qcloud.com/common_access?appid=%s&interface=Live_Tape_Start&Param.s.channel_id=%s&Param.s.start_time=%s&Param.s.end_time=%s&Param.n.task_sub_type=%s&t=%s&sign=%s&Param.s.file_format=%s";
        String startTime,endTime;
        try {
            Date currentTime=new Date();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(currentTime);
            startTime=sdf.format(currentTime);
            startTime= URLEncoder.encode(startTime,"utf-8");
            calendar.add(Calendar.SECOND,5);
//            calendar.add(Calendar.HOUR_OF_DAY,2);  // 默认加两个小时
            endTime=sdf.format(calendar.getTime());
            endTime=URLEncoder.encode(endTime,"utf-8");

            String task_sub_type=String.valueOf(0); // 定时录制模式
            String file_format="mp4";
            String t= String.valueOf((new Date().getTime())/1000+60);
            String sign= MD5Util.MD5EncodeUtf8(webrtcConstant.key+t);
            url=String.format(url,webrtcConstant.appid,input_stream_id,startTime,endTime,task_sub_type,t,sign,file_format);

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

//            java.net.URL posturl=new java.net.URL(URL);
//            BufferedReader in=null;
//            StringBuffer sb=new StringBuffer();
//            in=new BufferedReader(new InputStreamReader(posturl.openStream(),"utf-8"));
//            String str=null;
//            while ((str=in.readLine())!=null)
//                sb.append(str);
            JSONObject res=JSONObject.parseObject(sb.toString());
            if(res.getInteger("ret")==0)
                obj.put("errorCode",0);
        }catch (UnsupportedEncodingException uee){
            log.error("[设置开始录制时时间URL编码出错]："+uee.getMessage());
        }catch (MalformedURLException mfue){
            log.error("[对开始录制的URL包装出错]："+mfue.getMessage());
        }catch (IOException ioe){
            log.error("[接收开始录制的URL返回出错]："+ioe.getMessage());
        }
        return obj;
    }
}
