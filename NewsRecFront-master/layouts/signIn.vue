<template>
  <v-app id="back">
    <nuxt/>
  </v-app>
</template>

<script>
export default {

  data: function () {
    return {}
  },
}
</script>

<style scoped>

#back {
  background-image: url('/img/Login/background.jpg');
  background-size: cover;
  width: 100%;
  overflow: hidden;
  background-attachment: fixed;
}

</style>
