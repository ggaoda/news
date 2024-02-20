<template>
  <div class="news-wrapper">
    <div class="border-wrap">
      <div class="nav-wrapper">
        <v-breadcrumbs
          :items="navItem"
          divider=">"
        ></v-breadcrumbs>
        <v-btn absolute right top icon dark color="green" @click="back">
          <v-icon small>iconfont icon-fanhui</v-icon>
        </v-btn>
        <div class="web-font-pingfang-thin font-22 " style="text-align:left;margin-left: 24px">
          {{ news.title }}
        </div>
        <div class="web-font-pingfang-thin font-12 info-wrapper">
          <div class="my-inline-div">
            {{ time }}
          </div>
          <div class="mx-3 my-inline-div">
            来源：{{ news.mediaName }}
          </div>
          <div class="my-inline-div" style="line-height:30px">
            <v-icon small class="mb-1">iconfont icon-eye1</v-icon>
            <div class="my-inline-div">{{ news.viewCount }}</div>
          </div>
          <div class="right mr-2">
            <v-icon color="#FF5D68" small class="mb-1">iconfont icon-hot</v-icon>
            <div class="my-inline-div web-font-pingfang-thin font-12" style="color:#FF5D68;">381</div>
          </div>
        </div>
      </div>
      <div class="web-font-pingfang-thin mt-3 font-16 markdown-body" v-html="news.htmlContent"></div>
    </div>
    <div class="key-score-wrapper web-font-pingfang-thin">
      <div style="height:50px;">
        <div class="left">
          评分：
          <v-rating
            v-model="newsRating.userScore"
            :readonly="readOnly"
            background-color="grey"
            class="my-inline-div"
            color="red accent-1"
            dense
            half-increments
            hover
            @input="setUserScore"
            size="18"
          ></v-rating>
        </div>
        <div class="score-wrapper">
          新闻价值：{{ newsRating.newsScore > 0 ? newsRating.newsScore : "评分太少，暂不显示" }}
        </div>
      </div>
      <div>
        关键字：{{ news.keywords }}
      </div>
    </div>
    <div class="similar-wrapper div-center web-font-pingfang-thin">
      <div class="font-22 my-inline-div">相似推荐</div>
      <v-btn absolute right text color="#FF5D68" rounded small @click="getSimilarNews">
        <v-icon small left>iconfont icon-shuaxin1</v-icon>
        换一批
      </v-btn>
      <div class="mt-2">
        <div v-for="(news,index) in similarNews" @click="clickNews(index)"
             class="transition-200ms font-14 text-more web-font-pingfang title-wrapper">
          {{ index + 1 }}. {{ news.title }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {Log, parseTime} from "@/utils";
import NewsApi from "@/API/NewsApi";
import userMix from "@/storeMix/userMix";

export default {
  name: "newsContent",
  props: {news: Object},
  mixins: [NewsApi, userMix],
  methods: {
    back() {
      this.$emit("back");
    },
    clickNews(index) {
      this.$emit("newsClick", this.similarNews[index].docId)
    },
    async setUserScore() {
      if (this.isLoginSt) {
        let newsRating = await this.setUserScoreApi(this.news.docId, this.newsRating.userScore)
        if (newsRating) {
          this.newsRating = newsRating;
          this.readOnly = true
        }
      } else {
        this.$message.warning("请登录后再进行评分！")
      }

    },
    init() {
      let self = document.getElementsByClassName('appendQr_wrap')[0];
      if (self) {
        let parent = self.parentElement;
        parent.removeChild(self);
      }
      this.getSimilarNews()
      this.getNewsScore()
    },
    async getNewsScore() {
      if (this.news.docId) {
        let newsRating = await this.getUserNewsScoreApi(this.news.docId)
        if (newsRating) {
          this.newsRating = newsRating;
          this.readOnly = newsRating.userScore > 0
        }
      }
    },
    async getSimilarNews() {
      if (this.news.docId)
        this.similarNews = await this.getSimilarNewsApi(this.news.docId)
    }

  },
  data() {
    return {

      readOnly: false,
      newsRating: {
        userScore: 0,
        newsScore: 0
      },
      similarNews: []
    }
  },
  computed: {
    time() {
      return parseTime(this.news.createTime)
    },
    navItem() {
      return [
        {
          text: "首页",
          disabled: false,
          href: "/",
          nuxt: true
        },
        {
          text: this.news.channelName,
          disabled: false,
          href: "/?channelName=" + this.news.channelName,
          nuxt: true
        },
        {
          text: "正文",
          disabled: true
        }
      ]
    }
  },

  mounted() {
    this.init()
    this.$watch("news", this.init, {deep: true})
  }
}
</script>

<style scoped>
.markdown-body {
  line-height: 28px;
  padding-left: 13px;
  padding-right: 13px;
  text-align: left;
}

.nav-wrapper {
  width: 100%;
  position: relative;
}

.info-wrapper {
  color: #7F8C8D;
  text-align: left;
  margin-left: 24px;
  height: 30px;
  line-height: 30px;
}

.key-score-wrapper {
  width: 100%;
  line-height: 50px;
  text-align: left;
  color: #071F46;
  padding: 10px;
}

.score-wrapper {
  float: right;
  color: rgba(7, 31, 70, 0.7);
}

.similar-wrapper {
  margin-top: 10px;
  padding: 10px 40px;
  text-align: left;
  width: 100%;
  background: #FAFAFA;
  border-radius: 7px;
  color: #071F46;
  height: 230px;
  position: relative;
}

.title-wrapper:hover {

  color: #039FFC !important;
  cursor: pointer;
}

.title-wrapper {
  color: rgba(7, 31, 70, 0.7);
  line-height: 35px;
  height: 35px;
  /*background-color: #7F8C8D;*/
}

</style>
<style>
@import "~/assets/style/markdown.css";

.article-editor {
  text-align: right;
  right: 0
}
</style>
