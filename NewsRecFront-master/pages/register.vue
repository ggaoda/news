<template>
  <div class="content-wrap">
    <div class="pc-wrap hidden-sm-and-down">
      <div class="icon-wrap-pc">
        <nuxt-link to="/">
          <div class="my-inline-div web-font-pingfang font-45 icon-text">NewsRec</div>
        </nuxt-link>
      </div>
      <div class="register-wrap pt-5 elevation-10">
        <div class="login-register-wrap ">
          <div>
            <nuxt-link class="grey--text" to="/login">登录</nuxt-link>
            ·
            注册
          </div>
        </div>
        <div class="form-wrap">
          <v-form v-model="valid" ref="form" lazy-validation>
            <v-text-field label="用户名" class="pt-1 px-3 text" v-model="user.nickname" :rules="nicknameRules"
                          prepend-icon="iconfont icon-yonghu" @keyup.enter="register"
                          required/>
            <v-text-field
              class="pt-1 px-3"
              prepend-icon="iconfont icon-email"
              v-model="user.email"
              :rules="emailRules"
              :error="emailError"
              :error-messages="emailMsg"
              label="邮箱"
              required
              @keyup.enter="register"
            />

            <v-text-field :type="show1?'text':'password'"
                          :append-icon="!show1?'mdi-eye' : 'mdi-eye-off'"
                          prepend-icon="iconfont icon-login_password"
                          class="pt-1 px-3"
                          v-model="password1"
                          :rules="passwordRules"
                          label="输入密码"
                          :error="passwordError"
                          :error-messages="errorMsg"
                          @click:append="show1=!show1"
                          @input="passwordStrength"
                          required
                          @keyup.enter="register"/>
            <v-text-field :type="show2?'text':'password'"
                          class="pt-1 px-3"
                          :append-icon="!show2?'mdi-eye' : 'mdi-eye-off'"
                          prepend-icon="iconfont icon-login_password"
                          v-model="password2"
                          :rules="passwordRules"
                          label="再次输入密码"
                          :error="passwordError"
                          :error-messages="errorMsg"
                          @click:append="show2=!show2"
                          @keyup.enter="register"
                          required/>
            <div class="action-wrap web-font-pingfang-thin">
              <v-progress-linear stream :value="strength" :color="strengthColor"/>
            </div>
            <div class="btn-wrap">
              <v-btn :disabled="!valid" @click="register" class="font-22 white--text" block large rounded color="green">
                注册
              </v-btn>
            </div>
          </v-form>

        </div>
      </div>
    </div>


  </div>
</template>

<script>

import UserApi from "@/API/UserApi";
import {Log} from "@/utils";
import userMix from "@/storeMix/userMix";

let $md5;
let $strength;
let $cookie;
export default {
  head: {
    title: "NewsRec - 注册"
  },
  layout: 'signIn',
  mixins: [UserApi, userMix],
  mounted() {
    $strength = require('zxcvbn');
    $md5 = require('js-md5');
    $cookie = require('js-cookie');

  },
  methods: {
    //处理密码强度
    passwordStrength() {
      this.flag = true
      let score = $strength(this.password1).score;
      let process;
      if (score === 0) {
        process = 0
      } else if (score === 1) {
        process = 25
      } else if (score === 2) {
        process = 50
      } else if (score === 3) {
        process = 75
      } else if (score === 4) {
        process = 100
      }
      this.changeProcess(score, process)
    },
    changeProcess(score, process) {
      let timer = setInterval(() => {
        if (this.strength === process || $strength(this.password1).score !== score) {
          clearInterval(timer)
        } else {
          if (this.strength > process) {
            this.strength--
          } else {
            this.strength++
          }
        }
      }, 15)
    },
    //用户注册
    async register() {
      let form = this.$refs.form

      if (form.validate() && this.valid) {
        if (this.strength < 25) {//如果密码强度太低，则进行提示用户加强
          this.$message.warning('密码太简单啦，加强一下吧！')
        } else {//通过验证后
          const user = await this.registerApi(this.user)
          Log(user)
          if (user) {
            this.loginSt(user)
            $cookie.set("Authorization", user.token)
            this.$router.push({path: `/`})
          }
        }
      }
    },
  },
  computed: {
    passwordError: function () {
      if (this.password1.length > 0 && this.password2.length > 0 && this.password1 !== this.password2) {
        this.errorMsg = '两次密码不一致';
        return true
      }
      this.errorMsg = '';
      this.user.password = this.password1;
      return false
    },

    emailError: function () {
      const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      if (typeof (this.user.email) === 'undefined' || pattern.test(this.user.email) || this.user.email.length === 0) {
        this.emailMsg = '';
        return false
      }
      this.emailMsg = '请输入正确的邮箱';
      return true
    },
    strengthColor: function () {
      if (!this.flag) {
        return 'grey'
      }
      if (this.strength <= 25) {
        return '#E74C3C'
      } else if (this.strength > 25 && this.strength <= 50) {
        return '#F1C40F'
      } else if (this.strength > 50 && this.strength <= 75) {
        return '#F39C12'
      } else if (this.strength > 75 && this.strength <= 100) {
        return '#28B463'
      }
    },
  },
  data: function () {
    return {
      valid: false,
      strength: 90,
      show1: false,
      show2: false,
      flag: false,
      user: {
        nickname: "yaser",
        password: "15250823423",
        gender: 0,
        email: "335767798@qq.com",
      },
      password1: '15250823423',
      password2: '15250823423',
      errorMsg: "",
      emailMsg: '',
      passwordRules: [
        v => !!v || '密码不为空',
        v => {
          if (typeof (v) === 'undefined') {
            return true
          }
          return v.length >= 8 || '密码不得少于8位'
        },
        v => {
          if (typeof (v) === 'undefined') {
            return true
          }
          return v.length <= 16 || '密码不得超过16位'
        }
      ],
      nicknameRules: [
        v => !!v || '用户名不为空',
        v => {
          if (typeof (v) === 'undefined') {
            return true
          }
          return v.length >= 3 || '用户名不得少于3位'
        },
        v => {
          if (typeof (v) === 'undefined') {
            return true
          }
          return v.length <= 10 || '用户名不得超过10位'
        }
      ],
      emailRules: [
        v => !!v || '邮箱不可为空'
      ]
    }
  }

}
</script>

<style scoped>
a {
  text-decoration: none;
}


.icon-wrap-pc {
  width: 100%;
  height: 100px;
  line-height: 100px;
  text-align: center;
}

.content-wrap {
  width: 100%;
  height: 100%;
}

.pc-wrap {
  width: 100%;
  height: 750px;
  margin-top: 5%;
}

.register-wrap {
  background-color: white;
  border-radius: 5px;
  height: 450px;
  width: 420px;
  margin-left: auto;
  margin-right: auto;
}

.btn-wrap {
  margin-top: 15px;
  width: 85%;
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
  color: #30304D;
}


.form-wrap {
  margin-top: 20px;
  margin-left: auto;
  margin-right: auto;
  width: 95%;
  text-align: center;
}


.action-wrap {
  margin-top: 10px;
  margin-left: auto;
  margin-right: auto;
  width: 85%;
  height: 20px;
}

</style>
