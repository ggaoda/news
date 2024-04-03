package com.yaser.news.repository;

import com.yaser.news.dataEntity.NewsChannel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewsChannelRepository  extends MongoRepository<NewsChannel, String> {
}
