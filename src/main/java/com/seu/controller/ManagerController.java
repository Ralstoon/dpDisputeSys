package com.seu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.*;
import com.seu.elasticsearch.MyTransportClient;
import com.seu.enums.DisputeProgressEnum;
import com.seu.form.VOForm.AdminForm;
import com.seu.form.VOForm.ManagerCaseForm;
import com.seu.repository.*;
import com.seu.service.ManagerService;
import com.seu.service.impl.DisputeProgressServiceImpl;
import com.seu.util.MD5Util;
import com.seu.utils.GetWorkingTimeUtil;
import com.seu.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @ClassName ManagerController
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/11/24 0024 下午 3:25
 * @Version 1.0
 **/

@RestController
@RequestMapping("/DisputeWeb")
@Slf4j
public class ManagerController {
    @Autowired
    private DisputecaseRepository disputecaseRepository;

    @PostMapping("/getProvinceChartData")
    public ResultVO getProvinceChartData(@RequestBody JSONObject obj){
        String province=obj.getString("province");
        JSONObject res=JSONObject.parseObject("{}");
        List<Object> cityList=disputecaseRepository.findDistinctProvince(province);
        ArrayList<String> arr=new ArrayList<>();
        for(Object o:cityList)
            arr.add(o.toString());
        res.put("opinion",arr);
        JSONArray res_1= JSONArray.parseArray("[]");
        for(String one:arr){
            JSONObject o1=JSONObject.parseObject("{}");
            o1.put("name",one);o1.put("value",disputecaseRepository.getCountByCity(one));
            res_1.add(o1);
        }
        res.put("opinionData",res_1);
        return ResultVOUtil.ReturnBack(res,200,"成功");
    }

    @PostMapping("/getCityChartData")
    public ResultVO getCityChartData(@RequestBody JSONObject map){
        String province=map.getString("province");
        String city=map.getString("city");
        JSONObject res=JSONObject.parseObject("{}");
        List<Object> centerList=disputecaseRepository.findDistinctCenter(province,city);
        ArrayList<String> arr=new ArrayList<>();
        for(Object o:centerList)
            arr.add(o.toString());
        res.put("opinion",arr);
        JSONArray res_1= JSONArray.parseArray("[]");
        for(String one:arr){
            JSONObject o1=JSONObject.parseObject("{}");
            o1.put("name",one);o1.put("value",disputecaseRepository.getCountByCenter(one));
            res_1.add(o1);
        }
        res.put("opinionData",res_1);
        return ResultVOUtil.ReturnBack(res,200,"成功");
    }

    @Autowired
    private DisputeProgressServiceImpl disputeProgressService;
    @PostMapping("/manager/getCase_judiciary")
    public ResultVO getCaseJudiciary(@RequestBody JSONObject map) throws Exception{

       return disputeProgressService.getManagerCaseList(map);
    }

    @Autowired
    private ManagerService managerService;
    //添加调解员
    @PostMapping("/manager/addMediator")
    public ResultVO addMediator(@RequestParam(value = "avatar", required=false) MultipartFile multipartFile,
                                @RequestParam("mediator_name") String mediatorName,
                                @RequestParam("id_card") String idCard,
                                @RequestParam("mediate_center") String mediateCenter,
                                @RequestParam("authority_confirm") String authorityConfirm,
                                @RequestParam("authority_judiciary") String authorityJudiciary,
                                @RequestParam("basic_information") String basicInformation,
                                @RequestParam("city") String city,
                                @RequestParam("province") String province,
                                @RequestParam("phone") String phone,
                                @RequestParam("password") String password) throws IOException {

        return managerService.addMediator(mediatorName, idCard, mediateCenter, authorityConfirm, authorityJudiciary, basicInformation,
                city,province,phone,password, multipartFile);
    }

    //删除调解员
    @PostMapping("/manager/deleteMediator")
    public ResultVO deleteMediator(@RequestBody JSONObject map) {
        String phone = map.getString("phone");
        return managerService.deleteMediator(phone);
    }

