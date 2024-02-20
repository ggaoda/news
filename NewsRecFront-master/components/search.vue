<template>
  <div class="search-wrapper">
    <div class="search-text-wrapper my-inline-div ">
      <input v-model="searchText" placeholder="搜索站内新闻" type="text" class="left"></input>
    </div>
    <v-btn height="40px" width="80px" depressed style="margin-left:-4px" dark color="rgba(3,159,252,0.83)"
           @click="$emit('searchEvent',searchText)">
      <v-icon size="15" left>iconfont icon-search</v-icon>
      搜索
    </v-btn>
  </div>
</template>

<script>
export default {
  name: "search",
  data() {
    return {
      searchText: ""
    }
  }
}
</script>

<style scoped>
.search-wrapper {
  width: 100%;
  border-radius: 5px;
  height: 50px;
  padding: 5px
}

.search-text-wrapper {
  background-color: #FAFAFA;
  width: 270px;
  height: 40px;
  line-height: 40px;
  color: rgba(30, 34, 57, 0.32);
}

.search-text-wrapper input {
  height: 40px;
  line-height: 40px;
  width: 98%;
  padding-left: 15px;
}

:focus {
  outline: none !important;
}
</style>
