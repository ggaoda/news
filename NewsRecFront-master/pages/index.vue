<template>
  <div class="page-wrap">
    <div class="nav-wrap  ">
      <navigation ref="nav" @changeChannel="handleChannelChangeEvent"></navigation>
    </div>
    <div class="content-wrap ">
      <transition name="fade">
        <div :key="1" v-if="showList" class="border-wrap">
          <newsItem @newsClick="newsClick" v-for="(news,index) in newsList" :key="index" :news-item="news"></newsItem>
          <v-skeleton-loader
            v-show="showLoading"
            class="mb-1"
            light
            type="list-item-two-line"
          ></v-skeleton-loader>
        </div>
        <div :key="2" v-else>
          <newsContent @newsClick="newsClick" @back="back" :news="news"></newsContent>
          <!--显示新闻内容-->
        </div>
      </transition>
    </div>
    <div class="other-wrap ">
      <search @searchEvent="getSearch"></search>
      <loginCard class="mt-5"></loginCard>
      <hotList class="mt-5" @newsClick="newsClick" :hotList="hotList"></hotList>
    </div>
  </div>
</template>

<script>

import {Log} from "@/utils";
import NewsApi from "@/API/NewsApi";

export default {
  head: {
    title: "NewsRec - 首页"
  },
  data() {
    return {
      page: {
        pageNum: 1
      },
      showList: false,
      showLoading: false,
      channelName: "推荐",
      newsList: [],
      ticking: false,
      news: {},
      hasAdd: false,
      history: [],

      hotList: {
        title: "新闻热榜",
        list: []
      }
    }
  },
  mixins: [NewsApi],
  async mounted() {
    const channelName = this.$route.query.channelName
    if (channelName) {
      Log(channelName)
      if (this.$refs.nav.allChannels.includes(channelName)) {
        this.$refs.nav.activeChannel(channelName)
        this.page.pageNum = 1
      } else {
        // await this.$router.push("/404")
      }
    } else {
      await this.handleChannelChangeEvent()
    }
  },
  destroyed() {
    this.handleScrollEvent(false)
  },
  methods: {

    handleScrollEvent(add = true) {
      if (add) {
        if (!this.hasAdd) {
          window.addEventListener("scroll", this.monitorScroll)
          this.hasAdd = true
        }
      } else {
        if (this.hasAdd) {
          window.removeEventListener("scroll", this.monitorScroll)
          this.hasAdd = false
        }
      }
    },
    async newsClick(newsId) {
      this.handleScrollEvent(false)
      let curRec
      Log("当前高度：", window.pageYOffset)
      if (this.showList) {
        curRec = {
          state: -1,//1为新闻间切换，0为列表间切换，，-1为新闻到列表的切换
          scroll: window.pageYOffset,
          channelName: this.channelName
        }
      } else {
        curRec = {
          state: 1,
          scroll: window.pageYOffset,
          news: this.news,
          channelName: this.news.channelName
        }
      }
      this.history.push(curRec)
      this.news = await this.getNewsByIdApi(newsId)
      this.$refs.nav.activeChannel(this.news.channelName)
      this.showList = false
      window.scrollTo({top: 0})
    },
    back() {
      const record = this.history.pop()
      if (record.state === 1) {
        this.news = record.news
      } else {
        this.handleScrollEvent(true)
        this.showList = true
      }
      this.$refs.nav.activeChannel(record.channelName, record.state)
      setTimeout(() => {
        Log("高度：", document.body.scrollHeight, "滚动到：", record.scroll)
        window.scrollTo({top: record.scroll})
      }, 100)
    },
    getSearch(searchText) {
      if (searchText.length > 0) {
        Log(searchText)
        //todo 调用搜索接口
      }
    },
    monitorScroll() {
      if (!this.ticking) {
        window.requestAnimationFrame(() => {
          this.ticking = false

          const rest = document.body.scrollHeight - window.innerHeight - window.pageYOffset
          if (rest < 50) {
            this.page.pageNum++
            this.getChannelList()
          }
        })
        this.ticking = true
      }
    },
    async changeHostListByChannel(channelName) {
      if (channelName) {
        this.channelName = channelName
      }
      if (this.channelName === "推荐") {
        this.hotList.title = "新闻热榜"
      } else {
        this.hotList.title = this.channelName + "热榜"
      }
      this.hotList.list = await this.getHotNewsApi(this.channelName)
    },
    async getChannelList(clearList) {
      this.showList = true
      this.showLoading = true
      let newsData = await this.getNewsByChannelApi(this.channelName, this.page.pageNum)
      Log(newsData)
      if (newsData) {
        this.page = newsData.page
        if (clearList) {
          this.newsList = []
        }
        newsData.newsList.forEach(newsItem => {
          this.newsList.push(newsItem)
        })
        this.showLoading = false
        this.handleScrollEvent()

      }
    },
    async handleChannelChangeEvent(channelName = "推荐", clearList = false, state = 0) {
      this.channelName = channelName
      Log(state)
      if (state === 0) {
        //显示列表
        await this.getChannelList(clearList)
      }
      await this.changeHostListByChannel(channelName)
    }
  }

}
</script>
<style scoped>
.page-wrap {
  width: 85%;
  margin-top: 20px;
  margin-left: auto;
  margin-right: auto;
  text-align: center;
  padding-bottom: 30px;
  /*background-color: #1b1f23;*/
  /*height: 2000px*/
}

.nav-wrap {
  width: 180px;
  left: 8%;
  /*background-color: blue;*/
  position: fixed;
  text-align: center;
}

.other-wrap {
  width: 360px;
  margin-left: 5px;
  position: fixed;
  right: 7%;
  top: 20px;
  /*background-color: red;*/
}

.content-wrap {
  width: calc(100% - 400px - 170px);
  margin-left: 200px;
  /*background-color: grey;*/
}

</style>
