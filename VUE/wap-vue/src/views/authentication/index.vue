<template>
  <!-- 申请身份认证 -->
  <div style="padding-bottom: 30px" class="text-26 authentication">
    <fx-header @back="loginOut">
      <template #title>
        {{ $t('primaryCertification') }}
      </template>
    </fx-header>
    <!-- <country-list /> -->
    <div v-if="show">
      <div class="pt-14 pb-14 box-border border-b-color" v-if="disabled() || status === 3">
        <div class="flex justify-between items-center px-8 textColor">
          <div class="text-26">{{ $t('authVerify') }}</div>
          <div class="flex items-center" v-if="resultArr[status]">
            <!-- <img
              :src="require(`@/assets/image/assets-center/${resultArr[status] && resultArr[status].split('_')[0]}.png`)"
              alt="success img" class="w-9 h-9" /> -->
            <img src="@/assets/image/assets-center/identifing.png" v-if="status == 1" class="w-10 h-10" />
            <img src="@/assets/image/assets-center/small-success.png" v-if="status == 0 || status == 2"
              class="w-10 h-10" />
            <img src="@/assets/image/assets-center/icon-close.png" v-if="status == 3" class="w-10 h-10" />
            <div class="text-24 ml-4">{{ resultArr[status] && resultArr[status].split('_')[1] }}</div>
          </div>
        </div>
      </div>
      <div class="pl-8 pr-8">
        <div class="">
          <div class=" mb-3 text-28 textColor">{{ $t('nationality') }}</div>
          <div class="pt-7 pb-7 pl-10 pr-10 flex justify-between items-center rounded inputBackground textColor box"
            @click="openBtn">
            <div class="flex items-center ml-2">
              <!-- <div class="iti-flag" :class="key" style="transform: scale(2.1)"></div> -->
              <div class="iti-flag mr-10 " :class="countryCode" style="transform: scale(2.1)"></div>
              <div>{{ countryName }}</div>
            </div>

            <img src="@/assets/image/down-arrow.png" class="w-5 h-3" alt="arrow" />
          </div>
        </div>
        <ExInput :label="$t('realName')" :disabled="disabled()" :clearBtn="!disabled()"
          :placeholderText="$t('entryRealName')" v-model="name" />
        <ExInput :label="$t('credentPassport')" :disabled="disabled()" :clearBtn="!disabled()"
          :placeholderText="$t('entryCredentPassport')" v-model="idnumber" />
        <div>
          <div v-if="resultArr.length > 0" class="mt-1 mb-3 textColor">{{ $t('uploadCredentPassport') }} </div>
          <div v-else class="mt-4 mb-12 textColor">{{ $t('uploadPicCredentPassport') }}</div>
          <div class="flex mb-10 justify-between">
            <div class="flex-1 flex flex-col text-center justify-center items-center">
              <div class="upload-wrap">
                <img src="../../assets/image/kyc/0.png" alt="" class="w-full"
                  v-if="[1, 2].includes(status) && frontFile.length === 0" />
                <van-uploader v-model="frontFile" :max-count="1" :deletable="!disabled()" :after-read="afterRead"
                  @click-upload="onClickUpload('frontFile')" v-else />

              </div>
              <div class="text-28 h-10 textColor1">{{ $t('credentFront') }}</div>
            </div>
            <div class="flex-1 flex flex-col text-center justify-center items-center">
              <div class="upload-wrap">
                <img src="../../assets/image/kyc/1.png" alt="" class="w-full"
                  v-if="[1, 2].includes(status) && reverseFile.length === 0" />
                <van-uploader v-model="reverseFile" :max-count="1" :disabled="disabled()" :deletable="!disabled()"
                  :after-read="afterRead" @click-upload="onClickUpload('reverseFile')" v-else />
              </div>
              <div class="text-28 h-10 textColor1">{{ $t('credentObverse') }}</div>
            </div>
          </div>
        </div>
        <template v-if="!disabled()">
          <div class="text-28 mb-8 textColor">{{ $t('photoExample') }}</div>
          <img src="@/assets/image/kyc/kyc-demo.png" alt="" style="width:100%;height:auto;" class="w-auto h-56 mb-5">
          <!-- <div class="mb-5 flex justify-center">
            <div class="flex flex-1 justify-center">
              <img src="../../assets/image/kyc/kyc_demo1.png" alt="" class="w-80 h-80" />
            </div>
            <div class="flex flex-1 justify-center">
              <img src="../../assets/image/kyc/kyc_demo2.png" alt="" class="w-80 h-80" />
            </div>
          </div> -->
        </template>
        <button class="apply-btn btnMain text-white text-26 h-24 rounded" @click="onSubmit" v-if="!disabled()">{{
          $t('Apply')
        }}</button>
        <div class="pt-9 pb-16 text-24" v-if="!disabled() || status === 3">
          <span class="text-grey">{{ $t('uploadTitle1') }} {{ $t('photoExample') }}</span>
          <span class="text-blue service-text" @click="tokefu"> {{ $t('ContactService') }}</span>
        </div>
        <nationality-list ref='controlChild' :title="$t('selectNation')" @getName="getName" v-if="!disabled()">
        </nationality-list>
      </div>
    </div>
  </div>
