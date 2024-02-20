import json
import os
import redis
from mongoengine import connect

rd = None
config = None
_global_dict = {}
NEWS_HOT_VAL_LIST_RANKED = "newsHotListRanked"
NEWS_CHANNEL_TOP_K = "newsChannelTopK"
NEWS_HOT_MAP = "newsHotMap"
NEWS_UTILS = "news_utils"
WORD_EMBEDDING_PATH = "word_embedding_path"
MODEL_WEIGHTS_PATH = "model_weights_path"
DICT_PATH = "dict_path"
WORD_EMBEDDING_MATRIX = "word_embedding_matrix"
WORD2IDX = "word2idx"
IDX2WORD = "idx2word"
"""公用模块，只要用于加载配置和链接数据库等操作"""


def init_common():
    global rd, config
    print("加载配置文件")
    config_path = "./config.json"
    assert os.path.exists(config_path), "配置文件不存在！"
    with open(config_path, mode="r", encoding="utf-8") as file:
        config = json.load(file)
    print("连接数据库")
    rd = redis.StrictRedis(host=config["DBConfig"]["redis"]["host"], port=config["DBConfig"]["redis"]["port"], db=0)
    connect(config["DBConfig"]["mongoDB"]["name"])


def set_val(key: str, val: object):
    global _global_dict
    _global_dict[key] = val


def get_val(key):
    if _global_dict[key] is None:
        raise KeyError("{} is not exist".format(key))
    return _global_dict[key]


init_common()
