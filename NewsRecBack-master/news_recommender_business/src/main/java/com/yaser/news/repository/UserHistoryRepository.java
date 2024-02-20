package com.yaser.news.repository;

import com.yaser.news.dataEntity.UserHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserHistoryRepository extends MongoRepository<UserHistory, Long> {

    void deleteAllByUidAndNewsId(long uid, String newsId);

    boolean existsByUidAndNewsId(long uid, String newsId);

    int countByUid(long uid);
}
