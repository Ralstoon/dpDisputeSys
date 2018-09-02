package com.seu.elasticsearch;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.seu.common.InitConstant;
import com.seu.domian.Operation;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ESInsert
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/28 14:21
 * @Version 1.0
 **/
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
            String[] line=readLine.trim().split("\t");
            String keyword=line[0];
            String operations=line[1].trim();
            Operation oper=new Operation(keyword,operations);
            doInsert(oper,client,index,index_type);
        }
        bufferedReader.close();
        read.close();
        client.close();
    }


    public static void main(String[] args) throws Exception {
//        String index="神经外科_index";
//        String index_type=index+"_type";
//        String url="D:\\worksapce_python\\666\\data\\1.txt";
//        new ESInsert().insertJSON(url,index,index_type);
        JSONObject jsonObject=JSONArray.parseArray(InitConstant.init_mediateStage).getJSONObject(0);
        JSONObject temp1=jsonObject.getJSONObject("预约调解").getJSONObject("currentStageContent");


    }

}
