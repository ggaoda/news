package com.yaser.news.service;

import com.mongodb.client.MongoClients;
import com.yaser.news.controller.globalHandler.APIException;
import com.yaser.news.constant.ResultCode;
import com.yaser.news.dataEntity.News;
import com.yaser.news.dataEntity.NewsChannel;
import com.yaser.news.dataEntity.RecUser;
import com.yaser.news.dataEntity.UserNewsScore;
import com.yaser.news.repository.NewsChannelRepository;
import com.yaser.news.repository.NewsRepository;
import com.yaser.news.repository.UserNewsScoreRepository;
import com.yaser.news.service.dataWrap.NewsDetails;
import com.yaser.news.service.dataWrap.NewsSimple;
import com.yaser.news.service.dataWrap.PageData;
import com.yaser.news.utils.ServiceContextHolder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.yaser.news.constant.ResultCode.CHANNEL_NOT_EXIST;

@Slf4j
@Service
public class NewsService {
    private final NewsRepository newsRepository;
    private final NewsChannelRepository newsChannelRepository;
    private final static int PAGE_SIZE = 10;
    private final MongoOperations newsRecommender = new MongoTemplate(MongoClients.create(), "news_recommender");
    MongoOperations newsDb = new MongoTemplate(MongoClients.create(), "news_db");

    private final UserHistoryService userHistoryService;
    private final UserNewsScoreRepository userNewsRepository;
    private final RedisTemplate redisTemplate;

    @Autowired
    public NewsService(NewsRepository newsRepository, NewsChannelRepository newsChannelRepository, UserHistoryService userHistoryService, UserNewsScoreRepository userNewsRepository, RedisTemplate redisTemplate) {
        this.newsRepository = newsRepository;
        this.newsChannelRepository = newsChannelRepository;
        this.userHistoryService = userHistoryService;
        this.userNewsRepository = userNewsRepository;
        this.redisTemplate = redisTemplate;
    }

    //读取page所有信息，并提取出数据
    private void iteratorPage(Page<News> newsPage, int pageNum, HashMap<String, Object> result) {
        PageData<News> pageData = new PageData<>(newsPage, pageNum);
        result.put("page", pageData);
        List<NewsSimple> newsSimpleList = new ArrayList<>();
        newsPage.forEach(news -> {
            var newsSimple = new NewsSimple();
            BeanUtils.copyProperties(news, newsSimple);
            newsSimple.setHotValue(this.calculateNewsHotValue(news));
            newsSimpleList.add(newsSimple);
        });
        result.put("newsList", newsSimpleList);
    }


    public Map<String, Integer> cleanData() {
        List<News> newsList = newsDb.find(new Query(), News.class);
        List<NewsChannel> newsChannelList = newsChannelRepository.findAll();
        val newsChannelDict = new HashMap<String, NewsChannel>();
        newsChannelList.forEach(newsChannel -> {
            newsChannelDict.put(newsChannel.getChannelName(), newsChannel);
        });

        newsList.forEach(news -> {
            Random random = new Random();
            String channelName = news.getChannelName();

            if (channelName != null && channelName.length() > 0 && channelName.length() < 5) {
                if (channelName.contains("新浪")) {
                    news.setChannelName(channelName.replace("新浪", ""));
                }
            } else {
                //空的channel或者channel过长都统一为 “其它”
                news.setChannelName("其它");
            }
            newsChannelDict.put(channelName, newsChannelDict.getOrDefault(channelName, new NewsChannel(channelName, 1)));
            news.setViewCount(random.nextInt(1000));
            news.setCommentTotal(random.nextInt(500));
            newsRecommender.insert(news);
        });
        newsDb.dropCollection(News.class);//删除原始数据
        List<NewsChannel> channelNamesList = new ArrayList<>();
        newsChannelDict.forEach((k, v) -> channelNamesList.add(v));
        channelNamesList.sort((o1, o2) -> o1.getCount() > o2.getCount() ? -1 : 0);
        channelNamesList.forEach(newsRecommender::insert);
        HashMap<String, Integer> res = new HashMap<>();
        res.put("processCount", newsList.size());
        return res;
    }

    public NewsDetails getNewsDetails(String docId) {
        if (!this.newsRepository.existsById(docId)) {
            throw new APIException(ResultCode.NEW_NOT_EXIST);
        }
        Optional<News> newsRes = this.newsRepository.findById(docId);
        if (newsRes.isEmpty()) {
            throw new APIException(ResultCode.NEW_NOT_EXIST);
        }
        News news = newsRes.get();
        news.setViewCount(news.getViewCount() + 1);//浏览记录加一
        if (ServiceContextHolder.hasLogin()) {
            RecUser recUser = ServiceContextHolder.getContext().getRecUser();//直接从线程中读取信息
            userHistoryService.addUserHistory(recUser.getUid(), news.getDocId());//添加用户浏览记录
        }
        news = this.newsRepository.save(news);
        NewsDetails newsDetails = new NewsDetails();
        BeanUtils.copyProperties(news, newsDetails);
        newsDetails.setHotValue(this.calculateNewsHotValue(news));
        return newsDetails;
    }


    public List<NewsChannel> getAllChannels() {
        return this.newsChannelRepository.findAll();
    }

    public Map<String, Object> getNewsListByChannelName(String channelName, int pageNum) {
        if (!channelName.equals("推荐") && !this.newsRepository.existsByChannelName(channelName))
            throw new APIException(CHANNEL_NOT_EXIST);
        Page<News> newsPage;
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");
        if (pageNum < 1) pageNum = 1;
        var result = new HashMap<String, Object>();
        if (channelName.equals("推荐")) {
            //暂时先用全部数据代替
            int maxPageNum = (int) Math.ceil((double) this.newsRepository.count() / PAGE_SIZE);
            if (pageNum > maxPageNum) pageNum = maxPageNum;
            newsPage = this.newsRepository.findAll(PageRequest.of(pageNum - 1, PAGE_SIZE, sort));
        } else {
            int maxPageNum = (int) Math.ceil((double) this.newsRepository.countAllByChannelName(channelName) / PAGE_SIZE);
            if (pageNum > maxPageNum) pageNum = maxPageNum;
            newsPage = this.newsRepository.findAllByChannelName(PageRequest.of(pageNum - 1, PAGE_SIZE, sort), channelName);
            log.info(newsPage.toString());
        }
        this.iteratorPage(newsPage, pageNum, result);
        return result;
    }

    public List<NewsSimple> getSimilarNews(String newsId) {
        return this.getRandomNews(5);
    }

    private void setChannelHot() {
        //计算所有新闻的热度
        HashMap<String, Integer> newsHotVal = new HashMap<>();
        HashMap<String, PriorityQueue<NewsSimple>> channelTop10 = new HashMap<>();


    }

/*    public List<NewsSimple> getHotNewsByChannel(String channelName) {

    }*/

    public List<NewsSimple> getRandomNews(int num) {
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.sample(num));

        val aggregationResults = newsRecommender.aggregate(aggregation, "news", News.class);
        List<NewsSimple> newsList = new ArrayList<>();
        aggregationResults.forEach(news -> {
            NewsSimple newsSimple = new NewsSimple();
            BeanUtils.copyProperties(news, newsSimple);
            newsSimple.setHotValue(this.calculateNewsHotValue(news));
            newsList.add(newsSimple);
        });
        return newsList;
    }

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
