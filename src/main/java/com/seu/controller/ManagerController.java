package com.seu.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.Disputecase;
import com.seu.repository.DisputecaseRepository;
import lombok.extern.slf4j.Slf4j;
import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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
        JSONObject res_1= JSONObject.parseObject("{}");
        for(String one:arr){
            res_1.put(one,disputecaseRepository.getCountByCity(one));
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
        JSONObject res_1= JSONObject.parseObject("{}");
        for(String one:arr){
            res_1.put(one,disputecaseRepository.getCountByCenter(one));
        }
        res.put("opinionData",res_1);
        return ResultVOUtil.ReturnBack(res,200,"成功");
    }
}
