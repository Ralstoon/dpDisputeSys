import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seu.DpdisputesysApplication;
import com.seu.common.RedisConstant;
import com.seu.domian.*;
import com.seu.form.HistoricTaskForm;
import com.seu.repository.*;
import com.seu.service.DisputeProgressService;
import com.seu.service.UserService;
import com.seu.service.impl.DisputeProgressServiceImpl;
import com.seu.util.MD5Util;
import com.seu.utils.GetTitleAndAbstract;
import com.seu.utils.GetWorkingTimeUtil;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.python.antlr.ast.Str;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import com.seu.util.MD5Util;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DpdisputesysApplication.class)
public class test {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService iNormalUserService;

    @Autowired
    private DisputeProgressService disputeProgressService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NormalUserRepository normalUserRepository;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private DiseaseListRepository diseaseListRepository;

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private RepositoryService repositoryService;


    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    public void redisTest(){
//        redisTemplate.opsForList().leftPush("qwe123","A");
//        redisTemplate.opsForList().leftPush("qwe123","B");
        List<String> list=redisTemplate.opsForList().range("qwe123",0,7);
        System.out.println(list.toString());
    }

    @Test
    public void jsarray(){
        JSONArray arr=JSONArray.parseArray("[123,321,534]");
        for(int i=0;i<arr.size();++i)
            System.out.println(arr.getString(i));
    }



    @Test
    @Deployment(resources = "processes/test.bpmn")
    public void test11(){
        ProcessInstance pi=runtimeService.startProcessInstanceByKey("test_1");
        Task curTask=taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        Map<String,Object> map=new HashMap();
        map.put("caseAccept",1);
        taskService.complete(curTask.getId(),map);
        curTask=taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println(curTask.getName());
    }



    @Test
    @Deployment(resources = "processes/disputeProgress.bpmn")
    public void webServiceTaskTest() {
        System.out.println(taskService.createTaskQuery().taskCandidateGroup("mediator").list().size());
    }

    @Test
    public void findPhoneByuserIdTest() {
//        System.out.println(iNormalUserService.findPhoneByUserId("1532004655415202278"));
    }

    @Test
    public void historicTaskListTest() {
        List<HistoricTaskForm> historicTaskFormList = disputeProgressService
                .getHistoricTaskListByDisputeId("1532413108579745686");
        System.out.println("asd");
    }

    @Test
    public void addCommentTest() {
        commentRepository.addComment("123", "123", "qwe", "123");
    }

    @Test
    public void findCommentByTaskIdTest() {
        Comment comment = commentRepository.findCommentByTaskId("123");
//        NormalUser normalUser = normalUserRepository.findNormalUserByUserId("1532005285945799016");
        System.out.println(comment.getComment());
        //historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey("s").singleResult().
    }

    @Test
    public void roomListTest() {
        ConstantData constantData = diseaseListRepository.findByName("room_list");
        JSONObject jsStr = JSONObject.parseObject(constantData.getData());
        List<String> city = new ArrayList<>();
        List<List> hospital = new ArrayList<>();
        List<Object> room = new ArrayList<>();
        List<Object> hospitalCity = new ArrayList<>();
        List<Object> roomHp = new ArrayList<>();


        // a.stream().map()
        for (String key : jsStr.keySet()) {
            city.add(key);
            for (String hpKey : ((JSONObject) jsStr.get(key)).keySet()) {
                hospitalCity.add(hpKey);
                roomHp.add(((JSONObject) jsStr.get(key)).get(hpKey));
            }

            hospital.add(hospitalCity);
            room.add(roomHp);
            roomHp = new ArrayList<>();
            hospitalCity = new ArrayList<>();
        }


        List a = ((List<JSONObject>) jsStr.get("南京市"));

    }

    @Autowired
    private DisputecaseRepository disputecaseRepository;



    @Test
    public void test(){
        Disputecase disputecase = disputecaseRepository.findOne("333");
        disputecase.setId("333");
        disputecase.setMediatorId("999999");
        disputecaseRepository.save(disputecase);
    }

