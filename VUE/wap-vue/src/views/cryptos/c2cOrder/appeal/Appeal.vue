<template>
  <div id="AppealPage">
    <div id="full" class="w-full h-full">
      <order-nav>
        <template #title>
          {{ $t('申诉') }}
        </template>
      </order-nav>
      <div class="pt-5 px-8 mainBackground pb-12">
        <div class="payment-input-wrapper">
          <div class="label text-28 mb-4 textColor2">
            {{ $t('申诉理由 （必选）') }}
          </div>
          <div class="payment-input w-full">
            <van-field v-model="info.reason" :placeholder="$t('我已付款，但订单已取消')" />
          </div>
        </div>
        <div class="payment-input-wrapper">
          <div class="label text-28 mb-4 textColor2">
            {{ $t('申诉描述') }}
          </div>
          <div class="payment-input w-full">
            <van-field class="textarea" type="textarea" rows="5" v-model="info.description"
              :placeholder="$t('请尽可能完整的描述信息')" />
          </div>
        </div>

        <div class="mt-16">
          <div class="text-28 mb-3 c2cColor">{{ $t('添加凭证 （必填）') }}</div>
          <p class="text-22 text-grey">{{ $t('付款及沟通记录的截图或音视频，最多5个文件，总大小不超过50MB。') }}</p>
          <div class="mt-8">
            <van-uploader v-model="fileList" :afterRead="afterRead" :max-count="1" :max-size="50000 * 1024"
              accept="image/*,video/*" @oversize="oversize">
              <div class="uploader">
                <van-icon name="plus" />
              </div>
            </van-uploader>
          </div>
        </div>
      </div>
      <div class="px-8 mt-4 pt-7 pb-6 mainBackground connect">
        <van-cell-group>
          <van-cell>
            <template #title>
              <span class="text-28">{{ $t('联系人') }}</span>
            </template>
            <template #default>
              <input class="w-60" type="text" :placeholder="$t('联系人')" v-model="info.name">
            </template>
          </van-cell>
          <van-cell class="mt-5">
            <template #title>
              <span class="text-28">{{ $t('联系电话') }}</span>
            </template>
            <template #default>
              <div class="flex items-center justify-end">
                <input class="w-60" type="text" :placeholder="$t('请输入联系方式')" v-model="info.phone">
                <img class="w-6 h-6 ml-4" src="~@/assets/image/c2c/Group1222.png" alt="">
              </div>
            </template>
          </van-cell>
        </van-cell-group>
      </div>
      <div class="px-8 mt-16 mainBackground pt-9 pb-10">
        <van-button :disabled="!fullDisabled" color="#1D91FF" class="w-full h-100 rounded-xl" type="info"
          @click="onAppeal">{{ $t('我要申述') }}
        </van-button>
      </div>
      <!--  弹窗  -->
      <van-popup position="right" class="w-full h-full" v-model:show="show">
        <appeal-waiting @back="back" />
      </van-popup>
    </div>
  </div>
</template>

<script>
import {
  Uploader,
  Icon,
  CellGroup,
  Cell,
  Popup,
  showLoadingToast,
  closeToast,
  showToast
} from "vant";
import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import OtcCircle from "@/components/Transform/otcCircle/index.vue";
import AppealWaiting from "@/views/cryptos/c2cOrder/components/appeal/AppealWaiting.vue";
import AppealSuccess from "@/views/cryptos/c2cOrder/components/appeal/AppealSuccess.vue";
import {
  mapGetters,
  mapMutations,
} from "vuex";
import {
  REASON_FOR_CANCELLATION
} from "@/store/const.store";
import { _uploadImage } from "@/service/upload.api.js";
import otcApi from "@/service/otc.api";

export default {
  name: "Appeal",
  data() {
    return {
      show: false,
      disable: false,
      info: {
        reason: '', // 理由
        description: '', // 描述
        name: '',
        phone: '',
      },
      fileList: [],
    }
  },
  computed: {
    ...mapGetters('c2c', ['getReasonForCancellation', 'orderNo']),
    fullDisabled() {
      return this.disable && this.fileList.length > 0
    }
  },
  created() {
    this.info.reason = this.getReasonForCancellation
  },
  beforeUnmount() {
    this[REASON_FOR_CANCELLATION]('');
  },
  methods: {
    ...mapMutations('c2c', [REASON_FOR_CANCELLATION]),
    afterRead(file) {
      file.status = 'uploading'
      file.message = this.$t('上传中...')
      // 上传图片到服务器
      _uploadImage(file).then(res => {
        file.status = 'success';
        file.message = this.$t('上传成功');
        file.resURL = res
      }).catch(err => {
        file.status = 'failed';
        file.message = this.$t('图片上传失败');
      })
    },
    // 超出大小
    oversize() {
      showToast(this.$t('超出50MB！'))
    },
    back() {
      this.show = false;
      this.$router.push('/cryptos/wantBuy')
    },
    // 申诉
    async onAppeal() {
      showLoadingToast({
        duration: 0,
        forbidClick: true,
        message: this.$t('加载中')
      })
      const params = {
        order_no: this.orderNo,
        ...this.info,
        img: this.fileList[0].resURL
      }

      console.log(params);

      const res = await otcApi.c2cAppeal(params);
      closeToast();
      this.show = true;
    }
  },
  watch: {
    info: {
      deep: true,
      handler() {
        const res = Object.keys(this.info).filter(key => {
          return this.info[key] === "" || this.info[key] === undefined
        })
        console.log(res);
        this.disable = res.length === 0;
      },
      immediate: true
    }
  },
  components: {
    [Uploader.name]: Uploader,
    [Icon.name]: Icon,
    [CellGroup.name]: CellGroup,
    [Cell.name]: Cell,
    [Popup.name]: Popup,
    OrderNav,
    OtcCircle,
    AppealWaiting,
    AppealSuccess,
  }
}
</script>

<style lang="scss" scoped>
#AppealPage {
  font-size: 30px;

  :deep(.payment-input) {
    input {
      height: 90px;
    }
  }

  textarea {
    &::placeholder {
      color: #B8BCC5;
    }
  }

  .uploader {
    position: relative;
    width: 180px;
    height: 180px;
    border: 2px dashed #EAEBEE;
    background: $tab_background;

    .van-icon {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      font-weight: 700;
      font-size: 50px;
      color: #9399A4;
    }

  }

  .connect {
    input {
      margin: 0;
      font-size: 28px;
      box-sizing: border-box;
      border: none;
      background: $main_background;
    }
  }

  #full {
    :deep(.van-cell-group) {
      color: $text_color1;
      background: $main_background;
    }

    :deep(.van-cell) {
      color: $text_color1;
      background: $main_background;
    }

    :deep(.van-cell__value) {
      color: $text_color;
    }

    :deep(.van-button--info) {
      background: $btn_main;
      border-color: $btn_main;
      color: $main-btn-color;
    }

  }

  :deep(.van-field) {
    background: $input_background !important;
    padding-left: 20px;
    border-radius: 10px;

    .van-field__control {
      color: $c2c_color;
    }
  }

  .textarea {
    padding: 30px !important;
  }


}
</style>
