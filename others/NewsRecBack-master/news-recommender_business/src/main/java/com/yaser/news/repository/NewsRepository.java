package com.yaser.news.repository;

import com.yaser.news.dataEntity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface NewsRepository extends MongoRepository<News, String> {
    Page<News> findAll(Pageable pageable);

    boolean existsByChannelName(String channelName);

    long countAllByChannelName(String channelName);

    Page<News> findAllByChannelName(Pageable pageable, String channelName);


}
