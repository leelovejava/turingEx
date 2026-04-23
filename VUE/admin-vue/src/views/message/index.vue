<template>
  <div class="mod-transport">
      <el-container slot="container" class="full-height mod-transport">
        <!-- 左侧侧边栏 -->
        <el-aside width="320px" class="aside-box full-height">
          <el-container class="full-height" direction="vertical">
            <!-- 搜索栏 -->
            <div class="header" style="height: 87px;">
              <div class="from-search">
                <el-input
                  v-model="input"
                  prefix-icon="el-icon-search"
                  placeholder="搜索成功后接手该用户"
                  size="small"
                />
              </div>

              <!-- 工具栏 -->
              <div class="tools">
                <el-button
                  circle
                  plain
                  size="small"
                  icon="el-icon-search"
                  @click="search()"
                />
              </div>
            </div>

            <!-- 对话列表栏 -->
            <el-scrollbar
              tag="section"
              ref="menusScrollbar"
              :native="false"
              :style="{'height': (this.documentClientHeight-87)+'px','max-height': (this.documentClientHeight-87)+'px'}"
            >
              <el-main class="main">

                <!-- 对话列表 -->
                <template>
                  <div
                    v-for="item in talks"
                    :key="item.id"
                    class="talk-item pointer"
                    :class="{ active: index_name == item.id }"
                    @click="clickTab(item)"
                  >
                    <div class="">
                      <img v-if="index_name == item.id"
                        style="width: 50px; height: 50px"
                        src=".././../assets/img/avatar1.png"
                        alt=""
                      />
                      <img v-if="index_name != item.id"
                        style="width: 50px; height: 50px"
                        src=".././../assets/img/avatar2.png"
                        alt=""
                      />
                    </div>
                    <div class="card-box">
                      <div class="title">
                        <div class="card-name">
                          <p class="nickname">
                            {{
                              item.remarks ? item.username+"("+item.remarks+")" : item.username
                            }}
                          </p>
                        </div>
                        
                        

                        <div style="margin-top: 20px;margin-right: 25px;">
                            <el-badge class="mark" :value="item.unreadmsg" v-if="item.unreadmsg > 0"/>
                        </div>

                      </div>
                      <div class="title">
                        <div class="card-name">
                          <p class="nickname-1">
                            {{
                              item.content
                            }}
                          </p>
                        </div>

                        <div class="card-time">
                          <p>{{ item.updatetime }}</p>
                        </div>

                      </div>
                    </div>
                  </div>
                </template>
              </el-main>
            </el-scrollbar>
          </el-container>
        </el-aside>

        <!-- 聊天面板容器 -->
        <el-main class="ov-hidden full-height no-padding padding0">
          <TalkPanel
            ref="talkPanel"
            class="full-height"
            :params="params"
            :is-online="isFriendOnline"
            @change-talk="changeTalk"
            @close-talk="closeTalk"
            @event="handleTalkPanelEvent"
          />
        </el-main>

        <el-dialog title="" v-show="imageViewer.isShow">
          <!-- 图片查看器 -->
          <MeEditorImageView
            ref="imageViewer"
            v-model="imageViewer.isShow"
            :file="imageViewer.file"
            @confirm="confirmUploadImage"
            @close="handleClose"
          />
        </el-dialog>

      </el-container>
      <!-- <div v-if="this.$store.state.talks.clinkStatus == 1" style="background-color: white;width: 100%;height:100%;
                top:0;left:0;opacity: 0.8;
                z-index: 3000;position:fixed;">
          <div class="loading"></div> 
          <div class="loadingText">消息加载中...</div>          
      </div> -->
      
  </div>
</template>
<script>
import { mapGetters, mapState } from 'vuex'
import WelcomeModule from '@/components/layout/WelcomeModule'
import GroupLaunch from '@/components/group/GroupLaunch'
import TalkPanel from '@/components/chat/panel/TalkPanel'
import UserSearch from '@/components/user/UserSearch'
import MeEditorImageView from '@/components/editor/MeEditorImageView'
import {
  createNewAdminOnlineChatAction,
  userlistNewAdminOnlineChatAction,
  getUserInfoNewAdminOnlineChatAction,
  unreadNewAdminOnlineChatAction,
} from '@/api/chat'

import { findTalkIndex } from '@/utils/talk'

const title = document.title

