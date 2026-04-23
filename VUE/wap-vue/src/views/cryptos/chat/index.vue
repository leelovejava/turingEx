<template>
  <div id="cryptos">
    <div class="w-full h-full flex flex-col chat-page" ref="mianscroll">
      <div class="w-full">
        <order-nav>
          <template #left>
            <van-icon name="cross" class="font-bold" color="#868D9A" />
          </template>
          <template #title>
            <div class="flex items-center">
              <img class="w-8 h-8" :src="detail.c2cUserHeadImg" alt="">
              <span class="ml-11 font-bold text-28">{{ detail.c2cUserNickName }}</span>
            </div>
          </template>
        </order-nav>
        <div class="pt-8 px-8 pb-8 tabBackground">
          <template v-if="detail.direction == 'buy'">
            <template v-if="detail.state == 0">
              <div class="text-40 c2cColor flex items-center" v-if="time > 0">{{ $t('订单将在倒计时结束时取消。') }}&nbsp;<span
                  class="text-blue">
                  <!-- {{ time
                |
                format }} -->
                  <van-count-down class="flex font-bold mx-3 textColor" :time="time">
                    <template #default="timeData">
                      <span class="block text-blue">{{ timeData.hours }}</span>
                      <span class="colon text-blue">:</span>
                      <span class="block text-blue">{{ timeData.minutes }}</span>
                      <span class="colon text-blue">:</span>
                      <span class="block text-blue">{{ timeData.seconds }}</span>
                    </template>
                  </van-count-down>
                </span>

              </div>

              <div class="text-40 c2cColor" v-else>{{ $t('您的订单已超时') }}</div>
              <!-- <van-count-down class="flex font-bold mx-10 textColor" :time="time">
                <template #default="timeData">
                  <span class="block">{{ timeData.hours }}</span>
                  <span class="colon">:</span>
                  <span class="block">{{ timeData.minutes }}</span>
                  <span class="colon">:</span>
                  <span class="block">{{ timeData.seconds }}</span>
                </template>
              </van-count-down> -->
              <!-- <span class="block" v-if="time.hours">{{
              time.hours
            }}</span>
            <span class="colon" v-if="time.hours">:</span>
            <span class="block">{{ time.minutes }}</span>
            <span class="colon">:</span>
            <span class="block">{{ time.seconds }}</span> -->
            </template>
            <template v-else>
              <div class="text-40 font-semibold c2cColor">{{ fixStrBuy() }}</div>
            </template>
          </template>
          <template v-else>
            <div class="text-40 c2cColor">{{ fixStrSell() }}</div>
          </template>
          <div class="flex justify-between items-center mt-8 mb-11">
            <div class="text-26 text-black">
              <span class="text-grey">{{ $t('金额') }}</span>
              <span class="ml-3 c2cColor">{{ detail.currency }} {{ detail.amount && (detail.amount).toFixed(2) }}</span>
            </div>
            <div class="text-30 text-blue" @click="$router.push('/cryptos/appeal')">{{ $t('举报') }}</div>
          </div>
          <van-button v-if="detail.direction == 'buy' && detail.state == 0" class="w-full text-32 rounded-lg"
            color="#1D91FF" type="info" @click="gotoPay">{{ $t('去付款') }}</van-button>
        </div>
      </div>
      <div class="content flex-1 pb-52">
        <div class="flex flex-col px-8 box-border">
          <div class="w-full py-2 text-grey text-center pt-24" @click="onMore"
            :style="{ 'visibility': finished ? 'hidden' : 'visiable' }">
            {{ $t('历史消息') }}</div>
          <ul class="flex flex-col pt-5">
            <li v-for="(item, index) in list" :key="item.id" class="flex flex-col mt-5">
              <p class="text-26 text-center py-20 text-grey">{{
                item.createtime }}</p>
              <div class="flex" :class="item.send_receive === 'send' ? 'justify-end' : ''">

                <template v-if="item.send_receive === 'receive'">
                  <img :src="detail.c2cUserHeadImg" class="w-20 h-20 mr-10" />
                  <div class="responser bg-grey px-12 py-9 rounded-lg text-26 chatBg  bg-right"
                    style="border-radius: 20px 20px 20px 0">
                    <p class="break-word textColor" style="max-width: 230px; word-break: break-word;"
                      v-if="item.type === 'text'">{{ item.content }}</p>
                    <img v-else :src="item.content" class="w-48 h-48" @click="onPreview(item.content)" />
                  </div>
                </template>
                <div class="py-6 px-10 rounded-lg flex flex-col chatBg bg-right" style="border-radius: 20px 20px 0 20px"
                  v-else>
                  <img :src="`${item.content}`" class="w-48 h-48" v-if="item.type === 'img'"
                    @click="onPreview(item.content)" />
                  <p class="break-word textColor" v-else style="max-width: 230px; word-break: break-word;color: #fff">
                    {{ item.content }}</p>
                </div>
              </div>
            </li>
          </ul>
        </div>
      </div>
      <!-- <div class="flex-1 w-full overflow-auto bg-f5">
      <div class="w-full pt-43">
        <div class="text-24 text-grey text-center">2022-07-07 20：18：10</div>
        <div class="px-30 mt-45">
          <div class="flex items-end">
            <img class="w-64 h-64 mr-26" src="~@/assets/image/c2c/Group11762.png" alt="">
            <div class="h-100 pl-38 pr-52 py-32 box-border bg-white sell">{{$t('银行卡三天流水明细截图')}}</div>
          </div>
          <div class="flex justify-end mt-58">
            <div class="buyer bg-blue text-white py-32 px-48">{{$t('误划，取消！')}}</div>
          </div>
        </div>
      </div>
    </div> -->
      <div class="fixed bottom-0 bottom-box left-0 w-full px-8 box-border pb-20">
        <div
          class="pl-8 flex justify-between relative w-full h-28 items-center pr-36 box-border rounded-xl tabBackground chat-input">
          <van-uploader :max-size="10000 * 1024" @oversize="onOversize" :after-read="afterRead">
            <img src="@/assets/image/service/photo.png" class="w-16 h-16" />
          </van-uploader>
          <input class="w-full h-full text-32 pl-10 inputBackground c2cColor" type="text" v-model="message"
            :placeholder="$t('请输入您的消息...')">
          <div class="right-8 chat-icon" @click="send('text', message)">
            <img class="w-14" src="~@/assets/image/c2c/Vector(1).png" alt="">
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  Icon,
  Button,
  Uploader, showImagePreview,
  CountDown,
  showLoadingToast,
  showToast,
  closeToast
} from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import { _uploadImage } from "@/service/upload.api.js";
import otcApi from "@/service/otc.api";

