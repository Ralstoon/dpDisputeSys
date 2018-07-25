package com.seu.service.impl;

import com.seu.repository.CommentRepository;
import com.seu.service.CommentService;
import com.seu.utils.KeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public int addComment(String task_id, String comment,String user_id) {
        String comment_id = KeyUtil.genUniqueKey();
        commentRepository.addComment(comment_id,task_id,comment,user_id);
        return 1;
    }
}
