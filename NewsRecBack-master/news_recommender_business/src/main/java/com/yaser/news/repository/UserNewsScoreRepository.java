package com.yaser.news.repository;

import com.yaser.news.dataEntity.UserHistory;
import com.yaser.news.dataEntity.UserNewsScore;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserNewsScoreRepository extends MongoRepository<UserNewsScore, Long> {
    boolean existsByUidAndNewsId(long uid, String newsId);

    List<UserNewsScore> findAllByNewsId(String newsId);

    UserNewsScore findByUidAndNewsId(long uid, String newsId);
}