    @Autowired
    private DisputecaseAccessoryRepository disputecaseAccessoryRepository;

    @Test
    public void test222(){
        DisputecaseAccessory disputecaseAccessory = disputecaseAccessoryRepository.findByDisputecaseId("gdgtrgrr");
        String normalusrUpload = disputecaseAccessory.getNormaluserUpload();
        List<NormalUserUpload> normalUserUploadList = net.sf.json.JSONArray.fromObject(normalusrUpload);
        NormalUserUpload normalUserUpload = new NormalUserUpload("ttt", "t", "tttt", "ttt", "t");
        normalUserUploadList.add(normalUserUpload);
        //DisputecaseAccessory disputecaseAccessory = new DisputecaseAccessory(id, disputeID, null, normaluserUpload);
        disputecaseAccessory.setNormaluserUpload(net.sf.json.JSONArray.fromObject(normalUserUploadList).toString());
        disputecaseAccessoryRepository.save(disputecaseAccessory);
    }



    @Test
    public void activitiTest(){

        //1.流程启动
        //ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process_pool1");

        //ProcessInstance processInstance = runtimeService.
        //2.完成一个任务
        //ProcessInstance pi=runtimeService.createProcessInstanceQuery().processInstanceId("325023").singleResult();
        Task task = taskService.createTaskQuery().processInstanceId("325023").singleResult();
        System.out.println(task.getName());
        taskService.complete(task.getId());

    }

    @Test
    public void date(){
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date=sDateFormat.format(new Date());

    }

    @Test
    public void currentTask() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("00");
    }


    //调解前处理
    @Test
    @Deployment(resources = "processes/test.bpmn")
    public void tiaojieqianchuli(){
        //ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process_pool1");
        Task task = taskService.createTaskQuery().processInstanceId("550036").singleResult();
        System.out.println(task.getName());
        Map<String,Object> var=new HashMap<>();
        var.put("paramBeforeMediate",0);
        disputeProgressService.completeCurrentTask(task.getId(),var);
        task = taskService.createTaskQuery().processInstanceId("550036").singleResult();
        System.out.println(task.getName());
    }

    @Autowired
    private DisputecaseProcessRepository disputecaseProcessRepository;

    @Test
    public void autoTest(){
        disputecaseProcessRepository.findAllByIsSuspended(1).stream().forEach(disputecaseProcess -> {
            Calendar c = Calendar.getInstance();
            c.setTime(disputecaseProcess.getEndtimeDisputecase());
            c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
            disputecaseProcess.setEndtimeDisputecase(c.getTime());
            disputecaseProcessRepository.save(disputecaseProcess);
        });
    }

    @Autowired
    private GetWorkingTimeUtil getWorkingTimeUtil;

    @Test
    public void test0929() throws Exception {
        System.out.println(getWorkingTimeUtil.calRemainTime("1538123913134925965"));
    }

    @Autowired
    private ConstantDataRepository constantDataRepository;

    @Test
    public void getCityListTest(){
        ConstantData constantData = constantDataRepository.findByName("room_list");
        JSONObject citys = JSONObject.parseObject(constantData.getData());
        for(String city: citys.keySet()){
            System.out.println(city);
        }
        System.out.println(citys.keySet());
    }

    @Test
    public void tttt(){
        System.out.println(MD5Util.MD5EncodeUtf8("123456789"));
    }

