package com.seu.domian;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private String commentId;
    private String taskId;
    private String comment;
    private String userId;
    private Date createTime;

    public Comment() {
    }

    public Comment(String commentId, String taskId, String comment, String userId, Date createTime) {
        this.commentId = commentId;
        this.taskId = taskId;
        this.comment = comment;
        this.userId = userId;
        this.createTime = createTime;
    }
}
