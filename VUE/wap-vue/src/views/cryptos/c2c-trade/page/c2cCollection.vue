<template>
  <div id="c2cCollection">
    <div id="full" class="w-full h-full c2cCollection mainBackground">
      <assets-head :title="$t('C2C收款方式')" :backFunc="() => $router.replace('/cryptos/c2cTrade')" />
      <!--  判断是否有收款方式  -->
      <div v-if="paymentList.length === 0" class="pt-12 pb-20 text-center">
        <div class="flex items-center justify-center">
          <img class="w-44 h-44" src="~@/assets/image/c2c/23@3x1.png" alt="">
        </div>
        <div class="mt-2 text-grey text-24">{{ $t('暂无支持的收款方式') }}</div>
      </div>
      <div v-else>
        <car-item class="pb-9" v-for="(item, index) in paymentList" :key="index" :item="item"
          @click.native="enterCollection(item, $event)" />
      </div>

    </div>
  </div>
</template>

<script>
import otcApi from "@/service/otc.api";
import { Collapse, CollapseItem, Icon } from "vant";
import CarItem from "@/views/cryptos/placeAnOrder/page/payment-method/CarItem.vue";
import assetsHead from "@/components/Transform/assets-head/index.vue";
export default {
  name: "c2cCollection",
  props: ['isEmpty', 'id'],
  data() {
    return {
      show: true,
      activeNames: [],
      paymentList: [],
      // carData: [
      //   {
      //     title: "银行卡",
      //     number: "4367421420489044633",
      //     name: "James",
      //     bankName: "中国农业银行",
      //     desc: "北京朝阳路支行",
      //     type: 'CN'
      //   },
      //   {
      //     title: "银行卡",
      //     number: "4367421420489041111",
      //     name: "James",
      //     bankName: "中国银行",
      //     desc: "北京朝阳路支行",
      //     type: 'CN'
      //   },
      // ]
    }
  },
  created() {
    // otcApi.ctcPaymentMethodPayList({id: this.id}).then(res => {
    //   this.paymentList = res.data;
    //   console.log(this.paymentList);
    // })
    otcApi.ctcPaymentMethodUserPay({ id: this.id, language: this.$i18n.locale }).then(res => {
      console.log('d111', res)
      this.paymentList = res;
    })
  },
  methods: {
    change() {
      setTimeout(() => {
        if (this.activeNames.length > 0) {

          this.show = false;
        } else {
          this.show = true;
        }
      }, 200)
    },
    // 折叠面板隐藏
    hide() {
      this.$refs.collapseItem.toggle(false);
      this.show = true;
    },
    // 返回出售界面
    enterCollection(paymentItem, e) { // 获取银行卡号

      if (e.target.className.indexOf('edit') === -1) {
        this.$router.replace({
          name: 'c2cTrade',
          query: {
            type: this.$route.query.type,
            total: this.$route.query.total,
            data: this.$route.query.data,
            reciveType: JSON.stringify(paymentItem),
          }
        })
      }
    }
  },
  components: {
    [Icon.name]: Icon,
    [Collapse.name]: Collapse,
    [CollapseItem.name]: CollapseItem,
    assetsHead,
    CarItem,
  }
}
</script>

<style lang="scss" scoped>
#c2cCollectionPage {
  font-size: 30px;

  .c2cCollection {
    :deep(.van-cell) {
      background: #F5F5F5;
    }

    :deep(.van-cell) {
      background: #F5F5F5;
    }

    :deep(.van-collapse-item__wrapper) {
      background: #F5F5F5;
    }

    :deep(.van-collapse-item__content) {
      padding: 0;
    }
  }

  :deep(.van-cell) {
    background: #F5F5F5;
  }

  :deep(.van-collapse-item__wrapper) {
    margin-top: 36px;
  }

  :deep(.van-collapse-item__content) {
    padding: 0;
  }
}
</style>
