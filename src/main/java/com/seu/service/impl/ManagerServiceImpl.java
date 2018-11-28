package com.seu.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.*;
import com.seu.repository.*;
import com.seu.service.DisputecaseAccessoryService;
import com.seu.service.ManagerService;
import com.seu.util.MD5Util;
import com.seu.utils.KeyUtil;
import com.seu.utils.RegisterIMUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MediatorRepository mediatorRepository;
    @Autowired
    private RegisterIMUtil registerIMUtil;
    @Autowired
    private ContactListRepository contactListRepository;
    @Autowired
    private ConstantDataRepository constantDataRepository;
    @Autowired
    private DisputecaseAccessoryService disputecaseAccessoryService;

    @Override
    public ResultVO addMediator(String mediatorName, String idCard, String mediateCenter, String authorityConfirm,
                                String authorityJudiciary, String basicInformation, String city, String province,
                                String phone, String password, MultipartFile multipartFile) throws IOException {



        //验证手机号是否已存在
//        int resultCount = normalUserRepository.checkUser(phone);
        if(userRepository.findByPhone(phone)!=null){
            return ResultVOUtil.ReturnBack(1, "该手机号码已经注册");
        }
        //进行注册操作
        //1、首先对user表
        String normalId= KeyUtil.genUniqueKey();
        String ID=KeyUtil.genUniqueKey();
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user=new User(ID,phone,md5Password,"1",normalId);
        User saveUser=userRepository.save(user);
        if(saveUser==null)
            return ResultVOUtil.ReturnBack(2, "注册失败");
        //2、其次对mediator表

        FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
        String url = disputecaseAccessoryService.uploadFile(inputStream, normalId + multipartFile.getOriginalFilename());

        Mediator mediator =new Mediator(normalId, ID, mediatorName, idCard, mediateCenter, authorityConfirm,
                authorityJudiciary,basicInformation,city,province, url);
        Mediator saveMediator =  mediatorRepository.save(mediator);

        /** 注册IM */
        registerIMUtil.registerIM(phone,password);
        if(saveMediator==null)
            return ResultVOUtil.ReturnBack(2, "注册失败");
        else
            return ResultVOUtil.ReturnBack(3, "调解员添加成功");

    }

    @Override
    public ResultVO addContactList(String name, String tele, String contactPerson, String contactPhone, String role, String location, String province, String zone, String city) {
        ContactList contactList = new ContactList(name,
                tele,
                contactPerson,
                contactPhone,
                role,
                location,
                province,
                zone,
                city);
        contactListRepository.save(contactList);
        return ResultVOUtil.ReturnBack(3, "医院联系方式添加成功");
    }

    @Override
    public ResultVO addHospitalAndRoom(String zone, String city, String hospital, JSONArray room) {

        ConstantData constantData = constantDataRepository.findByName("room_list");
        JSONObject jsonObject = JSON.parseObject(constantData.getData());
        if (jsonObject.getJSONObject(city) == null){
            jsonObject.put(city, JSON.parseObject("{}"));
        }
        if(jsonObject.getJSONObject(city).getJSONObject(zone) == null){
            jsonObject.getJSONObject(city).put(zone,JSON.parseObject("{}"));
        }

        if(jsonObject.getJSONObject(city).getJSONObject(zone).getJSONArray(hospital)==null){
            jsonObject.getJSONObject(city).getJSONObject(zone).put(hospital,room);
        }else{
            JSONArray temp = jsonObject.getJSONObject(city).getJSONObject(zone).getJSONArray(hospital);
            for (int i = 0; i<room.size();i++){
                temp.add(room.get(i));
            }
            List<String> list =  JSONObject.parseArray(JSONObject.toJSONString(temp), String.class);
            Set<String> set = new HashSet<>(list);
            JSONArray result = JSON.parseArray("[]");
            for (int i = 0; i<set.size();i++){
                result.add(set.toArray()[i]);
            }
            jsonObject.getJSONObject(city).getJSONObject(zone).put(hospital,result);
        }


        constantData.setData(jsonObject.toJSONString());
        constantDataRepository.save(constantData);
        return ResultVOUtil.ReturnBack(3, "医院科室添加成功");
    }

    @Override
    public ResultVO deleteMediator(String phone) {
        User user = userRepository.findByPhone(phone);
        Mediator mediator = mediatorRepository.findByFatherId(user.getID());
        userRepository.delete(user);
        mediatorRepository.delete(mediator);
        return ResultVOUtil.ReturnBack(3, "调解员删除成功");
    }

    @Override
    public ResultVO updateMediator(String mediatorName, String idCard, String mediateCenter, String authorityConfirm, String authorityJudiciary, String basicInformation, String city, String province, String phone, String password, MultipartFile multipartFile) throws IOException {
        User user = userRepository.findByPhone(phone);
        Mediator mediator = mediatorRepository.findByFatherId(user.getID());
        //String mediatorName, String idCard, String mediateCenter, String authorityConfirm,
        // String authorityJudiciary, String basicInformation, String city, String province,
        // String phone, String password, MultipartFile multipartFile
        if(mediatorName!=null && !mediatorName.equals(""))
            mediator.setMediatorName(mediatorName);
        if(idCard!=null && !idCard.equals(""))
            mediator.setIdCard(idCard);
        if(mediateCenter!=null && !mediateCenter.equals(""))
            mediator.setMediateCenter(mediateCenter);
        if(authorityConfirm!=null && !authorityConfirm.equals(""))
            mediator.setAuthorityConfirm(authorityConfirm);
        if(authorityJudiciary!=null && !authorityJudiciary.equals(""))
            mediator.setAuthorityJudiciary(authorityJudiciary);

        if(basicInformation!=null && !basicInformation.equals(""))
            mediator.setBasicInformation(basicInformation);
        if(city!=null && !city.equals(""))
            mediator.setCity(city);
        if(province!=null && !province.equals(""))
            mediator.setProvince(province);
        if(phone!=null && !phone.equals(""))
            user.setPhone(phone);
        if(password!=null && !password.equals("")){
            String md5Password = MD5Util.MD5EncodeUtf8(password);
            user.setPassword(md5Password);
        }

        if(multipartFile!=null){
            FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
            String url = disputecaseAccessoryService.uploadFile(inputStream, mediator.getMediatorId() + multipartFile.getOriginalFilename());
            mediator.setAvatar(url);
        }
        userRepository.save(user);
        mediatorRepository.save(mediator);
        return ResultVOUtil.ReturnBack(3, "调解员秀改成功");
    }

    @Autowired
    private DisputecaseRepository disputecaseRepository;

    @Override
    public ResultVO deleteContactList(String id) {
        ContactList contactList = contactListRepository.findOne(Integer.parseInt(id));
        contactListRepository.delete(contactList);
        return ResultVOUtil.ReturnBack(3, "删除医院联系方式成功");
    }

    @Override
    public ResultVO updateContactList(String id, String name, String tele, String contactPerson, String contactPhone, String role, String location, String province, String zone, String city) {
        ContactList contactList = contactListRepository.findOne(Integer.parseInt(id));
        if(name != null || !name.equals(""))
            contactList.setName(name);
        if(tele != null || !tele.equals(""))
            contactList.setTele(name);
        if(contactPerson != null || !contactPerson.equals(""))
            contactList.setContactPerson(name);
        if(contactPhone != null || !contactPhone.equals(""))
            contactList.setContactPhone(name);
        if(role != null || !role.equals(""))
            contactList.setRole(name);
        if(location != null || !location.equals(""))
            contactList.setLocation(name);
        if(province != null || !province.equals(""))
            contactList.setProvince(name);
        if(zone != null || !zone.equals(""))
            contactList.setZone(name);
        if(city != null || !city.equals(""))
            contactList.setCity(name);
        contactListRepository.save(contactList);

        return ResultVOUtil.ReturnBack(3,"更新医院联系方式成功");
    }

    @Override
    public ResultVO getContactList(Integer size, Integer page, String province, String city, String zone) {
        PageRequest pageRequest=new PageRequest(page,size);
        Page<ContactList> contactListPage=contactListRepository.findByLoc(province,city,zone,pageRequest);


        Integer totalPages=contactListPage.getTotalPages();
        Map<String,Object> var=new HashMap<>();
        var.put("contactList",contactListPage.getContent());
        var.put("totalPages",totalPages);
        return ResultVOUtil.ReturnBack(var,3,"获得医院联系方式列表成功");
    }


}
