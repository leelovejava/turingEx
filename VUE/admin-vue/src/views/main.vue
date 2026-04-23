<template>
  <div
    class="site-wrapper"
    :class="{ 'site-sidebar--fold': sidebarFold }"
    v-loading.fullscreen.lock="loading"
    element-loading-text="拼命加载中"
  >
    <template v-if="!loading">
      <main-navbar />
      <main-sidebar v-if="!isShowService" />
      <div
        class="site-content__wrapper"
        :style="{ 'min-height': documentClientHeight + 'px' }"
        v-if="!isShowService"
      >
        <main-content />
      </div>
      <div
        class="site-content__wrapper"
        :style="{
          'min-height': documentClientHeight + 'px',
          'margin-left': '0px',
        }"
        v-if="isShowService"
      >
        <!-- <MainService /> -->
      </div>
      <audio src="" id="eventAudio" muted="muted"></audio>
    </template>
  </div>
</template>

<script>
import MainNavbar from "./main-navbar";
import MainSidebar from "./main-sidebar";
import MainContent from "./main-content";
// import MainService from "./main-service.vue";
import { menuTipAdd } from "../config";
import { isAuth } from "@/utils";
export default {
  data() {
    return {
      loading: true,
      isShowService: false,
      tips: [],
      isTimeUpdate:true,
    };
  },
  components: {
    MainNavbar,
    MainSidebar,
    MainContent,
    // MainService,
  },
  computed: {
    documentClientHeight: {
      get() {
        return this.$store.state.common.documentClientHeight;
      },
      set(val) {
        this.$store.commit("common/updateDocumentClientHeight", val);
      },
    },
    sidebarFold: {
      get() {
        return this.$store.state.common.sidebarFold;
      },
    },
    userId: {
      get() {
        return this.$store.state.user.id;
      },
      set(val) {
        this.$store.commit("user/updateId", val);
      },
    },
    userName: {
      get() {
        return this.$store.state.user.name;
      },
      set(val) {
        this.$store.commit("user/updateName", val);
      },
    },
    googleAuthBind: {
      get() {
        return this.$store.state.user.googleAuthBind;
      },
      set(val) {
        this.$store.commit("user/googleAuthBind", val);
      },
    },
    kefuInfo: {
      get() {
        return this.$store.state.common.kefuInfo;
      },
      set(val) {
        this.$store.commit("common/updateKefuInfo", val);
      },
    },
  },
  created() {
    Vue.prototype.main = this;

    this.getUserInfo();
    if (!this.IS_DEBUG) {
      // this.getNewTips();
      this.getTips();
    }
  },
  mounted() {
    this.resetDocumentClientHeight();
  },
  methods: {
    // 重置窗口可视高度
    resetDocumentClientHeight() {
      this.documentClientHeight = document.documentElement["clientHeight"];
      window.onresize = () => {
        this.documentClientHeight = document.documentElement["clientHeight"];
      };
    },
    // 获取当前管理员信息
    getUserInfo() {
      this.$http({
        url: this.$http.adornUrl("/sys/user/info"),
        method: "get",
        params: this.$http.adornParams(),
      }).then(({ data }) => {
        this.loading = false;
        this.userId = data.userId;
        this.userName = data.username;
        this.googleAuthBind = data.googleAuthBind;
        this.$login = true;
        this.kefuInfo = {};
        this.kefuInfo.isOnline = null;

        let isKefu = false;
        // this.setUsername(data.username);
        if (data.roleName && data.roleName.length) {
          for (let i = 0; i < data.roleName.length; i++) {
            let item = data.roleName[i];
            console.log("item = " + item);
            if (item.indexOf("客服") >= 0) {
              let timer = setTimeout(() => {
                clearTimeout(timer);
                this.$bus.$emit("isShowKefu", true);
              }, 500);
              Vue.prototype.$isKefu = true;
              //请求客服信息
              //console.log("客服");
              //console.log("this isKe = " + Vue.prototype.$isKefu);
              this.personalCustomer();
              isKefu = true;
              break;
            } else {
            }
          }
        }

        if (!isKefu) {
          this.kefuInfo = {};
          Vue.prototype.$isKefu = false;
          //console.log("不是客服");
        }
        if (data.roleName && data.roleName.length) {
          for (let i = 0; i < data.roleName.length; i++) {
            let item = data.roleName[i];
            console.log("item = " + item);
            if (item.indexOf("管理") >= 0) {
              let timer = setTimeout(() => {
                clearTimeout(timer);
                this.$bus.$emit("isShowKefu", true);
              }, 500);
              console.log("是管理");
            }
          }
        }
      });
    },
    personalCustomer() {
      this.$http({
        url: this.$http.adornUrl(
          "/normal/adminPersonalCustomerAction!personalCustomer.action"
        ),
        method: "get",
        data: this.$http.adornData({}),
      }).then(({ data }) => {
        console.log("personalCustomer = " + JSON.stringify(data));
        if (data.code == 0) {
          let dataForm = data.data;
          if (dataForm.online_state == 1) {
            this.kefuInfo.isOnline = true;
          } else {
            this.kefuInfo.isOnline = false;
          }
          console.log("kefuInfo = " + JSON.stringify(this.kefuInfo));
          this.$bus.$emit("updateKefuInfo", this.kefuInfo);
        } else {
          this.$message(data.msg);
          this.kefuInfo.isOnline = null;
        }
      });
    },
    getNewTips() {
      //getNewTips
      if (this.$login) {
        try {
          this.getNewTips2();
        } catch (e) {
          console.log(JSON.stringify(e));
        }
      }
      let timer = setTimeout(() => {
        clearTimeout(timer);
        this.getNewTips();
      }, 5000);
    },
    isShowTips(name, urls) {
      let map = {
        "/user-relation-basics": "user-relation/basics",
        "/user-relation-senior": "user-relation/senior",
      };
      let url = map[name];
      if (!url) {
        return true;
      }
      if (urls.indexOf(url) >= 0) {
        return true;
      }
      return false;
    },
    getNewTips2() {
      let urls1 = sessionStorage.getItem("menuUrls");
      let urls = JSON.parse(urls1);
      // console.log("urls = " + urls);
      //--------------------------------------------------------------

      var time_stamp = localStorage.getItem("timeStamp");
      var params = { timeStamp: time_stamp }; //1688432842000
      localStorage.setItem("timeStamp", new Date().getTime());
      //start
      this.$http({
        url: this.$http.adornUrl("/tip/"),
        method: "get",
        params: this.$http.adornParams(Object.assign({}, params)),
      })
        .then(({ data }) => {
          // console.log("getNewTips data = " + JSON.stringify(data));
          if (data.code == 0) {
            let timer = [];
            for (let i = 0; i < data.data.length; i++) {
              if (data.data[i].tip_message != null) {
                timer[i] = setTimeout(() => {
                  clearTimeout(timer[i]);
                  let type = data.data[i].tip_type;

                  if (urls && this.isShowTips(data.data[i].tip_url, urls)) {
                    //
                    switch (type) {
                      case "1": //充值
                        this.playAudioOfRecharge();
                        break;
                      case "2": //提现
                        this.playAudioOfWithdraw();
                        break;
                      case "3": //认证
                        this.playAudioOfRzzt();
                        break;
                      default:
                        this.playAudioOfMute();
                        break;
                    }
                    this.openNewURL(
                      data.data[i].tip_content_num,
                      data.data[i].tip_url,
                      data.data[i].tip_message
                    );
                    //
                  }
                }, 100 * i);
                //this.openURL(data.data[i]);
              }
            }

            //
            var temp = data.data;
            //
          } else if (data.code == 403) {
            this.$login = false;
            console.log("getNewTips catch1 = ");
          }
        })
        .catch((e) => {
          // console.log("getNewTips catch = "+JSON.stringify(e));
          this.$login = false;
        });

      // 交割单 OP_ADMIN_FUTURES_ORDER_TIP
      // 区块链充值模块 OP_ADMIN_RECHARGE_BLOCKCHAIN_TIP
      // 三方充值模块 OP_ADMIN_RECHARGE_TIP
      // 提现模块 OP_ADMIN_WITHDRAW_TIP
      // 认证模块 OP_ADMIN_KYC_TIP
      // 高级认证模块 OP_ADMIN_KYC_HIGH_LEVEL_TIP
      // OTC订单模块 OP_ADMIN_OTC_ORDER_TIP
      // OTC订单聊天模块 OP_ADMIN_OTC_ORDER_ONLINECHAT_TIP
      // 银行卡订单模块 OP_ADMIN_BANK_CARD_ORDER_TIP
      // 用户资金密码申请模块 OP_ADMIN_USER_SAFEWORD_APPLY_TIP
      // 永续合约持仓单 OP_ADMIN_CONTRACT_ORDER_TIP
      // 用户客服模块 OP_ADMIN_ONLINECHAT
    },
    openURL(item) {
      var test1 = window.location.protocol + "//";
      var test2 = window.location.host;
      var url = test1 + test2 + item.tip_url;
      console.log('<a href="' + url + '">' + item.tip_message + "</a>");
      this.$notify({
        title: item.tip_message,
        dangerouslyUseHTMLString: true,
        message: '<a href="' + url + '">' + item.tip_message + "</a>",
        position: "bottom-right",
      });
      // function toUser(url){
      //   window.open(url);
      // }
      // this.$notify({
      //   title: item.tip_message,
      //   dangerouslyUseHTMLString: true,
      //   message: '<a onclick="function(){window.loaction.href = url;}" style="cursor: pointer;">'+item.tip_message+'</a>',
      //   position: 'bottom-right'
      // });
    },
    getTips() {
      //getNewTips
      if (this.$login) {
        this.getTips2();
      }

      let timer = setTimeout(() => {
        clearTimeout(timer);
        this.getTips();
      }, 5000);
    },
    getTips2() {
      if(this.$route.path == "/login"){
        return;
      }
      var time_stamp = localStorage.getItem("timeStamp");
      var params = { timeStamp: time_stamp }; //1688432842000
      localStorage.setItem("timeStamp", new Date().getTime());
      //start
      this.$http({
        url: this.$http.adornUrl("/tip/getAdminTips"),
        method: "get",
        params: this.$http.adornParams(Object.assign({}, params)),
      }).then((data1) => {
        // console.log("getTips data = " + JSON.stringify(data1));
        let data = data1.data
        if (data.code == 0) {
          var temp = data.data;

          var businessNum = 0;
          var userNum = 0;
          var moneyNum = 0;
          var c2cNum = 0;
          this.tips = {};
          this.initTipCountHandle();

          // 遍历tip
          if (temp.tipList.length > 0) {
            temp.tipList.forEach((ele) => {
              this.countHandle(ele.tip_dom_name, ele.tip_content_sum);
              // if (ele.tip_dom_name == ".automonitor_threshold_order_untreated_cout"
              //   || ele.tip_dom_name == ".automonitor_approve_order_untreated_cout"
              //   || ele.tip_dom_name == ".contract_order_untreated_cout"
              //   || ele.tip_dom_name == ".futures_order_untreated_cout") {
              //   // 业务
              //   businessNum = businessNum + ele.tip_content_sum;
              // } else if (ele.tip_dom_name == ".kyc_untreated_cout"
              //   || ele.tip_dom_name == ".kyc_high_level_untreated_cout"
              //   || ele.tip_dom_name == ".user_safeword_apply_untreated_cout") {
              //   // 用户
              //   userNum = userNum + ele.tip_content_sum;
              // } else if (ele.tip_dom_name == ".automonitor_withdraw_order_untreated_cout"
              //   || ele.tip_dom_name == ".withdraw_order_untreated_cout"
              //   || ele.tip_dom_name == ".recharge_blockchain_order_untreated_cout"
              //   || ele.tip_dom_name == ".bank_card_order_untreated_cout") {
              //   // 财务
              //   moneyNum = moneyNum + ele.tip_content_sum;
              // }
            });

            //

            menuTipAdd.forEach((value, key) => {
              // console.log(key+":"+value);
              let all = 0;
              for (let i = 0; i < value.length; i++) {
                let num = this.tips[value[i]] || 0;
                if (
                  value[i] == ".kyc_untreated_cout" &&
                  !isAuth("suer:user:jichu")
                ) {
                  num = 0;
                }
                if (
                  value[i] == ".kyc_high_level_untreated_cout" &&
                  !isAuth("suer:user:gaoji")
                ) {
                  num = 0;
                }
                all += num;
              }
              this.countHandle(key, all);
            });

            // console.log("this.tips = " + JSON.stringify(this.tips));
          }

          //消息通知
          if (temp.popTipList && temp.popTipList.length > 0) {
            //
            for (let i = 0; i < temp.popTipList.length; i++) {
              console.log("tip_url = " + temp.popTipList[i].tip_url);

              if (temp.popTipList[i].tip_message != null) {
                setTimeout(() => {
                  let type = temp.popTipList[i].tip_type;
                  switch (type) {
                    case "1": //充值
                      this.playAudioOfRecharge();
                      break;
                    case "2": //提现
                      this.playAudioOfWithdraw();
                      break;
                    case "3": //认证
                      this.playAudioOfRzzt();
                      break;
                    default:
                      console.log("playAudioOfMute 声音:" + type);
                      if (temp.popTipList[i].tip_url == "/message") {
                        this.playAudioOfMuteKeFu();
                        //客服
                      } else if (temp.popTipList[i].tip_url == "/c2c-bank_pay_order") {
                        this.playAudioOfBank();
                        //银行订单
                      } else if (temp.popTipList[i].tip_url == "/c2c-c2c_pay_order") {
                        this.playAudioOfC2c();
                        //C2C订单
                      } else {
                        this.playAudioOfMute();
                      }
                  }

                  let url = temp.popTipList[i].tip_url;
                  console.log("url = " + url);

                  if(type==199){
                    let array = url.split("@");
                    console.log("array => " + array);
                    url = array[0];
                    this.$store.commit("common/updateStocksValue", array[1])
                    if(url == "/union-stocks-transport"){
                      this.$bus.$emit("update-transport", array[1]);
                    }else if(url == "/union-stocks-hotSearch"){
                      this.$bus.$emit("update-hotSearch", array[1]);
                    }else if(url == "/union-stocks-pickAddr"){
                      this.$bus.$emit("update-pickAddr", array[1]);
                    }
                    
                    
                    

                    
                  }

                  let message = temp.popTipList[i].tip_message.replace(
                    "{0}",
                    `<span style='color:#E05561'>${temp.popTipList[i].tip_content_num}</span>`
                  );
                  this.openNewURL(
                    temp.popTipList[i].tip_content_num,
                    url,
                    message
                  );
                }, 100 * i);
              }
            }
            //
          }

          // // 业务
          // this.countHandle(".business_untreated_count",businessNum);
          // // 用户
          // this.countHandle(".user_untreated_count",userNum);
          // // 财务
          // this.countHandle(".money_untreated_count",moneyNum);
        }else{
          if(this.$route.path == "/login"){
            // this.isTimeUpdate = false;
          }
          console.log("this.isTimeUpdate === " + this.isTimeUpdate);
        }
      }).catch(err => {
        if(this.$route.path == "/login"){
          // this.isTimeUpdate = false;
        }
        // 
        console.log("catch: ", err);
      })
      // console.log("this.menuMap => " + JSON.stringify(this.menuMap));

      // 交割单 OP_ADMIN_FUTURES_ORDER_TIP
      // 区块链充值模块 OP_ADMIN_RECHARGE_BLOCKCHAIN_TIP
      // 三方充值模块 OP_ADMIN_RECHARGE_TIP
      // 提现模块 OP_ADMIN_WITHDRAW_TIP
      // 认证模块 OP_ADMIN_KYC_TIP
      // 高级认证模块 OP_ADMIN_KYC_HIGH_LEVEL_TIP
      // OTC订单模块 OP_ADMIN_OTC_ORDER_TIP
      // OTC订单聊天模块 OP_ADMIN_OTC_ORDER_ONLINECHAT_TIP
      // 银行卡订单模块 OP_ADMIN_BANK_CARD_ORDER_TIP
      // 用户资金密码申请模块 OP_ADMIN_USER_SAFEWORD_APPLY_TIP
      // 永续合约持仓单 OP_ADMIN_CONTRACT_ORDER_TIP
      // 用户客服模块 OP_ADMIN_ONLINECHAT
    },
    initTipCountHandle() {
      // 目录
      this.countHandle(".business_untreated_count", 0);
      this.countHandle(".user_untreated_count", 0);
      this.countHandle(".money_untreated_count", 0);

      // 业务
      this.countHandle(".automonitor_approve_order_untreated_cout", 0);
      this.countHandle(".automonitor_threshold_order_untreated_cout", 0);
      this.countHandle(".contract_order_untreated_cout", 0);
      // 用户
      this.countHandle(".kyc_untreated_cout", 0);
      this.countHandle(".kyc_high_level_untreated_cout", 0);
      this.countHandle(".user_safeword_apply_untreated_cout", 0);
      // 财务
      this.countHandle(".automonitor_withdraw_order_untreated_cout", 0);
      this.countHandle(".withdraw_order_untreated_cout", 0);
      this.countHandle(".recharge_blockchain_order_untreated_cout", 0);
      this.countHandle(".bank_card_order_untreated_cout", 0);
    },
    //展示处理
    countHandle(ele, count) {
      if (isNaN(count)) {
        count = 0;
      }
      if (ele == null) {
        count = 0;
      }
      this.tips[ele] = count;
    },
    openNewURL(num, url, message) {
      let title = "新消息";
      // let num = 21;
      // let url = "home"
      this.$notify({
        dangerouslyUseHTMLString: true,
        showClose: true, // 关闭自带的关闭按钮
        // duration: 500,
        position: "bottom-right",
        customClass: "notifyClass", // 这个样式只能放在无scoped的style中才能生效
        message: this.$createElement("div", { class: "notify_parent" }, [
          this.$createElement("div", { class: "notify_div" }, [
            this.$createElement("div", { class: "notify_title" }, [
              this.$createElement("div", { class: "circle" }, [
                this.$createElement(
                  "span",
                  { class: num < 10 ? "text_num0" : "text_num1" },
                  num
                ),
              ]),
              // this.$createElement(
              //   'el-badge',
              //   // { class: 'circle' },
              //   {class: 'badge'},
              //   num
              // ),
              this.$createElement("span", { class: "text" }, title),
            ]),
            // this.$createElement(
            //   'i',
            //   { class: 'el-icon-close', on: { click: this.closeNotify } }
            // )
          ]),
          this.$createElement(
            "div",
            {
              // domProps: {
              //   innerHTML: '<em>你好啊</em>' // htmlString就是带HTML格式的字符串'<em>你好啊</em>'
              // },
              // src: process.env.VUE_APP_HTTP + window.location.hostname
              //     + process.env.VUE_APP_PORT
              //     + process.env.VUE_APP_RESOURCES_URL + "/new.png"
              class: "notifyContent",
            },
            [
              this.$createElement("img", {
                style:
                  "width: 46px;height: 46px;margin-top:20px; margin-left: 18px;",
                attrs: { src: require("../../public/new.png") },
              }),
              this.$createElement(
                "div",
                {
                  class: "notifyContent1",
                },
                [
                  this.$createElement("div", {
                    class: "notifyContent2",
                    domProps: {
                      // innerHTML: '你<span style="color:#E05561">好啊</span>asasasss'
                      innerHTML: message,
                    },
                  }),
                  this.$createElement("a", {
                    class: "notifyContent3",
                    style: "cursor:pointer;",
                    domProps: {
                      innerHTML: "点击前往", // 显示的值
                    },
                    on: {
                      click: () => {
                        this.handleTitleClick(url);
                      },
                    },
                  }),
                ]
              ),
            ]
          ),
        ]),
      });
    },
    // 点击事件，跳转到另一个页面
    handleTitleClick(url) {
      let urlA = url.trim();
      console.log("url = $" + url + "$");
      console.log("router = " + this.$router);

      this.$router.push({
        path: urlA,
      });
      this.$bus.$emit("updateOfBasics", { flag: true });
      this.$bus.$emit("updateOfSenior", { flag: true });
      this.$bus.$emit("updateOfReset", { flag: true });
      this.$bus.$emit("updateOfRechange", { flag: true });
      this.$bus.$emit("updateOfWithdraw", { flag: true });
      // this.$router.push({ path: "/user-relation-senior" });
    },
  },
};
</script>

