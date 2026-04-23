<template>
  <div class="chat" v-loading="loading">
    <el-scrollbar style="height: 500px" ref="scroll">
      <div ref="scrollInner">
        <div
          class="history"
          @click="onMore"
          :style="{ visibility: finished ? 'hidden' : 'visiable' }"
        >
          {{ $t("message.home.historyMsg") }}
        </div>
        <!-- 对话框消息 -->
        <ul>
          <li v-for="(item, index) in chatList" :key="item.id">
            <template v-if="item.delete_status != -1">
              <p class="date" v-if="showTime(index)">
                {{ item.createtime }}
              </p>

              <div
                class="content-box"
                :class="
                  item.send_receive === 'send' ? 'justify-end' : 'justify-start'
                "
              >
                <!-- 机器人消息 -->
                <template v-if="item.send_receive === 'receive'">
                  <img
                    src="/src/assets/images/chat/responser.png"
                    class="avatar"
                  />
                  <div
                    class="responser bg-grey px-50 py-35 font-30 rounded-lg font-26"
                  >
                    <p
                      class="break-words"
                      style="max-width: 300px"
                      v-if="item.content_type === 'text'"
                    >
                      {{ item.content }}
                    </p>
                    <!-- <img v-else :src="item.content" class="w-200 h-200" @click="onPreview(item.content)"/> -->
                    <el-image
                      v-else
                      :src="item.content"
                      class="w-200 h-200"
                      :preview-src-list="[item.content]"
                    ></el-image>
                  </div>
                </template>
                <!-- 用户消息 -->
                <div class="responser" v-else>
                  <el-image
                    :src="`${item.content}`"
                    class="w-200 h-200"
                    :preview-src-list="[item.content]"
                    v-if="item.content_type === 'img'"
                  ></el-image>

                  <p class="break-words" v-else style="max-width: 300px">
                    {{ item.content }}
                  </p>
                </div>
              </div>
            </template>
          </li>
        </ul>
      </div>
    </el-scrollbar>
    <!-- 发送消息 -->
    <div class="input-box">
      <img
        src="/src/assets/images/chat/photo.png"
        class="w-72 h-72"
        @click="handleChoiceFile"
      />
      <input
        ref="upload"
        type="file"
        hidden
        @change="onImageFileChanged"
        accept="image/png, image/gif, image/jpeg, image/bmp, image/x-icon"
      />
      <input
        ref="input"
        type="text"
        v-model="value"
        :placeholder="$t('input')"
        class="flex-1 mx-20 h-full border-none"
      />
      <!-- 发送按钮 -->
      <img
        src="/src/assets/images/chat/send.png"
        class="w-72 h-72"
        @click="handleSend('text', value)"
      />
    </div>
  </div>
</template>

