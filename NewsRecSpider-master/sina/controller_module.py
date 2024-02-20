from flask import Flask, request
from response_entry import *
from spider_service import *
from multiprocessing import Process
from news_service import *

app = Flask(__name__)


@app.route("/runSpider")
def run_spider():
    spider_name = request.args.get("spiderName")
    crawl_num = request.args.get("crawlNum")
    if crawl_num is not None:
        crawl_num = int(crawl_num)
    p = Process(target=start_spider, args=(spider_name, crawl_num))
    p.start()
    return success()


@app.route("/spiderStatus")
def get_spider_status():
    spider_name = request.args.get("spiderName")
    spider_info = json.loads(rd.get(SPIDER_INFO(spider_name)))
    return success(spider_info)


@app.route("/setNewsEmbedding", methods=["POST"])
def set_news_embedding():
    titles_obj = json.loads(request.get_data(), encoding="utf-8")
    generate_news_embedding(titles_obj=titles_obj)
    return success()


@app.route("/setUserRecommenderList", methods=["POST"])
def set_user_recommender_list():
    """生成一个用户的推荐列表"""
    user_info = json.loads(request.get_data(), encoding="utf-8")
    generate_user_recommender_list(user_info)
    return success()


@app.route("/getSimilarNewsList")
def get_similar_news_list():
    doc_id = request.args.get("docId")
    res = similar_news_list(doc_id)
    return success({"similarNews": res})
