package com.seu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seu.common.RedisConstant;
import com.seu.common.webrtcConstant;
import com.seu.domian.DisputecaseProcess;
import com.seu.service.DisputeProgressService;
import com.seu.service.MediatorService;
import com.seu.service.WebrtcService;
import com.seu.util.MD5Util;
import com.seu.utils.StrIsEmptyUtil;
import com.seu.webrtc.LiveTapeApi;
import com.seu.webrtc.WebRTCSigApi;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName WebrtcController
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/10/11 0011 下午 2:49
 * @Version 1.0
 **/

@RestController
@RequestMapping("/webrtc")
public class WebrtcController {
    @Autowired
    private WebRTCSigApi webRTCSigApi;
    @Autowired
    private WebrtcService webrtcService;
    @Autowired
    private LiveTapeApi liveTapeApi;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DisputeProgressService disputeProgressService;

    @PostMapping("/testRTC")
    public JSONObject testRTC(@RequestParam("roomid") Integer roomid,
                              @RequestParam("userid") String userid,
                              @RequestParam("caseId") String caseId){
        /** 向redis添加key为stream_id的临时变量，value为caseId，时效为6个小时(防止云端视频视频存储过慢) */
        redisTemplate.opsForValue().set(webrtcConstant.bizid+"_"+ MD5Util.MD5EncodeUtf8(roomid+"_"+userid+"_main").toLowerCase(),caseId,RedisConstant.BIG_EXPIRE, TimeUnit.SECONDS);
        return webRTCSigApi.entrance(roomid,userid);
    }


    @PostMapping("/mixStream")
    public JSONObject mixStream(@RequestParam("time") Integer time){
        // 没写暂停方法
        return webrtcService.mixStream(time);
    }

    @PostMapping("/startLiveTape")
    public JSONObject startLiveTape(@RequestParam("roomid") Integer roomid,
                              @RequestParam("userid") String userid){
        return liveTapeApi.startLiveTape(roomid,userid);
    }

    @PostMapping("/endLiveTape")
    public JSONObject endLiveTape(@RequestParam("channel_id") String channel_id,
                                    @RequestParam("task_id") String task_id){
        return liveTapeApi.endLiveTape(channel_id,task_id);
    }

    @PostMapping("/startLiveChannel")
    public JSONObject startLiveChannel(@RequestParam("roomid") Integer roomid,
                                    @RequestParam("userid") String userid){
        return liveTapeApi.startLiveChannel(roomid,userid);
    }

    @PostMapping("/endLiveChannel")
    public JSONObject endLiveChannel(@RequestParam("roomid") Integer roomid,
                                     @RequestParam("userid") String userid){
        return liveTapeApi.endLiveChannel(roomid,userid);
    }

    @PostMapping("/callback")
    public void callback(@RequestBody JSONObject obj){
        String app=obj.getString("app");
        Integer event_type=obj.getInteger("event_type");
        System.out.println(event_type);
        if(event_type==0)
            System.out.println("主动切断了直播流");
        else if(event_type==100) {
            String video_url=obj.getString("video_url");
            System.out.println("新录制文件已生成");
            System.out.println("下载地址为："+video_url);
            /** 将下载地址存入本次纠纷流程的结果中*/
            String caseId=redisTemplate.opsForValue().get(obj.getString("stream_id")).toString();
            if(!StrIsEmptyUtil.isEmpty(caseId))
                disputeProgressService.saveMediateVideo(caseId,video_url);
        }else if(event_type==1){
            System.out.println("直播流开始推流");
            System.out.println(obj.toString());
        }
        else{
            System.out.println(obj.toString());
        }
    }
}
