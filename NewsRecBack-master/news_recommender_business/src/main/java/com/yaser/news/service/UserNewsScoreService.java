package com.yaser.news.service;

import com.yaser.news.constant.ResultCode;
import com.yaser.news.controller.globalHandler.APIException;
import com.yaser.news.dataEntity.RecUser;
import com.yaser.news.dataEntity.UserNewsScore;
import com.yaser.news.repository.UserNewsScoreRepository;
import com.yaser.news.service.dataWrap.UserNewsScoreWrap;
import com.yaser.news.utils.ServiceContextHolder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserNewsScoreService {
    private final UserNewsScoreRepository userNewsRepository;

    public UserNewsScoreService(UserNewsScoreRepository userNewsRepository) {
        this.userNewsRepository = userNewsRepository;
    }

    public UserNewsScoreWrap setUserNewsScore(double score, String newsId) {
        RecUser recUser = ServiceContextHolder.getContext().getRecUser();
        if (this.userNewsRepository.existsByUidAndNewsId(recUser.getUid(), newsId)) {
            throw new APIException(ResultCode.USER_NEWS_SCORE_EXIST);
        }
        UserNewsScore userNewsScore = new UserNewsScore(recUser.getUid(), newsId, score);
        this.userNewsRepository.save(userNewsScore);
        //返回新闻评分和用户评分

        return new UserNewsScoreWrap(score, this.getNewsScore(newsId));
    }

    public UserNewsScoreWrap getUserNewsScore(String newsId) {
        double userScore = 0;
        if (ServiceContextHolder.hasLogin()) {
            RecUser recUser = ServiceContextHolder.getContext().getRecUser();
            val userNewsScore = this.userNewsRepository.findByUidAndNewsId(recUser.getUid(), newsId);
            if (userNewsScore != null) {
                userScore = userNewsScore.getScore();
            }
        }

        return new UserNewsScoreWrap(userScore, this.getNewsScore(newsId));
    }

    private double getNewsScore(String newsId) {
        List<UserNewsScore> userNewsScoreList = this.userNewsRepository.findAllByNewsId(newsId);
        double newsScore = userNewsScoreList.stream().mapToDouble(UserNewsScore::getScore).sum();
        if (newsScore != 0) {
            newsScore /= userNewsScoreList.size();
        }
        return newsScore;
    }
}
