<template>
  <div class="hot-list-wrapper web-font-pingfang-thin">
    <div class="font-22">
      {{ hotList.title }}
    </div>
    <div class="hr"></div>
    <div class="transition-200ms hot-title-wrapper  font-14 web-font-pingfang "
         v-for="(news,index) in hotList.list"
         :key="news.docId" style="  line-height: 35px;height: 35px;">
      {{ index + 1 }}.
      <div class="my-inline-div title-wrapper text-more" @click="clickHot(news.docId)">
        {{ news.title }}
      </div>
      <div class="my-inline-div" style="width: 15%;text-align: right;">
        <v-icon small>iconfont icon-hot</v-icon>
        <span class="font-12">{{ news.hotValue }}</span>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: "hotList",
  props: {
    hotList: Object
  },
  methods: {
    clickHot(newsId) {
      this.$emit("newsClick", newsId)
    }
  }
}
</script>
<style>
.hot-title-wrapper:hover .v-icon {
  color: #FF5D68 !important;
  cursor: pointer;
}

.hot-title-wrapper:hover {
  color: #FF5D68 !important;
  cursor: pointer;
}
</style>
<style scoped>
.hot-list-wrapper {
  height: 340px;
  width: 100%;
  padding: 10px;
  border-radius: 7px;
  background-color: #FAFAFA;
  text-align: left;
  color: #1E2239;
}

.hot-title-wrapper {
  color: rgb(7, 31, 70);

}

.title-wrapper {
  width: 78%;
  /*background-color: #7F8C8D;*/
}
</style>
