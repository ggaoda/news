<template>
  <div class="login-card-outline-wrapper ">
    <v-dialog v-model="showLabels" hide-overlay width="500px" persistent>
      <v-card width="550px" height="350px" style="padding:15px 30px">
        <div class="card-title web-font-pingfang-thin font-22 ">
          兴趣标签选择
        </div>
        <div class="card-content">
          <v-chip @click="addLabel(label)" outlined color="#7F8C8D" style="border-radius: 5px" class="mr-3 mt-3" dark
                  v-for="label in labels" :key="label">
            {{ label }}
          </v-chip>
          <v-btn class=" web-font-pingfang-thin font-12 mr-4 mt-3 right" text color="#81D7A9" small @click="getLabels">
            <v-icon left>
              iconfont icon-shuaxin1
            </v-icon>
            换一批
          </v-btn>
          <div class="hr mt-5"></div>
          <div class="mt-2 ">
            <v-chip label color="#039FFC" style="border-radius: 5px" class="mr-3 mt-3" dark
                    v-for="label in selectedLabels" :key="label">
              {{ label }}
              <v-btn icon x-small class="ml-2" @click="removeLabel(label)">
                <v-icon size="8px">iconfont icon-close</v-icon>
              </v-btn>

            </v-chip>
            <v-chip v-if="!showCustom" outlined color="#262322" style="border-radius: 5px" class="mr-3 mt-3" dark
                    @click="showCustom=true">
              <v-icon left small>
                iconfont icon-Add
              </v-icon>
              自定义
            </v-chip>
            <div class="my-inline-div custom-wrapper mt-3 web-font-pingfang-thin " v-else>
              <input v-model="customLabel" placeholder="标签添加" class="my-inline-div" type="text" maxlength="4">
              <v-btn icon color="green" class="my-inline-div ml-2" style="margin-top:6px" @click="addLabel(customLabel)"
                     x-small>
                <v-icon size="15px">iconfont icon-done</v-icon>
              </v-btn>
              <v-btn icon color="red" class="my-inline-div ml-1" style="margin-top:6px" x-small
                     @click="addLabel(false)">
                <v-icon size="8px">iconfont icon-close</v-icon>
              </v-btn>
            </div>
          </div>
        </div>
        <div class="card-action mt-5 pr-3">
          <v-btn class="mr-4" text dark color="green" @click="setUserLabel(true)">确定</v-btn>
          <v-btn color="red" text dark @click="setUserLabel(false)">取消</v-btn>
        </div>
      </v-card>
    </v-dialog>
    <div class="login-card-inner div-center">
      <div style="width:100%;height:100%" v-if="!isLoginSt">
        <div class="login-btn-wrapper">
          <v-btn color="#FF5D68" height="40px" width="140px" dark depressed :nuxt="true" to="/login"
                 class="div-center web-font-pingfang-thin font-18">登录
          </v-btn>
        </div>
        <div class="web-font-pingfang-thin font-12 div-center" style="color:#1E2239">登录后可以保存您的浏览喜好、评论等</div>
      </div>
      <div v-else>
        <div class="up-wrapper web-font-pingfang-thin pa-3">
          <v-btn small absolute right color="red" text dark @click="logout">
            <v-icon size="14" left>iconfont icon-sign-out</v-icon>
            退出
          </v-btn>
          <div class="my-inline-div font-20" style="line-height:40px;height:40px;text-transform: capitalize">
            {{ userSt.nickname }}
          </div>
          <div class="my-inline-div font-12" style="line-height:40px;height:40px">
            <v-icon color="#071F46" size="18px">iconfont icon-eye1</v-icon>
            浏览了{{ userSt.userViewedNum }}篇新闻
          </div>
          <div class="web-font-pingfang-thin font-14">
            <div class="my-inline-div pt-1">兴趣：</div>
            <div class="my-inline-div" style="width:83%">
              <v-chip label color="blue" class="ma-1" small dark :key="userLabel" v-for="userLabel in userSt.labels">
                {{ userLabel }}
              </v-chip>
              <v-chip label outlined color="blue" class="ma-1" small dark @click="openLabelsDialog">
                <v-icon size="14px" left>
                  iconfont icon-add1
                </v-icon>
                {{ userSt.labels.length === 0 ? "还没有兴趣标签，请添加！" : "添加新的标签" }}
              </v-chip>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import userMix from "@/storeMix/userMix";
import LabelApi from "@/API/LabelApi";
import {Log} from "@/utils";
import myUtils from "@/components/mixin/myUtils";

export default {
  name: "loginCard",
  mixins: [userMix, LabelApi, myUtils],
  data() {
    return {
      labels: [],
      showLabels: false,
      showCustom: false,
      selectedLabels: [],
      customLabel: ""
    }
  },

  methods: {
    logout() {
      const $cookie = require('js-cookie');
      $cookie.remove("Authorization")
      this.logoutSt()
      location.reload();
    },
    openLabelsDialog() {
      this.showCustom = false
      this.showLabels = true
      this.selectedLabels = this.$_.clone(this.userSt.labels)
      this.customLabel = []
      this.getLabels()
    },
    removeLabel(label) {
      this.selectedLabels = this.selectedLabels.filter(value => value !== label)
    },

    async getLabels() {
      this.labels = await this.getLabelsApi()
    },
    async setUserLabel(set) {
      if (set) {
        const userInfo = await this.setUserLabelsApi(this.selectedLabels)
        Log(userInfo)
        if (userInfo) {
          this.loginSt(userInfo)
          this.$message.success("标签设置成功！")
        }
      }
      this.showLabels = false

    },
    addLabel(label) {

      if (label) {
        if (this.selectedLabels.length >= 8) {
          this.$message.info("每个人的标签至多8个！")
          return
        }
        if (this.selectedLabels.indexOf(label) !== -1) {
          this.$message.info("标签已存在！")
          return
        }
        this.selectedLabels.push(label)
      }
      this.customLabel = ""
      this.showCustom = false
    }
  }

}
</script>

<style scoped>
.login-card-outline-wrapper {
  width: 100%;
  min-height: 120px;
  border-radius: 7px;
  background-color: #FAFAFA;
  padding-top: 15px;
  padding-bottom: 15px;

}

.login-card-inner {
  width: 90%;
  border-radius: 7px;
  background-color: white;
  min-height: 90px;
}

.login-btn-wrapper {
  width: 100%;
  text-align: center;
  padding-top: 10px;
  min-height: 60px;
}

.up-wrapper {
  color: #071F46;
  text-align: left;
  position: relative;
}

.card-title {
  width: 100%;
  color: #262322;
  /*background-color:grey;*/

}

.card-content {
  height: 230px;
  padding-top: 15px;
}

.custom-wrapper {
  border: 1px solid #262322;
  width: 150px;
  height: 32px;
  line-height: 32px;
  border-radius: 5px;
  /*text-align: center;*/
  padding-left: 10px;
}

.custom-wrapper input {
  height: 25px;
  line-height: 25px;
  margin-top: 2px;
  width: 70px;
  border-bottom: 1px solid;
  font-size: 14px;
  color: #262322;

}

:focus {
  outline: none !important;
}

::placeholder {
  color: #262322;

  /*font-size: 11px;*/
}

.card-action {
  text-align: right;
  width: 100%;
  height: 40px;
}
</style>
