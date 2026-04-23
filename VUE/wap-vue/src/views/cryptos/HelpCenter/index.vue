<template>
  <div id="cryptos">
    <div class="helpCenter">
      <assets-head :title="$t('帮助中心')" />
      <div class="content">
        <div class="item" v-for="(item, index) in listArr" :key="index" @click="$router.push({
          path: '/helpDetail',
          query: { // 这个位置把参数都带过去
            ...item
          }
        })">
          <span class="textColor">{{ item.title }}</span>
          <div class="moreBox"><img src="@/assets/image/userCenter/more.png" alt=""></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { Icon, showToast } from 'vant';
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { _helpCenter } from "@/service/user.api"

export default {
  name: "helpCenter",
  components: {
    [Icon.name]: Icon,
    assetsHead
  },
  data() {
    return {
      listArr: []
    }
  },
  mounted() {
    this.getHelpCenter()
  },
  methods: {
    onClickLeft() {
      this.$router.push('/')
    },
    getHelpCenter() {
      _helpCenter({
        model: 'help_center',
        language: this.$i18n.locale,
      }).then((res) => {
        this.listArr = res
      }).catch((error) => {
        if (error.code === 'ECONNABORTED') { showToast(this.$t('网络超时！')); }
        else if (error.msg !== undefined) { showToast(this.$t(error.msg)); }
      });
    },
  }

}
</script>

<style lang="scss" scoped>
#cryptos {
  .helpCenter {
    width: 100%;
    box-sizing: border-box;
  }

  .content {
    font-size: 28px;
    padding: 0 32px;
  }

  .item {
    padding: 35px 0;
    text-decoration: underline;
    display: flex;
    justify-content: space-between;
  }

  .moreBox {
    width: 26px;
    height: 26px;

    img {
      width: 100%;
      height: 100%;
    }
  }
}
</style>