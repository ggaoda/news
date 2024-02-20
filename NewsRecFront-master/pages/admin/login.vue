<template>
  <div class="content-wrap">
    <div class="pc-wrap">
      <div class="icon-wrap">
        <div class="my-inline-div web-font-pingfang font-45 icon-text">RecNews后台登录</div>
      </div>
      <div class="login-wrap pt-4 mt-3  elevation-4">
        <div class="form-wrap">
          <v-form v-model="valid" ref="form" lazy-validation>
            <v-text-field
              class="pt-2 px-3"
              prepend-icon="account_circle"
              v-model="user.email"
              label="用户名"
              required
            />

            <v-text-field
              class="pt-3 px-3"
              :type="show?'text':'password'"
              :append-icon="show?'visibility_off':'visibility'"
              prepend-icon="lock"
              v-model="user.password"
              :rules="passwordRules"
              label="密码"
              @click:append="show=!show"
              required
              @keyup.enter="login"
            />
          </v-form>
          <div class="btn-wrap">
            <v-btn :disabled="!valid" block depressed large class="white-text web-font-pingfang-thin font-30 "
                   color="light-blue "
                   @click="login">登录
            </v-btn>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>


import UserApi from "@/API/UserApi";
import {Log} from "@/utils";
import userMix from "@/storeMix/userMix";

export default {
  head: {
    title: "RecNews - 管理员登录"
  },
  mixins: [UserApi, userMix],
  data: function () {
    return {
      user: {
        email: "admin",
        password: ""
      },
      valid: false,
      show: false,
      passwordRules:
        [v => !!v || '密码不可为空']
    }
  },
  mounted() {
    this.valid = false;
  },
  methods: {
    async login() {
      const form = this.$refs.form;
      if (form.validate() && this.valid) {
        const user = await this.loginApi(this.user, true)
        if (user) {
          Log(user)
          if (user.role === "ADMIN") {
            this.loginSt(user)
            const $cookie = require('js-cookie');
            $cookie.set("AdminAuthorization", user.token)
            this.$router.push({path: `/admin`})
          }
        }
      }
    }
  }
}
</script>
<style scoped>
a {
  text-decoration: none;
}

.icon-wrap {
  width: 100%;
  height: 110px;
  line-height: 110px;
  text-align: center;
}

.content-wrap {
  width: 100%;
  height: 100%;
  background-color: #34495e;

}

.pc-wrap {
  width: 100%;
  height: 100%;
  padding-top: calc(50vh - 280px);
}

.login-wrap {
  background-color: white;
  border-radius: 10px;
  height: 230px;
  width: 400px;
  margin-left: auto;
  margin-right: auto;
}


.btn-wrap {
  margin-top: 10px;
  width: 370px;
  margin-left: auto;
  margin-right: auto;
}

.login-register-wrap {
  width: 100%;
  height: 50px;
  text-align: center;
  font-size: 44px;
}

.icon-text {
  color: white;
}

.form-wrap {
  margin-left: auto;
  margin-right: auto;
  width: 390px;
  text-align: center;
}

</style>
