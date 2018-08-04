import com.seu.DpdisputesysApplication;
import com.seu.domian.Comment;
import com.seu.domian.NormalUser;
import com.seu.form.HistoricTaskForm;
import com.seu.repository.CommentRepository;
import com.seu.repository.NormalUserRepository;
import com.seu.service.DisputeProgressService;
import com.seu.service.UserService;
import org.activiti.engine.HistoryService;
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
    private UserService iNormalUserService;

    @Autowired
    private DisputeProgressService disputeProgressService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private NormalUserRepository normalUserRepository;

    @Autowired
    private HistoryService historyService;

    @Test
    @Deployment(resources = "processes/disputeProgress.bpmn")
    public void webServiceTaskTest(){
        System.out.println(taskService.createTaskQuery().taskCandidateGroup("mediator").list().size());
    }

    @Test
    public void findPhoneByuserIdTest(){
//        System.out.println(iNormalUserService.findPhoneByUserId("1532004655415202278"));
    }

    @Test
    public void historicTaskListTest(){
        List<HistoricTaskForm> historicTaskFormList = disputeProgressService
                .getHistoricTaskListByDisputeId("1532413108579745686");
        System.out.println("asd");
    }

    @Test
    public void addCommentTest(){
        commentRepository.addComment("123","123","qwe","123");
    }

    @Test
    public void findCommentByTaskIdTest(){
        Comment comment = commentRepository.findCommentByTaskId("123");
//        NormalUser normalUser = normalUserRepository.findNormalUserByUserId("1532005285945799016");
        System.out.println(comment.getComment());
        //historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey("s").singleResult().
    }
}
