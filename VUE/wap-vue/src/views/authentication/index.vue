<template>
  <!-- 实名认证页面主容器 -->
  <div style="padding-bottom: 30px" class="text-26 authentication">
    <!-- 顶部导航栏，点击返回跳转到认证中心 -->
    <fx-header @back="loginOut">
      <template #title>
        {{ $t('primaryCertification') }}
      </template>
    </fx-header>

    <!-- 数据加载完成后才显示内容，防止闪烁 -->
    <div v-if="show">
      <!-- 审核状态栏：仅在已提交（审核中/已通过）或被驳回时显示 -->
      <div class="pt-14 pb-14 box-border border-b-color" v-if="disabled() || status === 3">
        <div class="flex justify-between items-center px-8 textColor">
          <div class="text-26">{{ $t('authVerify') }}</div>
          <!-- 根据 status 显示对应状态图标和文字
               status: 0=未提交, 1=审核中, 2=已通过, 3=已驳回 -->
          <div class="flex items-center" v-if="resultArr[status]">
            <!-- 审核中图标 -->
            <img src="@/assets/image/assets-center/identifing.png" v-if="status == 1" class="w-10 h-10" />
            <!-- 已通过图标（status=0 为初始已认证，status=2 为审核通过） -->
            <img src="@/assets/image/assets-center/small-success.png" v-if="status == 0 || status == 2" class="w-10 h-10" />
            <!-- 已驳回图标 -->
            <img src="@/assets/image/assets-center/icon-close.png" v-if="status == 3" class="w-10 h-10" />
            <!-- 状态文字，从 resultArr 中取 "_" 后半部分 -->
            <div class="text-24 ml-4">{{ resultArr[status] && resultArr[status].split('_')[1] }}</div>
          </div>
        </div>
      </div>

      <div class="pl-8 pr-8">
        <!-- 国籍选择区域 -->
        <div class="">
          <div class="mb-3 text-28 textColor">{{ $t('nationality') }}</div>
          <!-- 点击打开国家列表弹窗，已提交状态下禁止点击 -->
          <div class="pt-7 pb-7 pl-10 pr-10 flex justify-between items-center rounded inputBackground textColor box"
            @click="openBtn">
            <div class="flex items-center ml-2">
              <!-- 国旗图标，class 动态绑定国家代码（如 cn、us） -->
              <div class="iti-flag mr-10" :class="countryCode" style="transform: scale(2.1)"></div>
              <div>{{ countryName }}</div>
            </div>
            <img src="@/assets/image/down-arrow.png" class="w-5 h-3" alt="arrow" />
          </div>
        </div>

        <!-- 真实姓名输入框，已提交后禁用编辑 -->
        <ExInput :label="$t('realName')" :disabled="disabled()" :clearBtn="!disabled()"
          :placeholderText="$t('entryRealName')" v-model="name" />

        <!-- 证件号码输入框，已提交后禁用编辑 -->
        <ExInput :label="$t('credentPassport')" :disabled="disabled()" :clearBtn="!disabled()"
          :placeholderText="$t('entryCredentPassport')" v-model="idnumber" />

        <!-- 证件照上传区域 -->
        <div>
          <!-- 已有审核记录时显示"上传证件照"，否则显示"请上传证件照片" -->
          <div v-if="resultArr.length > 0" class="mt-1 mb-3 textColor">{{ $t('uploadCredentPassport') }}</div>
          <div v-else class="mt-4 mb-12 textColor">{{ $t('uploadPicCredentPassport') }}</div>

          <div class="flex mb-10 justify-between">
            <!-- 证件正面上传 -->
            <div class="flex-1 flex flex-col text-center justify-center items-center">
              <div class="upload-wrap">
                <!-- 审核中/已通过且未上传时，显示占位图 -->
                <img src="../../assets/image/kyc/0.png" alt="" class="w-full"
                  v-if="[1, 2].includes(status) && frontFile.length === 0" />
                <!-- 其他情况显示上传组件 -->
                <van-uploader v-model="frontFile" :max-count="1" :deletable="!disabled()" :after-read="afterRead"
                  @click-upload="onClickUpload('frontFile')" v-else />
              </div>
              <div class="text-28 h-10 textColor1">{{ $t('credentFront') }}</div>
            </div>

            <!-- 证件背面上传 -->
            <div class="flex-1 flex flex-col text-center justify-center items-center">
              <div class="upload-wrap">
                <!-- 审核中/已通过且未上传时，显示占位图 -->
                <img src="../../assets/image/kyc/1.png" alt="" class="w-full"
                  v-if="[1, 2].includes(status) && reverseFile.length === 0" />
                <!-- 其他情况显示上传组件 -->
                <van-uploader v-model="reverseFile" :max-count="1" :disabled="disabled()" :deletable="!disabled()"
                  :after-read="afterRead" @click-upload="onClickUpload('reverseFile')" v-else />
              </div>
              <div class="text-28 h-10 textColor1">{{ $t('credentObverse') }}</div>
            </div>
          </div>
        </div>

        <!-- 拍摄示例区域：仅在未提交状态下显示 -->
        <template v-if="!disabled()">
          <div class="text-28 mb-8 textColor">{{ $t('photoExample') }}</div>
          <!-- 证件正面示例 + 证件背面示例，并排展示 -->
          <div class="mb-5 flex justify-around">
            <div class="flex flex-1 justify-center">
              <img src="../../assets/image/kyc/kyc_demo1.png" alt="" class="w-80 h-80" />
            </div>
            <div class="flex flex-1 justify-center">
              <img src="../../assets/image/kyc/kyc_demo2.png" alt="" class="w-80 h-80" />
            </div>
          </div>
        </template>

        <!-- 申请认证按钮，已提交状态下隐藏 -->
        <button class="apply-btn btnMain text-white text-26 h-24 rounded" @click="onSubmit" v-if="!disabled()">
          {{ $t('Apply') }}
        </button>

        <!-- 底部提示文字：上传失败提示 + 联系客服入口，未提交或被驳回时显示 -->
        <div class="pt-9 pb-16 text-24" v-if="!disabled() || status === 3">
          <span class="text-grey">{{ $t('uploadTitle1') }} {{ $t('photoExample') }}</span>
          <span class="text-blue service-text" @click="tokefu"> {{ $t('ContactService') }}</span>
        </div>

        <!-- 国家选择弹窗组件，选中后回调 getName 更新国籍，仅未提交时可用 -->
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
import countries from "./components/countryList";
import { getCurrentInstance } from 'vue';
import { useI18n } from "vue-i18n";

