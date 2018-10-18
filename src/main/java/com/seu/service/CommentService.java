package com.seu.service;

import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.domian.Comment;

public interface CommentService {
    ResultVO addComment(Comment comment);
}
