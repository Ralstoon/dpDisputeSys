package com.seu.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.*;
import com.seu.domian.ConstantData;
import com.seu.domian.DisputecaseProcess;
import com.seu.domian.Mediator;
import com.seu.elasticsearch.MyTransportClient;
import com.seu.enums.DisputeRegisterEnum;
import com.seu.repository.*;
import com.seu.repository.DiseaseListRepository;
import com.seu.repository.DisputecaseProcessRepository;
import com.seu.repository.MediatorRepository;
import com.seu.service.DisputeProgressService;
import com.seu.service.DisputeRegisterService;
import com.seu.utils.GetTitleAndAbstract;
import com.seu.utils.KeyUtil;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private DiseaseListRepository diseaseListRepository;
    @Autowired
    private DisputecaseRepository disputecaseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NormalUserRepository normalUserRepository;
    @Autowired
    private DisputecaseApplyRepository disputecaseApplyRepository;
    @Autowired
    private DisputecaseAccessoryRepository disputecaseAccessoryRepository;
    @Autowired
    private DisputecaseProcessRepository disputecaseProcessRepository;

    @Autowired
    private MediatorRepository mediatorRepository;
    @Autowired
    private DisputeProgressService disputeProgressService;
    @Autowired
    private DisputecaseActivitiRepository disputecaseActivitiRepository;
    @Autowired
    private TaskService taskService;


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


//    @Autowired
//    DisputecaseProcessRepository disputecaseProcessRepository;

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

    @Override
    public ResultVO getCaseId() {
        try {
            String caseId=KeyUtil.genUniqueKey();
            Disputecase disputecase=new Disputecase();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            Date d=new Date();
            String dateS = df.format(d);
            disputecase.setApplyTime(df.parse(dateS));
            disputecase.setId(caseId);

            disputecase=disputecaseRepository.save(disputecase);
            Map<String,Object> var=new HashMap<>();
            var.put("CaseId",caseId);
            return ResultVOUtil.ReturnBack(var,DisputeRegisterEnum.GETCASEID_SUCCESS.getCode(),DisputeRegisterEnum.GETCASEID_SUCCESS.getMsg());
        }catch (ParseException pe){
            pe.printStackTrace();
            return ResultVOUtil.ReturnBack(DisputeRegisterEnum.GETCASEID_FAIL.getCode(),DisputeRegisterEnum.GETCASEID_FAIL.getMsg());
        }
    }

    @Override
    @Transactional
    public ResultVO sendInvolvedPeopleInfo(String caseId, String involvedPeople) {
        /** disputecase表和disputecaseApply表 */
        Disputecase disputecase=disputecaseRepository.getOne(caseId);
        JSONArray array=JSONArray.parseArray(involvedPeople);
        String proposerId="";
        String agentId="";
        for(int i=0;i<array.size();++i){
            JSONObject obj=array.getJSONObject(i);
            String name=obj.getString("name");
            String idCard=obj.getString("cardID");
            String role=(obj.getString("picked").trim().equals("申请人"))?"0":"1";
            String phone=obj.getString("phone");
            DisputecaseApply applyOne=new DisputecaseApply();
            String applyId=KeyUtil.genUniqueKey();
            if(role=="0")
                proposerId+=applyId+",";
            else
                agentId+=applyId+",";
            applyOne.setDisputecaseId(caseId);
            applyOne.setId(applyId);
            applyOne.setIdCard(idCard);
            applyOne.setName(name);
            applyOne.setRole(role);
            /** 查看该手机是否已注册 */
            User user=userRepository.findByPhone(phone);
            if(user==null){
                // 未注册,帮忙注册 密码初始为111111
                String id=KeyUtil.genUniqueKey();
                User newUser=new User();
                newUser.setID(id);
                newUser.setPassword("111111");
                newUser.setPhone(phone);
                newUser.setRole("0");
                NormalUser newNU=new NormalUser();
                String nuId=KeyUtil.genUniqueKey();
                newNU.setIdCard(idCard);
                newNU.setNormalId(nuId);
                newNU.setFatherId(id);
                newUser.setSpecificId(nuId);
                userRepository.save(newUser);
                normalUserRepository.save(newNU);
            }else {
                // 更新信息表中身份证
                NormalUser normalUser=normalUserRepository.findByFatherId(user.getID());
                normalUser.setIdCard(idCard);
                normalUserRepository.save(normalUser);
            }

            disputecaseApplyRepository.save(applyOne);
        }
        disputecase.setProposerId(proposerId.substring(0,proposerId.length()-1));
        if(agentId=="")
            disputecase.setAgnetId("");
        else
            disputecase.setAgnetId(agentId.substring(0,agentId.length()-1));
        disputecaseRepository.save(disputecase);
        return ResultVOUtil.ReturnBack(DisputeRegisterEnum.GETINVOLVEDPEOPLEINFO_SUCCESS.getCode(),DisputeRegisterEnum.GETINVOLVEDPEOPLEINFO_SUCCESS.getMsg());
    }

    @Override
    @Transactional
    public ResultVO getBasicDivideInfo(String stageContent, String caseId, Integer mainRecStage, String require, Integer claimAmount) {
        Disputecase disputecase=disputecaseRepository.getOne(caseId);
        disputecase.setAppeal(require);
        disputecase.setMainRecStage(mainRecStage+"");
        disputecase.setClaimMoney(claimAmount+"");
        disputecase.setMedicalProcess(stageContent);
        String[] temp=disputecase.getProposerId().trim().split(",");
        List<String> names=new ArrayList<>();
        for(String s:temp){
            names.add(disputecaseApplyRepository.getOne(s).getName());
        }
        Map<String,String> result=GetTitleAndAbstract.generateCaseTitleDetail(stageContent,names);
        disputecase.setCaseName(result.get("title"));
        disputecase.setBriefCase(result.get("detail"));


        /** 创建process表和Accessory表 */
        String processId=KeyUtil.genUniqueKey();
        disputecase.setProcessId(processId);
        DisputecaseProcess disputecaseProcess=new DisputecaseProcess();
        disputecaseProcess.setId(processId);
        disputecaseProcess.setDisputecaseId(caseId);
        disputecaseProcess.setStatus("0");
        disputecaseProcessRepository.save(disputecaseProcess);

        String accessoryId=KeyUtil.genUniqueKey();
        DisputecaseAccessory disputecaseAccessory=new DisputecaseAccessory();
        disputecaseAccessory.setId(accessoryId);
        disputecaseAccessory.setDisputecaseId(caseId);
        disputecaseAccessoryRepository.save(disputecaseAccessory);

        disputecaseRepository.save(disputecase);

        /** 开启流程，并完成纠纷登记 */
        Map<String,Object> var =new HashMap<>();
        var.put("disputeId",caseId);
        var.put("paramProfesor",0);
        var.put("paramAuthenticate",0);
        disputeProgressService.startProcess(caseId,var);

        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        Task currentTask=disputeProgressService.searchCurrentTasks(caseId).get(0);  // 纠纷登记
        disputeProgressService.completeCurrentTask(currentTask.getId());

        return  ResultVOUtil.ReturnBack(DisputeRegisterEnum.GETBASICDIVIDEINFO_SUCCESS.getCode(),DisputeRegisterEnum.GETBASICDIVIDEINFO_SUCCESS.getMsg());
    }
}
