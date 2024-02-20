package com.yaser.news.service.dataWrap;

import lombok.Data;

@Data

public class NewsDetails {
    private String docId;
    private String title;
    private Integer createTime;
    private String channelName;
    private String htmlContent;
    private String keywords = "无";
    private String mediaName;
    private Integer commentTotal;
    private Integer viewCount;
    private String author;
    private double newsScore;
    private double userScore;
    private int hotValue;
}
