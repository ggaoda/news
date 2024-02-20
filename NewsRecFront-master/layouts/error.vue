<template>
  <v-app class="background">
    <v-layout row justify-center wrap>
      <v-flex md4 xs12 class="text-md-center " style="margin-top: 13vh">
        <v-img transition="scale-transition" src="/img/Other/404.jpg" aspect-ratio="1.718"/>
        <div class="font-10 mt-4 wrong-text">阿勒，出错了。。。。</div>
        <div class="mt-5 wrong-action">
          <v-btn text color="green" @click="back"><span class="font-3">返回上一页</span></v-btn>
          <v-btn color="blue" outlined nuxt to="/"><span class="font-3">返回首页</span></v-btn>
        </div>
      </v-flex>
    </v-layout>
  </v-app>
</template>

<script>

  export default {
    props: ['error'],
    name: 'error',

    methods: {
      back() {
        this.$router.go(-1)
      },
    }
  }
</script>

<style scoped>
  .background {
    background-color: #F3F3F3 !important;
    width: 100%;
  }

  .font-10 {
    font-size: 4vh;
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
  }

  .wrong-text {
    width: 100%;
    text-align: center;
    color: grey;
  }

  .wrong-action {
    width: 100%;
    text-align: center;
  }
</style>
