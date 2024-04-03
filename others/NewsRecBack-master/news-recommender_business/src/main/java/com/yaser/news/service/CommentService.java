package com.yaser.news.service;

import com.yaser.news.controller.globalHandler.APIException;
import com.yaser.news.constant.ResultCode;
import com.yaser.news.dataEntity.Comment;
import com.yaser.news.dataEntity.RecUser;
import com.yaser.news.repository.CommentsRepository;
import com.yaser.news.repository.NewsRepository;
import com.yaser.news.repository.RecUserRepository;
import com.yaser.news.service.dataWrap.UserCommentWrap;
import com.yaser.news.service.dataWrap.UserInfoWrap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CommentService {
    private final CommentsRepository commentsRepository;
    private final NewsRepository newsRepository;
    private final RecUserRepository recUserRepository;

    @Autowired
    public CommentService(CommentsRepository commentsRepository, NewsRepository newsRepository, RecUserRepository recUserRepository) {
        this.commentsRepository = commentsRepository;
        this.newsRepository = newsRepository;
        this.recUserRepository = recUserRepository;
    }

    public List<UserCommentWrap> getCommentListByDocId(String docId) {
        if (!newsRepository.existsById(docId)) throw new APIException(ResultCode.NEW_NOT_EXIST);

        List<Comment> commentList = commentsRepository.findAllByDocId(docId);
        List<UserCommentWrap> userCommentWraps = new ArrayList<>();
        commentList.forEach(comment -> {
            RecUser recUser = recUserRepository.findByUid(comment.getUid());
            if (recUser != null) {
                UserInfoWrap userInfoWrap = new UserInfoWrap();
                BeanUtils.copyProperties(recUser, userInfoWrap);
                userCommentWraps.add(new UserCommentWrap(userInfoWrap, comment));
            }
        });
        return userCommentWraps;
    }
}

