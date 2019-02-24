package com.seu.utils;

import com.seu.common.EndDate;
import com.seu.common.VerifyCodeGlobal;
import com.seu.domian.DisputecaseProcess;
import com.seu.repository.DisputecaseProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class  deadlineCalculate {
    @Autowired
    private DisputecaseProcessRepository disputecaseProcessRepository;

    @Autowired
    private GetWorkingTimeUtil getWorkingTimeUtil;

    @Scheduled(cron = "0 1 0 * * ?") //每天 0:01 自动执行
    public void scheduled(){
        disputecaseProcessRepository.findAllByIsSuspended(1).stream().forEach(disputecaseProcess -> {
            Calendar c = Calendar.getInstance();
            c.setTime(disputecaseProcess.getEndtimeDisputecase());
            c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
            disputecaseProcess.setEndtimeDisputecase(c.getTime());
            disputecaseProcessRepository.save(disputecaseProcess);
        });
        disputecaseProcessRepository.findAllByIsSuspended(2).stream().forEach(disputecaseProcess -> {
            Calendar c = Calendar.getInstance();
            c.setTime(disputecaseProcess.getEndtimeDisputecase());
            c.add(Calendar.DAY_OF_MONTH, 1);// 今天+1天
            disputecaseProcess.setEndtimeDisputecase(c.getTime());
            disputecaseProcessRepository.save(disputecaseProcess);
        });
    }

    @Scheduled(cron = "0 10 0 * * ?") //每天 0:10 自动执行
    public void setEndDate() throws Exception {
        Date currentDate = new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        EndDate.endDate = getWorkingTimeUtil.calWorkingTime(currentDate, 30);
        Calendar c=Calendar.getInstance();
        c.setTime(currentDate);
        EndDate.isWeekday = new HashMap<>();
        for(int i = 0; i<60; i++){
            EndDate.isWeekday.put(sdf.parse(sdf.format(c.getTime())), getWorkingTimeUtil.getResult(c.getTime()));
            c.add(Calendar.DAY_OF_MONTH,1);
        }
        VerifyCodeGlobal.registerCode.clear();
        VerifyCodeGlobal.registerCode=null;
    }

}
