<template>
  <div id="cryptos">
    <div v-if="configDetail" class="flex flex-col w-full h-full">
      <order-nav class="w-full">
        <template #title>
          {{ $t(configDetail.methodName) }}
        </template>
      </order-nav>
      <div class="flex-1 w-full pt-14 overflow-auto">
        <div class="content pr-8 pl-8">
          <div class="flex items-center justify-center mb-14">
            <img class="w-20" :src="imgSrc(configDetail.methodImg)" alt="">
          </div>
          <div class="payment-input-wrapper">
            <div class="label text-28 mb-4 textColor2">
              {{ $t('支付方式配置') }}
            </div>
            <div class="payment-input w-full">
              <van-field disabled v-model="configDetail.methodName" />
            </div>
          </div>
          <div class="payment-input-wrapper">
            <div class="label text-28 mb-4 textColor2">
              {{ $t('支付方式类型') }}
            </div>
            <div class="payment-input w-full">
              <van-field disabled v-model="configDetail.methodTypeName" />
            </div>
          </div>
          <div class="payment-input-wrapper">
            <div class="label text-28 mb-4 textColor2">
              {{ $t('真实姓名') }}
            </div>
            <div class="payment-input w-full">
              <van-field v-model="params.name" :placeholder="$t('真实姓名')" />
            </div>
          </div>
          <div class="payment-input-wrapper" v-for="(item, index) in list" :key="index">
            <div class="label text-28 mb-4 textColor2">
              {{ $t(item[`paramName${parseInt(index) + 1}`]) }}
            </div>
            <div class="payment-input w-full">
              <van-field v-model="item[`param_value${parseInt(index) + 1}`]"
                :placeholder="$t(item[`paramName${parseInt(index) + 1}`])" />
            </div>
          </div>

          <div class="mb-14" v-if="configDetail.methodType !== 1">
            <p class="mb-1 text-28" style="color: rgb(134, 141, 154);">{{ $t('支付二维码') }}</p>
            <van-uploader v-model="fileList" :after-read="afterRead" :max-count="1" />
          </div>
          <div class="payment-input-wrapper">
            <div class="label text-28 mb-1 textColor2">
              {{ $t('备注') }}
            </div>
            <div class="payment-input w-full">
              <van-field class="textarea" type="textarea" rows="5" v-model="params.remark" :placeholder="$t('备注')" />
            </div>
          </div>
        </div>
        <div class="w-full pl-8 pr-8 pb-16 mt-80 box-border">
          <div class="text-24 mb-6" :style="{ 'color': '#868D9A' }">
            {{ $t('温馨提示：当您出售数字货币时，您选择的收款方式将向买方展示，请确认信息填写准确无误。') }}
          </div>
          <otc-button :disabled="fullDisabled" :text="$t('保存')" @btnClick="handlerClick"></otc-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import otcApi from "@/service/otc.api";

import { Button, showToast, Uploader } from 'vant';
import { _uploadImage } from "@/service/upload.api.js";

import OrderNav from "@/components/Transform/order-nav/OrderNav.vue";
import OtcButton from "@/components/Transform/otc-button/OtcButton.vue";
import { BASE_URL } from "@/config";