    //修改调解员
    @PostMapping("/manager/updateMediator")
    public ResultVO updateMediator(@RequestParam(value = "avatar", required=false) MultipartFile multipartFile,
                                @RequestParam("mediator_name") String mediatorName,
                                @RequestParam("id_card") String idCard,
                                @RequestParam("mediate_center") String mediateCenter,
                                @RequestParam("authority_confirm") String authorityConfirm,
                                @RequestParam("authority_judiciary") String authorityJudiciary,
                                @RequestParam("basic_information") String basicInformation,
                                @RequestParam("city") String city,
                                @RequestParam("province") String province,
                                @RequestParam("phone") String phone,
                                @RequestParam("password") String password) throws IOException {

        return managerService.updateMediator(mediatorName, idCard, mediateCenter, authorityConfirm, authorityJudiciary, basicInformation,
                city,province,phone,password, multipartFile);
    }

    //添加医院联系方式
    @PostMapping("/manager/addContactList")
    public ResultVO addContactList(@RequestBody JSONArray map){
//        String name = map.getString("name");
//        String tele = map.getString("tele");
//        String contactPerson= map.getString("contact_person");
//        String contactPhone=map.getString("contact_phone");
//        String role=map.getString("role");
//        String location=map.getString("location");
//        String province=map.getString("province");
//        String zone=map.getString("zone");
//        String city=map.getString("city");
////        managerService.addHospitalAndRoom(
////                zone,
////                city,
////                name,
////                JSON.parseArray("[]"));
        return managerService.addContactList(map);
    }

    //删除医院联系方式
    @PostMapping("/manager/deleteContactList")
    public ResultVO deleteContactList(@RequestBody JSONObject map){
        String id = map.getString("id");

        return managerService.deleteContactList(id);
    }

    //修改医院联系方式
    @PostMapping("/manager/updateContact")
    public ResultVO updateContactList(@RequestBody JSONObject map){
        String id = map.getString("id");
        String name = map.getString("name");
        String tele = map.getString("tele");
        String contactPerson= map.getString("contactPerson");
        String contactPhone=map.getString("contactPhone");
        String role=map.getString("role");
        String location=map.getString("location");
        String province=map.getString("province");
        String zone=map.getString("zone");
        String city=map.getString("city");

        return managerService.updateContactList(id, name,
                tele,
                contactPerson,
                contactPhone,
                role,
                location,
                province,
                zone,
                city);
    }

    //查医院联系方式
    @PostMapping("/manager/getContactList")
    public ResultVO getContactList(@RequestBody JSONObject map){
        Integer size=map.getInteger("size");
        Integer page=map.getInteger("page")-1;
        String province=map.getString("province");
        String city=map.getString("city");
        String zone=map.getString("zone");
        String hospital = map.getString("hospital");

        return managerService.getContactList(size, page, province, city, zone, hospital);
    }
//    Integer size=map.getInteger("size");
//    Integer page=map.getInteger("page")-1;
//    String filterStatus=map.getString("filterStatus").trim();
//    String filterMediator=map.getString("filterMediator").trim();
//    Date startTime=map.getDate("startTime");
//    Date endTime=map.getDate("endTime");
//    String province=map.getString("province");
//    String city=map.getString("city");
//    String mediateCenter=map.getString("mediate_center");

    //添加医院、科室
    @PostMapping("/manager/addHospital")
    public ResultVO addHospitalAndRoom(@RequestBody JSONObject map){
        String hospital = map.getString("hospital");
        JSONArray room = map.getJSONArray("room");
        String province=map.getString("province");
        String zone=map.getString("zone");
        String city=map.getString("city");

        JSONArray contactList=map.getJSONArray("contactList");




        return managerService.addHospitalAndRoom(
                zone,
                city,
                hospital,
                room,
                contactList);
    }

