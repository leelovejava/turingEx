<template>
  <nav class="site-navbar" :class="'site-navbar--' + navbarLayoutType">
    <div class="site-navbar__header">
      <h1
        style="float: left"
        class="site-navbar__brand"
        @click="$router.push({ name: 'home' })"
      >
        <img
          src="../assets/img/logo.png"
          alt=""
          style="float: left; width: 54px; margin-left: 4px"
        />
        <a class="site-navbar__brand-lg" href="javascript:;"> 管理系统</a>
      </h1>
    </div>
    <div class="site-navbar__body clearfix" style="height: auto;">
      <el-menu class="site-navbar__menu" mode="horizontal">
        <!-- 收缩菜单 -->
        <!-- <el-menu-item
          class="site-navbar__switch"
          index="0"
          @click="sidebarFold = !sidebarFold"
        >
          <icon-svg name="zhedie"></icon-svg>
        </el-menu-item> -->

        <!-- 顶部导航 -->

        <!-- <el-menu-item index="1">处理中心</el-menu-item> -->
        <!-- <el-submenu index="2">
          <template slot="title">我的工作台</template>
          <el-menu-item index="2-1">选项1</el-menu-item>
          <el-menu-item index="2-2">选项2</el-menu-item>
          <el-menu-item index="2-3">选项3</el-menu-item>
          <el-submenu index="2-4">
            <template slot="title">选项4</template>
            <el-menu-item index="2-4-1">选项1</el-menu-item>
            <el-menu-item index="2-4-2">选项2</el-menu-item>
            <el-menu-item index="2-4-3">选项3</el-menu-item>
          </el-submenu>
        </el-submenu> -->
        <!-- <el-menu-item index="3" disabled>消息中心</el-menu-item>
        <el-menu-item index="4"><a href="https://www.ele.me" target="_blank">订单管理</a></el-menu-item> -->
        <div
        class="site-navbar__menu site-navbar__menu--right"
        mode="horizontal"
        style="display: flex"
      >
        <!-- 模糊搜索 -->
        <el-autocomplete
          v-model="state"
          :fetch-suggestions="querySearchAsync"
          placeholder="请输入菜单名称"
          suffix-icon="el-icon-search"
          clearable
          @select="handleSelect"
          class="searchInput"
        ></el-autocomplete>
        <!-- <el-menu-item class="site-navbar__avatar" index="3"> -->
        <div v-if="!isShowKefu" style="width: 10px"></div>
        <div
          v-if="isShowKefu"
          @click="showChat()"
          class="isPng"
          style="
            width: 31px;
            float: left;
            padding: 10px 0px;
            margin-right: 10px;
            margin-left: 10px;
          "
        >
        </div>
        <el-badge
          v-if="isShowKefu"
          :value="countNum('.unread')"
          v-show="countNum('.unread') > 0"
          style="
            align-items: center;
            display: flex;
            padding-top: 15px;
            padding-right: 15px;
          "
        ></el-badge>
        <!-- <el-button stype="info" icon="el-icon-s-comment" style="margin-right: 0px;border: none;" @click="showChat()"></el-button> -->
        <div
          style="width: 20px; display: flex; padding: 20px 5px"
          v-if="kefuInfo.isOnline == true"
        >
          <img
            src="~@/assets/image/online.png"
            width="10px"
            height="10px"
            style="align-items: center; display: flex"
          />
        </div>
        <div
          style="width: 20px; display: flex; padding: 20px 5px"
          v-if="kefuInfo.isOnline == false"
        >
          <div
            style="
              width: 10px;
              height: 10px;
              background: #dedede;
              border-radius: 5px;
            "
          ></div>
        </div>
        <div style="align-items: center; display: flex">
          <el-dropdown
            :show-timeout="0"
            placement="bottom"
            class="ddd1"
            @visible-change="visibleChange"
            style="align-items: center"
          >
            <!-- <img src="~@/assets/image/online.png" width="10px" height="10px"/> -->
            <span class="el-dropdown-link">
              {{ userName }}
              <i
                class="el-icon-arrow-down el-icon--right"
                v-show="!dropdownVisiable"
              ></i>
              <i
                class="el-icon-arrow-up el-icon--right"
                v-show="dropdownVisiable"
              ></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item @click.native="customerCentor()" v-show="isKeFu"
                ><span class="el-icon-edit"></span> 客服中心</el-dropdown-item
              >
              <el-dropdown-item @click.native="updatePasswordHandle()"
                ><span class="el-icon-edit"></span>
                修改登录密码</el-dropdown-item
              >
              <el-dropdown-item @click.native="updatePassmoneyHandle()"
                ><span class="el-icon-coin"></span
                >修改资金密码</el-dropdown-item
              >
              <!-- <el-dropdown-item v-if="isAuth('sys:user:update')" @click.native="message()">谷歌验证器</el-dropdown-item> -->
              <el-dropdown-item @click.native="logoutHandle()"
                ><span class="el-icon-switch-button"></span
                >退出登录</el-dropdown-item
              >
            </el-dropdown-menu>
          </el-dropdown>
        </div>

        <!-- </el-menu-item> -->
      </div>

        <!-- 以下是顶部导航 -->
        <main-navbar-sub-menu
          v-for="item in menuListArr"
          :key="item.menuId"
          :menu="item"
          :dynamicMenuRoutes="dynamicMenuRoutes"
          :type="0"
        >
        </main-navbar-sub-menu>
      </el-menu>

    </div>
    <!-- 弹窗, 修改密码，资金密码，谷歌验证器 main-navbar-update-googleAuthCode-->
    <customer-centor
      v-if="customerCentorVisible"
      ref="customerCentor"
    ></customer-centor>
    <update-password
      v-if="updatePassowrdVisible"
      ref="updatePassowrd"
    ></update-password>
    <update-passmoney
      v-if="updatePassmoneyVisible"
      ref="updatePassmoney"
    ></update-passmoney>
    <update-Passgoogle
      v-if="googleAuthCode"
      ref="updatePassgoogle"
    ></update-Passgoogle>
  </nav>
