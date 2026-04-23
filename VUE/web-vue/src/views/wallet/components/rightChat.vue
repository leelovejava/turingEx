<template>
  <div class="main-right">
    <div class="huihua">
      <!-- 头部 -->
      <div class="hui-top">{{ t("message.home.dibu18") }}</div>
      <!-- 聊天内容 -->
      <div class="hui-center" ref="scroll">
        <div class="hui-li" v-for="(item, index) in msgList" :key="index">
          <div class="list-tiem">{{ item.createtime }}</div>
          <p
            class="hui-n"
            :class="[
              item.send_receive === 'receive' ? 'bg-ta' : '',
              item.send_receive === 'receive' ? 'left-is-ta' : 'right-is-ta',
            ]"
            v-if="item.type === 'text'"
          >
            {{ item.content }}
          </p>
          <img
            :class="[
              item.send_receive === 'receive' ? 'bg-ta' : '',
              item.send_receive === 'receive' ? 'left-is-ta' : 'right-is-ta',
            ]"
            class="content-img"
            v-else
            :src="item.content"
          />
        </div>
      </div>
      <!-- 发送聊天信息 -->
      <div class="hui-bottom">
        <img
          src="@/assets/images/c2c/orderSuccess/Group1862.png"
          alt=""
          @click="handleSendImg"
        />
        <!-- 图片 -->
        <input
          ref="uploadRef"
          type="file"
          hidden
          @change="onImageFileChanged"
          accept="image/png, image/gif, image/jpeg, image/bmp, image/x-icon"
        />
        <el-input v-model="message" :placeholder="t('message.c2c.tip152')" />
        <img
          @click="handleSend('text', message)"
          src="@/assets/images/c2c/orderSuccess/Group1859.png"
          alt=""
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import Axios from "@/api/c2c.js";
import { _uploadImage } from "@/api/chat.js";
import { useI18n } from "vue-i18n";
import { onMounted,onUnmounted } from "vue";

const { t } = useI18n();

const msgList = ref([]);
const lastMsgId = ref();
const uploadRef = ref(null);
const message = ref();
const props = defineProps({
  detailInfo: {
    type: Object,
    default: {},
  },
});

onMounted(() => {
  fetchChatList();
});

const fetchChatList = (message_id = "") => {
  let obj = {
    messageId: message_id,
    orderNo: props.detailInfo?.orderNo || props.detailInfo?.order_no,
  };
  // 获取消息列表
  Axios.getOtcOnlinechat(obj).then((res) => {
    let data = res.data;
    scrollToBottom();
    if (!lastMsgId.value) {
      lastMsgId.value = data.length && data[data.length - 1]["id"];
    }
    if (data.length) {
      if (message_id) {
        // 加载更多
        lastMsgId.value = data[data.length - 1]["id"];
        msgList.value = [...data.reverse(), ...msgList.value];
      } else {
        msgList.value = [...msgList.value, ...data.reverse()];
        let hash = {};
        msgList.value = msgList.value.reduce(function (preVal, curVal) {
          hash[curVal.id]
            ? " "
            : (hash[curVal.id] = true && preVal.push(curVal));
          return preVal;
        }, []);
      }
    }
  });
};

const timeOutTimer1 = ref(null);
const scrollToBottom = () => {
  const doms = document.getElementsByClassName("hui-center")[0];
  timeOutTimer1.value = setTimeout(() => {
    // this.$nextTick(() => {
    doms.scrollTop = doms.scrollHeight;
    // });
  }, 1000);
};
// 选中图片
const onImageFileChanged = (e) => {
  const file = e.target.files[0];
  afterRead(file);
};
// 文件上传
const afterRead = (file) => {
  _uploadImage(file).then((data) => {
    handleSend("img", data.httpUrl);
  });
};

const handleSendImg = () => {
  uploadRef.value.click();
};
const handleSend = (type = "text", content = "") => {
  // 发送消息, img 也当消息text
  if (!content) {
    this.$message.error(t("message.c2c.tip142"));
    return;
  }
  Axios.sendOtcOnlinechat({
    orderNo: props.detailInfo.orderNo,
    type,
    content,
  }).then((data) => {
    message.value = "";
    scrollToBottom();
    fetchChatList();
  });
};

 // 清除定时器
onUnmounted(()=>{
  if(timeOutTimer1.value){
    clearTimeout(timeOutTimer1.value)
    timeOutTimer1.value = null
  }
})
</script>
