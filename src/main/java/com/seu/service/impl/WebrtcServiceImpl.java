package com.seu.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seu.service.WebrtcService;
import jdk.internal.util.xml.impl.Input;
import lombok.extern.slf4j.Slf4j;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * @ClassName WebrtcServiceImpl
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/10/12 0012 下午 8:52
 * @Version 1.0
 **/
@Service
@Slf4j
@ConfigurationProperties(prefix = "py")
public class WebrtcServiceImpl implements WebrtcService {
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public JSONObject mixStream(Integer time) {
        JSONObject obj= JSONObject.parseObject("{}");
        String result="";

//        String[] arguments = new String[] { "python", path+"pyfile.py"};
//        try {
//            Process process = Runtime.getRuntime().exec(arguments);
//            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
//            String line = null;
//            while ((line = in.readLine()) != null) {
//                System.out.println(line);
//            }
//            in.close();
//            int re = process.waitFor();
//            System.out.println(re);
//            obj.put("errorCode",0);
//        } catch (Exception e) {
//            e.printStackTrace();
//            obj.put("errorCode",-1);
//        }finally {
//            return obj;
//        }








        try {
            PythonInterpreter interpreter=new PythonInterpreter();
            InputStream filepy=new FileInputStream(path+"pyfile.py");
            interpreter.execfile(filepy);
            PyFunction function=(PyFunction)interpreter.get("__main__",PyFunction.class);
            PyObject o = function.__call__(new PyString());
            System.out.println(o.toString());

            filepy.close();
            obj.put("errorCode",0);
        }catch (IOException ioe){
            log.error("[调用python脚本并读取结果时出错]:" + ioe.getMessage());
            obj.put("errorCode",-1);
        }finally {
            return obj;
        }
    }
}
