<template>
  <div class="content-wrap">
    <div class="pc-wrap hidden-sm-and-down">
      <div class="icon-wrap">
        <nuxt-link to="/">
          <div class="my-inline-div web-font-pingfang font-45 icon-text">NewsRec</div>
        </nuxt-link>
      </div>
      <div class="login-wrap pt-5 elevation-10">
        <div class="login-register-wrap ">
          <div>登录·
            <nuxt-link class="grey--text" to="/register">注册</nuxt-link>
          </div>
        </div>
        <div class="form-wrap">
          <v-form v-model="valid" ref="form" lazy-validation>
            <v-text-field
              class="pt-1 px-3"
              prepend-icon="iconfont icon-email"
              v-model="user.email"
              :rules="emailRules"
              :error="emailError"
              :error-messages="emailMsg"
              label="邮箱"
              required
              @keyup.enter="login"
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
            <div class="action-wrap web-font-pingfang-thin">
              <v-checkbox
                class="check-wrap "
                color="#40A1FA"
                v-model="remember"
                label="记住密码"
              />
              <!--              <v-btn class="forget-wrap" text color="blue" rounded>-->
              <!--                <nuxt-link class="blue&#45;&#45;text" to="/forget">忘记密码?</nuxt-link>-->
              <!--              </v-btn>-->
            </div>
          </v-form>
          <div class="btn-wrap">
            <v-btn :disabled="!valid" block rounded depressed large class="white-text font-30 " color="light-blue "
                   @click="login">登录
            </v-btn>
          </div>
        </div>
      </div>
    </div>


  </div>
</template>
<script>
import {Log} from "@/utils";
import UserApi from "@/API/UserApi";
import userMix from "@/storeMix/userMix";

let $cookie;

export default {
  head: {
    title: "NewsRec - 登录"
  },
  mixins: [UserApi, userMix],
  layout: 'signIn',
  computed: {
    emailError: function () {
      const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      if (typeof (this.user.email) === 'undefined' || pattern.test(this.user.email) || this.user.email.length === 0) {
        this.emailMsg = '';
        return false
      }
      this.emailMsg = '请输入正确的邮箱';
      return true
    },
  },
  data: function () {
    return {
      user: {
        email: "335767798@qq.com",
        password: "15250823423"
      },
      emailRules: [
        v => !!v || '邮箱不可为空'
      ],
      remember: false,
      valid: false,
      show: false,
      emailMsg: '',
      passwordRules:
        [v => !!v || '密码不可为空']
    }
  },
  methods: {
    async login() {
      let form = this.$refs.form;
      if (form.validate() && this.valid) {
        const user = await this.loginApi(this.user)
        if (user) {
          Log(user)
          this.loginSt(user)
          $cookie.set("Authorization", user.token)
          this.$router.push({path: `/`})
        }
      }
    },
    handleRemember() {
      //判断是否需要记住密码
      //手机端默认记住密码
      if (this.remember || this.$store.state.isMobile) {
        //将用户信息加密后存入cookie中
        let nickname = this.$utils.encrypt(this.user.nickname);
        let password = this.$utils.encrypt(this.user.password);
        $cookie.set('nickname', nickname, {expires: 7});
        $cookie.set('password', password, {expires: 7})
      } else {
        $cookie.remove('nickname');
        $cookie.remove('password')
      }
    },
    readCookie() {
      //读取本地cookie信息，查看之前是否有将用户的登录信息存放在cookie中
      let nickname = $cookie.get('nickname');
      let password = $cookie.get('password');
      if (!_.isUndefined(nickname) && !_.isUndefined(password)) {
        //读取到信息，开始解析数据
        nickname = this.$utils.decode(nickname);
        password = this.$utils.decode(password);
        this.user.nickname = nickname;
        this.user.password = password;
        this.remember = true
      }
    }
  },
  mounted() {
    //初始化
    $cookie = require('js-cookie');
    this.readCookie()
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
}

.pc-wrap {
  width: 100%;
  height: 550px;
  margin-top: calc(50vh - 280px);
}

.login-wrap {
  background-color: white;
  border-radius: 3px;
  height: 390px;
  width: 400px;
  margin-left: auto;
  margin-right: auto;
}

.check-wrap {
  float: left;
  margin-top: 3px;
  margin-left: 12px;
}

.forget-wrap {
  float: right;
  margin-top: 3px;
  margin-right: 5px;
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
  color: #30304D;
}


.form-wrap {
  margin-top: 20px;
  margin-left: auto;
  margin-right: auto;
  width: 390px;
  text-align: center;
}


.action-wrap {
  width: 100%;
  height: 50px;
  margin-top: 15px;
}

</style>
