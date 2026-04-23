<template>
  <div id="panel-header" class="header">
    <div class="module1 left-module left-1">

      <div style="width: 100%; height: 100%;display: flex;justify-content: space-between;">
        <div class="module" style="">
          <div>
            <span class="nickname">{{ params.username }}</span>
          </div>

          <div>
            <span class="nickname" v-if="!params.username && !params.role_name && params.partyId" >{{ params.partyId }}</span>
          </div>
          
          <div>
            <span class="nickname">{{ params.remarks ? '(' + params.remarks + ')' : '' }}</span>
          </div>
          
          
          <div class="pointer" style="height: auto;padding-left: 10px;" @click="editFriendRemarks(params)">
            <img src="~@/assets/img/ri_delete-bin-3-line.png" width="24px" height="25px"/>
          </div>

          <div>
            <span class="nickname">设置备注</span>
          </div>

        </div>

        <div class="module" style="height: auto;display: flex;justify-content: flex-end;">
          <div class="pointer" style="height: auto;margin-right: 40px;" @click="deleteChat(params)">
            <i class="el-icon-delete"></i>
          </div>
          <div class="pointer" style="height: auto;margin-right: 20px;" @click="close()">
            <img src="~@/assets/img/clear.png" width="28px" height="28px"/>
          </div>       
        </div>
      </div>


      
      

    </div>

    <div class="module left-module left-2">
      <span class="text1">UID:{{ params.usercode }}</span>
      <span class="text1">账户类型:<span style="color:#38E1A5;">{{ getRoleName(params.role_name) }}</span></span>
      <span class="text1">推荐人:{{ params.recom_parent_name }}</span>
      <span class="text1" v-if="params.role_name">登录IP:{{ params.loginIp }}</span>
      <span class="text1" v-if="!params.role_name&&params.partyId">登录IP:{{ params.partyId }}</span>
      <span class="text1">注册时间:{{ params.create_time }}</span>
      <span class="text1">最后登录时间:{{ params.last_login_time }}</span>
    </div>
  </div>
</template>
<script>

import {
  delNewAdminOnlineChatAction,
  resetRemarksNewAdminOnlineChatAction,
} from '@/api/chat'

export default {
  props: {
    data: {
      type: Object,
      default: () => {
        return {
          // role_name: "MEMBER",
          // recom_parent_name: null,
          // last_login_time: null,
          // create_time: "2023-01-25 22:19:34",
          // loginIp: null,
          // partyId: "ff80808185cb47530185e94c5bae0044",
          // usercode: "3987564",
          // remarks: null,
          // username: "test001"
        }
      },
    },
    online: {
      type: Boolean,
      default: false,
    },
    keyboard: {
      type: [Boolean, Number],
      default: false,
    },
  },
  data() {
    return {
      params: {
        // role_name: "MEMBER",
        // recom_parent_name: null,
        // last_login_time: null,
        // create_time: "2023-01-25 22:19:34",
        // loginIp: null,
        // partyId: "ff80808185cb47530185e94c5bae0044",
        // usercode: "3987564",
        // remarks: null,
        // username: "test001"
      }
    }
  },
  created() {
  },
  watch: {
    data(value) {
    },
    online(value) {
      this.setOnlineStatus(value)
    },
    keyboard(value) {
      this.isKeyboard = value
     let timer = setTimeout(() => {
        clearTimeout(timer);
        this.isKeyboard = false
      }, 2000)
    },
  },
  methods: {
    setOnlineStatus(value) {
      this.isOnline = value
    },
    setGroupNum(value) {
      this.groupNum = value
    },
    triggerEvent(event_name) {
      this.$emit('event', event_name)
    },
    //选择用户
    selectUser(data){
      this.params = data
    },
    // 修改好友备注信息
    editFriendRemarks(item) {
      let title = `您正在设置【${item.username}】的备注信息`

      if (item.remarks) {
        title += `，当前备注为【${item.remarks}】`
      }

      this.$prompt(title, '修改备注', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        customClass: 'border-radius0',
        inputPlaceholder: '请设置好友备注信息',
        inputValue: item.remarks ? item.remarks : item.username,
        inputValidator(val) {
          return val == null || val == '' ? '好友备注不能为空' : true
        },
      })
        .then(({ value }) => {
          if (value == item.remarks) {
            return false
          }

          resetRemarksNewAdminOnlineChatAction({
            partyid: this.params.partyId,
            remarks: value,
          },(data)=>{
            console.log("res = " + JSON.stringify(data));
            if (data.code == "0") {
              this.triggerEvent({type:"remarks",data:data.data});
              this.$notify({
                title: '成功',
                message: '好友备注修改成功...',
                type: 'success',
              })
              this.params.remarks = data.data;
            } else {
              this.$notify({
                title: '消息',
                message: data.msg,
                type: 'warning',
              })
            }
          })
        })
        .catch(() => {})
    },
    deleteChat(item){
      let title = `移除当前列表后聊天记录不会删除，产生新消息该用户将重新分配给在线客服`
      this.$confirm(title, '是否确认移除?', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          delNewAdminOnlineChatAction({partyId:this.params.partyId},(data)=>{
            if(data.code == 0){
              this.params = {}
              this.triggerEvent({type:"userlist"});
              this.$message({
                type: 'success',
                message: '删除成功!'
              });
            }else{
              this.$message({
                type: 'info',
                message: data.msg
              }); 
            }
          })
  
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          });          
        });
    },
    close(){
      //this.$router.push({ name: 'home' })
      this.$router.go(-1);
    },
    getRoleName(name){
      let names = {
        'MEMBER':'正式账号','GUEST':'演示账号','TEST':'试用账号'
        ,'':'游客',undefined:'游客',null:'游客'
        ,'AGENT':'代理商','AGENTLOW':'代理商'
      };
      return names[name];
    }
  },
}
</script>
<style lang="less" scoped>

  .pointer {
    cursor: pointer;
  }

  .header{
    // max-width: 1450px;
    // max-width: 1290px;
    width: auto;
    height: 87px;
    background-color: #2E2F33;
    // display: flex;
  }

