package com.seu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seu.service.WebrtcService;
import com.seu.webrtc.LiveTapeApi;
import com.seu.webrtc.WebRTCSigApi;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/testRTC")
    public JSONObject testRTC(@RequestParam("roomid") Integer roomid,
                              @RequestParam("userid") String userid){
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
//        System.out.println(request.getURI().toString());
        String app=obj.getString("app");
        Integer event_type=obj.getInteger("event_type");
        System.out.println(event_type);
        if(event_type==0)
            System.out.println("主动切断了直播流");
        else if(event_type==100) {
            System.out.println("新录制文件已生成");
            System.out.println("下载地址为："+obj.getString("video_url"));
        }else if(event_type==1){
            System.out.println("直播流开始推流");
            System.out.println(obj.toString());
        }
        else{
            System.out.println(obj.toString());
        }
    }
}
