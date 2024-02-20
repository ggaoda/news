package com.yaser.news.repository;

import com.yaser.news.dataEntity.Labels;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LabelsRepository extends MongoRepository<Labels, String> {
    boolean existsByLabel(String label);

}
