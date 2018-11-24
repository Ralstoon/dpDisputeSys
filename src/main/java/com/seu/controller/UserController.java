package com.seu.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.RedisConstant;
import com.seu.common.ServerResponse;
import com.seu.domian.Comment;
import com.seu.domian.DisputecaseAccessory;
import com.seu.domian.User;
import com.seu.enums.RegisterEnum;
import com.seu.enums.UpdateInfoEnum;
import com.seu.form.LoginForm;
import com.seu.form.NormalUserForm;
import com.seu.form.VOForm.UserForm;
import com.seu.repository.CommentRepository;
import com.seu.repository.DisputecaseAccessoryRepository;
import com.seu.repository.UserRepository;
import com.seu.service.*;
import com.seu.util.MD5Util;
import com.seu.utils.KeyUtil;
import com.seu.utils.Request2JSONobjUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@CrossOrigin
@RequestMapping("/user/")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private NormalUserService normalUserService;
    @Autowired
    private RedisTemplate redisTemplate;

    /*
     *@Author 吴宇航
     *@Description  处理所有用户统一登录的方法
     *@Date 21:10 2018/8/3
     *@Param [phone, password, httpServletResponse]
     *@return com.seu.common.ServerResponse<com.seu.domian.NormalUser>
     **/
    @Autowired
    private UserRepository userRepository;
    @RequestMapping(value = "updatePassword",method = RequestMethod.POST)
    public ResultVO updatePassword(@RequestBody JSONObject map) {
        String phone=map.getString("phone");
        String password=map.getString("password");

        User user = userRepository.findByPhone(phone);
        user.setPassword(MD5Util.MD5EncodeUtf8(password));

        userRepository.save(user);

        return ResultVOUtil.ReturnBack(112, "密码修改成功");
    }

    /*
     *@Author 吴宇航
     *@Description  注册似乎一定是普通用户
     *@Date 21:57 2018/8/3
     *@Param [phone, password]
     *@return com.seu.ViewObject.ResultVO
     **/
    @RequestMapping(value = "register",method = RequestMethod.POST)
    public ResultVO register(@RequestBody Map<String,String> map){
        String phone=map.get("phone");
        String password=map.get("password");
//        log.info("【注册phone为：】{}",phone);
        int resultNum=userService.register(phone,password);
        if(resultNum==1){
            return ResultVOUtil.ReturnBack(RegisterEnum.REGISTER_SUCCESS.getCode(),RegisterEnum.REGISTER_SUCCESS.getMsg());
        }else {
            return ResultVOUtil.ReturnBack(RegisterEnum.REGISTER_FAIL.getCode(),RegisterEnum.REGISTER_FAIL.getMsg());
        }
    }

    /*
     *@Author 吴宇航
     *@Description  普通用户个人信息修改
     *@Date 13:30 2018/8/4
     *@Param [normalUserDetailForm, bindingResult]
     *@return com.seu.ViewObject.ResultVO
     **/
    // TODO
    @RequestMapping(value = "updateInfo",method = RequestMethod.POST)
    public ResultVO updateUserInfo(@RequestBody Map<String,Object> map){
//        if(bindingResult.hasErrors()){
//            Map<String,Object> param=new HashMap<>();
//            param.put("data",bindingResult.getFieldError().getDefaultMessage());
//            return ResultVOUtil.ReturnBack(param,UpdateInfoEnum.UPDATE_FAIL.getCode(),UpdateInfoEnum.UPDATE_FAIL.getMsg());
//        }
//        NormalUser currentUser=(NormalUser)session.getAttribute("currentUser");
        NormalUserForm normalUserForm1=new NormalUserForm();
        NormalUserForm result= normalUserService.updateNormalUser(normalUserForm1);
        if(result!=null)
            return ResultVOUtil.ReturnBack(result,UpdateInfoEnum.UPDATE_SUCCESS.getCode(),UpdateInfoEnum.UPDATE_SUCCESS.getMsg());
        else
            return ResultVOUtil.ReturnBack(UpdateInfoEnum.UPDATE_FAIL.getCode(),UpdateInfoEnum.UPDATE_FAIL.getMsg());
    }


    @RequestMapping(value = "loginout",method = RequestMethod.POST)
    public ResultVO loginout(HttpServletRequest request) throws Exception {
        JSONObject map=Request2JSONobjUtil.convert(request);
        String ID=map.getString("ID");
        String role=map.getString("role");
        return userService.loginout(ID,role);
    }

