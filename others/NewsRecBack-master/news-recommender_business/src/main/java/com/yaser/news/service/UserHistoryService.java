package com.yaser.news.service;

import com.yaser.news.dataEntity.UserHistory;
import com.yaser.news.repository.UserHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserHistoryService {
    private final UserHistoryRepository userHistoryRepository;

    public UserHistoryService(UserHistoryRepository userHistoryRepository) {
        this.userHistoryRepository = userHistoryRepository;
    }

    public void addUserHistory(long uid, String newsId) {
        if (userHistoryRepository.existsByUidAndNewsId(uid, newsId)) {
            userHistoryRepository.deleteAllByUidAndNewsId(uid, newsId);
        }
        UserHistory userHistory = new UserHistory(uid, newsId, System.currentTimeMillis());
        userHistoryRepository.save(userHistory);
    }
}
