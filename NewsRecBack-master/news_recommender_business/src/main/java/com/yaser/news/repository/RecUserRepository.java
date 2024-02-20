package com.yaser.news.repository;

import com.yaser.news.dataEntity.RecUser;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecUserRepository extends MongoRepository<RecUser, Long> {
    boolean existsByEmail(String email);

    RecUser findByEmail(String email);

    RecUser findByUid(long uid);

}
