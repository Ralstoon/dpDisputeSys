package com.seu.utils;

import com.seu.common.EndDate;
import com.seu.domian.DisputecaseProcess;
import com.seu.repository.DisputecaseProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class  deadlineCalculate {
    @Autowired
    private DisputecaseProcessRepository disputecaseProcessRepository;

    @Scheduled(cron = "0 15 1 * * ?") //每天 1:15 自动执行
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

    @Scheduled(cron = "0 1 0 * * ?") //每天 0:01 自动执行
    public void setEndDate(Date endDate) throws Exception {
        EndDate.endDate = new GetWorkingTimeUtil().calWorkingTime(new Date(), 30);
    }
}
