package com.seu.webrtc;

import com.alibaba.fastjson.JSONObject;
import com.seu.common.webrtcConstant;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.zip.Deflater;


@Component
public class WebRTCSigApi {
    private int mSdkAppid = 0;
    private PrivateKey mPrivateKey = null;
    private PublicKey mPublicKey = null;
    

    public void setSdkAppid(int sdkappid) {
        this.mSdkAppid = sdkappid;
    }

    public void setPrivateKey(String privateKey) {
        String privateKeyPEM = privateKey.replace("-----BEGIN PRIVATE KEY-----", "")
        .replace("-----END PRIVATE KEY-----", "")
        .replace("\n", "");
        System.out.println(privateKeyPEM);
        byte[] encodedKey = Base64.getDecoder().decode(privateKeyPEM.getBytes());
        
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            this.mPrivateKey = keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public void setPublicKey(String publicKey) {
        String publicKeyPEM = publicKey.replace("-----BEGIN PUBLIC KEY-----", "")
        .replace("-----END PUBLIC KEY-----", "")
        .replace("\n", "");
        byte[] encodedKey = Base64.getDecoder().decode(publicKeyPEM.getBytes());
        
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            this.mPublicKey = keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    public byte[] sign(byte[] data) {
        try {
            Signature signer = Signature.getInstance("SHA256withECDSA");
            signer.initSign(this.mPrivateKey);
            signer.update(data);
            return signer.sign();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean verify(byte[] data, byte[] sig) {
        try {
            Signature signer = Signature.getInstance("SHA256withECDSA");
            signer.initVerify(this.mPublicKey);
            signer.update(data);
            return signer.verify(sig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private byte[] base64UrlEncode(byte[] data) {
        byte[] encode = Base64.getEncoder().encode(data);
        for (int i = 0; i < encode.length; i++) {
            if (encode[i] == '+') {
                encode[i] = '*';
            } else if (encode[i] == '/') {
                encode[i] = '-';
            } else if (encode[i] == '=') {
                encode[i] = '_';
            }
        }
        return encode;
    }
    

    private byte[] base64UrlDecode(byte[] data) {
        byte[] encode = Arrays.copyOf(data, data.length);
        for (int i = 0; i < encode.length; i++) {
            if (encode[i] == '*') {
                encode[i] = '+';
            } else if (encode[i] == '-') {
                encode[i] = '/';
            } else if (encode[i] == '_') {
                encode[i] = '=';
            }
        }
        return encode;
    }
    

    public String genUserSig(String userid, int expire) {
        String time = String.valueOf(System.currentTimeMillis()/1000);
        String serialString =
        "TLS.appid_at_3rd:" + 0 + "\n" +
        "TLS.account_type:" + 0 + "\n" +
        "TLS.identifier:" + userid + "\n" +
        "TLS.sdk_appid:" + this.mSdkAppid + "\n" +
        "TLS.time:" + time + "\n" +
        "TLS.expire_after:" + expire +"\n";
        
        byte[] signBytes = sign(serialString.getBytes(Charset.forName("UTF-8")));
        String sig = Base64.getEncoder().encodeToString(signBytes);
        
        String jsonString = "{"
        + "\"TLS.account_type\":\"" + 0 +"\","
        +"\"TLS.identifier\":\"" + userid +"\","
        +"\"TLS.appid_at_3rd\":\"" + 0 +"\","
        +"\"TLS.sdk_appid\":\"" + this.mSdkAppid +"\","
        +"\"TLS.expire_after\":\"" + expire +"\","
        +"\"TLS.sig\":\"" + sig +"\","
        +"\"TLS.time\":\"" + time +"\","
        +"\"TLS.version\": \"201512300000\""
        +"}";
        
        //compression
        Deflater compresser = new Deflater();
        compresser.setInput(jsonString.getBytes(Charset.forName("UTF-8")));
        
        compresser.finish();
        byte [] compressBytes = new byte [512];
        int compressBytesLength = compresser.deflate(compressBytes);
        compresser.end();
        String userSig = new String(base64UrlEncode(Arrays.copyOfRange(compressBytes, 0, compressBytesLength)));
        
        return userSig;
    }
    

    public String genPrivateMapKey(String userid, int roomid, int expire) {
        String time = String.valueOf(System.currentTimeMillis()/1000);
        

        int accountLength = userid.length();
        int offset = 0;
        byte[] bytes = new byte[1+2+accountLength+4+4+4+4+4];

        //cVer
        bytes[offset++] = 0;

        //wAccountLen
        bytes[offset++] = (byte)((accountLength & 0xFF00) >> 8);
        bytes[offset++] = (byte)(accountLength & 0x00FF);
        
        //buffAccount
        for (; offset < 3 + accountLength; ++offset) {
            bytes[offset] = (byte)userid.charAt(offset - 3);
        }

        //dwSdkAppid
        bytes[offset++] = (byte)((this.mSdkAppid & 0xFF000000) >> 24);
        bytes[offset++] = (byte)((this.mSdkAppid & 0x00FF0000) >> 16);
        bytes[offset++] = (byte)((this.mSdkAppid & 0x0000FF00) >> 8);
        bytes[offset++] = (byte)(this.mSdkAppid & 0x000000FF);
        
        //dwAuthId
        long nRoomId = Long.valueOf(roomid);
        bytes[offset++] = (byte)((nRoomId & 0xFF000000) >> 24);
        bytes[offset++] = (byte)((nRoomId & 0x00FF0000) >> 16);
        bytes[offset++] = (byte)((nRoomId & 0x0000FF00) >> 8);
        bytes[offset++] = (byte)(nRoomId & 0x000000FF);
        
        //dwExpTime
        long expiredTime = Long.valueOf(time) + expire;
        bytes[offset++] = (byte)((expiredTime & 0xFF000000) >> 24);
        bytes[offset++] = (byte)((expiredTime & 0x00FF0000) >> 16);
        bytes[offset++] = (byte)((expiredTime & 0x0000FF00) >> 8);
        bytes[offset++] = (byte)(expiredTime & 0x000000FF);

        //dwPrivilegeMap     
        bytes[offset++] = (byte)((255 & 0xFF000000) >> 24);
        bytes[offset++] = (byte)((255 & 0x00FF0000) >> 16);
        bytes[offset++] = (byte)((255 & 0x0000FF00) >> 8);
        bytes[offset++] = (byte)(255 & 0x000000FF);
        
        //dwAccountType
        bytes[offset++] = (byte)((0 & 0xFF000000) >> 24);
        bytes[offset++] = (byte)((0 & 0x00FF0000) >> 16);
        bytes[offset++] = (byte)((0 & 0x0000FF00) >> 8);
        bytes[offset++] = (byte)(0 & 0x000000FF);
        
        String userbuf = Base64.getEncoder().encodeToString(bytes);
        
        String serialString =
        "TLS.appid_at_3rd:" + 0 + "\n" +
        "TLS.account_type:" + 0 + "\n" +
        "TLS.identifier:" + userid + "\n" +
        "TLS.sdk_appid:" + this.mSdkAppid + "\n" +
        "TLS.time:" + time + "\n" +
        "TLS.expire_after:" + expire +"\n" +
        "TLS.userbuf:" + userbuf + "\n";
        
        byte[] signBytes = sign(serialString.getBytes(Charset.forName("UTF-8")));
        String sig = Base64.getEncoder().encodeToString(signBytes);
        
        String jsonString = "{"
        +"\"TLS.appid_at_3rd\":\"" + 0 +"\","
        +"\"TLS.account_type\":\"" + 0 +"\","
        +"\"TLS.identifier\":\"" + userid +"\","
        +"\"TLS.sdk_appid\":\"" + this.mSdkAppid +"\","
        +"\"TLS.expire_after\":\"" + expire +"\","
        +"\"TLS.sig\":\"" + sig +"\","
        +"\"TLS.time\":\"" + time +"\","
        +"\"TLS.userbuf\":\"" + userbuf +"\","
        +"\"TLS.version\": \"201512300000\""
        +"}";
        
        //compression
        Deflater compresser = new Deflater();
        compresser.setInput(jsonString.getBytes(Charset.forName("UTF-8")));
        
        compresser.finish();
        byte [] compressBytes = new byte [512];
        int compressBytesLength = compresser.deflate(compressBytes);
        compresser.end();
        String privateMapKey = new String(base64UrlEncode(Arrays.copyOfRange(compressBytes, 0, compressBytesLength)));
        
        return privateMapKey;
    }
    
    
    public JSONObject entrance(Integer roomid,String userid) {
        int sdkappid = webrtcConstant.sdkAppid;

        File privateKeyFile = new File(webrtcConstant.private_key);
        byte[] privateKey = new byte[(int)privateKeyFile.length()];
        
        File publicKeyFile = new File(webrtcConstant.public_key);
        byte[] publicKey = new byte[(int)publicKeyFile.length()];
        
        try {

            FileInputStream in1 = new FileInputStream(privateKeyFile);
            in1.read(privateKey);
            in1.close();
            

            FileInputStream in2 = new FileInputStream(publicKeyFile);
            in2.read(publicKey);
            in2.close();
            
        } catch (Exception e ) {
            e.printStackTrace();
        }
        
        WebRTCSigApi api = new WebRTCSigApi();
        api.setSdkAppid(sdkappid);
        api.setPrivateKey(new String(privateKey));
        api.setPublicKey(new String(publicKey));
        

        String userSig = api.genUserSig(userid, 300);
        

        String privateMapKey = api.genPrivateMapKey(userid, roomid, 300);
        JSONObject obj=JSONObject.parseObject("{}");
        obj.put("userSig",userSig);
        obj.put("privateMapKey",privateMapKey);
        obj.put("errorCode",0);
        obj.put("sdkappid",sdkappid);
        return obj;
//        System.out.println("userSig:\n" + userSig);
//        System.out.println("privateMapKey:\n" + privateMapKey);
    }
    
}

