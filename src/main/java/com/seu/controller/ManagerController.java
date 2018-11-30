package com.seu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.Disputecase;
import com.seu.domian.DisputecaseAccessory;
import com.seu.domian.DisputecaseProcess;
import com.seu.domian.Mediator;
import com.seu.enums.DisputeProgressEnum;
import com.seu.form.VOForm.ManagerCaseForm;
import com.seu.repository.*;
import com.seu.service.ManagerService;
import com.seu.service.impl.DisputeProgressServiceImpl;
import com.seu.utils.GetWorkingTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    public ResultVO addContactList(@RequestBody JSONObject map){
        String name = map.getString("name");
        String tele = map.getString("tele");
        String contactPerson= map.getString("contact_person");
        String contactPhone=map.getString("contact_phone");
        String role=map.getString("role");
        String location=map.getString("location");
        String province=map.getString("province");
        String zone=map.getString("zone");
        String city=map.getString("city");
        managerService.addHospitalAndRoom(
                zone,
                city,
                name,
                JSON.parseArray("[]"));
        return managerService.addContactList(name,
                tele,
                contactPerson,
                contactPhone,
                role,
                location,
                province,
                zone,
                city);
    }

    //删除医院联系方式
    @PostMapping("/manager/deleteContactList")
    public ResultVO deleteContactList(@RequestBody JSONObject map){
        String id = map.getString("id");

        return managerService.deleteContactList(id);
    }

    //修改医院联系方式
    @PostMapping("/manager/updateContactList")
    public ResultVO updateContactList(@RequestBody JSONObject map){
        String id = map.getString("id");
        String name = map.getString("name");
        String tele = map.getString("tele");
        String contactPerson= map.getString("contact_person");
        String contactPhone=map.getString("contact_phone");
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
    @PostMapping("/manager/addHospitalAndRoom")
    public ResultVO addHospitalAndRoom(@RequestBody JSONObject map){
        String hospital = map.getString("hospital");
        JSONArray room = map.getJSONArray("room");
        String province=map.getString("province");
        String zone=map.getString("zone");
        String city=map.getString("city");



        return managerService.addHospitalAndRoom(
                zone,
                city,
                hospital,
                room);
    }

    //获取调解员列表（后台管理）
    @PostMapping("/manager/getMediatorListBackEnd")
    public ResultVO getMediatorList(@RequestBody JSONObject map){
        Integer size = map.getInteger("size");
        Integer page = map.getInteger("page");
        String mediationCenter = map.getString("mediationCenter");
        String province=map.getString("province");
        String city=map.getString("city");
        String mediatiorName = map.getString("mediatiorName");



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
        Integer page = map.getInteger("page");
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
        Integer size = map.getInteger("size");
        Integer page = map.getInteger("page");
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

}
