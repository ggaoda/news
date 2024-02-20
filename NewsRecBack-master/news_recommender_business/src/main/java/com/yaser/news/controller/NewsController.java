package com.yaser.news.controller;

import com.yaser.news.dataEntity.NewsChannel;
import com.yaser.news.service.NewsService;
import com.yaser.news.service.UserNewsScoreService;
import com.yaser.news.service.dataWrap.NewsDetails;
import com.yaser.news.service.dataWrap.NewsSimple;
import com.yaser.news.service.dataWrap.UserNewsScoreWrap;
import com.yaser.news.utils.UseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(allowCredentials = "true", origins = "http://localhost:3000")
@RequestMapping(value = "/v1/news")
public class NewsController {
    private final UserNewsScoreService userNewsScoreService;

    private final NewsService newsService;

    @Autowired
    public NewsController(UserNewsScoreService userNewsScoreService, NewsService newsService) {
        this.userNewsScoreService = userNewsScoreService;
        this.newsService = newsService;
    }

    @PostMapping("/setUserNewsScore")
    @UseToken
    public UserNewsScoreWrap setUserNewsScore(@RequestParam double score, @RequestParam String newsId) {
        return userNewsScoreService.setUserNewsScore(score, newsId);
    }

    @GetMapping("/getNewsScore")
    @UseToken(must = false)
    public UserNewsScoreWrap getNewsScore(@RequestParam String newsId) {
        return userNewsScoreService.getUserNewsScore(newsId);
    }

    @GetMapping("/cleanData")
    public Map<String, Integer> cleanData() {
        return newsService.cleanData();
    }


    @GetMapping("")
    @UseToken(must = false)
    public NewsDetails getNewsById(@RequestParam String newsId) {
        return newsService.getNewsDetails(newsId);
    }

    @GetMapping("/getChannels")
    public List<NewsChannel> getChannels() {
        return newsService.getAllChannels();
    }

    @GetMapping("/getNewsListByChannelName")
    @UseToken(must = false)
    public Map<String, Object> getNewsListByChannelName(@RequestParam String channelName, @RequestParam int pageNum) {
        return newsService.getNewsListByChannelName(channelName, pageNum);
    }

    @GetMapping("/getHotNews")
    public List<NewsSimple> getHotNewsByChannel(@RequestParam String channelName) {
        return this.newsService.getRandomNews(8);
    }

    @GetMapping("/getSimilarNews")
    public List<NewsSimple> getSimilarNews(@RequestParam String targetId) {
        return this.newsService.getSimilarNews(targetId);
    }
}
