<template>
  <div id="cryptos">
    <div id="payment" class="w-full h-full payment">
      <div class="flex flex-col w-full h-full">
        <assets-head class="w-full" :title="$t('C2C收款方式')" ref="orderNav"
          :backFunc="() => $router.push('/cryptos/wantBuy')" />

        <div class="payment_content w-full flex-1 bg-white">
          <car-item class="pb-14" v-for="(item, index) in items" :item="item" :key="index" :index="index" />
        </div>
        <div ref="add" class="add w-full mainBackground flex justify-center pt-11 pb-24">
          <van-button type="info" @click="enterPaymentMethod" class="w-5/6">{{ $t('添加收款方式') }}</van-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  mapState,
} from "vuex";
import {
  Button, showLoadingToast, closeToast
} from 'vant';
import assetsHead from "@/components/Transform/assets-head/index.vue";
import CarItem from "./CarItem.vue";
import otcApi from "@/service/otc.api";
import {
  SET_NAV_HEIGHT,
} from "@/store/const.store";

export default {
  name: "Payment",
  data() {
    return {
      footerHeight: 0,
      items: [
        // {
        //   title: this.$t('银行卡'),
        //   number: "4367421420489044633",
        //   name: "James",
        //   bankName:  this.$t('中国农业银行'),
        //   desc:  this.$t('北京朝阳路支行'),
        //   type: 'CN'
        // },
        // {
        //   title: "AI-Rafidain QiServices",
        //   number: "4367421420489044633",
        //   phone: "13146872211",
        //   name: "James",
        //   desc: "Supplementary card",
        //   type: 'EN'
        // },
      ]
    }
  },
  created() {
    this.getPaymentMethods();
  },
  mounted() {
    console.log(this.items);
    // 获取导航高度
    const height = this.$refs.orderNav.$el.getBoundingClientRect().height;
    this.$store.commit(`payment/${SET_NAV_HEIGHT}`, {
      height,
    })
    // 获取底部高度
    this.footerHeight = this.$refs.add.getBoundingClientRect().height;
  },
  methods: {
    getPaymentMethods() {
      showLoadingToast({
        duration: 0,
        forbidClick: true,
        message: this.$t('加载中')
      })
      otcApi.ctcPaymentMethodList({ language: this.$i18n.locale }).then(res => {
        closeToast();
        this.items = res;
      })
    },
    // 进入添加收款方式
    enterPaymentMethod() {
      this.$router.push({
        path: '/cryptos/wantBuy/addPaymentMethod'
      })
    }
  },
  components: {
    [Button.name]: Button,
    assetsHead,
    CarItem,
  },
  computed: {
    ...mapState("payment", ["navHeight"])
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 30px;

  .payment {
    box-sizing: border-box;

    :deep(.van-button) {
      border-radius: 10px;
    }
  }

  .payment_content {
    overflow-y: scroll;
    background: $tab_background;


    //.payment_content {
    //  padding: 0 30px;
    //}
  }

  :deep(.van-button--info) {
    background: $btn_main;
    border-color: $btn_main;
    color: $main-btn-color;
  }
}
</style>
