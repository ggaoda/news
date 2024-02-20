<template>
  <div class="navbar-wrap">
    <div class="newsRec-wrap web-font-pingfang font-25 my-1">NewsRec</div>
    <div class="channel-wrap my-3">
      <div v-for="(channel,index) in channels"
           class="transition-200ms per-item-wrap web-font-pingfang font-16 mt-1"
           :class="{'per-item-active-wrap':index===activeIndex}"
           @click="changeChannel(index,0)"
      >
        {{ channel }}
      </div>
      <v-menu
        v-model="moreMenu"
        open-on-hover
        :close-on-content-click="false"
        :nudge-width="200"
        offset-x
        content-class="more-list-wrap"
      >
        <template v-slot:activator="{ on, attrs }">
          <div
            v-bind="attrs"
            v-on="on"
            class="transition-200ms per-item-wrap web-font-pingfang font-16 mt-1">
            更多
          </div>
        </template>
        <div>
          <div style="width:120px" v-for="(channel,index) in more"
               @click="exchangeIndex(index)"
               class="my-inline-div transition-200ms per-item-wrap web-font-pingfang font-16 mt-1">
            {{ channel }}
          </div>
        </div>

      </v-menu>

    </div>
  </div>
</template>

<script>
import NewsApi from "@/API/NewsApi";
import {Log} from "@/utils";
import myUtils from "@/components/mixin/myUtils";

export default {
  name: "navigation",

  data: function () {
    return {
      channels: [],
      more: [],
      activeIndex: 0,
      moreMenu: false,
      allChannels: [],
      curState: 0 //0 状态显示列表否则不显示列表
    }
  },
  methods: {
    changeChannel(index, state = 1) {
      this.activeIndex = index
      this.curState = state
      if (state === 0) {
        window.scrollTo({top: 0})
      }
      this.$emit("changeChannel", this.channels[index], true, this.curState)
    },
    exchangeIndex(index, state) {
      let channelTmp = this.more[index]
      this.more[index] = this.channels.pop();
      this.channels.push(channelTmp)
      this.changeChannel(this.channels.length - 1, state)
      this.moreMenu = false
    },
    //从父组件发来的激活请求
    activeChannel(channelName, state) {
      if (this.channels.includes(channelName)) {
        this.changeChannel(this.channels.indexOf(channelName), state)
      } else if (this.more.includes(channelName)) {
        this.exchangeIndex(this.more.indexOf(channelName), state)
      }
    }
  },
  mixins: [NewsApi, myUtils],
  async mounted() {
    let channels = await this.getChannelsApi()
    let channelsName = []
    channels.forEach(channel => {
      channelsName.push(channel.channelName)
    })
    this.allChannels = this.$_.clone(channelsName)
    this.more = channelsName.splice(11)
    this.channels = channelsName

  }

}
</script>

<style scoped>
.newsRec-wrap {
  color: #039FFC;

}

.navbar-wrap {
  width: 170px;
}

.per-item-wrap {
  width: 110px;
  margin-right: auto;
  margin-left: auto;
  color: #1E2239;
  height: 40px;
  line-height: 40px;
  text-align: center;
}

.per-item-wrap:hover {
  color: white;
  cursor: pointer;
  background-color: #039FFC;
  border-radius: 5px;
}

.per-item-active-wrap {
  color: white !important;
  background-color: #039FFC !important;
  border-radius: 5px;
}

.more-list-wrap {
  text-align: center;
  background-color: white;
  width: 250px;
  top: 250px !important;
  min-width: 50px !important;
  padding: 5px;
}
</style>
