package com.seu.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.ConstantData;
import com.seu.domian.DisputecaseProcess;
import com.seu.domian.Mediator;
import com.seu.elasticsearch.MyTransportClient;
import com.seu.enums.DisputeRegisterEnum;
import com.seu.repository.DiseaseListRepository;
import com.seu.repository.DisputecaseProcessRepository;
import com.seu.repository.MediatorRepository;
import com.seu.service.DisputeRegisterService;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName DisputeRegisterServiceImpl
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/8/20 20:43
 * @Version 1.0
 **/
@Service
public class DisputeRegisterServiceImpl implements DisputeRegisterService {

    @Autowired
    DiseaseListRepository diseaseListRepository;

    @Autowired
    MediatorRepository mediatorRepository;

    @Override
    public ResultVO getDieaseList() {
        ConstantData constantData=diseaseListRepository.findByName("disease_list");
        JSONObject jsStr=JSONObject.parseObject(constantData.getData());
        List<String> diseaseKind=new ArrayList<>();
        List<Object> diseaseName=new ArrayList<>();
        for(String key:jsStr.keySet()){
            diseaseKind.add(key);
            diseaseName.add(jsStr.get(key));
        }
        Map<String,Object> map=new HashMap<>();
        map.put("DiseaseKind",diseaseKind);
        map.put("DiseaseName",diseaseName);
        return ResultVOUtil.ReturnBack(map,DisputeRegisterEnum.GETDISEASELIST_SUCCESS.getCode(),DisputeRegisterEnum.GETDISEASELIST_SUCCESS.getMsg());
    }


    @Override
    public ResultVO getMedicalBehaviorList() {
        ConstantData constantData=diseaseListRepository.findByName("medical_behavior_list");
        JSONObject jsStr=JSONObject.parseObject(constantData.getData());
        Map<String,Object> map=new HashMap<>();
        for(String key:jsStr.keySet()){
            JSONObject subJson=JSONObject.parseObject(jsStr.get(key).toString());
            Map<String,Object> subMap=new HashMap<>();
            for(String subKey:subJson.keySet()){
                subMap.put(subKey,subJson.get(subKey));
            }
            map.put(key,subMap);
        }
        return ResultVOUtil.ReturnBack(map,DisputeRegisterEnum.GETMEDICALBEHAVIOR_SUCCESS.getCode(),DisputeRegisterEnum.GETMEDICALBEHAVIOR_SUCCESS.getMsg());
    }

    @Override
    public ResultVO getRoomList() {
        ConstantData constantData=diseaseListRepository.findByName("room_list");
        JSONObject jsStr=JSONObject.parseObject(constantData.getData());
        List<String> city = new ArrayList<>();
        List<List> hospital = new ArrayList<>();
        List<Object> room = new ArrayList<>();
        List<Object> hospitalCity = new ArrayList<>();
        List<Object> roomHp = new ArrayList<>();


        // a.stream().map()
        for(String key:jsStr.keySet()){
            city.add(key);
            for(String hpKey: ((JSONObject)jsStr.get(key)).keySet()){
                hospitalCity.add(hpKey);
                roomHp.add(((JSONObject)jsStr.get(key)).get(hpKey));
            }

            hospital.add(hospitalCity);
            room.add(roomHp);
            roomHp = new ArrayList<>();
            hospitalCity = new ArrayList<>();
        }

        Map<String,Object> map=new HashMap<>();
        map.put("City",city);
        map.put("Hospital",hospital);
        map.put("Department",room);
        return ResultVOUtil.ReturnBack(map,DisputeRegisterEnum.GETROOMLIST_SUCCESS.getCode(),DisputeRegisterEnum.GETROOMLIST_SUCCESS.getMsg());

    }

    @Autowired
    MyTransportClient client;

    /* 模拟仅单个科室和关键词 */
    @Override
    public ResultVO getOperations(String keyword,String room) throws Exception {
        String indexName=room+"_index";
        String typeName=indexName+"_type";
        String[] ss=new String[]{};
//        Client client=new MyTransportClient().getClient();

        SearchRequestBuilder requestbuilder=client.getClient().prepareSearch(indexName).setTypes(typeName);
        SearchResponse searchResponse=requestbuilder.setQuery(QueryBuilders.matchPhraseQuery("keyword",keyword))
                .setFrom(0).setSize(10).setExplain(true).execute().actionGet();
        Map<String,Object> map=new HashMap<>();
        SearchHits hits=searchResponse.getHits();
//        JSONObject jsStr=JSONObject.parseObject(hits.getHits()[0].getSourceAsString());
        List<String> operList=new ArrayList<>();
        for(int i=0;i<hits.getHits().length;++i){
            JSONObject jsStr=JSONObject.parseObject(hits.getHits()[i].getSourceAsString());
            String[] temp=jsStr.get("operations").toString().trim().split(",");
            for(String s:temp)
                operList.add(s);
        }
        map.put(room,operList);
        return ResultVOUtil.ReturnBack(map,0,"成功");
    }

    @Autowired
    DisputecaseProcessRepository disputecaseProcessRepository;

    @Override
    public ResultVO getMediatorList(String id) {
        DisputecaseProcess disputecaseProcess=disputecaseProcessRepository.findByDisputecaseId(id);
        String mediatorAvoid = disputecaseProcess.getAvoidStatus();
        List<Mediator> mediatorList = mediatorRepository.findAll();
        for(String s:mediatorAvoid.trim().split(",")){
            mediatorList.stream().filter((Mediator mediator) -> mediator.getMediatorId() == s);
        }

        return ResultVOUtil.ReturnBack(mediatorList, 122, "获取所有调解员列表成功");
    }
}
