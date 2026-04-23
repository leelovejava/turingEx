<template>
  <div class="service-box flex flex-col" :class="homesStore.kefu_url ? '' : 'pb-40'">
    <div>
      <van-nav-bar ref="navEl" :title="$t('onLineService')" left-arrow @click-left="onClickLeft" fixed />
      <div class="px-3.5 py-5" v-if="state == 0">
        <div class="white">{{ $t('OrdersWill') }} <span style="color: #1194F7">{{ dayjs.duration(remainingTime,
          'seconds').format('mm:ss') }}</span> {{
    $t('afterCancel')
  }}</div>
        <div class="mt-3">
          <span class="mr-1" style="color: #8A919E">{{ $t('lumpSum') }}</span>
          <span class="white">{{ payInfo.currency }} {{ payInfo.amount }}</span>
        </div>
        <div class="mt-5">
          <van-button class="w-full" type="primary" @click="router.back()">{{ $t('toPay') }}</van-button>
        </div>
      </div>
    </div>
    <div class="flex-1" v-if="homesStore.kefu_url">
      <iframe :src="generateExtranetLink()" width="100%" height="100%" frameborder="0"></iframe>
    </div>
    <div class="localKefu flex-1 flex" v-else>
      <div class="flex flex-col px-10 box-border" ref="boxScrollEl" style="overflow:auto;">
        <div class="w-full py-4 text-grey text-center pt-30 text-30" @click="onMore"
          :style="{ 'display': finished ? 'none' : 'block' }">
          {{ $t('historyMessage') }}
        </div>
        <ul class="flex flex-col pt-3">
          <li v-for="(item, index) in list" :key="item.id" class="flex flex-col my-3">
            <!-- <p class="text-center py-2 text-grey text-30" v-if="showTime(index)">{{
              item.createtime &&
              item.createtime.split(' ')[0]
            }}</p> -->
            <template v-if="item.delete_status != -1">
              <p class="text-center pb-3 text-grey text-30">{{
                item.createtime
              }}</p>
              <div class="flex" :class="item.send_receive === 'send' ? 'justify-end' : ''">
                <template v-if="item.send_receive === 'receive'">
                  <img src="@/assets/image/service/responser.png" class="w-20 h-20 mr-5" />
                  <div class="responser px-12 py-8 text-30 left-chatBg">
                    <p class="break-word textColor  text-30" style="max-width: 200px;"
                      v-if="item.content_type === 'text' || item.type === 'text'">
                      {{ item.content }}</p>
                    <img v-else :src="item.content" class="w-200 h-200" @click="onPreview(item.content)" />
                  </div>
                </template>
                <div class="py-8 px-12 rounded-md flex flex-col right-chatBg" v-else>
                  <img :src="`${item.content}`" class="w-200 h-200"
                    v-if="item.content_type === 'img' || item.type === 'img'" @click="onPreview(item.content)" />
                  <p class="break-word text-white  text-30" v-else style="max-width: 200px;">{{ item.content }}</p>
                </div>
              </div>
            </template>
          </li>
        </ul>
      </div>

      <div
        class="bottom bottomBox flex justify-between items-center w-full fixed bottom-0 borderTop px-4 box-border bgBottom">
        <van-uploader :max-size="10000 * 1024" @oversize="onOversize" :after-read="afterRead">
          <img src="@/assets/image/service/photo2.png" class="w-12 h-12" />
        </van-uploader>
        <input type="text" v-model="message" :placeholder="$t('entryYouMessage')"
          class="flex-1 mx-3 h-full border-none textColor chatBg" maxlength="500" />
        <img src="@/assets/image/service/send2.png" class="w-12 h-12" @click="throttleSend(message)" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { Uploader, showImagePreview } from 'vant'
