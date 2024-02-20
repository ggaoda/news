import time

import tensorflow.keras as keras
from tensorflow.python.keras.models import Model
from tensorflow.keras.layers import *
import jieba
import tensorflow as tf
import numpy as np
from common_module import *
from sina.models.newsEmbedding import NewsEmbedding
from sklearn.metrics.pairwise import cosine_similarity


class Encoder(Model):
    def __init__(self, vocab_size, embedding_matrix, units=None, **kwargs):
        super().__init__(**kwargs)
        if units is None:
            units = [400, 200]
        self.embedding_layer = Embedding(
            vocab_size,
            output_dim=200,
            mask_zero=True,
            trainable=True,
            weights=[embedding_matrix])
        self.gru_layer1 = GRU(units=units[0], return_sequences=True, return_state=True, dropout=0.25)
        self.gru_layer2 = GRU(units=units[1], return_sequences=True, return_state=True, dropout=0.25)
        self.bn_layer = BatchNormalization()

    def call(self, inputs):
        embed = self.embedding_layer(inputs)
        bn = self.bn_layer(embed)
        encoder_output_1, state_h_1 = self.gru_layer1(bn)

        encoder_output_2, state_h_2 = self.gru_layer2(encoder_output_1)

        return [encoder_output_2, encoder_output_1], [state_h_2, state_h_1]


class Decoder(Model):
    def __init__(self, vocab_size, embedding_matrix, units=None, **kwargs):
        super().__init__(**kwargs)
        if units is None:
            units = [200, 400]
        self.embedding_layer = Embedding(
            vocab_size,
            output_dim=200,
            trainable=True,
            mask_zero=True,
            weights=[embedding_matrix])
        self.gru_layer1 = GRU(units=units[0], return_sequences=True, return_state=True, dropout=0.25)
        self.gru_layer2 = GRU(units=units[1], return_sequences=True, return_state=True, dropout=0.25)
        self.bn_layer = BatchNormalization()

    def call(self, inputs, state, encoder_outputs):
        embed = self.embedding_layer(inputs)
        bn = self.bn_layer(embed)
        decoder_output_1, state_h_1 = self.gru_layer1(bn, initial_state=state[0])
        decoder_output_2, state_h_2 = self.gru_layer2(decoder_output_1, initial_state=state[1])
        return decoder_output_2, [state_h_1, state_h_2]


class NewsUtils(object):
    def __init__(self, load_model=True):
        print("加载用户字典数据。。。")
        jieba.load_userdict(get_val(DICT_PATH))
        print("用户字典数据加载完成。。。")
        self.word2idx = get_val(WORD2IDX)
        self.idx2word = get_val(IDX2WORD)
        self.word_embedding_martix = get_val(WORD_EMBEDDING_MATRIX)
        self.news_model = None
        self.max_len = config["modelConfig"]["titleMaxLen"]
        if load_model:
            self.load_news_model()

    def cut_titles(self, news_titles):
        print("标题分词处理...")
        cut_result = []
        for title in news_titles:
            cut_result.append(jieba.lcut(title.strip()))
        print("标题分词完成...")
        return cut_result

    def get_title_seq(self, cut_news_titles):
        title_seq = []
        for news_title in cut_news_titles:
            words_idx = [self.word2idx.get(word, 3) for word in news_title]
            title_seq.append(words_idx)

        title_seq = keras.preprocessing.sequence.pad_sequences(sequences=title_seq,
                                                               maxlen=self.max_len,
                                                               padding="post")

        return title_seq

    def cal_similar_news(self, doc_id):
        # 计算相似新闻
        target_news = list(NewsEmbedding.objects(doc_id=doc_id))[0]
        target_embedding = np.array(list(target_news["embedding"])).reshape((1, 200))
        last_time = int(time.time()) - config["modelConfig"]["similarNewsDay"]
        news_embedding_obj = list(NewsEmbedding.objects(create_time__gt=last_time))  # 只读取近N天的新闻，来计算相似度
        news_embedding_list = []
        for news_embedding in news_embedding_obj:
            news_embedding_list.append(list(news_embedding["embedding"]))
        # 计算余弦相似度，并将结果平铺
        news_similar_score = cosine_similarity(news_embedding_list, target_embedding).flatten()
        score_index = np.argsort(-news_similar_score)  # 获取索引排序，从大到小排序
        news_sorted_list = []
        for news_index in score_index[:30]:  # 取前 30 篇
            similar_doc_id = news_embedding_obj[news_index]["doc_id"]
            if doc_id != similar_doc_id:
                news_sorted_list.append(similar_doc_id)
        return news_sorted_list

    def generate_title_embedding(self, news_titles):
        assert self.news_model is not None
        print("获取模型编码部分")
        encoder = self.news_model.get_layer("encoder")
        cut_titles_seq = self.cut_titles(news_titles)
        print("获取标题分词embedding。。。")
        title_seq = self.get_title_seq(cut_titles_seq)
        print("获取标题分词embedding成功！")
        news_embedding = []
        print("获取标题分词embedding成功！")
        print("开始获取标题embedding。。。")
        index = 0
        for news_title in title_seq:
            print("编码标题：" + news_titles[index])
            index += 1
            _, encoder_state = encoder(tf.constant([news_title]))
            news_embedding.append(np.array(encoder_state[0]).reshape(200, ).tolist())
        print("获取标题embedding成功。。。")
        return news_embedding

    def load_news_model(self):
        print("开始加载新闻文本模型结构。。。")
        encoder_input = Input(shape=(self.max_len,), name="encoder_inputs")
        decoder_input = Input(shape=(self.max_len + 1,), name="decoder_inputs")
        vocab_size = len(self.word_embedding_martix)
        encoder_outputs, encoder_state = Encoder(vocab_size, embedding_matrix=self.word_embedding_martix,
                                                 name="encoder")(encoder_input)
        decoder_output, _ = Decoder(vocab_size, embedding_matrix=self.word_embedding_martix, name="decoder")(
            inputs=decoder_input,
            state=encoder_state,
            encoder_outputs=encoder_outputs)

        output = Dense(vocab_size, activation="softmax", name="output")(decoder_output)
        model = Model([encoder_input, decoder_input], [output])
        print("新闻文本模型结构加载完成。。。")
        print("开始加载新闻文本模型参数。。。")
        model.load_weights(get_val(MODEL_WEIGHTS_PATH))
        print("新闻文本模型参数加载完成。。。")
        self.news_model = model
