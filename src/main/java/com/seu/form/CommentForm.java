package com.seu.form;

import lombok.Data;

import java.util.Date;

@Data
public class CommentForm {
    String comment;
    String taskId;
    String taskName;
    Date createtime;

    public CommentForm(String comment, String taskId, String taskName, Date createtime) {
        this.comment = comment;
        this.taskId = taskId;
        this.taskName = taskName;
        this.createtime = createtime;
    }
}