#panel-header {
  align-items: center;
  justify-content: space-between;
  box-sizing: border-box;
  border-bottom: 1px solid #f5eeee;
  color: #FFFFFF;


  .module1 {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-shrink: 0;
  }

  .module {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
  }

  .left-1{
    background: linear-gradient(90deg, #121212 0%, #2F3032 51.04%, #6E6F75 100%),
    linear-gradient(0deg, #393939, #393939);
    // width: 1171px;
    height: 55px;
    // top: 204px;
    // left: 720px;
    border-radius: 0px 10px 0px 0px;

  }

  .left-2{
    background-color: #2E2F33;
    // width: 1171px
    height: 32px;
    // top: 259px
    // left: 720px

  }

  .left-module {
    padding-right: 0px;

    .nickname {
      // overflow: hidden;
      // white-space: nowrap;
      // text-overflow: ellipsis;
      font-family: PingFang SC;
      font-size: 18px;
      font-weight: 500;
      line-height: 25px;
      letter-spacing: 0em;
      text-align: center;

      padding-left: 10px;
    }

    .text1{
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
      font-family: PingFang SC;
      font-size: 12px;
      font-weight: 400;
      line-height: 17px;
      letter-spacing: 0em;
      text-align: center;

      font-family: PingFang SC;
      font-size: 12px;
      font-weight: 500;
      line-height: 17px;
      letter-spacing: 0em;
      text-align: center;

      padding-left: 10px;
    }

  }

  .center-module {
    flex-direction: column;
    justify-content: center;

    .online {
      color: #cccccc;
      font-weight: 300;
      font-size: 15px;

      &.color {
        color: #1890ff;
      }

      .online-status {
        position: relative;
        top: -1px;
        display: inline-block;
        width: 6px;
        height: 6px;
        vertical-align: middle;
        border-radius: 50%;
        position: relative;
        background-color: #1890ff;
        margin-right: 5px;

        &:after {
          position: absolute;
          top: -1px;
          left: -1px;
          width: 100%;
          height: 100%;
          border: 1px solid #1890ff;
          border-radius: 50%;
          -webkit-animation: antStatusProcessing 1.2s ease-in-out infinite;
          animation: antStatusProcessing 1.2s ease-in-out infinite;
          content: '';
        }
      }
    }

    .keyboard-status {
      height: 20px;
      line-height: 18px;
      font-size: 10px;
      animation: inputfade 600ms infinite;
      -webkit-animation: inputfade 600ms infinite;
    }
  }

  .right-module {
    display: flex;
    justify-content: flex-end;
    align-items: center;

    p {
      cursor: pointer;
      margin: 0 8px;
      font-size: 20px;
      color: #828f95;
      &:active i {
        font-size: 26px;
        transform: scale(1.3);
        transition: ease 0.5s;
        color: red;
      }
    }
  }
}

/* css 动画 */
@keyframes inputfade {
  from {
    opacity: 1;
  }

  50% {
    opacity: 0.4;
  }

  to {
    opacity: 1;
  }
}

@-webkit-keyframes inputfade {
  from {
    opacity: 1;
  }

  50% {
    opacity: 0.4;
  }

  to {
    opacity: 1;
  }
}

@-webkit-keyframes antStatusProcessing {
  0% {
    -webkit-transform: scale(0.8);
    transform: scale(0.8);
    opacity: 0.5;
  }

  to {
    -webkit-transform: scale(2.4);
    transform: scale(2.4);
    opacity: 0;
  }
}

@keyframes antStatusProcessing {
  0% {
    -webkit-transform: scale(0.8);
    transform: scale(0.8);
    opacity: 0.5;
  }

  to {
    -webkit-transform: scale(2.4);
    transform: scale(2.4);
    opacity: 0;
  }
}
</style>
