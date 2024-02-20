package com.yaser.news.dataEntity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;


@Data
@Document(collection = "news")
public class News {
    @MongoId
    private String docId;

    private String title;

    @Field(name = "create_time")
    private int createTime;

    @Field(name = "channel_name")
    private String channelName;

    private String url;

    @Field(name = "html_content")
    private String htmlContent;

    @Field(name = "text_content")
    private String textContent;

    private String intro;

    @Field(name = "media_name")
    private String mediaName;

    private String keywords = "无";

    @Field(name = "comment_id")
    private String commentId;

    @Field(name = "comment_total")
    private int commentTotal;

    @Field(name = "view_count")
    private int viewCount;

    private String author;

}
