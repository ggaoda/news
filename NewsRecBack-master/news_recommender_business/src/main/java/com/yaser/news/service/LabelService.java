package com.yaser.news.service;

import com.mongodb.client.MongoClients;
import com.yaser.news.constant.ResultCode;
import com.yaser.news.controller.globalHandler.APIException;
import com.yaser.news.dataEntity.Labels;
import com.yaser.news.dataEntity.RecUser;
import com.yaser.news.repository.LabelsRepository;
import com.yaser.news.repository.NewsChannelRepository;
import com.yaser.news.repository.RecUserRepository;
import com.yaser.news.service.dataWrap.UserInfoWrap;
import com.yaser.news.utils.ServiceContextHolder;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LabelService {
    private final RecUserRepository recUserRepository;
    private final LabelsRepository labelsRepository;
    private final NewsChannelRepository channelRepository;

    private final MongoOperations newsDB = new MongoTemplate(MongoClients.create(), "news_recommender");

    public LabelService(RecUserRepository recUserRepository, LabelsRepository labelsRepository, NewsChannelRepository channelRepository) {
        this.recUserRepository = recUserRepository;
        this.labelsRepository = labelsRepository;
        this.channelRepository = channelRepository;
    }

    public List<String> getLabels(int num) {
        if (!newsDB.collectionExists(Labels.class)) {
            newsDB.createCollection(Labels.class);
            //将channel数据存入到label中
            channelRepository.findAll().stream().filter(item -> !item.getChannelName().equals("推荐"))
                    .forEach(channelName -> labelsRepository.save(new Labels(channelName.getChannelName())));
        }
        val labels = new ArrayList<String>();
        log.info(String.valueOf(num));
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.sample(num));
        val aggregationResults = newsDB.aggregate(aggregation, "labels", Labels.class);

        aggregationResults.forEach(label -> {
            labels.add(label.getLabel());
            log.info(label.getLabel());
        });

        return labels;
    }

    public UserInfoWrap setUserLabels(List<String> labels) {
        if (labels.size() > 8) {
            throw new APIException(ResultCode.LABELS_TOO_MUCH);
        }
        labels.forEach(label -> {
            //保存用户自定义的数据
            if (label.length() > 4) {
                throw new APIException(ResultCode.LABEL_TOO_LONG);
            }
            if (!labelsRepository.existsByLabel(label)) {
                labelsRepository.save(new Labels(label));
            }
        });
        RecUser recUser = ServiceContextHolder.getContext().getRecUser();//直接从线程中读取信息
        recUser.setLabels(labels);
        this.recUserRepository.save(recUser);
        UserInfoWrap userInfoWrap = new UserInfoWrap();
        BeanUtils.copyProperties(recUser, userInfoWrap);
        return userInfoWrap;
    }


}
