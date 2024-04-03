package com.yaser.news.service.dataWrap;


import lombok.Data;

import java.io.Serializable;

@Data
public class NewsSimple implements Comparable<NewsSimple>, Serializable {
    private String docId;
    private String title;
    private Integer createTime;
    private String channelName;
    private String intro;
    private String mediaName;
    private Integer commentTotal;
    private Integer viewCount;
    private String author;
    private int hotValue;


    @Override
    public int compareTo(NewsSimple o) {
        return o.hotValue - hotValue;
    }
}
