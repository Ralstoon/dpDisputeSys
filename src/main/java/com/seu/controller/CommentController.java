package com.seu.controller;

import com.seu.ViewObject.ResultVO;
import com.seu.ViewObject.ResultVOUtil;
import com.seu.common.Const;
import com.seu.domian.Comment;
import com.seu.domian.NormalUser;
import com.seu.enums.DisputeProgressEnum;
import com.seu.service.CommentService;
import com.seu.utils.DisputeProcessReturnMap;
import org.activiti.engine.HistoryService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/comment")
@CrossOrigin
public class CommentController {
    @Autowired
    private CommentService commentService;

    @PostMapping(value="/addComment")
    public ResultVO temporaryConfirm(HttpSession session,
                                     @RequestParam(value = "task_id") String taskId,
                                     @RequestParam(value = "comment") String comment){
        //todo 身份认证
        String userId=((NormalUser)session.getAttribute(Const.CURRENT_USER)).getUserId();
        commentService.addComment(taskId,comment,userId);

        return ResultVOUtil.ReturnBack(DisputeProgressEnum.ADD_TASKCOMMIT_SUCCESS.getCode(), DisputeProgressEnum.ADD_TASKCOMMIT_SUCCESS.getMsg());
    }
}
