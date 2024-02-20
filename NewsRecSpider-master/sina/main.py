from gensim.models import KeyedVectors
from common_module import *
import os
import numpy as np
from controller_module import app
from sina.models.newsEmbedding import NewsEmbedding
from sina.news_module import NewsUtils


def load_word_embedding():
    print("加载word_embedding数据。。。。")
    # 判断文件是否存在
    word_embedding_path = get_val(WORD_EMBEDDING_PATH)
    assert os.path.exists(word_embedding_path), "{} is not exist".format(word_embedding_path)
    limit = None
    if config["embeddingConfig"]["limit"]:
        print("开启embedding限制")
        limit = config["embeddingConfig"]["wordLimit"]

    wordVec = KeyedVectors.load_word2vec_format(fname=word_embedding_path, binary=False, limit=limit)
    wordVec.init_sims(replace=True)
    index = 4
    # 生成embedding矩阵
    word_embedding_matrix = np.random.randn(len(wordVec.vocab.keys()) + index, 200)
    word2idx = {"<PAD>": 0, "<START>": 1, "<END>": 2, "<UNK>": 3}
    idx2word = {v: k for (k, v) in word2idx.items()}
    # 读取文件数据中的embedding到embedding矩阵中
    for word in wordVec.vocab.keys():
        word_embedding_matrix[index] = wordVec[word]
        word2idx[word] = index
        idx2word[index] = word
        index += 1
    print("word_embedding加载完成。。。。")
    return word_embedding_matrix, word2idx, idx2word


def load_news_embedding():
    print("加载新闻Embedding数据")
    news_embedding_list = list(NewsEmbedding.objects())
    news_embedding_matrix = []
    news_id2idx = {}
    idx2news_id = {}
    idx = 0
    for news_embedding in news_embedding_list:
        news_embedding_matrix.append(news_embedding["embedding"])
        news_id2idx[news_embedding["doc_id"]] = idx
        idx2news_id[idx] = news_embedding["doc_id"]
        idx += 1
    print("新闻Embedding数据加载完成")
    return news_embedding_matrix, news_id2idx, idx2news_id


def load_embeddings(load_embedding):
    """加载词向量以及新闻向量"""
    print("开始加载Embedding数据")
    if load_embedding:
        word_embedding_matrix, word2idx, idx2word = load_word_embedding()
    else:
        print("取消加载embedding")
        word_embedding_matrix, word2idx, idx2word = "", "", ""
    set_val(WORD_EMBEDDING_MATRIX, word_embedding_matrix)
    set_val(WORD2IDX, word2idx)
    set_val(IDX2WORD, idx2word)
    # todo 新闻embedding数据的加载，等wide&deep完成后加入
    # _news_embedding_matrix, _news_id2idx, _idx2news_id = load_news_embedding()
    print("Embedding数据加载完成")


def load_models(load_model):
    print("开始加载模型。。。")
    news_utils = NewsUtils(load_model)
    set_val(NEWS_UTILS, news_utils)
    # TODO wide&deep 待加入
    # wide_deep_model = get_wide_deep_model()


def init_all(load_embedding=True, load_model=True):
    print("开始初始化。。。")
    # 加载路径
    word_embedding_path = config["dataPath"]["WORD_EMBEDDING_PATH"]
    model_weights_path = config["dataPath"]["MODEL_WEIGHTS_PATH"]
    dict_path = config["dataPath"]["DICT_PATH"]
    set_val(WORD_EMBEDDING_PATH, word_embedding_path)
    set_val(MODEL_WEIGHTS_PATH, model_weights_path)
    set_val(DICT_PATH, dict_path)

    # 加载embedding数据
    load_embeddings(load_embedding)
    # 加载模型数据
    load_models(load_model)
    print("初始化完成")


if __name__ == '__main__':
    init_all(load_model=False, load_embedding=False)
    app.run(port=config["hostPort"])
