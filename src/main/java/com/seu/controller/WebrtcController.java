package com.seu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seu.webrtc.WebRTCSigApi;
import org.springframework.beans.factory.annotation.Autowired;
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

//    @PostMapping("/testRTC")
//    public JSONObject testRTC(@RequestBody JSONObject obj){
//        Integer roomid=obj.getInteger("roomid");
//        String userid=obj.getString("userid");
//        return webRTCSigApi.entrance(roomid,userid);
//    }

    @PostMapping("/testRTC")
    public JSONObject testRTC(@RequestParam("roomid") Integer roomid,
                              @RequestParam("userid") String userid){
//        Integer roomid=obj.getInteger("roomid");
//        String userid=obj.getString("userid");
        return webRTCSigApi.entrance(roomid,userid);
    }
}
