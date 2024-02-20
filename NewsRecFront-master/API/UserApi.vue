<template>

</template>

<script>

import ApiHelper from "@/API/ApiHelper";

export default {
  name: "UserApi",
  extends: ApiHelper,
  data() {
    return {
      baseUrl: "/v1/user",
    }
  },
  methods: {
    registerApi(user) {
      return this.post(this.baseUrl + "/register", user)
    },
    loginApi(user, isAdmin = false) {
      user.isAdmin = isAdmin
      return this.post(this.baseUrl + "/login", user)

    }
  }

}
</script>

<style scoped>

</style>
