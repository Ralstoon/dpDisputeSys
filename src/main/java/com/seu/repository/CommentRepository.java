package com.seu.repository;

import com.seu.domian.Comment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository {
    int addComment(@Param("comment_id")String comment_id, @Param("task_id")String task_id, @Param("comment") String comment, @Param("user_id") String user_id);
    Comment findCommentByTaskId(@Param("task_id") String task_id);
}
