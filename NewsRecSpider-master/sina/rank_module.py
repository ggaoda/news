import tensorflow.keras as keras
from tensorflow.python.keras.models import Model
from tensorflow.keras.layers import *


class UserEmbedding(Model):
    def __init__(self, h_param, news_embedding_matrix, labels_embedding, *args, **kwargs):
        super().__init__(*args, **kwargs)
        # 接受用户历史记录
        self.news_embedding = Embedding(
            h_param["news_size"],
            output_dim=h_param["news_embedding_dim"],
            trainable=False,
            mask_zero=True,
            weights=[news_embedding_matrix])
        self.history_layer = GRU(units=h_param["gru_units"][0], dropout=0.2, return_sequences=True)
        self.high_score_layer = Dense(units=400, activation="selu",
                                      kernel_initializer=keras.initializers.lecun_uniform())
        self.merge_news_layer = Dense(units=200, activation="selu",
                                      kernel_initializer=keras.initializers.lecun_uniform())
        # self.history_layer_2 = GRU(units=h_param["gru_units"][1], dropout=0.35)
        # 接受用户标签
        self.label_embedding = Embedding(
            h_param["label_size"],
            output_dim=h_param["user_label_dim"],
            trainable=False,
            mask_zero=True,
            weights=[labels_embedding])
        self.label_layer = Dense(units=h_param["label_dense_units"][0], activation="selu",
                                 kernel_initializer=keras.initializers.lecun_uniform())
        self.mean_layer = Average()
        # self.label_layer_2 = Dense(units=h_param["label_dense_units"][1], activation="selu",
        #                            kernel_initializer=keras.initializers.lecun_uniform())

        # 生成用户embedding
        self.user_feature_layer = Dense(units=h_param["user_embedding"], activation="selu",
                                        kernel_initializer=keras.initializers.lecun_uniform())

        self.bn = BatchNormalization()
        self.connect = Concatenate()

    def call(self, history_inputs, interesting_inputs, high_score_inputs):
        # 获得用户历史新闻embedding序列
        history_emb = self.news_embedding(history_inputs)
        history_bn = self.bn(history_emb)  # 正则化处理
        history_out = self.history_layer(history_bn)  # 提取用户历史新闻特征
        # 获得用户历史新闻embedding序列
        high_score_news_emb = self.news_embedding(high_score_inputs)
        high_score_news_bn = self.bn(high_score_news_emb)  # 正则化处理
        high_score_out = self.high_score_layer(high_score_news_bn)  # 提取用户高评分新闻特征
        connect_out = self.connect([history_out, high_score_out])  # 新闻特征拼接
        merge_out = self.merge_news_layer(connect_out)  # 新闻特征融合
        # 获取用户兴趣标签embedding序列
        interesting_emb = self.label_embedding(interesting_inputs)
        interesting_bn = self.bn(interesting_emb)  # 正则化处理
        interesting_mean_out = self.mean_layer(interesting_bn)  # 计算兴趣均值
        connect_out2 = self.connect([merge_out, interesting_mean_out])  # 新闻融合特征与用户兴趣特征拼接
        user_feature_out = self.user_feature_layer(connect_out2)  # 融合得到用户特征
        return user_feature_out


class UserClickProbModel(Model):
    def __init__(self, h_param, news_embedding_matrix, label_embedding_matrix, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.news_embedding = Embedding(
            h_param["news_size"],
            output_dim=h_param["news_embedding_dim"],
            trainable=False,
            mask_zero=True,
            weights=[news_embedding_matrix])
        self.bn = BatchNormalization()
        self.user_embedding = UserEmbedding(h_param, news_embedding_matrix, label_embedding_matrix)
        self.connect = Concatenate()
        self.dense_1 = Dense(units=h_param["click_dense_units"][0], activation="selu")
        self.dense_2 = Dense(units=h_param["click_dense_units"][1], activation="selu")

    def call(self, history_inputs, candidate_news, label_inputs):
        candidate_emb = self.news_embedding(candidate_news)
        user_emb = self.user_embedding(history_inputs=history_inputs, label_inputs=label_inputs)
        candidate_bn = self.bn(candidate_emb)
        user_bn = self.bn(user_emb)
        connect_out = self.connect([candidate_bn, user_bn])
        dense_out = self.dense_1(connect_out)
        bn = self.bn(dense_out)
        dense_out = self.dense_2(bn)
        bn = self.bn(dense_out)
        return bn


def get_wide_deep_model(h_params, news_embedding_matrix, labels_embedding_matrix):
    history_input = Input(shape=(h_params["user_history_size"],), name="history_input")
    label_input = Input(shape=(h_params["user_label_size"],), name="label_input")
    candidate_input = Input(shape=(h_params["news_size"],), name="candidate_news_input")
    userClickProbModel = UserClickProbModel(h_params, news_embedding_matrix, labels_embedding_matrix)
    prob_out = userClickProbModel(history_input, candidate_input, label_input)
    output = Dense(units=1, activation="sigmoid", name="output")(prob_out)
    model = Model([history_input, label_input, candidate_input], [output])
    return model


def rank_candidate(candidate_news_list, user_info):
    pass
