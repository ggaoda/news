import time

import numpy as np
from common_module import *
from sina.models.newsEmbedding import NewsEmbedding
from sina.news_module import NewsUtils
from sklearn.metrics.pairwise import cosine_similarity


def multi_recall(user_info, candidate_news_embedding_obj):
    """主要根据兴起，历史记录以及高分新闻进行召回"""
    candidate_news_embedding_matrix = []
    for news_embedding_obj in candidate_news_embedding_obj:
        candidate_news_embedding_matrix.append(news_embedding_obj["embedding"])
    # 执行三路召回策略
    interesting_recall_idx_list = recall_by_interesting(user_info["labels"], candidate_news_embedding_matrix)
    history_recall_idx_list = recall_by_news(user_info["history"], candidate_news_embedding_matrix)
    high_news_recall_idx_list = recall_by_news(user_info["highNews"], candidate_news_embedding_matrix)

    candidate_news_score = np.zeros((len(candidate_news_embedding_matrix),))  # 候选新闻评分

    assert config["recallRate"]["userInteresting"] and config["recallRate"]["history"] \
           and config["recallRate"]["highRate"]
    # 计算每一个类别需要召回的数据量
    interesting_nums = int(len(interesting_recall_idx_list) * config["recallRate"]["userInteresting"])
    history_nums = int(len(history_recall_idx_list) * config["recallRate"]["history"])
    high_rate_nums = int(len(high_news_recall_idx_list) * config["recallRate"]["highRate"])
    # 将三个数组进行拼接，生成一个大的数组
    candidate_news_idx_list = interesting_recall_idx_list[:interesting_nums] \
                              + history_recall_idx_list[:history_nums] \
                              + high_news_recall_idx_list[:high_rate_nums]
    # 计算每一个候选集的分数
    for news_score in candidate_news_idx_list:
        candidate_news_score[news_score["index"]] += news_score["score"]
    # 排序
    final_news_idx = np.argsort(-candidate_news_score)
    assert config["recallRate"]["totalRecall"]

    candidate_news_id_list = []  # 候选新闻的id列表，顺序就是初步的推荐程度
    total_recall_num = int(len(final_news_idx) * config["recallRate"]["totalRecall"])
    for news_idx in final_news_idx[:total_recall_num]:
        candidate_news_id_list.append(candidate_news_embedding_obj[news_idx]["doc_id"])

    return candidate_news_id_list


def handle_news_score(news_score_list):
    # 对余弦相似度进行索引排序
    news_index_list = np.argsort(-news_score_list)
    # 将索引与余弦相似度进行包装
    result = []
    for news_index in news_index_list:
        result.append({"index": news_index, "score": news_score_list[news_index]})
    return result  # 返回指定召回率的新闻候选集的索引，以及对应的相似度值


def recall_by_news(source_news, candidate_news_embedding):
    """计算用户看过的新闻或者高评分新闻与候选新闻的相似度"""
    if len(source_news) == 0:
        return []
    # 查找该新闻的embedding
    news_embedding_list = list(NewsEmbedding.objects.filter(doc_id__in=source_news))
    news_embedding_matrix = []
    for news_embedding in news_embedding_list:
        news_embedding_matrix.append(list(news_embedding["embedding"]))

    news_feature_embedding = np.mean(news_embedding_matrix, axis=-2).reshape((1, 200))
    news_score_list = cosine_similarity(candidate_news_embedding, news_feature_embedding).flatten()

    return handle_news_score(news_score_list)


def recall_by_interesting(interesting_tags, candidate_news_embedding):
    # 基于用户标签来召回新闻
    if len(interesting_tags) == 0:
        # 如果该用户没有兴趣标签，则返回零向量
        return []
    user_interest_embedding_matrix = []  # 用户兴趣向量矩阵
    news_utils: NewsUtils = get_val(NEWS_UTILS)
    interesting_tags_cut = news_utils.cut_titles(interesting_tags)
    word2idx = get_val(WORD2IDX)
    word_embedding_matrix = get_val(WORD_EMBEDDING_MATRIX)
    for interesting_tag_cut_one in interesting_tags_cut:
        for interesting_tag in interesting_tag_cut_one:
            # 获得兴趣标签对应的词向量id
            idx = word2idx.get(interesting_tag, -1)
            if idx != -1:  # 如果词向量存在
                user_interest_embedding_matrix.append(word_embedding_matrix[idx])  # 将词向量添加到兴趣矩阵中
    # 对兴趣矩阵求平均值，得到用户兴趣embedding
    user_interest_embedding = np.mean(user_interest_embedding_matrix, axis=-2).reshape((1, 200))
    # 计算余弦相似度
    news_score_list = cosine_similarity(candidate_news_embedding, user_interest_embedding).flatten()
    return handle_news_score(news_score_list)


def cal_news_score(user_interest_embedding, user_history_embedding, user_history):
    user_embedding_matrix = np.array([user_interest_embedding, user_history_embedding]).reshape((200, 2))
    last_time = int(time.time()) - 60 * 60 * 24 * 5
    news_embedding_obj_list = list(NewsEmbedding.objects(create_time__gt=last_time))  # 获取最近一周的新闻
    news_embedding_list = []

    for news_embedding_obj in news_embedding_obj_list:
        # 剔除掉已看过的
        if news_embedding_obj["doc_id"] not in user_history:
            news_embedding_list.append(list(news_embedding_obj["embedding"]))

    news_score = np.sum(np.dot(news_embedding_list, user_embedding_matrix), axis=-1)
    score_index = np.argsort(-news_score)  # 获取索引排序
    news_sorted = []
    for news_index in score_index:
        news_sorted.append(news_embedding_obj_list[news_index]["doc_id"])
    return news_sorted
    # 按照顺序给score排序
