<template>

</template>

<script>
  export default {
    name: "userMix",
    computed: {
      userStore: function () {
        return this.$store.state;
      },
      userSt: function () {
        return this.userStore.user;
      },
      isLoginSt: function () {
        return this.userStore.isLogin;
      }
    },
    methods: {
      commit(name, data) {
        this.$store.commit(`${name}`, data)
      },
      loginSt(user) {
        this.commit("login", user)
      },
      logoutSt() {
        this.commit("logout")
      }
    }
  }
</script>

<style scoped>

</style>
