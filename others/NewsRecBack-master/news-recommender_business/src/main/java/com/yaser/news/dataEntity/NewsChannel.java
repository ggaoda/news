package com.yaser.news.dataEntity;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@Document(collection = "news_channel")
public class NewsChannel {
    @Field(name = "channel_name")
    private String channelName;
    @Field(name = "count")
    private int count;


}
