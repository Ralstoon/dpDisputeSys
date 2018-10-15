package com.seu.common;

import org.python.antlr.ast.Str;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ClassName webrtcConstant
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/10/11 0011 下午 2:37
 * @Version 1.0
 **/
public class webrtcConstant {


    // TODO 用户签名是有效期( 在生成时设置，一般为三个月 )的，超过期限的用户签名无法使用；
    // TODO 用户使用用户签名过程中，用户签名过期，会出现被踢下线，SDK 会上抛相应的事件通知( 需要用户设置监听 )
    public static String private_key="/root/webrtc/"+"private_key";
    public static String public_key="/root/webrtc/"+"public_key";
    public static final Integer sdkAppid=1400150649;

    /**  视频直播控制台-接入管理-直播码接入-接入配置 */
    public static String appid="1257795492";
    public static String key="f57599935fc45e7b7571de8ea6fc0f33";
    public static String bizid = "33601";
}