    //获取调解员列表（后台管理）
    @PostMapping("/manager/getMediatorListBackEnd")
    public ResultVO getMediatorList(@RequestBody JSONObject map){
        Integer size = map.getInteger("size");
        Integer page = map.getInteger("page")-1;
        String mediationCenter = map.getString("mediationCenter");
        String province=map.getString("province");
        String mediatiorName = map.getString("mediatorName");
        String city=map.getString("city");





        return managerService.getMediatorList(
                size,
                page,
                mediationCenter,
                province,
                city,
                mediatiorName);
    }

    //获取医院列表
    @PostMapping("/manager/getHospitalList")
    public ResultVO getHospitalList(@RequestBody JSONObject map){
        Integer size = map.getInteger("size");
        Integer page = map.getInteger("page")-1;
        String zone = map.getString("zone");
        String province=map.getString("province");
        String city=map.getString("city");
        String hospitalName = map.getString("hospitalName");



        return managerService.getHospitalList(
                size,
                page,
                zone,
                province,
                city,
                hospitalName);
    }

    //修改医院updateHospital
    @PostMapping("/manager/updateHospital")
    public ResultVO updateHospital(@RequestBody JSONObject map){
        String zone = map.getString("zone");
        String city=map.getString("city");
        String hospital = map.getString("hospital");
        String newZone = map.getString("newZone");
        String newCity=map.getString("newCity");
        String newHospital = map.getString("newHospital");
        JSONArray room = map.getJSONArray("room");
        String province=map.getString("province");

        return managerService.updateHospital(
                zone,
                city,
                hospital,
                newZone,
                newCity,
                newHospital,
                room);
    }

    //DisputeWeb/manager/updateRoom
    @PostMapping("/manager/updateRoom")
    public ResultVO updateRoom(@RequestBody JSONObject map){
        String hospital = map.getString("hospitalName");
        JSONArray room = map.getJSONArray("room");
        String province=map.getString("province");
        String zone=map.getString("zone");
        String city=map.getString("city");

        return managerService.updateRoom(
                zone,
                city,
                hospital,
                room);
    }

    //获取基础科室，方便录入科室  /manager/getBasicRoom
    @GetMapping("/manager/getBasicRoom")
    public ResultVO getBasicRoom(){

        return managerService.getBasicRoom();
    }

