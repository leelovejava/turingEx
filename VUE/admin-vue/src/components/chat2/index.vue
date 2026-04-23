<template>
  <div class="chat" v-loading="loading">
    <el-scrollbar style="height: 500px" ref="scroll">
      <div
        class="history"
        @click="onMore"
        :style="{ visibility: finished ? 'hidden' : 'visiable' }"
      >
        历史记录
      </div>
      <!-- 对话框消息 -->
      <ul>
        <li v-for="(item, index) in chatList" :key="item.id">
          <!-- v-if="showTime(index)" -->
          <p class="date">
            {{ item.createtime}}
          </p>

          <div
            class="content-box"
            :class="
              item.send_receive === 'receive' ? 'justify-end' : 'justify-start'
            "
          >
            <!-- 机器人消息 -->
            <template v-if="item.send_receive === 'receive'">
              <div
                class="responser bg-grey px-50 py-35 font-30 rounded-lg font-26"
                style="background: #1ebafc;color: #fff;" 
              >
              <span style="font-size: 13px;margin-right: 10px;margin-top: 10px;">{{ item.delete_status == -1 ? '已撤回' :
                item.is_read == 1 ? '已读' : '未读' }}</span>
                <p
                  class="break-words"
                  style="max-width: 300px;background: #1ebafc;color: #fff;"
                  v-if="item.type === 'text'"
                >
                  {{ item.content }}
                </p>
                <!-- <img v-else :src="item.content" class="w-200 h-200" @click="onPreview(item.content)"/> -->
                <el-image
                  v-if="item.type === 'img' && !status[item.id] "
                  src="./load.png"
                  class="w-200 h-200"
                  @click="openLoad(`${item.id}`)"
                ></el-image>
                <el-image
                  v-if="item.type === 'img' && status[item.id] == 2 "
                  :src="item.content"
                  class="w-200 h-200"
                  @click="open(`${item.content}`)"
                ></el-image>

                <div class="loading" v-if="status[item.id]==1"></div>

              </div>
              <img src="./responser.png" class="avatar" style="margin-left: 20px;"/>
            </template>
            <!-- 用户消息 -->
            <template v-else>
              <img src="./avatar1.png" class="avatar" style=""/>
              <div class="responser" >
                
                <div>
                  <el-image
                    src="./load.png"
                    class="w-200 h-200"
                    :preview-src-list="[item.content]"
                    v-if="item.type === 'img' && !status[item.id] "
                    @click="openLoad(`${item.id}`)"
                  ></el-image>

                  <el-image
                    :src="`${item.content}`"
                    class="w-200 h-200"
                    :preview-src-list="[item.content]"
                    v-if="item.type === 'img' && status[item.id] == 2"
                    @click="open(`${item.content}`)"
                  ></el-image>
                  <div class="loading" v-if="status[item.id] == 1"></div>

                  <p class="break-words" v-if="item.type === 'text'" style="max-width: 300px">
                    {{ item.content }}
                  </p>
                </div>
        
              </div>
            </template>

          </div>
        </li>
      </ul>
    </el-scrollbar>
    <div class="input-box">
      <img src="./photo.png" class="w-72 h-72" @click="test" />
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
        placeholder="请输入您的消息..."
        class="flex-1 mx-20 h-full border-none"
      />
      <!-- <el-input v-model="value" placeholder="请输入内容"></el-input> -->
      <img v-if="this.$store.state.talks.sendStatus == 0" src="./send.png" class="w-72 h-72" @click="send('text', value)" />
      <div v-if="this.$store.state.talks.sendStatus == 1" class="loading2"></div>
    </div>
  </div>
</template>

