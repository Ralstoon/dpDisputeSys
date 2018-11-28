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
import com.seu.service.impl.DisputeProgressServiceImpl;
import com.seu.utils.GetWorkingTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
