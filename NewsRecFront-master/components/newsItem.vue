<template>
  <div class="card-wrap div-center">
    <div class="card-top ">
      <div class="transition-500ms my-inline-div web-font-pingfang title-wrap font-20 text-more pl-1"
           @click="newsClick">
        <strong>{{ newsItem.title }}</strong>
      </div>
      <div class="my-inline-div web-font-pingfang-thin font-12 flag-wrap">
        <div class="text-center flag px-2">
          {{ newsItem.channelName }}
        </div>
      </div>
    </div>
    <div class="card-bottom">
      <div class="my-inline-div pr-4">
        <v-icon dense color="#7F8C8D" style="font-size:22px!important" class="my-inline-div">iconfont icon-news</v-icon>
        <div class="web-font-pingfang-thin font-12 my-inline-div icon-text">
          {{ newsItem.mediaName }}
        </div>
      </div>
      <div class="my-inline-div pr-4">
        <v-icon dense color="#7F8C8D" class="my-inline-div">iconfont icon-eye1</v-icon>
        <div class="web-font-pingfang-thin font-12 my-inline-div icon-text pl-1">
          {{ newsItem.viewCount }}
        </div>
      </div>
      <!--      <div class="my-inline-div pr-4">
              <v-icon dense color="#7F8C8D" class="my-inline-div">iconfont icon-discuss</v-icon>
              <div class="web-font-pingfang-thin font-12 my-inline-div icon-text pl-1">{{ newsItem.commentTotal }}</div>
            </div>-->
      <div class="my-inline-div pr-4">
        <v-icon dense color="#7F8C8D" style="font-size:18px!important;padding-top: 1px" class="my-inline-div">iconfont
          icon-riqi
        </v-icon>
        <div class="web-font-pingfang-thin font-12 my-inline-div icon-text pl-1">
          {{ time }}
        </div>
      </div>
      <div class="right mr-2">
        <v-icon color="#FF5D68" dense class="my-inline-div">iconfont icon-hot</v-icon>
        <div class="my-inline-div web-font-pingfang-thin font-12" style="color:#FF5D68;padding-top: 2px">{{ newsItem.hotValue }}</div>
      </div>
    </div>
    <div class="hr"></div>
  </div>
</template>

<script>
import {parseTime} from "@/utils";

export default {
  name: "newsItem",
  props: {
    newsItem: Object
  },
  computed: {
    time() {
      return parseTime(this.newsItem.createTime)
    }
  },
  methods: {
    newsClick() {
      this.$emit("newsClick", this.newsItem.docId)
    }
  }

}
</script>

<style scoped>
.card-wrap {
  width: 97%;
  padding: 8px;
}

.card-top {
  width: 100%;
  height: 40px;
  text-align: left;
}

.card-bottom {
  width: 100%;
  height: 25px;
  text-align: left;
  padding-top: 5px;
}

.title-wrap:hover {
  cursor: pointer;
  color: #039FFC;
}

.title-wrap {
  text-align: left;
  max-width: 85%;
  line-height: 40px;
  height: 40px;
}

.flag-wrap {
  width: 13%;
  height: 100%;
  float: right;
  /*padding-right: 5px;*/
  padding-top: 6px;
}


.flag {
  max-width: 85px;
  min-width: 50px;

  border-radius: 4px;
  border: 1px solid #039FFC;
  color: #039FFC;
  float: right;
  padding-top: 2px;
  margin-top: 3px;
  padding-bottom: 2px;
}

.icon-text {
  color: #7F8C8D;
  line-height: 20px;
}


</style>