</template>

<script setup>
import { _getIdentify, _info, _applyIdentify } from "@/service/user.api.js";
import { _uploadImage } from "@/service/upload.api.js";
import { onMounted, ref } from 'vue';
import nationalityList from './components/nationalityList.vue'
import { useRouter } from "vue-router";
import { showToast, Uploader } from "vant"
// import ExInput from "@/components/ex-input";
import countries from "./components/countryList";
import { getCurrentInstance } from 'vue';
import { useI18n } from "vue-i18n";
const { locale, t } = useI18n()
const router = useRouter()
const countryName = ref(t('selectNation'))
const countryCode = ref('af')
const idnumber = ref('')
const name = ref('')
const frontFile = ref([])
const reverseFile = ref([])
const curFile = ref('frontFile')
const status = ref('')
const imgs = ref([])
const idcard_path_front_path = ref('')
const idcard_path_back_path = ref('')
const resultArr = ref(['small-success_' + t('applynoView'), 'identifing_' + t('reviewing'), 'small-success_' + t('passView'), 'icon-close_' + t('noPassView')])
const show = ref(false)
const controlChild = ref(null)
const { proxy } = getCurrentInstance();


onMounted(() => {
  fetchInfo();
})
const loginOut = () => {
  router.push('/certificationCenter')
}
const fetchInfo = () => {   // 获取状态
  _getIdentify().then(data => {
    show.value = true
    status.value = data.status
    if (data.id != null) {
      countryName.value = countries[locale.value][data.nationality.toLowerCase()].name
      countryCode.value = data.nationality.toLowerCase()

      idnumber.value = data.idnumber
      name.value = data.name
      frontFile.value = data.idimg_1 ? [{ url: data.idimg_1_path, resURL: data.idimg_1 }] : []
      reverseFile.value = data.idimg_2 ? [{ url: data.idimg_2_path, resURL: data.idimg_2 }] : []
    }
  })
}
const onClickUpload = (type) => {
  console.log(type)
  curFile.value = type
}
const disabled = () => { // 是否禁用按钮
  return ![0, 3, ''].includes(status.value)
}
const afterRead = (file) => {
  file.status = 'uploading'
  file.message = t('uploading')

  _uploadImage(file).then(data => {
    file.status = 'success'
    file.message = t('uploadSuccess')
    file.resURL = data
  }).catch(err => {
    file.status = 'failed'
    file.message = t('uploadFailed')
  })
};



//打开国家列表底部弹窗
const openBtn = () => {
  if (!disabled()) {
    proxy.$refs.controlChild.open()
  }
}
//获取到当前选中国家的code值
const getName = (params) => {
  countryName.value = params.name;
  countryCode.value = params.code;
}

const onSubmit = () => {
  if (!countryName.value) {
    showToast(t('selectNation'))
    return
  }
  if (!name.value) {
    showToast(t('entryName'))
    return
  }
  if (!idnumber.value) {
    showToast(t('entryCredent'))
    return
  }
  if (!frontFile.value.length || !reverseFile.value.length) {
    showToast(t('uploadComplete'))
    return
  }
  _applyIdentify({
    name: name.value,
    idnumber: idnumber.value,
    frontFile: frontFile.value,
    reverseFile: reverseFile.value,
    countryName: countryCode.value // this.countryName 存储的 code, 回来再遍历
  }).then(() => {
    showToast(t('submitSuccess'))
    router.push('/verified')
    // this.fetchInfo()
  }).catch(err => {
    if (err.code === 'ECONNABORTED') { showToast('网络超时！'); }
    else if (err.msg !== undefined) { showToast(err.msg); }
  })
}
const tokefu = () => {
  router.push('/customerService')
}

</script>
<style lang="scss" scoped>
@import "@/views/authentication/components/intl.css";

.box {
  padding: 1.5rem !important;
}

.authentication {
  width: 100%;
  box-sizing: border-box;
}

.upload-wrap {
  // width: 220px;
  // height: 220px;
  display: flex;
  justify-content: center;
  align-items: center;
}

input {
  font-size: 35px;
}

input:disabled {
  background: $mainbgWhiteColor;
}

.list-view {
  overflow-y: scroll;
  border-bottom: 1px solid $border_color;
}

.kyc-input {
  width: 100%;
  border: none;
}

.apply-btn {
  border: none;
  outline: none;
  width: 100%;
  height: 50px;
}

.service-text {
  text-decoration: underline;
}

.rounded {
  padding: 0.6rem;
}

.mb-32 {
  margin-bottom: 16px;
}

.pt-35 {
  padding-top: 16px;
}
</style>