//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//    @RequestMapping(value="testt",method = RequestMethod.GET)
//    public Set<String> testt(){
//        String pattern="123321";
//        Set<String> result=stringRedisTemplate.keys("*"+pattern);
//        return result;
//    }

    @Autowired
    DisputeProgressService disputeProgressService;

    @PostMapping(value = "getUserCaseList")
    public ResultVO getUserCaseList(@RequestBody Map<String, String> map){

        String usrId = map.get("id");
        return disputeProgressService.getUserCaseList(usrId);
    }

    /** 注册调解员 */
    @PostMapping(value = "registerMediator")
    public ResultVO registerMediator(@RequestBody JSONObject var){
//        System.out.println(var.toString());
        JSONArray map=var.getJSONArray("addMeditorList");
        return userService.registerMediator(map);
    }

    //用户撤销申请
    @PostMapping(value = "caseCancelApply")
    public ResultVO caseCancelApply(@RequestBody Map<String,String> map){
        String userId = map.get("id");
        String caseId = map.get("caseId");
        String reason = map.get("reason");
        return disputeProgressService.setCaseCancelApply(caseId, reason);
    }

    //用户撤销调解
    @PostMapping(value = "caseCancelMediation")
    public ResultVO caseCancellMediation(@RequestBody Map<String,String> map){
        String userId = map.get("id");
        String caseId = map.get("caseId");
        String reason = map.get("reason");
        return disputeProgressService.setCaseCancellMediation(caseId, reason);
    }

    //申请结案
    @PostMapping(value = "caseSettle")
    public ResultVO caseSettle(@RequestBody Map<String,String> map){
        String userId = map.get("id");
        String caseId = map.get("caseId");
        return disputeProgressService.setCaseSettle(caseId);
    }

    //申请再次调解
    @PostMapping(value = "reMediation")
    public ResultVO reMediation(@RequestBody Map<String,String> map){
        String userId = map.get("id");
        String caseId = map.get("caseId");
        return disputeProgressService.setCasereMediation(caseId);
    }

    //用户撤销申请
    @PostMapping(value = "changeMediator")
    public ResultVO changeMediator(@RequestBody JSONObject obj){
        String userId = obj.getString("id");
        String caseId = obj.getString("caseId");
        String reason = obj.getString("reason");
        List<String> mediatorId = (List<String>) obj.get("mediatorIdList");
        return disputeProgressService.changeMediator(caseId, mediatorId, reason);
    }

    @Autowired
    private CommentService commentService;

    //用户添加评价
    @PostMapping(value = "addComment")
    public ResultVO addComment(@RequestBody Map<String,String> map){
        String caseId = map.get("caseId");
        String userId = map.get("userId");
        String otherEvaluation = map.get("otherEvaluation");
        String mediatorEvaluation = map.get("mediatorEvaluation");
        String state = map.get("status");
        Comment comment = new Comment(KeyUtil.genUniqueKey(),caseId,otherEvaluation,userId, mediatorEvaluation,state);
        return commentService.addComment(comment);
    }

    @Autowired
    private DisputecaseAccessoryService disputecaseAccessoryService;

    @Autowired
    private DisputecaseAccessoryRepository disputecaseAccessoryRepository;
    //用户中心上传资料
    @PostMapping(value = "uploadFile")
    public ResultVO uploadFile(@RequestParam(value = "file", required=false) MultipartFile multipartFile,
                               @RequestParam("caseId") String caseId,
                               @RequestParam("id") String userId,
                               @RequestParam("fileMessage") String fileMessage,
                               @RequestParam("status") String state) throws IOException {


        FileInputStream inputStream = (FileInputStream) multipartFile.getInputStream();
        String url = disputecaseAccessoryService.uploadFile(inputStream, multipartFile.getOriginalFilename());


        DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId(caseId);
        JSONObject each = JSONObject.parseObject("{}");
        each.put("id", userId);
        each.put("fileMessage", fileMessage);
        each.put("status", state);
        each.put("file", url);

        JSONArray jsonArray = null;

        if (disputecaseAccessory.getUserUpload()==null){
            jsonArray = JSONArray.parseArray("[]");
        } else {
            jsonArray = JSONArray.parseArray(disputecaseAccessory.getUserUpload());
        }
        jsonArray.add(each);
        disputecaseAccessory.setUserUpload(jsonArray.toJSONString());

        disputecaseAccessoryRepository.save(disputecaseAccessory);
        return ResultVOUtil.ReturnBack(123,"上传成功");
    }

    @RequestMapping(value = "login",method = RequestMethod.POST)
    public ServerResponse<UserForm> login(@RequestBody JSONObject map) {
        String phone=map.getString("phone");
        String password=map.getString("password");
//        System.out.println(password+"  "+phone);
        //TODO 没有处理用户反复登陆以及换账户登录,希望该功能由前端检查，若反复登陆和切换账户，则可以推荐用户先注销
//        session.getAttribute("NormalUser");
        ServerResponse<UserForm> response = userService.login(phone, password);

        if (response.isSuccess()) {
//            NormalUser currentUser=(NormalUser)response.getData();
//            session.setAttribute(Const.CURRENT_USER, response.getData());
            UserForm userForm=response.getData();
            // 先放入redis服务器，设置key为token_
            String ID=userForm.getId();
            String role=userForm.getRole();
            Integer expire=RedisConstant.EXPIRE;
            redisTemplate.opsForValue().set(String.format(RedisConstant.USER_RREFIX,role,ID),userForm,expire,TimeUnit.SECONDS);
        }
        return response;
    }

    //注册验证码请求
    @RequestMapping(value = "getCode",method = RequestMethod.POST)
    public ResultVO getZoneList(@RequestBody Map<String, String > map) {
        String phone = map.get("phone");

        Map<String, String> data = new HashMap<>();
        data.put("authcode", userService.getCode(phone));
        return ResultVOUtil.ReturnBack(data,123,"获取验证码成功");
    }
}