</template>

<script>
import CustomerCentor from "./main-navbar-customerCentor";
import UpdatePassword from "./main-navbar-update-password";
import UpdatePassmoney from "./main-navbar-update-passmoney";
import UpdatePassgoogle from "./main-navbar-update-googleAuthCode";
import MainNavbarSubMenu from "./main-navbar-sub-menu";
import { clearLoginInfo } from "@/utils";
import { unreadNewAdminOnlineChatAction } from "@/api/chat";
export default {
  data() {
    return {
      customerCentorVisible: false,
      updatePassowrdVisible: false,
      updatePassmoneyVisible: false,
      googleAuthCode: false,
      dynamicMenuRoutes: [],
      dropdownVisiable: false,
      isKeFu: false,
      restaurants: [],
      state: "",
      timeout: null,
      kefuInfo: {},
      unreadNum: 0,
      isShowKefu: false,
    };
  },
  components: {
    CustomerCentor,
    UpdatePassword,
    UpdatePassmoney,
    UpdatePassgoogle,
    MainNavbarSubMenu,
  },
  computed: {
    navbarLayoutType: {
      get() {
        return this.$store.state.common.navbarLayoutType;
      },
    },
    sidebarFold: {
      get() {
        return this.$store.state.common.sidebarFold;
      },
      set(val) {
        this.$store.commit("common/updateSidebarFold", val);
      },
    },
    mainTabs: {
      get() {
        return this.$store.state.common.mainTabs;
      },
      set(val) {
        this.$store.commit("common/updateMainTabs", val);
      },
    },
    userName: {
      get() {
        return this.$store.state.user.name;
      },
    },
    googleAuthBind: {
      get() {
        return this.$store.state.user.googleAuthBind;
      },
    },
    menuListArr: {
      get() {
        return this.$store.state.common.menuListArr;
      },
      set(val) {
        this.$store.commit("common/updateMenuTopList", val);
      }, //重新存过滤后的数组updateMenuTopList
    },
    // kefuInfo: {
    //   get () {
    //     return this.$store.state.common.kefuInfo
    //   },
    //   set (val) { this.$store.commit('common/updateKefuInfo', val) }
    // },
  },
  created() {
    let menuListArr = [];
    if (
        sessionStorage.getItem("menuList") &&
        sessionStorage.getItem("menuList") != `"[]"`
      ) {
        menuListArr = JSON.parse(sessionStorage.getItem("menuList"));
      }
    if(menuListArr.length > 0){
      this.menuListArr = menuListArr.filter(
      (item) => item.orderNum < 24 && item.orderNum > 0
    );
    }
   
    // console.log("menuListArr = " + JSON.stringify(this.menuListArr))
    this.dynamicMenuRoutes = JSON.parse(
      sessionStorage.getItem("dynamicMenuRoutes") || "[]"
    );

    // 获取当前管理员信息
    this.$http({
      url: this.$http.adornUrl("/sys/user/info"),
      method: "get",
      params: this.$http.adornParams(),
    }).then(({ data }) => {
      this.$login = true;
    });
    // if (!this.IS_DEBUG) {
    //   let timer = setTimeout(() => {
    //     clearTimeout(timer);
    //     this.unreadNumCall();
    //   }, 2000);
    // }
  },
  mounted() {
    //let searchAll = JSON.parse(sessionStorage.getItem("menuList") || "[]");
    //   this.restaurants = searchAll.filter(
    //   (item) => item.list
    // );
    //console.log(this.restaurants)
    this.filterMenuList();

    this.$bus.$on("updateKefuInfo", (data) => {
      console.log("updateKefuInfo = " + JSON.stringify(data));
      this.kefuInfo = data;
    });

    this.$bus.$on("isShowKefu", (data) => {
      this.isShowKefu = data;
      console.log("显示客服");
    });
  },
  beforeDestroy() {
    this.$bus.$off("updateKefuInfo");
    this.$bus.$off("isShowKefu");
  },
  methods: {
    countNum(name) {
      let num = this.main.tips[name];
      if (isNaN(num)) {
        num = 0;
      }
      return num;
    },
    unreadNumCall() {
      //
      if (this.$login) {
        unreadNewAdminOnlineChatAction(
          {},
          (data) => {
            console.log(
              "unreadNewAdminOnlineChatAction = " + JSON.stringify(data)
            );
            this.unreadNum = data.data;
          },
          (err) => {
            console.log("err => " + JSON.stringify(err));
            this.$login = false;
          }
        );
      }
      //
      let timer = setTimeout(() => {
        clearTimeout(timer);
        this.unreadNumCall();
      }, 5000);
      //
    },
    // filterMenuList() {
    //   let searchAll = JSON.parse(sessionStorage.getItem("menuList") || "[]");
    //   // 过滤数组并生成新的数组对象
    //   this.restaurants = searchAll.flatMap(menuItem => {
    //     return menuItem.list?.map(item => ({
    //       value: item.name,
    //       url: item.url,
    //     })) || [];
    //   });
    // },
    // 路由操作
    routeHandle(route) {
      if (route.meta.isTab) {
        // tab选中, 不存在先添加
        var tab = this.mainTabs.filter((item) => item.name === route.name)[0];
        if (!tab) {
          if (route.meta.isDynamic) {
            route = this.dynamicMenuRoutes.filter(
              (item) => item.name === route.name
            )[0];
            if (!route) {
              return console.error("未能找到可用标签页!");
            }
          }
          tab = {
            menuId: route.meta.menuId || route.name,
            name: route.name,
            title: route.meta.title,
            type: isURL(route.meta.iframeUrl) ? "iframe" : "module",
            iframeUrl: route.meta.iframeUrl || "",
          };
          this.mainTabs = this.mainTabs.concat(tab);
        }
        this.menuActiveName = tab.menuId + "";
        this.mainTabsActiveName = tab.name;
      }
    },
    // 客服中心
    customerCentor() {
      this.customerCentorVisible = true;
      this.$nextTick(() => {
        this.$refs.customerCentor.init();
      });
    },
    // 修改密码
    updatePasswordHandle() {
      this.updatePassowrdVisible = true;
      this.$nextTick(() => {
        this.$refs.updatePassowrd.init();
      });
    },
    // 修改资金密码
    updatePassmoneyHandle() {
      this.updatePassmoneyVisible = true;
      this.$nextTick(() => {
        this.$refs.updatePassmoney.init();
      });
    },
    // 谷歌验证码绑定
    updategoogleAuthCode() {
      if (this.googleAuthBind) {
        this.$confirm("谷歌验证器已绑定", "谷歌验证器", {
          //是否已绑定
          distinguishCancelAndClose: true,
          confirmButtonText: "确定",
          cancelButtonText: "解绑",
          type: "success",
        })
          .then(() => {
            this.$message({
              type: "success",
            });
          })
          .catch((action) => {
            if (action === "cancel") {
              this.googleAuthCode = true;
              this.$nextTick(() => {
                this.$refs.updatePassgoogle.init();
              });
            }
          });
      } else {
        this.googleAuthCode = true;
        this.$nextTick(() => {
          this.$refs.updatePassgoogle.init();
        });
      }
    },
    // 退出
    logoutHandle() {
      this.$confirm(`确定进行[退出]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/logOut"),
            method: "post",
            data: this.$http.adornData(),
          }).then(({ data }) => {
            //
            this.$login = false;
            Vue.prototype.$login = false;
            console.log("/logOut");
            this.$bus.$emit("tabsCloseAllHandle", {});
            this.$router.push({ path: "/login" });
            clearLoginInfo();

            console.log("this.$login  = " + this.$login);
            //this.$router.push({ name: 'login' })
          });
        })
        .catch(() => {});
    },
    showChat() {
      // this.main.isShowService = !this.main.isShowService;
      //this.$router.push({ name: "message" });
      if (this.$route.name !== "message") {
        // 如果当前路由不是 "message"，则跳转到 "message" 路由
        this.$router.push({ name: "message" });
      } else {
        // 如果当前路由已经是 "message"，则返回到上一个路由
        this.$router.go(-1);
      }
    },
    visibleChange(flag) {
      this.isKeFu = Vue.prototype.$isKefu;
      this.dropdownVisiable = flag;

      console.log(" this.isKeFu= " + this.isKeFu);
    },
    message() {
      this.$router.push({ name: "message" });
    },
    querySearchAsync(queryString, cb) {
      // 模糊搜索
      var restaurants = this.restaurants;
      var results = queryString
        ? restaurants.filter(this.createStateFilter(queryString))
        : restaurants;

      clearTimeout(this.timeout);
      this.timeout = setTimeout(() => {
        cb(results);
      }, 2000 * Math.random());
    },
    createStateFilter(queryString) {
      // 模糊搜索
      return (state) => {
        return (
          state.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0
        );
      };
    },
    handleSelect(item) {
      // 模糊搜索
      const modifiedUrl = item.url.replace(/\//g, "-");
      this.$router.push({ path: modifiedUrl });
    },
    filterMenuList() {
      let searchAll = [];

      if (
        sessionStorage.getItem("menuList") &&
        sessionStorage.getItem("menuList") != `"[]"`
      ) {
        searchAll = JSON.parse(sessionStorage.getItem("menuList"));
      }
      // 过滤数组并生成新的数组对象
      //console.log(searchAll);
      if (searchAll.length > 0) {
        this.restaurants = searchAll.flatMap((menuItem) => {
          return menuItem.orderNum > 0 && menuItem.list
            ? menuItem.list
                .filter((item) => item.url.trim() !== "")
                .map((item) => ({
                  value: item.name,
                  url: item.url,
                }))
            : [];
        });
      }
    },
  },
};
</script>
<style scoped>
::v-deep .el-menu-item {
  background: none !important;
}
/* 顶部颜色 */
::v-deep .site-navbar__body { 
  background: #263238 !important;
  min-height: 100px;
}
::v-deep .el-submenu__title:hover {
  background-color: #0030ff;
}
.site-navbar {
  background-color: #263238;
}
.el-dropdown {
  color: #fff;
}
::v-deep .el-menu--horizontal {
  border: none !important;
}
.mnbuton {
  width: 0px;
  height: 0px;
  border: none;
}

.el-dropdown-link {
  cursor: pointer;
  color: #fff;
}
.el-icon-arrow-down {
  font-size: 12px;
  color: #fff;
}

.el-icon-arrow-up {
  font-size: 12px;
  color: #fff;
}
</style>

<style lang="scss" scoped>
.ddd {
  ::v-deep .el-submenu__title {
    color: #fff;
  }
  ::v-deep .el-submenu__icon-arrow {
    right: 5px;
    top: 56%;
    color: #fff;
  }
}
.searchInput {
  float: left;
  width: 200px;
  margin-top: 5px;
}
.el-popper[x-placement^="bottom"] .popper__arrow::after {
  border-bottom-color: white !important;
}
.isPng{
  background: url(../assets/img/Vector.png)  center no-repeat;
  background-size: 100%;
  transition: background-image 0.3s ease-in-out,box-shadow 0.2s ease-in-out; /* 添加过渡效果 */
  cursor: pointer;
}
.isPng:hover {
  background-image: url(../assets/img/VectorM.png); /* 鼠标悬停时改变背景图片 */
}
.isImg:active {
  background-image: url(../assets/img/VectorM.png); /* 点击时改变背景图片 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.3); /* 添加阴影效果 */
}
</style>