//    String g = "[{\n" +
//            "\t\"name\": \"疗程1\",\n" +
//            "\t\"resultOfRegConflict\": [{\n" +
//            "\t\t\t\"name\": \"诊断相关\",\n" +
//            "\t\t\t\"defaultBehavior\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseasesymptomBefore\": [\"dd\"],\n" +
//            "\t\t\t\"diseasesymptomAfter\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseaseBefore\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseasemAfter\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"test\": \"ff\",\n" +
//            "\t\t\t\"operation\": \"ss\",\n" +
//            "\t\t\t\"syndrome\": \"\",\n" +
//            "\t\t\t\"medicine\": \"ee\",\n" +
//            "\t\t\t\"added\": \"ss\"\n" +
//            "\t\t},\n" +
//            "\t\t{\n" +
//            "\t\t\t\"name\": \"检验相关\",\n" +
//            "\t\t\t\"defaultBehavior\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseasesymptomBefore\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseasesymptomAfter\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseaseBefore\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseasemAfter\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"test\": \"\",\n" +
//            "\t\t\t\"operation\": \"s\",\n" +
//            "\t\t\t\"syndrome\": \"s\",\n" +
//            "\t\t\t\"medicine\": \"ff\",\n" +
//            "\t\t\t\"added\": \"ddd\"\n" +
//            "\t\t}\n" +
//            "\t],\n" +
//            "\t\"ResultOfDamage\": \"一级甲等（死亡）\",\n" +
//            "\t\"InvolvedInstitute\": {\n" +
//            "\t\t\"City\": \"yz\",\n" +
//            "\t\t\"Zone\": \"hj\",\n" +
//            "\t\t\"Hospital\": \"yi\",\n" +
//            "\t\t\"Department\": [\"g\",\"p\"]\n" +
//            "\t}\n" +
//            "},\n" +
//            "{\n" +
//            "\t\"name\": \"疗程2\",\n" +
//            "\t\"resultOfRegConflict\": [{\n" +
//            "\t\t\t\"name\": \"诊断相关\",\n" +
//            "\t\t\t\"defaultBehavior\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseasesymptomBefore\": [\"dd\"],\n" +
//            "\t\t\t\"diseasesymptomAfter\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseaseBefore\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseasemAfter\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"test\": \"ff\",\n" +
//            "\t\t\t\"operation\": \"ss\",\n" +
//            "\t\t\t\"syndrome\": \"\",\n" +
//            "\t\t\t\"medicine\": \"ee\",\n" +
//            "\t\t\t\"added\": \"ss\"\n" +
//            "\t\t},\n" +
//            "\t\t{\n" +
//            "\t\t\t\"name\": \"检验相关\",\n" +
//            "\t\t\t\"defaultBehavior\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseasesymptomBefore\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseasesymptomAfter\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseaseBefore\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"diseasemAfter\": [\"dd\",\"ww\"],\n" +
//            "\t\t\t\"test\": \"\",\n" +
//            "\t\t\t\"operation\": \"s\",\n" +
//            "\t\t\t\"syndrome\": \"s\",\n" +
//            "\t\t\t\"medicine\": \"ff\",\n" +
//            "\t\t\t\"added\": \"ddd\"\n" +
//            "\t\t}\n" +
//            "\t],\n" +
//            "\t\"ResultOfDamage\": \"一级甲等（死亡）\",\n" +
//            "\t\"InvolvedInstitute\": {\n" +
//            "\t\t\"City\": \"yz\",\n" +
//            "\t\t\"Zone\": \"hj\",\n" +
//            "\t\t\"Hospital\": \"er\",\n" +
//            "\t\t\"Department\": [\"g\",\"p\"]\n" +
//            "\t}\n" +
//            "}]";

//    @RegisterIMUtil
//    public void test1011(){
//        List<String> names=new ArrayList<>();
//        names.add("wj");
//        Map d = GetTitleAndAbstract.generateCaseTitleDetail(g, names);
//        int a;
//    }



    @Test
    public void mmmm(){
        disputeProgressService.updateUserChoose("1539604432004672067", "11,22222");
    }

    @Test
    public void mm(){
        DisputecaseProcess currentProcess=disputecaseProcessRepository.findByDisputecaseId("1539667777946795348");
        System.out.println(currentProcess.getStatus());
    }

    @Test
    public void mmmmm() throws Exception {
        System.out.println(getWorkingTimeUtil.calWorkingTime(new Date(),30));
        System.out.println(getWorkingTimeUtil.getResult(new Date()));
    }
}