    /** 案例管理，分页查询ES案件 */
    @PostMapping("/manager/getESList")
    public ResultVO getESList(@RequestBody JSONObject map){
        Integer page=map.getInteger("page")-1;
        Integer size=map.getInteger("size");
        String type=map.getString("type");
        String indices=null;
        String types=null;
        if(type.trim().equals("dissension_ms")){
            indices="dissension_ms_index";
            types="dissension_mstexts";
        }else if(type.trim().equals("dissension_dx")){
            indices="dissension_dx_index";
            types="dissension_dxtexts";
        }else if(type.trim().equals("dissension")){
            indices="dissension_index";
            types="dissensiontexts";
        }else{
            return ResultVOUtil.ReturnBack(501,"文书类型错误，必须为[裁判文书、典型案例和纠纷案例]其中一种");
        }
        try {
            Client client=new MyTransportClient().getClient();
            SearchResponse response = client
                    .prepareSearch(indices)
                    .setTypes(types)
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(
                            QueryBuilders.matchAllQuery()).addSort("_id", SortOrder.ASC).setFrom(page).setSize(size).setExplain(true)
                    .execute().actionGet();

            SearchResponse response2 = client
                    .prepareSearch(indices)
                    .setTypes(types)
                    .setSearchType(SearchType.QUERY_THEN_FETCH)
                    .setQuery(
                            QueryBuilders.matchAllQuery()).addSort("_id", SortOrder.ASC).setExplain(true)
                    .execute().actionGet();

            SearchHits searchHits2=response2.getHits();
            long total = searchHits2.getTotalHits();
            long totalPage;
            if(total%size == 0)
                totalPage = total/size;
            else
                totalPage = total/size + 1;

            SearchHits searchHits=response.getHits();
            JSONArray res=JSONArray.parseArray("[]");
            for(SearchHit hit:searchHits){
                Map<String,Object> sourceAsMap=hit.getSource();
                JSONObject obj=JSONObject.parseObject("{}");
                if(type.trim().equals("dissension")){
                    obj.put("disputeName",sourceAsMap.get("disputeName"));
                    obj.put("disputeType",sourceAsMap.get("disputeType"));
                    obj.put("acceptDate","201602121");
                    obj.put("Abstract",sourceAsMap.get("Abstract").toString().replaceAll("</p>", "").replaceAll("<p>", "")
                            .replaceAll("&nbsp", ""));
                    obj.put("id",hit.getId());
                }else if(type.trim().equals("dissension_ms")){
                    obj.put("disputeName",sourceAsMap.get("DisputeName"));
                    obj.put("disputeType",sourceAsMap.get("DisputeType"));
                    obj.put("acceptDate","201602121");
                    String abs=sourceAsMap.get("JudgeDecision").toString().replaceAll("</p>", "").replaceAll("<p>", "")
                            .replaceAll("&nbsp", "").replaceAll(";","");
                    if(abs.length()>=500)
                        obj.put("Abstract",abs.substring(0,500));
                    else
                        obj.put("Abstract",abs);
                    obj.put("id",hit.getId());
                }else  if (type.trim().equals("dissension_dx")){
                    obj.put("disputeName",sourceAsMap.get("DisputeName"));
                    obj.put("disputeType",sourceAsMap.get("DisputeType"));
                    obj.put("acceptDate","201602121");
                    obj.put("Abstract",sourceAsMap.get("Abstract").toString().replaceAll("</p>", "").replaceAll("<p>", "")
                            .replaceAll("&nbsp", ""));
                    obj.put("id",hit.getId());
                }
                res.add(obj);
            }
            JSONObject jsonObject = JSONObject.parseObject("{}");
            jsonObject.put("caseList", res);
            jsonObject.put("totalPage", totalPage);

            return ResultVOUtil.ReturnBack(jsonObject,200,"success");
        }catch (Exception e){
            e.printStackTrace();
            return ResultVOUtil.ReturnBack(501,"查询出错");
        }
    }

