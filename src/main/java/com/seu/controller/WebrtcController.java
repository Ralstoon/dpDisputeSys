package com.seu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seu.service.WebrtcService;
import com.seu.webrtc.LiveTapeApi;
import com.seu.webrtc.WebRTCSigApi;
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

    @PostMapping("/callback")
    public void callback(@RequestBody JSONObject obj, HttpRequest request){
        System.out.println(request.getURI().toString());
        System.out.println(obj.toString());
    }
}