const { locale, t } = useI18n()
const router = useRouter()

// 当前选中的国家名称，默认显示"请选择国籍"
const countryName = ref(t('selectNation'))
// 当前选中的国家代码（ISO 2位小写），用于显示国旗
const countryCode = ref('af')
// 证件号码
const idnumber = ref('')
// 真实姓名
const name = ref('')
// 证件正面图片列表（vant uploader 格式）
const frontFile = ref([])
// 证件背面图片列表（vant uploader 格式）
const reverseFile = ref([])
// 当前正在上传的图片类型：'frontFile' 或 'reverseFile'
const curFile = ref('frontFile')
// 认证状态：0=未提交, 1=审核中, 2=已通过, 3=已驳回，空字符串=未认证
const status = ref('')
// 图片列表（备用，暂未使用）
const imgs = ref([])
// 证件正面本地路径（备用）
const idcard_path_front_path = ref('')
// 证件背面本地路径（备用）
const idcard_path_back_path = ref('')
// 各状态对应的图标名和文字，格式："图标名_显示文字"
// 索引对应 status 值：0=已认证, 1=审核中, 2=已通过, 3=已驳回
const resultArr = ref([
  'small-success_' + t('applynoView'),
  'identifing_' + t('reviewing'),
  'small-success_' + t('passView'),
  'icon-close_' + t('noPassView')
])
// 控制页面内容显示，接口返回后才渲染，避免闪烁
const show = ref(false)
// 国家列表弹窗组件的 ref，用于手动调用 open()
const controlChild = ref(null)
const { proxy } = getCurrentInstance();

