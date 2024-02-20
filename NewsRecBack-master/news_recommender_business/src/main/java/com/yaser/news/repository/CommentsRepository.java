package com.yaser.news.repository;

import com.yaser.news.dataEntity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentsRepository extends MongoRepository<Comment, String> {
    List<Comment> findAllByDocId(String docId);


}