export default {
  name: "ChatIndex",
  props: ['type'], // 标识买家还是卖家
  components: {
    [Icon.name]: Icon,
    [Button.name]: Button,
    OrderNav,
    [Uploader.name]: Uploader,
    [CountDown.name]: CountDown,
  },
  data() {
    return {
      list: [],
      lastMsgId: '',
      finished: false, // 没有历史消息
      message: '',
      orderNo: '',
      detail: {},
      inter: null,
      timerData: null,
      time: '' // 剩余时间
    }
  },
  filters: {
    format(val) {
      return Math.floor(val / 60) + ':' + val % 60
    }
  },
  async created() {
    this.orderNo = this.$store.state.c2c.order_no
    const res = await otcApi.ctcOrderGetDetail({ order_no: this.orderNo, language: this.$i18n.locale });
    this.detail = res
    const { autoCancelTimeRemain } = this.detail
    this.time = autoCancelTimeRemain * 1000
    // this.time = this.orderDetail.autoCancelTimeRemain * 1000
    if (this.time > 0) {
      this.timerData = setInterval(() => {
        this.time--
        if (this.time <= 0) {
          clearInterval(this.timerData)
        }
      }, 2000)
    }
    this.fetchList()
  },
  methods: {
    fixStrSell() {
      let str = ''
      if (this.detail.state == 0) {
        str = this.$t('等待买家付款')
      } else if (this.detail.state == 1) {
        str = this.$t('请放行')
      } else if (this.detail.state == 2) {
        str = this.$t('申诉中')
      } else if (this.detail.state == 3) {
        str = this.$t('已完成')
      } else if (this.detail.state == 4) {
        str = this.$t('已取消')
      } else if (this.detail.state == 5) {
        str = this.$t('已超时')
      }
      return str
    },
    fixStrBuy() {
      let str = ''
      if (this.detail.state == 1) {
        str = this.$t('等待卖家放行')
      } else if (this.detail.state == 2) {
        str = this.$t('申诉中')
      } else if (this.detail.state == 3) {
        str = this.$t('已完成')
      } else if (this.detail.state == 4) {
        str = this.$t('已取消')
      } else if (this.detail.state == 5) {
        str = this.$t('已超时')
      }
      return str
    },
    gotoPay() {
      if (this.detail.state == 0) {
        this.$router.push({ path: '/cryptos/paymentBuy' })
      }
    },
    onOversize(file) {
      console.log(file);
      showToast(this.$t('文件大小不能超过10m'));
    },
    onPreview(url) { // 预览
      showImagePreview([url])
    },
    showTime(index) { // 时间显示
      if (index === 0) {
        return true
      }
      if (index === this.list.length - 1) {
        return false
      }
      if (this.list[index].createtime.split(' ')[0] === this.list[index + 1].createtime.split(' ')[1]) {
        return false
      }
    },
    afterRead(file) { // 文件上传
      showLoadingToast({
        duration: 0,
        forbidClick: true,
      })
      _uploadImage(file, (percent) => {
        console.log(percent)
      }).then(data => {
        closeToast()
        this.send('img', data)
      }).catch(() => {
        closeToast()
      })
    },
    fetchList(message_id = '') { // 获取消息列表
      otcApi.otcOnlinechatList({
        messageId: message_id,
        orderNo: this.detail.orderNo
      }).then(res => { // this.lastMsgId
        if (!this.lastMsgId) {
          this.lastMsgId = res.length && res[res.length - 1]['id']
        }
        if (res.length) {
          if (message_id) { // 加载更多
            this.lastMsgId = res[res.length - 1]['id']
            this.list = [...res.reverse(), ...this.list]
          } else {
            this.list = [...this.list, ...res.reverse()]
            let hash = {};
            this.list = this.list.reduce(function (preVal, curVal) {
              hash[curVal.id] ? ' ' : hash[curVal.id] = true && preVal.push(curVal);
              return preVal
            }, []);
          }
          if (res.length < 10) {
            this.finished = true
          }
        } else {
          this.list = []
        }
        if (!message_id) {
          this.clearInterval()
          this.inter = setInterval(() => {
            this.fetchList()
          }, 2000)
        }
      })

    },
    onMore() { // 加载更多
      this.fetchList(this.lastMsgId)
    },
    clearInterval() {
      if (this.inter) {
        clearInterval(this.inter)
        this.inter = null
      }
    },
    send(type = 'text', content = '') { // 发送消息, img 也当消息text
      if (!content) {
        showToast(this.$t('请输入消息内容'))
        return
      }
      otcApi.otcOnlinechat({
        orderNo: this.detail.orderNo,
        type,
        content
      }).then(data => {
        console.log(data)
        this.message = ''
        // document.getElementById('bottom').click()
        this.fetchList()
        setTimeout(() => {
          this.bottomScrollClick()
        }, 1000)
        // setTimeout(() => {
        //   window.scrollTo(0, document.documentElement.scrollHeight)
        // }, 1000)
      })
    },
    bottomScrollClick() {
      this.$nextTick(() => {
        let scrollEl = this.$refs.mianscroll;
        scrollEl.scrollTo({ top: scrollEl.scrollHeight + 400, behavior: 'smooth' });
      });
    }
  },
  beforeUnmount() {
    this.clearInterval()
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 30px;

  .bg-left {
    background: #fff !important;
  }

  .bg-right {
    background: #1D91FF !important;
  }

  .sell {
    border-radius: 10.0022px 10.0022px 10.0022px 0;
  }

  .buyer {
    border-radius: 10.0022px 10.0022px 0 10.0022px;
    //transform: matrix(-1, 0, 0, 1, 0, 0);
  }

  .chat-input {
    input {
      border: none;
      outline: none;
    }
  }

  .chat-page {
    overflow: auto;
  }

  .chat-icon {
    position: absolute;
    top: 50%;
    transform: translateY(-50%);
  }

  .bottom-box {

    background: $main_background !important
  }
}
</style>