<style lang="scss">
.notifyClass {
  background-color: #4f5158;
  border-radius: 10px;
  width: 360px;
  height: 116px;
}
.notify_title {
  margin-top: 5px;
  font-weight: bold;
  display: flex;
  justify-content: space-between;
}
.notifyContent {
  display: flex;
  justify-content: space-between;
  color: #666666;
  height: 100px;
  overflow-y: auto;
  padding-right: 6px;
  &::-webkit-scrollbar {
    width: 5px;
    height: 7px;
    background-color: #fff;
  }
  &::-webkit-scrollbar-track {
    border-radius: 10px;
    background-color: #fff;
  }
  &::-webkit-scrollbar-thumb {
    height: 20px;
    border-radius: 10px;
    background-color: #cccdd1;
  }
}

.notifyContent1 {
  color: #666666;
  min-width: 260px;
  height: 100px;
  margin-left: -60px;
  // background-color: yellowgreen;
}

.notifyContent2 {
  color: #666666;
  font-family: PingFang HK;
  font-size: 14px;
  font-weight: 600;
  line-height: 20px;
  letter-spacing: 0em;
  text-align: left;
  margin-top: 20px;
}

.notifyContent3 {
  color: #333333;
  font-family: PingFang HK;
  font-size: 14px;
  font-weight: 600;
  line-height: 20px;
  letter-spacing: 0em;
  text-align: left;
  text-decoration: underline;
  margin-top: 20px;
}