<script>
import Axios from "@/api/chat";
// let interval
export default {
  name: "Chat",
  data() {
    return {
      chatList: [],
      value: "", //客服输入的消息
      lastMsgId: "",
      interval: null,
      timer: null,
      unread: 0,
      loading: false,
      finished: false, // 没有历史消息
    };
  },
  created() {
    this.fetchList();
    this.interval = setInterval(() => {
      this.fetchList();
    }, 1000);
  },
  methods: {
    getInputFocus() {},
    handleChoiceFile() {
      this.$refs["upload"].click();
    },
    showTime(index) {
      // 时间显示
      if (index === 0) {
        return true;
      }
      if (index === this.chatList.length - 1) {
        return false;
      }
      if (
        this.chatList[index].createtime.split(" ")[0] ===
        this.chatList[index + 1].createtime.split(" ")[1]
      ) {
        return false;
      }
      return true;
    },
    onImageFileChanged(e) {
      const file = e.target.files[0];
      this.handleUpload(file);
    },
    // 上传文件
    handleUpload(file) {
      this.loading = true;

      Axios._uploadImage(file, (percent) => {
        console.log(percent);
      })
        .then((res) => {
          this.loading = false;
          this.handleSend("img", res.httpUrl);
        })
        .catch(() => {
          this.loading = false;
        });
    },
    // 获取消息列表
    fetchList(message_id = "") {
      Axios._getMsg(message_id).then((data) => {
        if (!this.lastMsgId) {
          this.lastMsgId = data.length && data[data.length - 1]["id"];
          this.scrollToBottom();
        }

        if (data.length) {
          if (message_id) {
            // 加载更多
            this.lastMsgId = data[data.length - 1]["id"];
            this.chatList = [...data.reverse(), ...this.chatList];
          } else {
            this.chatList = [...this.chatList, ...data.reverse()];
            let hash = {};
            this.chatList = this.chatList.reduce(function (preVal, curVal) {
              hash[curVal.id]
                ? " "
                : (hash[curVal.id] = true && preVal.push(curVal));
              return preVal;
            }, []);
            this.chatList.forEach((item, index) => {
              data.forEach((item2, index2) => {
                if (item.id === item2.id) {
                  item.delete_status = item2.delete_status;
                }
              });
            });
          }
          if (data.length < 10) {
            this.finished = true;
          }
        } else {
          this.chatList = [
            {
              id: "1",
              send_receive: "receive",
              content: this.$t("welcome"),
              content_type: "text",
            },
          ];
        }
      });
    },
    scrollToBottom() {
      this.timer = setTimeout(() => {
        this.$nextTick(() => {
          this.$refs.scroll.setScrollTop(100000);
        });
      }, 1000);
    },
    // 加载更多
    onMore() {
      this.fetchList(this.lastMsgId);
    },
    hanldeClear() {
      clearInterval(this.interval);
      this.interval = null;

      if(this.timer){
        clearTimeout(this.timer)
        this.timer = null;
      }
    },
    // 获取未读
    fetchUnread() {
      Axios._getUnreadMsg().then((data) => {
        this.unread = data;
      });
    },
    // 发送消息
    handleSend(type = "text", content = "") {
      if (!content) {
        const text = this.$t("message.home.chatempty");
        this.$message(text);
        return;
      }
      Axios._sendMsg(type, content).then((data) => {
        this.value = "";
        this.scrollToBottom();
        this.fetchList();
      });
    },
  },
  //销毁定时器
  unmounted() {
    this.hanldeClear();
  },
};
</script>

<style scoped>
:deep(.el-scrollbar) .el-scrollbar__wrap {
  /* height: 620px; */
  overflow-x: hidden;
}

.w-100 {
  width: 200px;
}
.h-200 {
  height: 200px;
}
.date {
  text-align: center;
  padding: 5px 0;
  color: #000;
}
.justify-end {
  justify-content: flex-end;
}
.chat {
  width: 100%;
  display: flex;
  height: 100%;
  flex-direction: column;
}
.history {
  padding: 10px 0;
  text-align: center;
  width: 160px;
  height: 40px;
  line-height: 30px;
  background: #f0f0f0;
  margin: 0 auto;
  cursor: pointer;
  border-radius: 10px;
  margin-top: 10px;
}
.history:hover {
  background: #a8d3ec;
  color: #fff;
}
ul {
  /* display: flex; */
  flex: 1;
  /* overflow-y: scroll; */
  /* flex-direction: column; */
}
ul li {
  display: flex;
  margin-bottom: 10px;
  flex-direction: column;
}
ul li .content-box {
  display: flex;
  padding: 0 10px;
  width: 100%;
  box-sizing: border-box;
}
ul li .content-box .avatar {
  width: 40px;
  height: 40px;
  margin-right: 10px;
}

ul li .content-box .responser {
  position: relative;
  background: #f0f0f0;
  padding: 10px 20px;
  border-radius: 10px;
}

ul li .content-box.justify-end {
  padding-right: 20px;
}
ul li .content-box.justify-end .responser::after {
  content: "";
  width: 0;
  height: 0;
  border-top: 10px solid transparent;
  border-bottom: 10px solid transparent;
  /* border-right:20px solid #f3f3f3; */
  position: absolute;
  /* left: -10px; */
  top: 10px;
  border-left: 20px solid #f3f3f3;
  right: -10px;
}

ul li .content-box.justify-start .responser::after {
  content: "";
  width: 0;
  height: 0;
  border-top: 10px solid transparent;
  border-bottom: 10px solid transparent;
  border-right: 20px solid #f3f3f3;
  position: absolute;
  left: -10px;
  top: 10px;
}

ul li .content-box .responser .break-words {
  font-size: 16px;
  color: #000;
  word-break: break-all;
}
.input-box {
  height: 50px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-sizing: border-box;
  padding: 0 10px;
  position: relative;
  z-index: 2;
}
.input-box input {
  flex: 1;
  height: 100%;
  border: none;
  outline: none;
  padding-left: 5px;
}
.input-box img {
  width: 35px;
  height: 35px;
}
</style>
