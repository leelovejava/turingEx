<template>
  <main class="site-content" :class="{ 'site-content--tabs': $route.meta.isTab }">

    <!-- 主入口标签页 s -->
    <el-tabs
      v-if="$route.meta.isTab"
      v-model="mainTabsActiveName"
      :closable="true"
      @tab-click="selectedTabHandle"
      @tab-remove="removeTabHandle">
      <el-dropdown class="site-tabs__tools" :show-timeout="0">
        <i class="el-icon-arrow-down el-icon--right"></i>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item @click.native="tabsCloseCurrentHandle">关闭当前标签页</el-dropdown-item>
          <el-dropdown-item @click.native="tabsCloseOtherHandle">关闭其它标签页</el-dropdown-item>
          <el-dropdown-item @click.native="tabsCloseAllHandle">关闭全部标签页</el-dropdown-item>
          <el-dropdown-item @click.native="tabsRefreshCurrentHandle">刷新当前标签页</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-tab-pane
        v-for="item in mainTabs"
        :key="item.name"
        :label="item.title"
        :name="item.name">
        <el-card :body-style="siteContentViewHeight">
          <iframe
            v-if="item.type === 'iframe'"
            :src="item.iframeUrl"
            width="100%" height="100%" frameborder="0" scrolling="yes">
          </iframe>
          <keep-alive v-else>
            <router-view v-if="item.name === mainTabsActiveName" />
          </keep-alive>
        </el-card>
      </el-tab-pane>
    </el-tabs>
    <!-- 主入口标签页 e -->
    <el-card v-else :body-style="siteContentViewHeight">
      <keep-alive>
        <router-view />
      </keep-alive>
    </el-card>
    <el-dialog class="dialog"
      :visible.sync="showVisible"
      :close-on-press-escape="false"
      :close-on-click-modal="false"
      :show-close="false"
      append-to-body
      lock-scroll
      width="20%"
      top="25vh"
      center
    >
      <p class="sound-btn" @click="btnClick">请点击打开消息声音提醒</p>
    </el-dialog>
    <div v-if="this.$store.state.talks.clinkStatus == 1" style="background-color: white;width: 100%;height:100%;
                top:0;left:0;opacity: 0.8;
                z-index: 3000;position:fixed;">
          <div class="loading"></div> 
          <div class="loadingText">消息加载中...</div>          
    </div>
  </main>
</template>

<script>
  import { isURL } from '@/utils/validate'
  export default {
    data() {
      return {
        visible: true,
        soundVisible: true,
        authorization:false,
      };
    },
    mounted(){
      this.authorization = Vue.cookie.get('Authorization')
      // 监听页面刷新
      window.addEventListener("beforeunload", (e) => this.beforeunloadHandler(e));
      // 为全局事件总线绑定自定义事件
      this.$bus.$on('tabsCloseAllHandle', (data)=>{
        this.tabsCloseAllHandle()
      })
    },
    beforeDestroy() {
      // 组件被销毁了，不能进行数据传输
      // 解绑事件
      this.$bus.$off('tabsCloseAllHandle')
      // 销毁监听刷新
      window.removeEventListener("beforeunload", (e) =>
        this.beforeunloadHandler(e)
      );
      sessionStorage.removeItem("sound");
    },
    computed: {
      showVisible(){
        this.authorization = Vue.cookie.get('Authorization')
        // return this.authorization && sessionStorage.getItem("sound") == "true";
        return this.soundVisible && process.env.VUE_APP_SOUND == 'true';
        // return false
      },
      // 获取是否存在sound
      getSoundVisible() {
        return JSON.parse(sessionStorage.getItem("sound"));
      },
      documentClientHeight: {
        get () { return this.$store.state.common.documentClientHeight }
      },
      menuActiveName: {
        get () { return this.$store.state.common.menuActiveName },
        set (val) { this.$store.commit('common/updateMenuActiveName', val) }
      },
      mainTabs: {
        get () { return this.$store.state.common.mainTabs },
        set (val) { this.$store.commit('common/updateMainTabs', val) }
      },
      mainTabsActiveName: {
        get () { 
          return this.$store.state.common.mainTabsActiveName 
        },
        set (val) { this.$store.commit('common/updateMainTabsActiveName', val) }
      },
      siteContentViewHeight () {
        var height = this.documentClientHeight - 50 - 30 - 2
        if (this.$route.meta.isTab) {
          height -= 40
          return isURL(this.$route.meta.iframeUrl) ? { height: height + 'px' } : { minHeight: height + 'px' }
        }
        return { minHeight: height + 'px' }
      }
    },
    methods: {
      // 页面刷新
      beforeunloadHandler(e) {
        sessionStorage.setItem("sound", "true");
        this.authorization = Vue.cookie.get('Authorization')
        console.log("this.authorization = " + this.authorization)
        console.log(JSON.stringify(e))
      },

      btnClick() {
        console.log("btnClick");
        // sessionStorage.removeItem("sound");
        sessionStorage.setItem("sound", "false");
        this.soundVisible = false;
      },
      // tabs, 选中tab
      selectedTabHandle (tab) {
        tab = this.mainTabs.filter(item => item.name === tab.name)
        if (tab.length >= 1) {
          this.$router.push({ name: tab[0].name })
        }
      },
      // tabs, 删除tab
      removeTabHandle (tabName) {
        this.mainTabs = this.mainTabs.filter(item => item.name !== tabName)
        if (this.mainTabs.length >= 1) {
          // 当前选中tab被删除
          if (tabName === this.mainTabsActiveName) {
            this.$router.push({ name: this.mainTabs[this.mainTabs.length - 1].name }, () => {
              this.mainTabsActiveName = this.$route.name
            })
          }
        } else {
          this.menuActiveName = ''
          this.$router.push({ name: 'home' })
        }
      },
      // tabs, 关闭当前
      tabsCloseCurrentHandle () {
        this.removeTabHandle(this.mainTabsActiveName)
      },
      // tabs, 关闭其它
      tabsCloseOtherHandle () {
        this.mainTabs = this.mainTabs.filter(item => item.name === this.mainTabsActiveName)
      },
      // tabs, 关闭全部
      tabsCloseAllHandle () {
        console.log("tabsCloseAllHandle");
        this.mainTabs = []
        this.menuActiveName = ''
        this.$router.push({ name: 'home' })
      },
      // tabs, 刷新当前
      tabsRefreshCurrentHandle () {
        var tempTabName = this.mainTabsActiveName
        this.removeTabHandle(tempTabName)
        this.$nextTick(() => {
          this.$router.push({ name: tempTabName })
        })
      }
    }
  }
</script>


<style lang="less" scoped>
.dialog {
  ::v-deep .el-dialog {
    box-shadow: none;
    background-color: transparent;

    .sound-btn {
      background: #fff;
      padding: 10px 20px;
      border-radius: 5px;
      cursor: pointer;
      text-decoration: underline;
      color: #368ff3;
      box-shadow: 1px 1px 7px rgba(0, 0, 0, 0.2);
    }
  }

  ::v-deep .el-dialog__header {
    display: none;
  }

  ::v-deep .el-dialog__body {
    display: flex;
    justify-content: center;
    align-items: center;
  }

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

