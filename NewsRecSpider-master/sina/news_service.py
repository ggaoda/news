import random
from sina.models.userRecommenderList import UserRecommenderList
from recall_module import *


def generate_news_embedding(titles_obj):
    titles = []
    news_utils: NewsUtils = get_val(NEWS_UTILS)
    for title_obj in titles_obj:
        titles.append(title_obj["title"])
    title_embedding = news_utils.generate_title_embedding(titles)
    for index in range(len(titles)):
        news_embedding = NewsEmbedding()
        news_embedding["doc_id"] = titles_obj[index]["id"]
        news_embedding["embedding"] = title_embedding[index]
        news_embedding["create_time"] = titles_obj[index]["createTime"]
        news_embedding.save()


def load_news_embedding_by_K(user_history, k=7):
    """查询最近七天的新闻embedding数据，去除掉用户已看过的新闻"""
    last_time = int(time.time() - 24 * 3600 * k)
    recent_news = list(NewsEmbedding.objects(create_time__gt=last_time))
    # 去掉用户看过的新闻
    final_news = []
    user_history_news_id_set = set(user_history)

    for news in recent_news:
        if news["doc_id"] not in user_history_news_id_set:
            final_news.append(news)

    return final_news


def generate_user_recommender_list(user_info):
    # 使用多路召回策略生成用户候选集
    # 选取最近K天的embedding数据
    candidate_news_embedding_obj = load_news_embedding_by_K(user_info["history"])
    # 使用三路召回
    candidate_news_id_list = multi_recall(user_info, candidate_news_embedding_obj)
    # 读取热门新闻，将热门新闻数据也加入
    hot_news_list = rd.lrange(NEWS_HOT_VAL_LIST_RANKED, 0, min(500, rd.llen(NEWS_HOT_VAL_LIST_RANKED)))
    if hot_news_list is not None:
        candidate_news_id_set = set()
        for news_id in candidate_news_id_list:
            candidate_news_id_set.add(news_id)
        for hot_news_bytes in hot_news_list:
            print(type(hot_news_bytes))
            news_obj = json.loads(str(hot_news_bytes, "utf-8"))
            hot_news_id = news_obj["docId"]
            if hot_news_id not in candidate_news_id_set:
                candidate_news_id_list.append(hot_news_id)

    # 将推荐列表存入数据库中
    user_recommender_list = UserRecommenderList()
    user_recommender_list["user_id"] = int(user_info["userId"])
    user_recommender_list["recommender_list"] = candidate_news_id_list
    user_recommender_list.save()


def similar_news_list(doc_id):
    key = doc_id + "_similar"
    if rd.exists(key) > 0:
        print("存在类似")
        similar_news_list = json.loads(rd.get(key))
    else:
        print("不存在类似")
        news_utils: NewsUtils = get_val(NEWS_UTILS)
        similar_news_list = news_utils.cal_similar_news(doc_id)
        rd.set(key, json.dumps(similar_news_list), ex=config["DBConfig"]["redis"]["expires"])
    res = []
    idx = random.sample(range(0, 29), 5)
    for index in idx:
        res.append(similar_news_list[index])
    return res
