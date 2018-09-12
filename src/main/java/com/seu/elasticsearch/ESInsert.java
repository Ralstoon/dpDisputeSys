package com.seu.elasticsearch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.seu.common.InitConstant;
import com.seu.domian.Operation;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName ESInsert
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/28 14:21
 * @Version 1.0
 **/

@Slf4j
public class ESInsert {


    private boolean doInsert(Operation oper,Client client,String index,String index_type) throws Exception{
        String jsStr=new Gson().toJson(oper);
        IndexResponse response=client.prepareIndex(index,index_type)
                .setSource(jsStr).get();
        if(response.isCreated()){
            return true;
        }else {
            return false;
        }
    }

    public void insertJSON(String url,String index,String index_type) throws Exception{
        Client client=new MyTransportClient().getClient();
        InputStreamReader read=null;
        BufferedReader bufferedReader=null;
        read=new InputStreamReader(new FileInputStream(new File(url)),"utf-8");
        bufferedReader=new BufferedReader(read);
        String readLine=null;
        while((readLine=bufferedReader.readLine())!=null){
            readLine=readLine.trim();
//            String[] line=readLine.trim().split("\t");
//            String keyword=line[0];
//            String operations=line[1].trim();
            Operation oper=new Operation(readLine);
            boolean flag=doInsert(oper,client,index,index_type);
            log.info(flag+"");
        }
        bufferedReader.close();
        read.close();
        client.close();
    }


    public static void main(String[] args) throws Exception {
        String index="手术_index";
        String index_type=index+"_type";
        String url="D:\\Documents\\WorkSpace\\worksapce_python\\666\\data\\纯手术.txt";
        new ESInsert().insertJSON(url,index,index_type);

    }

}
