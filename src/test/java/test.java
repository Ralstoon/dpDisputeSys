import com.seu.DpdisputesysApplication;
import com.seu.form.HistoricTaskForm;
import com.seu.repository.DisputeInfoRepository;
import com.seu.service.DisputeProgressService;
import com.seu.service.INormalUserService;
import org.activiti.engine.TaskService;
import org.activiti.engine.test.Deployment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DpdisputesysApplication.class)
public class test {
    @Autowired
    private TaskService taskService;

    @Autowired
    private INormalUserService iNormalUserService;

    @Autowired
    private DisputeProgressService disputeProgressService;

    @Test
    @Deployment(resources = "processes/disputeProgress.bpmn")
    public void webServiceTaskTest(){
        System.out.println(taskService.createTaskQuery().taskCandidateGroup("mediator").list().size());
    }

    @Test
    public void findPhoneByuserIdTest(){
        System.out.println(iNormalUserService.findPhoneByUserId("1532004655415202278"));
    }

    @Test
    public void historicTaskListTest(){
        List<HistoricTaskForm> historicTaskFormList = disputeProgressService
                .getHistoricTaskListByDisputeId("1532413108579745686", 1, 10);
        System.out.println("asd");
    }

}
