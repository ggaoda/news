<template>

</template>

<script>

import ApiHelper from "@/API/ApiHelper";

export default {
  name: "NewsApi",
  extends: ApiHelper,
  data() {
    return {
      newsBaseUrl: "/v1/news",
    }
  },
  methods: {
    getUserNewsScoreApi(newsId) {
      return this.get(this.newsBaseUrl + "/getNewsScore", {newsId: newsId})
    },
    setUserScoreApi(newsId, score) {
      return this.post(this.newsBaseUrl + "/setUserNewsScore", {
        newsId: newsId,
        score: score
      })
    },
    getChannelsApi() {
      return this.get(this.newsBaseUrl + "/getChannels")
    },
    getNewsByChannelApi(channelName, pageNum) {
      return this.get(this.newsBaseUrl + "/getNewsListByChannelName", {channelName: channelName, pageNum: pageNum})
    },
    getNewsByIdApi(newsId) {
      return this.get(this.newsBaseUrl, {newsId: newsId})
    },
    getSimilarNewsApi(newsId) {
      return this.get(this.newsBaseUrl + "/getSimilarNews", {targetId: newsId})
    },
    getHotNewsApi(channelName) {
      return this.get(this.newsBaseUrl + "/getHotNews", {channelName: channelName})
    }

  }
}
</script>

<style scoped>

</style>