import { _getMsg, _getUnreadMsg, _sendMsg } from '@/service/im.api'
import { _uploadImage } from '@/service/upload.api'
import { ref, onMounted, onUnmounted, onBeforeMount } from "vue";
import { useI18n } from "vue-i18n";
import { throttle } from '@/utils/index'
import { closeToast, showToast, showLoadingToast } from "vant";
import { useRoute, useRouter } from 'vue-router';
import { getc2cOrder } from "@/service/recharge.api.js";
import { useHomesStore } from "@/store/homes.store";
import { SET_KEFU } from "@/store/types.store";
import { useUserStore } from '@/store/user';
import dayjs from 'dayjs';
import duration from 'dayjs/plugin/duration';
dayjs.extend(duration);

const userStore = useUserStore()
const { t } = useI18n()
const homesStore = useHomesStore();
const router = useRouter()
const route = useRoute()
const list = ref([])
const message = ref('')
const lastMsgId = ref('')
const interval = ref(null)
const unread = ref(0)
const finished = ref(false)
const isScrollBottom = ref(true)
const currentScrollTop = ref(0)
const androidAttrs = ref(null)
const navEl = ref(null);
const boxScrollEl = ref(null);
const navHeight = ref(0);
const payInfo = ref({})
const remainingTime = ref(0)

let state = ref(null)
let orderNo = ""
let partyId = ""

onMounted(() => {
  orderNo = ""
  partyId = ""
  navHeight.value = navEl.value.$el.getBoundingClientRect().height
  if (route.query.order_no) {
    getc2cOrderDetails(route.query.order_no, (data) => {
      console.log("getc2cOrderDetails = " + JSON.stringify(data))
      orderNo = data.orderNo;
      partyId = data.partyId;
      fetchList()
    })
  } else {
    if (!homesStore.kefu_url) {
      fetchList()
    }
  }
  setInterval(() => {
    getCountdown()
  }, 1000)
  const model = navigator.userAgent;
  // 判断是否是安卓手机，是则是true
  androidAttrs.value = model.indexOf('Android') > -1 || model.indexOf('Linux') > -1
  window.addEventListener('scroll', handleScroll, true)
})

onBeforeMount(() => {
  homesStore[SET_KEFU]()
})

//获取订单详情
const getc2cOrderDetails = (orderNo, call) => {
  getc2cOrder({ order_no: orderNo }).then((res) => {
    payInfo.value = res
    state.value = payInfo.value.state
    remainingTime.value = res.autoCancelTimeRemain
    if (call) { call(res) }
  })
}

//第三方客服带用户id
const generateExtranetLink = () => {
  let extranetLink = ''
  if (userStore.userInfo && userStore.userInfo.usercode) {
    const userData = encodeURIComponent(JSON.stringify({ name: userStore.userInfo.usercode, comment: '' }))
    let params = `&clientid=${userStore.userInfo.usercode}&metadata=${userData}`;
    extranetLink = homesStore.kefu_url + params;
  } else {
    extranetLink = homesStore.kefu_url
  }
  // extranetLink = homesStore.kefu_url + params;
  console.log('generateExtranetLink', extranetLink)
  // console.log('extranetLink',extranetLink)
  return extranetLink;
}

const throttleSend = throttle((message) => {
  send('text', message)
}, 500)