<script>
import { _sendMsg, _getMsg, _uploadImage ,uploadImageChange} from "@/api/chat.api";
import { openImage} from "@/common/image";
export default {
  name: "Chat",
  data() {
    return {
      allChatList: [],
      chatList: [],
      value: "", //客服输入的消息
      lastMsgId: "",
      interval: null,
      unread: 0,
      loading: false,
      finished: false, // 没有历史消息
      msg:{},
      order_no:0,
      isShow:false,
      lastId:"",
      status:{}
    };
  },
  created() {
    
    // const model=navigator.userAgent;
    // 判断是否是安卓手机，是则是true
    // this.androidAttrs=model.indexOf('Android') > -1 || model.indexOf('Linux') >-1
    // _getMsg().then(data => {
    //     console.log(data)
    // })
  },
  beforeDestroy() {
    this.clearInterval();
    this.isShow = false;
  },
  methods: {
    // onPreview(url) { // 预览
    //     ImagePreview([url])
    // },
    openLoad(id){
      this.status[id] = 1;
     let timer = setTimeout(() => {
        clearTimeout(timer);
        this.status[id] = 2;
      },1500)
    },
    open(img) {
      openImage(img);
    },
    close(){
      this.isShow = false;
      this.order_no = 0;
      this.clearInterval();
      this.chatList = [];
      this.allChatList = [];
    },
    init(msg1){
      this.isShow = true;
      this.clearInterval();
      this.chatList = [];
      this.order_no = msg1.order_no || 0;
      this.msg = msg1;
      this.fetchList();
      console.log("index = " + JSON.stringify(this.msg));
    },
    test() {
      this.$refs["upload"].click();
      console.log(111);
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
    },
    onImageFileChanged(e) {
      const file = e.target.files[0];
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isLt2M) {
          Toast.fail('上传头像图片大小不能超过 2MB!');
          return false;
      }
      this.afterRead(file);
    },
    afterRead(file) {
      // 文件上传
      this.loading = true;
      uploadImageChange(file,(url)=>{
        if(!url){
          this.loading = false;
          return;
        }
        _sendMsg("img", url,this.msg.order_no||"",this.msg.party_id||"").then((data) => {
          console.log(data.code)
          console.log("已经发送消息了", data);
          this.value = "";
          this.scrollToBottom();
          // document.getElementById('bottom').click()
          this.fetchList();
          this.loading = false;
        });
      })
      // _uploadImage(file, (percent) => {
      //   console.log(percent);
      // })
      //   .then((data) => {
      //     this.loading = false;
      //     this.send("img", data);
      //   })
      //   .catch(() => {
      //     this.loading = false;
      //   });
    },
    fetchList(message_id = "") {
      // 获取消息列表
      _getMsg(message_id,this.msg.order_no||"",this.msg.party_id||"").then((data1) => {
        if(this.msg.order_no != this.order_no){
          this.chatList = [];
        }
        let data = data1.data;
        // console.log("获取消息列表 = " + JSON.stringify(data));
        // this.lastMsgId
        if (!this.lastMsgId) {
          this.lastMsgId = data.length && data[data.length - 1]["id"];
        }
        //
        if (data.length) {
          if(!this.allChatList[this.msg.order_no]){
            this.allChatList[this.msg.order_no] = [];
          }
          if (message_id) {
            // 加载更多
            this.lastMsgId = data[data.length - 1]["id"];
            this.allChatList[this.msg.order_no] = [...data.reverse(), ...this.allChatList[this.msg.order_no]];
          } else {
            this.allChatList[this.msg.order_no] = [...this.allChatList[this.msg.order_no], ...data.reverse()];
            let hash = {};
            this.allChatList[this.msg.order_no] = this.allChatList[this.msg.order_no].reduce(function (preVal, curVal) {
              hash[curVal.id]
                ? " "
                : (hash[curVal.id] = true && preVal.push(curVal));
              return preVal;
            }, []);
          }
          this.chatList = [...this.allChatList[this.msg.order_no]]
          if (data.length < 10) {
            this.finished = true;
          }
        } else {
          this.chatList = [
            // {
            //   id: "1",
            //   send_receive: "receive",
            //   content: "你好，欢迎来到我们的数字货币平台!",
            //   type: "text",
            // },
          ];
        }
        if (!message_id) {
          this.clearInterval();
          console.log("this.isShow = " + this.isShow);
          if(this.isShow){
            this.interval = setInterval(() => {
              this.fetchList();
            }, 1000);
          }
        }
        if (!this.lastMsgId) {
          this.scrollToBottom();
        }
        if(data.length > 0){
          if(this.lastId!=data[data.length - 1]["id"]){
            this.lastId = data[data.length - 1]["id"];
            this.scrollToBottom();
            if(data[data.length - 1]["send_receive"] != "receive"){
              this.playAudioOfMute();
            }
          }
        }
        
      });
    },
    scrollToBottom() {
      let timer = setTimeout(() => {
        clearTimeout(timer);
        this.$nextTick(() => {
        this.$refs["scroll"].wrap.scrollTop = 20000000;
      });
      }, 500);
      // this.$nextTick(() => {
      //   let scrollElem = this.$refs.scroll;
      //   scrollElem.scrollTo({ top: scrollElem.scrollHeight, behavior: 'smooth' });
      // });
    },
    onMore() {
      // 加载更多
      this.fetchList(this.lastMsgId);
    },
    clearInterval() {
      if (this.interval) {
        clearInterval(this.interval);
        this.interval = null;
      }
    },
    fetchUnread() {
      // 获取未读
      _getUnreadMsg(this.msg.order_no||"",this.msg.party_id||"").then((data) => {
        this.unread = data;
        // console.log(data)
      });
    },
    send(type = "text", content = "") {
      // 发送消息, img 也当消息text
      if (!content) {
        const text = "请输入消息内容";
        this.$message(text);
        return;
      }
      if(this.$store.state.talks.sendStatus == 1){return;}
      this.$store.commit('SET_SEND_STATUS', 1)
      let timer = setTimeout(() => {
            clearTimeout(timer);
            this.$store.commit('SET_SEND_STATUS', 0);
        },5000);
      _sendMsg(type, content,this.msg.order_no||"",this.msg.party_id||"").then((data) => {
        console.log("已经发送消息了", data);
        this.$store.commit('SET_SEND_STATUS', 0)
        this.value = "";
        this.scrollToBottom();
        // document.getElementById('bottom').click()
        this.fetchList();
      });
    },
  },
  beforeDestroy() {
    this.clearInterval();
    this.isShow = false;
  },
};
</script>

<style scoped>
/deep/ .el-scrollbar .el-scrollbar__wrap {
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
  /* color: #fff; */
  color:#000;
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
  padding: 15px 10px;
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
  position: absolute;
  top: 10px;
  border-left: 20px solid #1ebafc;
  right: -10px;
  /* background-color: #1ebafc; */
  color: #1ebafc;
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

<style>
.loading {
  position: relative;
  width: 200px;
  height: 200px;
  border: 2px solid #000;
  border-top-color: rgba(0, 0, 0, 0.2);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.2);
  border-radius: 100%;

  animation: circle infinite 0.75s linear;
}

@keyframes circle {
  0% {
    transform: rotate(0);
  }
  100% {
    transform: rotate(360deg);
  }
}


.loading2 {
  position: relative;
  left: 0%;
  top: 0%;
  width: 33px;
  height: 33px;
  border: 2px solid #000;
  border-top-color: rgba(0, 0, 0, 0.2);
  border-right-color: rgba(0, 0, 0, 0.2);
  border-bottom-color: rgba(0, 0, 0, 0.2);
  border-radius: 100%;

  animation: circle infinite 0.75s linear;
}

@keyframes circle {
  0% {
    transform: rotate(0);
  }
  100% {
    transform: rotate(360deg);
  }
}



</style>