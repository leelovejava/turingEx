<template>
  <!-- 充值或提现申请提交页面 -->
  <div id="cryptos">
    <assets-head title="" :goHome="true" />
    <div class="pl-12 pr-12 page-box relative">
      <div class="content-box">
        <div class="flex flex-col items-center justify-center mt96">
          <img width="162" height="142" src="@/assets/image/assets-center/success.png" />
          <div class="text-44 font-bold success-title textColor">{{ title }} {{ $t('申请已提交') }}</div>
        </div>
        <div class="mb87">
          <div class="text-34 textColor">{{ title }} {{ $t('申请已提交') }}</div>
          <div class="text-grey text-30 mt14">{{ time }}</div>
        </div>
        <div class="mb87">
          <div class="text-34 textColor">{{ title }} {{ $t('申请已提交') }}</div>
          <div class="flex mt14">
            <div class="text-grey text-30">{{ $t('如果') }} {{ title }} {{ $t('未到账') }}？</div>
            <div class="service-text text-30 text-blue" @click="tokefu">{{ $t('联系客服') }}</div>
          </div>
        </div>
        <!--         <div class="mb87">-->
        <!--           <div class="text-34">{{ title }} {{ $t('已成功') }}</div>-->
        <!--           <div class="text-grey text-30 mt14">{{ $t('您会收到邮箱提醒') }}</div>-->
        <!--         </div>-->
      </div>
      <div class="btn-wrap">
        <button class="btnMain text-white next-btn text-34"
          @click="goRouter('/cryptos/assetsCenter/rechargeWithdrawRecord')">
          {{ $t('查看') }} {{ title }} {{ $t('历史') }}</button>
      </div>
    </div>
  </div>
</template>
  
<script>
import { Image as VanImage } from 'vant';
import assetsHead from "@/components/Transform/assets-head/index.vue";
import { dataTimeEx } from '@/utils/utis'
import dayjs from 'dayjs';
export default {
  name: 'rechargeSubmit',
  components: {
    assetsHead,
    [VanImage.name]: VanImage
  },
  props: {
    title: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      time: ''
    }
  },
  created() {
    if (this.$route.query?.orderTime) {
      this.time = dayjs(this.$route.query?.orderTime).format('YYYY-MM-DD hh:mm:ss')
    } else {
      this.time = dayjs().format('YYYY-MM-DD hh:mm:ss')
    }
  },
  methods: {
    onClickLeft() {
      this.$router.go(-1);
    },
    goRouter(params) {
      this.$router.push({
        path: params,
        query: {
          back: 1
        }
      });
    },
    tokefu() {
      this.$router.push('/customerService')
    }
  }
}
</script>
<style lang="scss" scoped>
#cryptos {
  .page-box {
    box-sizing: border-box;
  }

  .btn-wrap {
    position: fixed;
    left: 0;
    bottom: 86px;
    width: 100%;
    padding: 0 30px;
    box-sizing: border-box;
  }

  .mt96 {
    margin-top: 114px;
  }

  .success-title {
    margin-top: 70px;
    margin-bottom: 115px;
  }

  .mt14 {
    margin-top: 14px;
  }

  .mb87 {
    margin-bottom: 87px;
  }

  .service-text {
    text-decoration: underline;
  }

  .next-btn {
    width: 100%;
    height: 96px;
    border: none;
    outline: none;
    border-radius: 8px;
  }
}
</style>
  
