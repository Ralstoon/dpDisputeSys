package com.seu.service.impl;

import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.Comment;
import com.seu.repository.CommentRepository;
import com.seu.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public ResultVO addComment(Comment comment) {
        commentRepository.save(comment);
        return ResultVOUtil.ReturnBack(123, "添加评价成功");
    }
}