// 页面挂载时获取认证状态
onMounted(() => {
  fetchInfo();
})

// 返回按钮：跳转到认证中心
const loginOut = () => {
  router.push('/certificationCenter')
}

// 获取当前用户的实名认证状态及已填信息
const fetchInfo = () => {
  _getIdentify().then(data => {
    show.value = true
    status.value = data.status
    // 如果已有认证记录，回填表单数据
    if (data.id != null) {
      countryName.value = countries[locale.value][data.nationality.toLowerCase()].name
      countryCode.value = data.nationality.toLowerCase()
      idnumber.value = data.idnumber
      name.value = data.name
      // 回填证件照，有图片则构造 uploader 格式，否则为空数组
      frontFile.value = data.idimg_1 ? [{ url: data.idimg_1_path, resURL: data.idimg_1 }] : []
      reverseFile.value = data.idimg_2 ? [{ url: data.idimg_2_path, resURL: data.idimg_2 }] : []
    }
  })
}

// 记录当前点击的上传区域类型，供 afterRead 判断上传到哪个字段
const onClickUpload = (type) => {
  curFile.value = type
}

// 判断表单是否应禁用（审核中或已通过时禁止修改）
// status 为 0/3/'' 时可编辑，其余状态禁用
const disabled = () => {
  return ![0, 3, ''].includes(status.value)
}

// 图片选择后自动上传到服务器
const afterRead = (file) => {
  file.status = 'uploading'
  file.message = t('uploading')

  _uploadImage(file).then(data => {
    file.status = 'success'
    file.message = t('uploadSuccess')
    // 将服务器返回的图片 URL 存入 file 对象，提交时使用
    file.resURL = data
  }).catch(err => {
    file.status = 'failed'
    file.message = t('uploadFailed')
  })
};

// 打开国家选择弹窗，已提交状态下不允许修改
const openBtn = () => {
  if (!disabled()) {
    proxy.$refs.controlChild.open()
  }
}

// 国家列表选中回调，更新国家名称和国家代码
const getName = (params) => {
  countryName.value = params.name;
  countryCode.value = params.code;
}

// 提交实名认证申请，提交前做表单校验
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
  // 证件正面和背面必须都上传
  if (!frontFile.value.length || !reverseFile.value.length) {
    showToast(t('uploadComplete'))
    return
  }
  _applyIdentify({
    name: name.value,
    idnumber: idnumber.value,
    frontFile: frontFile.value,
    reverseFile: reverseFile.value,
    countryName: countryCode.value // 传国家代码（如 cn），后端根据代码查国家名
  }).then(() => {
    showToast(t('submitSuccess'))
    router.push('/verified')
  }).catch(err => {
    if (err.code === 'ECONNABORTED') { showToast('网络超时！'); }
    else if (err.msg !== undefined) { showToast(err.msg); }
  })
}

// 跳转到在线客服页面
const tokefu = () => {
  router.push('/workerOrder')
}
</script>

<style lang="scss" scoped>
/* 引入国际电话区号国旗样式 */
@import "@/views/authentication/components/intl.css";

/* 国籍选择框内边距 */
.box {
  padding: 1.5rem !important;
}

/* 页面整体宽度自适应 */
.authentication {
  width: 100%;
  box-sizing: border-box;
}

/* 证件照上传区域容器，居中对齐 */
.upload-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
}

input {
  font-size: 35px;
}

/* 禁用状态下输入框背景色 */
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

/* 申请认证按钮样式 */
.apply-btn {
  border: none;
  outline: none;
  width: 100%;
  height: 50px;
}

/* 联系客服文字下划线 */
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