    @PostMapping("/manager/deleteES")
    public ResultVO deleteES(@RequestBody JSONObject map){
        String type=map.getString("type");
        String id=map.getString("id");
        String indices=null;
        String types=null;
        if(type.trim().equals("dissension_ms")){
            indices="dissension_ms_index";
            types="dissension_mstexts";
        }else if(type.trim().equals("dissension_dx")){
            indices="dissension_dx_index";
            types="dissension_dxtexts";
        }else if(type.trim().equals("dissension")){
            indices="dissension_index";
            types="dissensiontexts";
        }else{
            return ResultVOUtil.ReturnBack(501,"文书类型错误，必须为[裁判文书、典型案例和纠纷案例]其中一种");
        }
        try {
            Client client=new MyTransportClient().getClient();
            DeleteResponse response=client.prepareDelete(indices,types,id).execute().actionGet();
            return ResultVOUtil.ReturnBack(200,"删除成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResultVOUtil.ReturnBack(501,"删除出错");
        }
    }

    @PostMapping("/manager/updateES")
    public ResultVO updateES(@RequestBody JSONObject map){
        System.out.println(map.toJSONString());
        String type=map.getString("type");
        String id=map.getString("id");
        JSONObject content=map.getJSONObject("content");
        String indices=null;
        String types=null;
        if(type.trim().equals("dissension_ms")){
            indices="dissension_ms_index";
            types="dissension_mstexts";
            content.put("DisputeName",content.getString("disputeName"));
            content.remove("disputeName");
            content.put("DisputeType",content.getString("disputeType"));
            content.remove("disputeType");
            content.put("CourtThink",content.getString("courtThink"));
            content.remove("courtThink");
            content.put("JudgeDecision",content.getString("judgeDecision"));
            content.remove("judgeDecision");
            content.put("DisputeTag",content.getString("disputeTag"));
            content.remove("disputeTag");
        }else if(type.trim().equals("dissension_dx")){
            indices="dissension_dx_index";
            types="dissension_dxtexts";
        }else if(type.trim().equals("dissension")){
            indices="dissension_index";
            types="dissensiontexts";
            content.put("Abstract",content.getString("abstract"));
            content.remove("abstract");
        }else{
            return ResultVOUtil.ReturnBack(501,"文书类型错误，必须为[裁判文书、典型案例和纠纷案例]其中一种");
        }
        System.out.println(content.toJSONString());
        try {
            Client client=new MyTransportClient().getClient();
//            UpdateResponse response=client.prepareUpdate(indices,types,id).setDoc(content.toJSONString(), XContentType.JSON).get();
            UpdateResponse response=client.prepareUpdate(indices,types,id).setDoc(content.toJSONString()).get();
            return ResultVOUtil.ReturnBack(200,"更新成功");
        }catch (Exception e){
            e.printStackTrace();
            return ResultVOUtil.ReturnBack(501,"更新出错");
        }
    }
    //案例管理，添加案件
    @PostMapping("/manager/addCase")
    public ResultVO getCaseList(@RequestParam(value = "file", required=false) MultipartFile multipartFile,
                                @RequestParam("caseKind") String caseKind){

        //1.文书文件处理
        //2.处理结果持久化至es


        return ResultVOUtil.ReturnBack(2,"添加成功");
    }

    @PostMapping("/advise")
    public ResultVO advise(@RequestBody JSONObject object){
        String advise = object.getString("advise");
        String caseId = object.getString("caseId");
        String id = object.getString("id");

        //todo: 持久化advise
        return ResultVOUtil.ReturnBack(233,"success");
    }


    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/manager/getManagerAuthorityList")
    public ResultVO getManagerAuthorityList(@RequestBody JSONObject object){
        String province = object.getString("province");
        String city = object.getString("city");
        String id = object.getString("id");
        int page = object.getInteger("page") - 1;
        int size = object.getInteger("size");
        String filterType = object.getString("filterType");
        String filterType2 = object.getString("filterType2");


       Admin currentAdmin = adminRepository.findByFatherId(id);

        //乱七八糟，先假定

        if(currentAdmin.getLevel().equals("")||currentAdmin.getLevel().equals("0")){
            return ResultVOUtil.ReturnBack(111,"無權限");
        }

        if(province == null)
            province = "";
        if(city == null)
            city = "";
        if(filterType == null)
            filterType = "";
        if(filterType2 == null)
            filterType2 = "";

        PageRequest pageRequest=new PageRequest(page,size);
        Page<Admin> adminList=null;

        //f1,f2都是空
        if(filterType.equals("") && filterType2.equals("")){

            adminList = adminRepository.findAllByProvinceAndCityAndLev(province, city,String.valueOf(Integer.parseInt(currentAdmin.getLevel()) - 1),
                    String.valueOf(Integer.parseInt(currentAdmin.getLevel()) - 1),currentAdmin.getId() , pageRequest);
        }

        //f1非空，f2空
        if(!filterType.equals("") && filterType2.equals("")){
            if(filterType.equals("1"))
                adminList = adminRepository.findAllWithHasLevel(province,city,String.valueOf(Integer.parseInt(currentAdmin.getLevel()) - 1), currentAdmin.getId(),pageRequest);
            if(filterType.equals("0"))
                adminList = adminRepository.findAllWithNotHasLevel(province, city,String.valueOf(Integer.parseInt(currentAdmin.getLevel()) - 1), currentAdmin.getId(), pageRequest);
        }
        //f1空，f2非空
        if(filterType.equals("") && !filterType2.equals("")){
            if(filterType2.equals("1"))
                adminList = adminRepository.findAllWithHasCaseManageLevel(province,city,String.valueOf(Integer.parseInt(currentAdmin.getLevel()) - 1), currentAdmin.getId(), pageRequest);
            if(filterType2.equals("0"))
                adminList = adminRepository.findAllWithNotHasCaseManageLevel(province, city,String.valueOf(Integer.parseInt(currentAdmin.getLevel()) - 1), currentAdmin.getId(), pageRequest);

        }
        //f1,2非空
        if(!filterType.equals("") && !filterType2.equals("")){
            if(filterType.equals("1") && filterType2.equals("1"))
                adminList = adminRepository.findAllWithHasLevelAndHasCaseManageLevel(province,city,String.valueOf(Integer.parseInt(currentAdmin.getLevel()) - 1), currentAdmin.getId(),pageRequest);
            if(filterType.equals("1") && filterType2.equals("0"))
                adminList = adminRepository.findAllWithHasLevelAndNotHasCaseManageLevel(province,city,String.valueOf(Integer.parseInt(currentAdmin.getLevel()) - 1),currentAdmin.getId(),pageRequest);
            if(filterType.equals("0") && filterType2.equals("1"))
                adminList = adminRepository.findAllWithNotHasLevelAndHasCaseManageLevel(province,city,String.valueOf(Integer.parseInt(currentAdmin.getLevel()) - 1),currentAdmin.getId(),pageRequest);
            if(filterType.equals("0") && filterType2.equals("0"))
                adminList = adminRepository.findAllWithNotHasLevelAndNotHasCaseManageLevel(province,city,currentAdmin.getId(),pageRequest);
        }

        Integer totalPages=adminList.getTotalPages();
        List<AdminForm> adminVoList=new ArrayList<>();
        for(Admin admin:adminList.getContent()){

            String
            level =admin.getLevel();

            boolean a1;
            boolean a2;

            String
            caseMangeLevel = admin.getCaseMangeLevel();

//            if(!level.equals("")){
//                level = "1";
//                a1=true;
//            }else {
//                level = "0";
//                a1 =false;
//            }
//
//            if(!caseMangeLevel.equals("")){
//                caseMangeLevel = "1";
//                a2 = true;
//            }else {
//                caseMangeLevel = "0";
//                a2 = false;
//            }

            if(level.equals(String.valueOf(Integer.parseInt(currentAdmin.getLevel()) - 1))){
                a1 = true;
            }else {
                a1 =false;
            }
            if(caseMangeLevel.equals(String.valueOf(Integer.parseInt(currentAdmin.getLevel()) - 1))){
                a2 = true;
            }else {
                a2 =false;
            }


            adminVoList.add(new AdminForm(admin.getId(),admin.getFatherId(),admin.getAdminName(),admin.getMediateCenter(),admin.getCity(),
                    admin.getProvince(),level,caseMangeLevel,admin.getDuty(),admin.getTele(),a1,a2));
        }
        Map<String,Object> var=new HashMap<>();
        var.put("adminVoList",adminVoList);
        var.put("totalPages",totalPages);
        return ResultVOUtil.ReturnBack(var,DisputeProgressEnum.GETNAMEOFAUTHORITY_SUCCESS.getCode(),DisputeProgressEnum.GETNAMEOFAUTHORITY_SUCCESS.getMsg());

    }

    @PostMapping("/manager/changeMangerAuthority")
    public ResultVO changeMangerAuthority(@RequestBody JSONObject object){

        String id = object.getString("id");
        boolean authority1  = object.getBoolean("authority1");
        boolean authority2 = object.getBoolean("authority2");

        String managerId = object.getString("managerId");
        Admin currentAdmin = adminRepository.getOne(managerId);
        String setLevel = "";
        if ( currentAdmin.getLevel().equals("2")){
            setLevel = "1";
        }
        if ( currentAdmin.getLevel().equals("1")){
            setLevel = "0";
        }

        Admin admin = adminRepository.getOne(id);
        if(authority1)
            admin.setLevel(setLevel);
        else
            admin.setLevel("");

        if (authority2)
            admin.setCaseMangeLevel(setLevel);
        else
            admin.setCaseMangeLevel("");

        adminRepository.save(admin);

        return ResultVOUtil.ReturnBack(123,"改好了");
    }

    @PostMapping("manager/deleteManger")
    public ResultVO deleteManger(@RequestBody JSONObject object){

        String id = object.getString("id");
        Admin admin = adminRepository.getOne(id);
        String fatherId = admin.getFatherId();
        adminRepository.delete(admin);
        userRepository.delete(userRepository.getOne(fatherId));

        return ResultVOUtil.ReturnBack(123,"删除成功");
    }

    @Autowired
    private RegisterRepository registerRepository;

    @PostMapping("manager/getRedisterList")
    public ResultVO getRedisterList(@RequestBody JSONObject object){
        String province = object.getString("province");
        String city = object.getString("city");
        String mediateCenter=object.getString("mediate_center");
        String id = object.getString("id");
        int page = object.getInteger("page") - 1;
        int size = object.getInteger("size");


        Admin currentAdmin = adminRepository.findByFatherId(id);

        if(currentAdmin.getCaseMangeLevel()==null || currentAdmin.getLevel() == null)
            ResultVOUtil.ReturnBack(111,"没有权限");

        String targetRole1 = "";
        String targetRole2 = "";

        if(currentAdmin.getCaseMangeLevel().equals("0")){
            targetRole1 = "调解员";
            targetRole2 = "调解员";
        }

        if (currentAdmin.getLevel().equals("1")){
            targetRole1 = "区指导管理者";
            targetRole2 = "区案件管理者";
        }

        if (currentAdmin.getLevel().equals("2")){
            targetRole1 = "市指导管理者";
            targetRole2 = "市案件管理者";
        }

        PageRequest pageRequest=new PageRequest(page,size);
        Page<Register> registerList=null;
        registerList = registerRepository.findAllByRole(province,city,mediateCenter,targetRole1,targetRole2,pageRequest);

        Map<String,Object> var=new HashMap<>();
        var.put("registerVoList",registerList.getContent());
        var.put("totalPages",registerList.getTotalPages());
        return ResultVOUtil.ReturnBack(var,DisputeProgressEnum.GETNAMEOFAUTHORITY_SUCCESS.getCode(),DisputeProgressEnum.GETNAMEOFAUTHORITY_SUCCESS.getMsg());

    }

    @PostMapping("manager/approveRegister")
    public ResultVO approveRegister(@RequestBody JSONObject object) throws IOException {
        boolean result = object.getBoolean("result");
        String registerId = object.getString("registerId");
        Register register = registerRepository.getOne(registerId);
        if (result){
            if (register.getRole().equals("调解员")){
                managerService.addMediator(register.getName(), "idcard", register.getMediationCenter(), "0", "0", "",
                        register.getCity(),register.getProvince(),register.getPhone(), register.getPassword(), null);
                registerRepository.delete(register);
            }
            if (register.getRole().equals("区指导管理者")){
                User user = userRepository.findByPhone(register.getPhone());
                if(user==null){

                    //进行注册操作
                    //1、首先对user表
                    String normalId= KeyUtil.genUniqueKey();
                    String ID=KeyUtil.genUniqueKey();
                    String md5Password = MD5Util.MD5EncodeUtf8(register.getPassword());
                    User newUser=new User(ID,register.getPhone(),md5Password,"2",normalId);
                    User saveUser=userRepository.save(newUser);
                    if(saveUser==null)
                        return ResultVOUtil.ReturnBack(2, "注册失败");
                    //2、其次对admin表
                    Admin newAdmin = new Admin(normalId,ID,register.getName(),"idcard",register.getMediationCenter(),register.getCity(),
                            register.getProvince(),"0","",register.getPosition(),register.getTelephone());
                    adminRepository.save(newAdmin);

                }else{
                    Admin admin = adminRepository.findByFatherId(user.getID());
                    admin.setLevel("0");
                    adminRepository.save(admin);
                }
            }
            if (register.getRole().equals("区案件管理者")){
                User user = userRepository.findByPhone(register.getPhone());
                if(user==null){

                    //进行注册操作
                    //1、首先对user表
                    String normalId= KeyUtil.genUniqueKey();
                    String ID=KeyUtil.genUniqueKey();
                    String md5Password = MD5Util.MD5EncodeUtf8(register.getPassword());
                    User newUser=new User(ID,register.getPhone(),md5Password,"2",normalId);
                    User saveUser=userRepository.save(newUser);
                    if(saveUser==null)
                        return ResultVOUtil.ReturnBack(2, "注册失败");
                    //2、其次对admin表
                    Admin newAdmin = new Admin(normalId,ID,register.getName(),"idcard",register.getMediationCenter(),register.getCity(),
                            register.getProvince(),"","0",register.getPosition(),register.getTelephone());
                    adminRepository.save(newAdmin);

                }else{
                    Admin admin = adminRepository.findByFatherId(user.getID());
                    admin.setCaseMangeLevel("0");
                    adminRepository.save(admin);
                }
            }
            if (register.getRole().equals("市指导管理者")){
                User user = userRepository.findByPhone(register.getPhone());
                if(user==null){

                    //进行注册操作
                    //1、首先对user表
                    String normalId= KeyUtil.genUniqueKey();
                    String ID=KeyUtil.genUniqueKey();
                    String md5Password = MD5Util.MD5EncodeUtf8(register.getPassword());
                    User newUser=new User(ID,register.getPhone(),md5Password,"2",normalId);
                    User saveUser=userRepository.save(newUser);
                    if(saveUser==null)
                        return ResultVOUtil.ReturnBack(2, "注册失败");
                    //2、其次对admin表
                    Admin newAdmin = new Admin(normalId,ID,register.getName(),"idcard",register.getMediationCenter(),register.getCity(),
                            register.getProvince(),"1","",register.getPosition(),register.getTelephone());
                    adminRepository.save(newAdmin);

                }else{
                    Admin admin = adminRepository.findByFatherId(user.getID());
                    admin.setLevel("1");
                    adminRepository.save(admin);
                }
            }
            if (register.getRole().equals("市案件管理者")){
                User user = userRepository.findByPhone(register.getPhone());
                if(user==null){

                    //进行注册操作
                    //1、首先对user表
                    String normalId= KeyUtil.genUniqueKey();
                    String ID=KeyUtil.genUniqueKey();
                    String md5Password = MD5Util.MD5EncodeUtf8(register.getPassword());
                    User newUser=new User(ID,register.getPhone(),md5Password,"2",normalId);
                    User saveUser=userRepository.save(newUser);
                    if(saveUser==null)
                        return ResultVOUtil.ReturnBack(2, "注册失败");
                    //2、其次对admin表
                    Admin newAdmin = new Admin(normalId,ID,register.getName(),"idcard",register.getMediationCenter(),register.getCity(),
                            register.getProvince(),"","1",register.getPosition(),register.getTelephone());
                    adminRepository.save(newAdmin);

                }else{
                    Admin admin = adminRepository.findByFatherId(user.getID());
                    admin.setCaseMangeLevel("1");
                    adminRepository.save(admin);
                }
            }

        }else {
            registerRepository.delete(register);
            return ResultVOUtil.ReturnBack(124,"reject");
        }

        registerRepository.delete(register);
        return ResultVOUtil.ReturnBack(123,"success");
    }
}