export default {
  name: "CN",
  // configType 编辑/添加
  props: ["configType", 'id'],
  data() {
    return {
      configDetail: null,
      disabled1: false, //
      disabled2: false, //
      list: [],
      params: {
        name: '',
        remark: '',
      },
      fileList: [],
      isClick: true
    }
  },
  created() {
    if (this.configType === 'add') {
      this.getPaymentMethodConfigDetail();
      this.getUserName();
    } else if (this.configType === 'edit') {
      this.getPaymentMethodDetail();
    }

  },
  methods: {
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
    handlerClick() {
      if (!this.isClick) {
        return
      }
      this.isClick = false
      if (this.configType === 'add') {
        this.save();
      } else {
        this.edit();
      }
    },
    // 获取实名
    async getUserName() {
      const res = await otcApi.getUserName()
      this.params.name = res.name || '';
    },
    // 添加
    async save() {
      const params = this.formatParams('method_config_id');
      const res = await otcApi.ctcPaymentMethodAddPay(params)
      showToast({
        message: this.$t('添加成功')
      })

      setTimeout(() => {
        this.isClick = true
        this.$router.replace({
          path: '/cryptos/paymentMethod'
        })
      }, 1000)
    },
    formatParams(id) {
      const params = {
        [id]: this.configDetail.uuid,
        real_name: this.params.name,
        remark: this.params.remark,
        qrcode: (this.fileList[0] && this.fileList[0].resURL) || (this.fileList[0] && this.fileList[0].url) || '',
      }

      this.list.forEach(item => {
        Object.keys(item).forEach(key => {
          if (key.indexOf('param_value') !== -1) {
            params[key] = item[key]
          }
        })
      })

      return params;
    },
    async edit() {
      const params = this.formatParams('id');
      console.log(params);

      const res = await otcApi.ctcPaymentMethodUpdate(params);
      console.log(res);
      showToast({
        message: this.$t('保存')
      })

      setTimeout(() => {
        this.isClick = true
        this.$router.replace({
          path: '/cryptos/paymentMethod'
        })
      }, 1000)
    },
    formatData(data) {
      let index = 1;
      Object.keys(data).forEach(key => {
        if (key.indexOf('paramName') !== -1) {
          if (this.configDetail[key] && this.configDetail[key].length > 0) {
            if (this.configType === 'edit') {
              console.log(key, index);
              this.list.push({
                ['paramName' + index]: this.configDetail['paramName' + index],
                ['param_value' + index]: this.configDetail['paramValue' + index],
              })
            } else {
              this.list.push({
                ['paramName' + index]: this.configDetail[key],
                ['param_value' + index]: '',
              })
            }
            index++;
          }
        }
      })
      console.log(this.list)
    },
    async getPaymentMethodConfigDetail() {
      const res = await otcApi.paymentMethodConfigDetail({ id: this.id, language: this.$i18n.locale })
      this.configDetail = res;

      this.formatData(this.configDetail)
    },
    async getPaymentMethodDetail() {
      const res = await otcApi.ctcPaymentMethodDetail({ id: this.id, language: this.$i18n.locale })
      this.configDetail = res;
      this.formatData(this.configDetail)
      this.initData();
    },
    initData() {
      this.params.name = this.configDetail.realName;
      this.params.remark = this.configDetail.remark;
      if (this.configDetail.qrcode) {
        this.fileList.push({
          url: this.imgSrc(this.configDetail.qrcode),
          resURL: this.configDetail.qrcode
        })
      }
    },
    imgSrc(path) {
      if (path.indexOf('http') !== -1) {
        return path
      } else {
        const url = `${BASE_URL}public/showimg!showImg.action?imagePath=`
        return url + path;
      }
    },
  },
  computed: {
    bankNumber: {
      get() {
        return this.info.number && this.info.number.replace(/(\d{4})/g, '$1 ').trim()
      },
      set(newVal) {
        this.info.number = newVal;
      }
    },
    fullDisabled() {
      console.log(this.disabled1, this.disabled2)
      return this.disabled1 && this.disabled2
    }
  },
  components: {
    [Button.name]: Button,
    [Uploader.name]: Uploader,
    OrderNav,
    OtcButton,
  },
  watch: {
    // 是否存在空值
    list: {
      deep: true,
      handler() {
        const res = this.list.filter((item, index) => {
          if (index != 2) {
            return item[`param_value${(index) + 1}`] === "" || item[`param_value${parseInt(index) + 1}`] === undefined
          }
        })
        this.disabled1 = res.length === 0;
      },
      immediate: true
    },
    params: {
      deep: true,
      handler() {
        const res = Object.keys(this.params).filter(key => {
          if (key !== 'remark') {
            return this.params[key] === "" || this.params[key] === undefined
          }
        }
        )
        this.disabled2 = res.length === 0;
      },
      immediate: true
    }
  }
}
</script>

<style lang="scss" scoped>
#cryptos {
  font-size: 30px;

  :deep(.payment-input-wrapper) {
    margin-bottom: 60px;

    input {
      height: 100px;
    }
  }

  :deep(.van-uploader__upload) {
    width: 180px !important;
    height: 180px !important;
  }

  :deep(.van-button--info) {
    background: $btn_main;
    border-color: $btn_main;
    color: $main-btn-color;
  }

  .desc {
    width: 100%;
    padding: 46px 24px;
    border-radius: 8px;
    background: #F0F1F4;
  }

  :deep(.van-field) {
    background: $input_background;
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