.notifyIcon {
  color: #f75343;
  margin-right: 5px;
}
.notify_div {
  display: flex;
  justify-content: space-between;
  background-color: #4f5158;
  width: 360px;
  height: 36px;
}

.notify_parent {
  background-color: #ffffff;
  margin-left: -26px;
  margin-top: -17px;
}

.badge {
  background: #e05561;
  border: 1px solid #e05561;
  border-radius: 50%;
  min-height: 20px;
  min-width: 20px;
  margin-left: 20px;
  color: #ffffff;
}

.circle {
  background: #e05561;
  border: 1px solid #e05561;
  border-radius: 50%;
  height: 20px;
  width: 20px;
  margin-left: 20px;
  color: #ffffff;
  text-align: left;
}

.text_num0 {
  color: #ffffff;
  font-family: PingFang HK;
  font-size: 14px;
  font-weight: 600;
  line-height: 20px;
  letter-spacing: 0em;
  text-align: center;
  margin-left: 5px;
}

.text_num1 {
  color: #ffffff;
  font-family: PingFang HK;
  font-size: 14px;
  font-weight: 600;
  line-height: 20px;
  letter-spacing: 0em;
  text-align: center;
  // margin-left: 5px;
}

.text {
  color: #ffffff;
  font-family: PingFang HK;
  font-size: 14px;
  font-weight: 600;
  line-height: 20px;
  letter-spacing: 0em;
  text-align: left;
  margin-left: 10px;
}
</style>
