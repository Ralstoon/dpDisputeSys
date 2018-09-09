package com.seu.utils;

import com.seu.common.InitConstant;
import com.seu.enums.DisputeProgressEnum;
import com.seu.exception.SetActivitiProcessException;
import com.seu.repository.DisputecaseActivitiRepository;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName VerifyProcessUtil
 * @Description TODO
 * @Author 吴宇航
 * @Date 2018/9/7 21:21
 * @Version 1.0
 **/

@Component
public class VerifyProcessUtil {

    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private DisputecaseActivitiRepository disputecaseActivitiRepository;


    public List<Task> verifyTask(String caseId,String currentProcess){
        String pid=disputecaseActivitiRepository.getOne(caseId).getProcessId();
        List<Task> tasks=taskService.createTaskQuery().processInstanceId(pid).list();
        int size=tasks.size();
        String strTasks="";
        for(int i=0;i<size;++i){
            strTasks+=tasks.get(i).getName()+"或";
        }

        strTasks=strTasks.substring(0,strTasks.length()-1);
        String[] temp=currentProcess.split(",");
        boolean flag=false;
        for(String s:temp){
            Pattern pattern=Pattern.compile(s);
            Matcher matcher=pattern.matcher(strTasks);
            if(matcher.find()){
                flag=true;
                break;
            }
        }
//        Pattern pattern=Pattern.compile(currentProcess);
//        Matcher matcher=pattern.matcher(strTasks);
        if(!flag){
            throw  new SetActivitiProcessException(DisputeProgressEnum.SETCURRENTPROCESS_FAIL.getCode(),String.format(InitConstant.currentProcess,strTasks));
        }else{
            return tasks;
        }
    }
}