export default {
  name: 'MessagePage',
  components: {
    GroupLaunch,
    TalkPanel,
    UserSearch,
    WelcomeModule,
    MeEditorImageView,
  },
  data() {
    return {
      subHeaderShadow: false,
      launchGroupShow: false,

      // 对话面板的传递参数
      params: {
        talk_type: 0,
        receiver_id: 0,
        nickname: '',
      },

      // 查询关键词
      input: '',

      // header 工具菜单
      subMenu: false,
      menus: [
        {
          name: '新的联系人',
          path: '/contacts/apply',
        },
        {
          name: '我的好友',
          path: '/contacts/friends',
        },
        {
          name: '我的群组',
          path: '/contacts/groups',
        },
      ],

      // 消息未读数计时器
      interval: null,
      loadStatus:0,
      index_name:1,
      talks: [],
      talks1: [
            {
                "index_name":0,
                "avatar": "",
                "gender": 0,
                "group_id": 0,
                "id": 5,
                "is_online": 0,
                "motto": "",
                "nickname": "test0",
                "remark": "test0",
                "avatar": "",
                "friend_apply": 0,
                "friend_status": 2,
                "gender": 0,
                "id": 5,
                "mobile": "18798272053",
                "motto": "",
                "nickname": "test0",
                "nickname_remark": "test0",
                "remark_name":"test0",
                "updated_at":"2023-07-03 07:33:06"
            },
            {
                "index_name":1,
                "avatar": "",
                "gender": 0,
                "group_id": 0,
                "id": 5,
                "is_online": 0,
                "motto": "",
                "nickname": "test3",
                "remark": "test3",
                "avatar": "",
                "friend_apply": 0,
                "friend_status": 2,
                "gender": 0,
                "id": 5,
                "mobile": "18798272053",
                "motto": "",
                "nickname": "test3",
                "nickname_remark": "test3",
                "remark_name":"test3",
                "updated_at":"2023-07-03 07:33:06"
            },
            {
                "index_name":2,
                "avatar": "",
                "gender": 0,
                "group_id": 0,
                "id": 5,
                "is_online": 0,
                "motto": "",
                "nickname": "test3",
                "remark": "test3",
                "avatar": "",
                "friend_apply": 0,
                "friend_status": 2,
                "gender": 0,
                "id": 5,
                "mobile": "18798272053",
                "motto": "",
                "nickname": "test3",
                "nickname_remark": "test3",
                "remark_name":"test3",
                "updated_at":"2023-07-03 07:33:06"
            },
            {
                "index_name":3,
                "avatar": "",
                "gender": 0,
                "group_id": 0,
                "id": 5,
                "is_online": 0,
                "motto": "",
                "nickname": "test3",
                "remark": "test3",
                "avatar": "",
                "friend_apply": 0,
                "friend_status": 2,
                "gender": 0,
                "id": 5,
                "mobile": "18798272053",
                "motto": "",
                "nickname": "test3",
                "nickname_remark": "test3",
                "remark_name":"test3",
                "updated_at":"2023-07-03 07:33:06"
            },
            {
                "index_name":4,
                "avatar": "",
                "gender": 0,
                "group_id": 0,
                "id": 5,
                "is_online": 0,
                "motto": "",
                "nickname": "test3",
                "remark": "test3",
                "avatar": "",
                "friend_apply": 0,
                "friend_status": 2,
                "gender": 0,
                "id": 5,
                "mobile": "18798272053",
                "motto": "",
                "nickname": "test3",
                "nickname_remark": "test3",
                "remark_name":"test3",
                "updated_at":"2023-07-03 07:33:06"
            },
            {
                "index_name":5,
                "avatar": "",
                "gender": 0,
                "group_id": 0,
                "id": 5,
                "is_online": 0,
                "motto": "",
                "nickname": "test3",
                "remark": "test3",
                "avatar": "",
                "friend_apply": 0,
                "friend_status": 2,
                "gender": 0,
                "id": 5,
                "mobile": "18798272053",
                "motto": "",
                "nickname": "test3",
                "nickname_remark": "test3",
                "remark_name":"test3",
                "updated_at":"2023-07-03 07:33:06"
            },
            {
                "index_name":6,
                "avatar": "",
                "gender": 0,
                "group_id": 0,
                "id": 5,
                "is_online": 0,
                "motto": "",
                "nickname": "test3",
                "remark": "test3",
                "avatar": "",
                "friend_apply": 0,
                "friend_status": 2,
                "gender": 0,
                "id": 5,
                "mobile": "18798272053",
                "motto": "",
                "nickname": "test3",
                "nickname_remark": "test3",
                "remark_name":"test3",
                "updated_at":"2023-07-03 07:33:06"
            },
            {
                "index_name":7,
                "avatar": "",
                "gender": 0,
                "group_id": 0,
                "id": 5,
                "is_online": 0,
                "motto": "",
                "nickname": "test3",
                "remark": "test3",
                "avatar": "",
                "friend_apply": 0,
                "friend_status": 2,
                "gender": 0,
                "id": 5,
                "mobile": "18798272053",
                "motto": "",
                "nickname": "test3",
                "nickname_remark": "test3",
                "remark_name":"test3",
                "updated_at":"2023-07-03 07:33:06"
            },
            {
                "index_name":8,
                "avatar": "",
                "gender": 0,
                "group_id": 0,
                "id": 5,
                "is_online": 0,
                "motto": "",
                "nickname": "test3",
                "remark": "test3",
                "avatar": "",
                "friend_apply": 0,
                "friend_status": 2,
                "gender": 0,
                "id": 5,
                "mobile": "18798272053",
                "motto": "",
                "nickname": "test3",
                "nickname_remark": "test3",
                "remark_name":"test3",
                "updated_at":"2023-07-03 07:33:06"
            },
            {
                "index_name":9,
                "avatar": "",
                "gender": 0,
                "group_id": 0,
                "id": 5,
                "is_online": 0,
                "motto": "",
                "nickname": "test3",
                "remark": "test3",
                "avatar": "",
                "friend_apply": 0,
                "friend_status": 2,
                "gender": 0,
                "id": 5,
                "mobile": "18798272053",
                "motto": "",
                "nickname": "test3",
                "nickname_remark": "test3",
                "remark_name":"test3",
                "updated_at":"2023-07-03 07:33:06"
            },
            {
                "index_name":10,
                "avatar": "",
                "gender": 0,
                "group_id": 0,
                "id": 5,
                "is_online": 0,
                "motto": "",
                "nickname": "test33",
                "remark": "test33",
                "avatar": "",
                "friend_apply": 0,
                "friend_status": 2,
                "gender": 0,
                "id": 5,
                "mobile": "18798272053",
                "motto": "",
                "nickname": "test33",
                "nickname_remark": "test33",
                "remark_name":"test33",
                "updated_at":"2023-07-03 07:33:06"
            }
      ],
      userInfo:{},
      dialogTableVisible: false,
      dialogFormVisible: false,
      imageViewer:{},
      talksMap:{}
    }
  },
  computed: {
    ...mapGetters(['topItems', 'talkItems', 'unreadNum', 'talkNum']),
    ...mapState({
    }),
    // 计算置顶栏目的高度
    subHeaderPx() {
      const n = 7 // 一排能显示的用户数
      const num = this.topItems.length
      let len = 60

      if (num > n) {
        len = (Math.floor(num / n) + (num % n > 0 ? 1 : 0)) * len
      }

      return `${len}px`
    },
    // 当前对话好友在线状态
    isFriendOnline() {
      let index = findTalkIndex(this.index_name)
      return index >= 0 && this.talks[index].is_online == 1
    },
    documentClientHeight: {
      get () { return this.$store.state.common.documentClientHeight },
      set (val) { this.$store.commit('common/updateDocumentClientHeight', val) }
    },
  },
  watch: {
  },
  created() {
    unreadNewAdminOnlineChatAction({},(data)=>{});
    this.userlist();
  },
  mounted() {
    this.resetDocumentClientHeight()
    this.scrollEvent()
    // 为全局事件总线绑定自定义事件
    this.$bus.$on('SET_IMAGE_VIEWER', (data)=>{
      console.log("data = " + JSON.stringify(data));
      this.imageViewer.isShow = data.isShow;
      this.imageViewer.file = data.file;
      console.log("this.imageViewer => " + JSON.stringify(this.imageViewer));
    })
    this.$bus.$on('updateCurrentUserChatList', (data)=>{
      this.updateCurrentUserChatList(data);
    })

    //获取聊天记录
    this.interval && clearInterval(this.interval)
    this.interval = setInterval(() => {
      console.log("this.$route.path => " + this.$route.path)
      if(!this.IS_DEBUG &&  this.$route.path == "/message"){
        this.userlistTime();
        this.updateCurrentUserChatList({flag:false});
      }
    }, 5000)
  },
  destroyed() {
    // 解绑事件
    this.$bus.$off('SET_IMAGE_VIEWER')
    this.$bus.$off('updateCurrentUserChatList')

    document.title = title
    clearInterval(this.interval)
    this.clearTalk()
  },
  methods: {
    // 重置窗口可视高度
    resetDocumentClientHeight () {
      this.documentClientHeight = document.documentElement['clientHeight'] - 80
      window.onresize = () => {
        this.documentClientHeight = document.documentElement['clientHeight'] - 80
      }
      document.documentElement.clientHeight
    },
    handleClose(){
      store.commit('SET_IMAGE_VIEWER', {})
    },
    // 美化时间格式
    // beautifyTime,

    // header 功能栏隐藏事件
    closeSubMenu() {
      this.subMenu = false
    },

    // 清除当前对话
    clearTalk() {
      this.params = {
        talk_type: 0,
        receiver_id: 0,
        nickname: '',
      }

      this.$store.commit('UPDATE_DIALOGUE_MESSAGE', {
        talk_type: 0,
        receiver_id: 0,
        is_robot: 0,
      })
    },

    // 工具栏事件
    triggerSubMenu(type) {
      this.closeSubMenu()

      if (type == 1) {
        this.launchGroupShow = true
      } else {
        this.$refs.searchUsers.open()
      }
    },

    // 监听自定义滚动条事件
    scrollEvent() {
      let scrollbarEl = this.$refs.menusScrollbar.wrap
      scrollbarEl.onscroll = () => {
        this.subHeaderShadow = scrollbarEl.scrollTop > 0
      }
    },

    // 发起群聊成功后回调方法
    groupChatSuccess(data) {
      this.launchGroupShow = false
      sessionStorage.setItem('send_message_index_name', `2_${data.group_id}`)
      this.$store.dispatch('LOAD_TALK_ITEMS')
    },

    // 切换聊天栏目
    clickTab(data) {
      console.log(" this.$store.state.talks.status = " +  this.$store.state.talks.status);
      // if(this.$store.state.talks.status == 0){
        this.$store.commit('SET_CLINK_STATUS', 1)
        this.index_name = data.id;
        this.getUserInfo(data.partyid);
      // }
      return;
    },
    //用户信息
    getUserInfo(id){
      console.log("this.$store.state.talks.status = " + this.$store.state.talks.status);
      // if(this.$store.state.talks.status == 0){
        this.$store.commit('SET_STATUS', 1)
        console.log(" this.$store.state.talks.status = " +  this.$store.state.talks.status);
        getUserInfoNewAdminOnlineChatAction({partyId: id||""},(data)=>{
          if(data==undefined){
            this.$store.commit('SET_STATUS', 0)
            console.log(" this.$store.state.talks.status = " +  this.$store.state.talks.status);
            return;
          }
          console.log("this.$store.state.talks.status = " + JSON.stringify(data));
          if(data.code == 0){
            this.$store.commit('SET_CLINK_STATUS', 0)
            this.userInfo = data.data || {};
            this.$refs.talkPanel.selectUser(this.userInfo,true);
          }
        })
      // }

     
    },
    userlist(){
      userlistNewAdminOnlineChatAction({},(data) => {
        if(data==undefined){
          this.$store.commit('SET_STATUS', 0)
          console.log(" this.$store.state.talks.status = " +  this.$store.state.talks.status);
          return;
        }
        if(data.code == 0){
          this.talks = data.data || [];
          // console.log("this.talks2 => " + JSON.stringify(this.talks));
          if(this.talks.length > 0){
            // console.log("data.data => "+JSON.stringify(data.data));
            this.index_name = this.talks[0].id;
            this.getUserInfo(this.talks[0].partyid);
          }else{
            // this.$refs.panelHeader.selectUser({});
            console.log("clear");
            this.$refs.talkPanel.clearHeader();
            this.userInfo = {}

          }
        }else{
          console.log("clear 2");

          // this.$refs.panelHeader.selectUser({});
          this.$refs.talkPanel.clearHeader();
          this.userInfo = {}
        }
        
      });
    },
    userlistTime(){
      userlistNewAdminOnlineChatAction({},(data) => {
        if(data==undefined){
          this.$store.commit('SET_STATUS', 0)
          console.log(" this.$store.state.talks.status = " +  this.$store.state.talks.status);
          return;
        }
        if(data.code == 0){
          this.talks = data.data || [];
          // console.log("this.talks1 => " + JSON.stringify(this.talks));
          if(this.talks.length <= 0){
            this.$refs.talkPanel.clearHeader();
            this.userInfo = {}
          }else{
            // console.log("this.talks3 => " + JSON.stringify(this.talks));
            // this.index_name = this.talks[0].id;
            // this.getUserInfo(this.talks[0].partyid);
            let flag = false;
            for(let i = 0 ; i < this.talks.length ; i++){
              if(this.talks[i].unreadmsg > 0){

                if(this.talksMap[this.talks[i].partyid]){
                  let date1s = this.talks[i].order_updatetime;
                  let date2s = this.talksMap[this.talks[i].partyid].order_updatetime;
                  var date1 = new Date(date1s)
                  var date2 = new Date(date2s)
                  if (date1.getTime() > date2.getTime()) {
                      console.log("新消息")
                      flag = true;
                  } else {
                      console.log("旧消息")
                  }
                }else{
                  flag = true;
                }
              }

              if(flag){
                this.playAudioOfMute();
              }

              
              this.talksMap[this.talks[i].partyid] = this.talks[i];
            }


          }

        }else{
          this.$refs.talkPanel.clearHeader();
          this.userInfo = {}
        }
        
      });
    },
    //刷新当前聊天记录
    updateCurrentUserChatList(data){
      // if(this.$store.state.talks.status == 0){
        this.$store.commit('SET_STATUS', 1)
        console.log(" this.$store.state.talks.status = " +  this.$store.state.talks.status);
        this.$refs.talkPanel.selectUser(this.userInfo,data.flag);
      // }
    },
    // 修改当前对话
    changeTalk(index_name) {
      sessionStorage.setItem('send_message_index_name', index_name)
      this.$store.dispatch('LOAD_TALK_ITEMS')
    },

    // 关闭当前对话及刷新对话列表
    closeTalk() {
      this.$store.commit('UPDATE_DIALOGUE_MESSAGE', {
        talk_type: 0,
        receiver_id: 0,
        is_robot: 0,
      })

      this.$store.dispatch('LOAD_TALK_ITEMS')
    },

    // 对话列表的右键自定义菜单
    talkItemsMenu(item, event) {
      let items = {
        items: [
          {
            label: '好友信息',
            icon: 'el-icon-user',
            disabled: item.talk_type == 2 || item.is_robot == 1,
            onClick: () => {
              this.$user(item.receiver_id)
            },
          },
          {
            label: '修改备注',
            icon: 'el-icon-edit-outline',
            disabled: item.talk_type == 2 || item.is_robot == 1,
            onClick: () => {
              this.editFriendRemarks(item)
            },
          },
          {
            label: item.is_top == 0 ? '会话置顶' : '取消置顶',
            icon: 'el-icon-top',
            onClick: () => {
              this.topChatItem(item)
            },
          },
          {
            label: item.is_disturb == 0 ? '消息免打扰' : '开启消息提示',
            icon:
              item.is_disturb == 0
                ? 'el-icon-close-notification'
                : 'el-icon-bell',
            disabled: item.is_robot == 1,
            onClick: () => {
              this.setNotDisturb(item)
            },
          },
          {
            label: '移除会话',
            icon: 'el-icon-remove-outline',
            divided: true,
            onClick: () => {
              this.delChatItem(item)
            },
          },
          {
            label: item.talk_type == 1 ? '删除好友' : '退出群聊',
            icon: 'el-icon-delete',
            disabled: item.is_robot == 1,
            onClick: () => {
              let title = item.talk_type == 1 ? '删除好友' : '退出群聊'
              this.$confirm(
                `此操作将 <span style="color:red;font-size:16px;">${title}</span>, 是否继续?`,
                '提示',
                {
                  confirmButtonText: '确定',
                  cancelButtonText: '取消',
                  type: 'warning',
                  center: true,
                  dangerouslyUseHTMLString: true,
                }
              ).then(() => {
                if (item.talk_type == 1) {
                  // this.removeFriend(item)
                } else {
                  // 退出群聊
                  // this.removeGroup(item)
                }
              })
            },
          },
        ],
        event: event,
        zIndex: 3,
      }

      this.$contextmenu(items)
      return false
    },

    // 置顶栏右键菜单栏
    topItemsMenu(item, event) {
      this.$contextmenu({
        items: [
          {
            label: item.is_top == 0 ? '会话置顶' : '取消置顶',
            icon: 'el-icon-top',
            onClick: () => {
              this.topChatItem(item)
            },
          },
        ],
        event: event,
        zIndex: 3,
      })

      return false
    },

    // 会话列表置顶
    topChatItem(item) {
    },

    // 设置消息免打扰
    setNotDisturb(item) {
    },

    // 移除会话列表
    delChatItem(item) {
    },

    // 解除好友关系
    removeFriend(item) {
    },

    // 退出群聊
    removeGroup(item) {
    },

    // 修改好友备注信息
    editFriendRemarks(item) {
      let title = `您正在设置【${item.name}】好友的备注信息`

      if (item.remark_name) {
        title += `，当前备注为【${item.remark_name}】`
      }

      this.$prompt(title, '修改备注', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        customClass: 'border-radius0',
        inputPlaceholder: '请设置好友备注信息',
        inputValue: item.remark_name ? item.remark_name : item.name,
        inputValidator(val) {
          return val == null || val == '' ? '好友备注不能为空' : true
        },
      })
        .then(({ value }) => {
          if (value == item.remark_name) {
            return false
          }

        })
        .catch(() => {})
    },

    //搜索
    search(){
      this.$store.commit('SET_STATUS', 0)
      console.log(" this.$store.state.talks.status = " +  this.$store.state.talks.status);
      console.log("search = " + this.input);
      createNewAdminOnlineChatAction({partyId: this.input},(data)=>{
        if(data.code == 0){
          this.userlist();
        }
      })
    },
    // 处理 Header 组件事件
    handleTalkPanelEvent(event_name) {
      // console.log(JSON.stringify(event_name));
      switch (event_name.type) {
        case 'userlist':
          this.userlist();
          break
        case 'remarks':
          this.userInfo.remarks = event_name.data;
          // this.talks.forEach(item => {if (item.id == this.userInfo.id) {item.remark_name = event_name.data}});
          for(let i = 0 ; i < this.talks.length ; i++){
            if(this.talks[i].id == this.index_name){
              this.talks[i].remarks = event_name.data;
            }
          }
          // console.log(JSON.stringify(this.talks));
          break
        case 'setting':
          this.group.panel = !this.group.panel
          break
      }
    },

    // 确认上传图片消息回调事件
    confirmUploadImage() {
      // const { imageViewer } = this.$store.state.talk

      let ref = this.$refs.imageViewer
      console.log("imageViewer.file = " + JSON.stringify(this.imageViewer.file));

      const isLt2M = this.imageViewer.size / 1024 / 1024 < 2;
        if (!isLt2M) {
            Toast.fail('上传图片大小不能超过 2MB!');
            return false;
        }

      this.$http({
        url: this.$http.adornUrl('/api/uploadFile'),
        method: 'post',
        data: this.$http.adornData(
          Object.assign(
            {
              file: this.imageViewer.file,
            }
          )
        )
      }).then(({ data }) => {
        //
        console.log("uploaded image = " + JSON.stringify(data));
        if(data.code == 0){

        }else{
          this.$message(''+data.msg)
        }
        //
      })
      .finally(() => {
        ref.loading = false
      })
    },

  },
}
</script>
<style lang="less" scoped>
/deep/.el-scrollbar__wrap {
  overflow-x: hidden;
}