const onOversize = (file) => {
  showToast(t('fileMaxLimit'));
}
const onPreview = (url) => { // 预览
  showImagePreview([url])
}
const showTime = (index) => { // 时间显示
  if (index === 0) {
    return true
  }
  if (index === list.value.length - 1) {
    return false
  }
  if (list.value[index].createtime.split(' ')[0] === list.value[index + 1].createtime.split(' ')[1]) {
    return false
  }
}
const afterRead = (file) => { // 文件上传
  showLoadingToast({ duration: 0 })
  _uploadImage(file, (percent) => {
    console.log(percent)
  }).then(data => {
    closeToast()
    send('img', data)
  }).catch(() => {
    showToast(t('失败'))
  })
}
const fetchList = async (message_id = '') => { // 获取消息列表
  console.log("orderNo = " + orderNo);
  console.log("partyId = " + partyId);
  _getMsg({ message_id }, orderNo, partyId).then(data => { //
    if (!lastMsgId.value) {
      lastMsgId.value = data.length && data[data.length - 1]['id']
    }
    if (data.length) {
      if (message_id) { // 加载更多
        lastMsgId.value = data[data.length - 1]['id']
        list.value = [...data.reverse(), ...list.value]
      } else {
        list.value = [...list.value, ...data.reverse()]
        let hash = {};
        list.value = list.value.reduce(function (preVal, curVal) {
          hash[curVal.id] ? ' ' : hash[curVal.id] = true && preVal.push(curVal);
          return preVal
        }, []);
        list.value.forEach((item, index) => {
          data.forEach((item2, index2) => {
            if (item.id === item2.id) {
              item.delete_status = item2.delete_status
            }
          })
        })

      }

      if (isScrollBottom.value) {
        boxScrollEl.value.scrollTop = boxScrollEl.value.scrollHeight - boxScrollEl.value.offsetHeight
      }
      currentScrollTop.value = boxScrollEl.value.scrollTop;
      if (data.length < 10) {
        finished.value = true
      }
    }
    if (!message_id) {
      clearIntervalTimer()
      interval.value = setInterval(() => {
        fetchList()
      }, 1000)
    }
  })
}

const handleScroll = () => {
  if (boxScrollEl.value) {
    if (boxScrollEl.value.scrollTop === currentScrollTop.value) {
      isScrollBottom.value = true
    } else {
      isScrollBottom.value = false
    }
  }
}

const onMore = () => { // 加载更多
  fetchList(lastMsgId.value)
}
const clearIntervalTimer = () => {
  if (interval.value) {
    clearInterval(interval.value)
    interval.value = null
  }
}
const fetchUnread = () => { // 获取未读
  _getUnreadMsg(orderNo, partyId).then(data => {
    unread.value = data
  })
}
const onClickLeft = () => { // 返回
  router.go(-1);
}
const send = (type = 'text', content = '') => { // 发送消息, img 也当消息text
  if (!content) {
    showToast(t('entryMessageContent'))
    return
  }
  _sendMsg(type, content, orderNo, partyId).then(async data => {
    message.value = ''
    isScrollBottom.value = true
    // document.getElementById('bottom').click()
    // await fetchList()
  })
}
const getCountdown = () => {
  if (remainingTime.value > 0) {
    remainingTime.value = remainingTime.value - 1
  } else {
    remainingTime.value = 0
  }
}

onUnmounted(() => {
  clearIntervalTimer()
})

</script>
<style lang="scss" scoped>
.service-box {
  font-size: 14px;
  width: 100%;
  height: 100vh;
  box-sizing: border-box;
  background: $mainBgColor;
  overflow: hidden;

  :deep(.van-hairline--bottom::after) {
    border-color: $mainBgColor;
  }
}

.break-word {
  word-wrap: break-word;
  white-space: pre-wrap;
}

.max-w-230 {
  max-width: 115px;
}

.responser {
  position: relative;

  &::after {
    content: '';
    width: 0;
    height: 0;
    border-top: 5px solid transparent;
    border-bottom: 5px solid transparent;
    border-right: 10px solid $input_background;
    position: absolute;
    left: -10px;
    top: 10px;
  }
}

.borderTop {
  border-top: 1px solid $border_color;
}

.bottomBox {
  height: 65px;
}

.white {
  color: $text_color;
}

.chatBg {
  background: $input_background;
  height: 36px;
  padding: 8px 18px;
  border-radius: 18px;
  color: $text_color;
  font-size: 14px;
  border: 1px solid $chat-border;
}

.right-chatBg {
  position: relative;
  background: $color_main;
  color: $text_color;

  &::after {
    content: '';
    width: 0;
    height: 0;
    border-top: 5px solid transparent;
    border-bottom: 5px solid transparent;
    border-left: 10px solid $color_main;
    position: absolute;
    right: -8px;
    top: 14px;
  }
}

.left-chatBg {
  background: $input_background;
}

.localKefu {
  overflow: auto;
  flex-direction: column;
}

.van-nav-bar--fixed {
  position: relative !important;
}
</style>
