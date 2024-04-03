package com.yaser.news.controller;

import com.yaser.news.service.CommentService;
import com.yaser.news.service.dataWrap.UserCommentWrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:3000")
@RequestMapping(value = "/v1/comments")
public class CommentsController {

    private final CommentService commentService;

    public CommentsController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("")
    public List<UserCommentWrap> getUserCommentList(@RequestParam String docId) {
        return commentService.getCommentListByDocId(docId);
    }
}