.aside-box {
  position: relative;
  border-right: 1px solid rgb(245, 245, 245);
  overflow: hidden;
  padding: 0;
  transition: width 0.3s;

  .header {
    display: flex;
    flex-direction: row;
    align-items: center;
    padding: 0 15px;

    .from-search {
      flex: 1 1;
      flex-shrink: 0;

      /deep/ .el-input .el-input__inner {
        border-radius: 20px;
      }
    }

    .tools {
      flex-basis: 32px;
      flex-shrink: 0;
      height: auto;
      margin-left: 15px;
      cursor: pointer;
      text-align: center;
      position: relative;
      user-select: none;

      .tools-menu {
        position: absolute;
        right: 0;
        top: 38px;
        width: 100px;
        min-height: 80px;
        box-sizing: border-box;
        background-color: rgba(31, 35, 41, 0.9);
        border-radius: 5px;
        z-index: 1;
        padding: 3px 0;

        .menu-item {
          height: 40px;
          line-height: 40px;
          color: white;
          font-size: 14px;

          &:hover {
            background-color: rgba(70, 72, 73, 0.9);
          }
        }
      }
    }
  }

}

.aside-box .main {
  overflow: hidden;
  padding: 0;

  .empty-data {
    text-align: center;
    padding-top: 40px;
    color: #ccc;
  }

  .main-menu {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    padding: 6px 10px 6px 10px;
    align-items: center;
    user-select: none;

    .title {
      font-size: 12px;
      font-weight: 600;
      color: #1f2329;
    }

    .icon {
      cursor: pointer;
    }
  }

  .talk-item {
    padding: 4px 0px;
    height: 94px;
    display: flex;
    flex-direction: row;
    align-items: center;
    border-left: 3px solid transparent;
    transition: 0.2s ease-in;
    cursor: pointer;
    // background-color: #4aa71c;


    .card-box {
      height: 50px;
      align-content: center;
      flex: 1 1;
      margin-left: 10px;
      overflow: hidden;
      
      .title {
        width: 100%;
        height: 25px;
        display: flex;
        align-items: center;

        .card-name {
          color: #1f2329;
          font-size: 14px;
          line-height: 20px;
          flex: 1 1;
          display: -webkit-flex;
          display: -ms-flexbox;
          display: flex;
          align-items: center;
          overflow: hidden;

          .nickname {
            font-weight: 400;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            margin-right: 3px;

            font-family: PingFang SC;
            font-size: 16px;
            font-weight: 600;
            line-height: 22px;
            letter-spacing: 0em;
            text-align: left;


          }

          .nickname-1 {
            font-weight: 400;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            margin-right: 3px;

            font-family: PingFang SC;
            font-size: 14px;
            font-weight: 400;
            line-height: 20px;
            letter-spacing: 0em;
            text-align: left;

          }

          .top {
            color: #dc9b04 !important;
            background-color: #faf1d1 !important;
          }

          .group {
            color: #3370ff !important;
            background-color: #e1eaff !important;
            font-size: 13px;
          }

          .disturb {
            color: #98999c !important;
            background-color: #ecedf1 !important;
            i {
              font-size: 12px;
            }
          }
        }
      }

      .card-time {
        color: #8f959e;
        font-size: 12px;
        margin-left: 10px;
        user-select: none;
      }

      .content {
        font-size: 13px;
        line-height: 18px;
        color: #8f959e;
        margin-top: 3px;
        font-weight: 300;
        overflow: hidden;
        white-space: nowrap;
        text-overflow: ellipsis;

        span:first-child {
          margin-right: 5px;
        }

        .online-color {
          color: #4aa71c;
          font-weight: 300;
        }

        .draft-color {
          color: red;
          font-weight: 300;
        }
      }
    }
    &:hover {

      background-color: #f3f5fa;
    }

    &.active {
      .avatar-box {
        border-radius: 5px;
      }

      background-color: #dfe6fc;
    }
  }
}



@media screen and (max-width: 800px) {
  .aside-box {
    width: 220px !important;

    .subheader {
      display: none;
    }

    .card-box .larkc-tag {
      display: none;
    }
  }
}

.menu-list {
  height: 42px;
  display: flex;
  align-items: center;
  padding-left: 20px;
  cursor: pointer;
  border-left: 3px solid white;
  position: relative;
  transition: 0.2s ease-in;
  font-size: 14px;
  color: black;

  &:hover {
    background-color: #eff0f1;
    border-color: #eff0f1;
  }

  &.selectd {
    border-color: #3370ff !important;
    background-color: #eff0f1;
  }
}

</style>


<style lang="scss" scoped>
.mod-transport {
  height: 100%;
  min-height: 759px;
  padding: 0;
}

.padding0{
  padding: 0;
}


.loading {
  position: relative;
  left: 50%;
  top: 50%;
  width: 120px;
  height: 120px;
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

.loadingText{
  position: relative;
  left: 50%;
  top: 50%;
  width: 120px;
  height: 50px;
  text-align: center;
  line-height: 50px;
}

</style>