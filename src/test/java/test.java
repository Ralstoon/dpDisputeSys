import com.alibaba.fastjson.JSONObject;
import com.seu.DpdisputesysApplication;
import com.seu.domian.*;
import com.seu.form.HistoricTaskForm;
import com.seu.repository.*;
import com.seu.service.DisputeProgressService;
import com.seu.service.UserService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.test.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.*;

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
}