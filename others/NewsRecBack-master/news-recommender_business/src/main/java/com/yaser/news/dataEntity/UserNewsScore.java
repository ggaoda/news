package com.yaser.news.dataEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@Document(collection = "user_news_score")
@AllArgsConstructor
public class UserNewsScore {
    private long uid;

    @Field(name = "news_id")
    private String newsId;

    private double score;
}
