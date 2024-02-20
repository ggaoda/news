package com.yaser.news;

import com.mongodb.client.MongoClients;
import com.yaser.news.dataEntity.News;
import com.yaser.news.dataEntity.NewsChannel;
import com.yaser.news.dataEntity.UserNewsScore;
import com.yaser.news.repository.NewsRepository;
import com.yaser.news.repository.UserNewsScoreRepository;
import com.yaser.news.service.dataWrap.NewsSimple;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

@SpringBootTest
class NewsRecommenderBusinessApplicationTests {
    MongoOperations mongoOpsSource = new MongoTemplate(MongoClients.create(), "news_db");
    MongoOperations mongoOpsTarget = new MongoTemplate(MongoClients.create(), "news_recommender");
    private final NewsRepository newsRepository;
    private final UserNewsScoreRepository userNewsRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    NewsRecommenderBusinessApplicationTests(NewsRepository newsRepository, UserNewsScoreRepository userNewsRepository, RedisTemplate redisTemplate) {
        this.newsRepository = newsRepository;
        this.userNewsRepository = userNewsRepository;
        this.redisTemplate = redisTemplate;
    }

    @Test
    void copyDB() {
        List<News> newsList = mongoOpsSource.find(new Query(), News.class);
        mongoOpsTarget.dropCollection(News.class);
        newsList.forEach(news -> {
            Random random = new Random();
            if (news.getChannelName() != null && news.getChannelName().contains("新浪")) {
                news.setChannelName(news.getChannelName().replace("新浪", ""));
            }
            news.setViewCount(random.nextInt(1000));
            news.setCommentTotal(random.nextInt(500));
            mongoOpsTarget.insert(news);
        });
    }

    @Test
    void clearData() {
        List<News> newsList = mongoOpsTarget.find(new Query(), News.class);
        newsList.forEach(news -> {
            if (news.getChannelName() == null) {
                news.setChannelName("其它");
                mongoOpsTarget.save(news);
            }
        });
    }

    @Test
    void createChannelTable() {
        if (mongoOpsTarget.collectionExists(NewsChannel.class)) {
            mongoOpsTarget.dropCollection(NewsChannel.class);
        }
        mongoOpsTarget.createCollection(NewsChannel.class);
        List<News> newsList = mongoOpsTarget.find(new Query(), News.class);
        List<NewsChannel> channelNamesList = new ArrayList<>();
        HashMap<String, Integer> channels = new HashMap<>();
        channels.put("推荐", newsList.size());
        channels.put("其它", 0);

        newsList.forEach(news -> {
            String channelName = news.getChannelName();
            if (channelName != null) {
                channels.put(channelName, channels.getOrDefault(channelName, 0) + 1);
            } else {
                channels.put("其它", channels.get("其它"));
            }
        });
        channels.forEach((k, v) -> {
            if (v >= 10) {
                NewsChannel newsChannel = new NewsChannel(k, v);
                channelNamesList.add(newsChannel);
            }
        });
        channelNamesList.sort((o1, o2) -> o1.getCount() > o2.getCount() ? -1 : 0);
        channelNamesList.forEach(channel -> {
            mongoOpsTarget.insert(channel);
        });
    }

    @Test
    void testHotValue() {
        //新闻热度计算公式：(初始热度(100*ln(总数/该类别数))+互动热度(评论*5+浏览*1+用户总评分))/e^(0.6*小时数)
        String newsId = "comos-kkntiam5750077";
        News news = this.newsRepository.findById(newsId).get();
        long nums = this.newsRepository.count();
        long channelCounts = this.newsRepository.countAllByChannelName(news.getChannelName());
        double scoreSum = this.userNewsRepository.findAllByNewsId(newsId).stream().mapToDouble(UserNewsScore::getScore).sum();
        double S0 = 100 * Math.log(nums + .0 / channelCounts) + news.getCommentTotal() * 5 + news.getViewCount() + scoreSum;
//        int diffHour = (int) ((System.currentTimeMillis() - news.getCreateTime()) / 3600);
        for (int diffHour = 0; diffHour < 24; diffHour++) {
            int hotValue = (int) (S0 / (Math.exp(0.5 * diffHour)));
            System.out.println(hotValue);
        }
    }

    @Test
    void testRedis() {

    }
/*
    private void setChannelHot() {
        //计算所有新闻的热度
        HashMap<String, Integer> newsHotVal = new HashMap<>();
        HashMap<String, PriorityQueue<NewsSimple>> channelTop10 = new HashMap<>();
        List<News> newsList = this.newsRepository.findAll();
        newsList.forEach(news -> {
            newsHotVal.put(news.getDocId(), calculateNewsHotValue(news));
            val queue = channelTop10.getOrDefault(news.getChannelName(), new PriorityQueue<>(10));
            if (queue.size() >10){
                queue.removeAt(11);
                queue.removeIf()
            }

        });

    }*/

    private int calculateNewsHotValue(News news) {
        //新闻热度计算公式：(初始热度(100*ln(总数/该类别数))+互动热度(评论*5+浏览*1+用户总评分))/e^(0.5*小时数)
        long nums = this.newsRepository.count();
        long channelCounts = this.newsRepository.countAllByChannelName(news.getChannelName());
        double scoreSum = this.userNewsRepository.findAllByNewsId(news.getDocId()).stream().mapToDouble(UserNewsScore::getScore).sum();
        double S0 = 100 * Math.log(nums + .0 / channelCounts) + news.getCommentTotal() * 5 + news.getViewCount() + scoreSum;
//        int diffHour = (int) ((System.currentTimeMillis() - news.getCreateTime()) / 3600);
        int diffHour = new Random().nextInt(8);//设置一个随机数
        return (int) (S0 / (Math.exp(0.5 * diffHour)));
    }
}

