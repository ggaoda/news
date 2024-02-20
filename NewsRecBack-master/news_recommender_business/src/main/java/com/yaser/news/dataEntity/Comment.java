package com.yaser.news.dataEntity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection = "comment")
public class Comment {
    @MongoId
    @Field(name = "comment_id")
    private  String commentId;
    private   String content;
    private  long uid;
    @Field(name = "doc_id")
    private   String docId;
    private   long time;
    @Field(name = "agree_nums")
    private   int agreeNums;

}
