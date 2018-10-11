package com.seu.common;

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
    public static String private_key="D:\\workspace_idea\\dpDisputeSys\\src\\main\\resources\\webrtcKeys\\private_key";
    public static String public_key="D:\\workspace_idea\\dpDisputeSys\\src\\main\\resources\\webrtcKeys\\public_key";
    public static final Integer sdkAppid=1400150649;
}
