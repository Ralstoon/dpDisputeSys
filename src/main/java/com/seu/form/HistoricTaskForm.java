package com.seu.form;

import lombok.Data;

import java.util.Date;

@Data
public class HistoricTaskForm {
    private String disputeId;
    private String taskName;
    private Date createTime;
    private Date completeTime;
    private String taskId;
}
