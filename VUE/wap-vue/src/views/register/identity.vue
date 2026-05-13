<template>
    <!-- 
        注册流程 - 身份认证页面
        功能：用户注册时进行实名认证，包括选择国籍、填写姓名、证件号码、上传身份证正反面照片
    -->
    <div class="identity pl-8 pr-8 text-24" style="padding-bottom: 30px;">
        <!-- 头部导航栏 -->
        <div class="header">
            <!-- 返回上一页 -->
            <div class="flex items-center" @click="$router.go(-1)">
                <img src="../../assets/image/assets-center/left-arrow.png" alt="返回" class="leftReturn" />
            </div>
            <!-- 跳过按钮，直接进入谷歌验证 -->
            <div class="textColor" @click="$router.push('/gooleVerify')">{{ $t('skip') }}</div>
        </div>
        
        <!-- 步骤指示器，当前为第2步 -->
        <Step :step="2"></Step>
        
        <!-- 页面标题 -->
        <div class="title textColor">{{ $t('realNameVertify') }}</div>
        
        <!-- 认证状态显示区域（仅当已提交认证或认证失败时显示） -->
        <div class="pt-8 pb-7 box-border border-b-color" v-if="disabled() || status === 3">
            <div class="flex justify-between items-center px-4">
                <div class="text-26">{{ $t('authVerify') }}</div>
                <div class="flex items-center" v-if="resultArr[this.status]">
                    <!-- 根据状态显示对应的图标 -->
                    <img :src="require(`@/assets/image/assets-center/${resultArr[this.status] && resultArr[this.status].split('_')[0]}.png`)"
                        alt="认证状态图标" class="w-4.5 h-4.5" />
                    <!-- 根据状态显示对应的文字 -->
                    <div class="text-16 ml-2">{{ resultArr[this.status] && resultArr[this.status].split('_')[1] }}</div>
                </div>
            </div>
        </div>
        
        <!-- 表单内容区域 -->
        <div>
            <!-- 国籍选择 -->
            <div class="mb-1">
                <div class="mt-27 mb-13 text-12 textColor">{{ $t('nationality') }}</div>
                <div class="pt-2 pb-2 pl-4 pr-19 flex justify-between items-center rounded inputBackground textColor"
                    @click="openBtn">
                    <div class="flex items-center">
                        <!-- 显示国旗图标 -->
                        <div class="iti-flag mr-8" :class="countryCode" style="transform: scale(1.3)"></div>
                        <!-- 显示国家名称 -->
                        <div>{{ countryName }}</div>
                    </div>
                    <!-- 下拉箭头 -->
                    <img v-if="resultArr.length === 0" src="@/assets/image/down-arrow.png" class="w-17 h-10" alt="下拉箭头" />
                </div>
            </div>
            
            <!-- 真实姓名输入框 -->
            <ExInput :label="$t('realName')" :placeholderText="$t('entryRealName')" v-model="name" />
            
            <!-- 证件号码输入框 -->
            <ExInput :label="$t('credentPassport')" :placeholderText="$t('entryCredentPassport')" v-model="idnumber" />
            
            <!-- 证件照片上传区域 -->
            <div>
                <div v-if="resultArr.length > 0" class="mb-13 textColor">{{ $t('uploadCredentPassport') }}</div>
                <div v-else class="mt-55 mb-13">{{ $t('uploadPicCredentPassport') }}</div>
                
                <div class="flex mt-4 mb-6 justify-between">
                    <!-- 身份证正面上传 -->
                    <div class="flex-1 flex flex-col text-center justify-center items-center">
                        <div class="upload-wrap">
                            <!-- 如果正在审核且没有上传图片，显示占位图 -->
                            <img src="@/assets/image/kyc/0.png" alt="正面占位图" class="w-full"
                                v-if="[1, 2].includes(status) && frontFile.length === 0" />
                            <!-- 图片上传组件 -->
                            <van-uploader v-model="frontFile" :max-count="1" :disabled="disabled()" :deletable="!disabled()"
                                :after-read="(file) => afterRead(file, frontFile)" v-else />
                        </div>
                        <div class="mt-3 text-20 h-5 textColor">{{ $t('credentFront') }}</div>
                    </div>
                    
                    <!-- 身份证反面上传 -->
                    <div class="flex-1 flex flex-col text-center justify-center items-center">
                        <div class="upload-wrap">
                            <!-- 如果正在审核且没有上传图片，显示占位图 -->
                            <img src="@/assets/image/kyc/1.png" alt="反面占位图" class="w-full"
                                v-if="[1, 2].includes(status) && reverseFile.length === 0" />
                            <!-- 图片上传组件 -->
                            <van-uploader v-model="reverseFile" :max-count="1" :disabled="disabled()"
                                :deletable="!disabled()" :after-read="(file) => afterRead(file, reverseFile)" v-else />
                        </div>
                        <div class="mt-3 text-20 h-5 textColor">{{ $t('credentObverse') }}</div>
                    </div>
                </div>
            </div>
            
            <!-- 照片示例（仅当可编辑时显示） -->
            <template v-if="!disabled()">
                <div class="mb-4 textColor text-26">{{ $t('photoExample') }}</div>
                <img src="@/assets/image/kyc/kyc-demo.png" alt="照片示例" style="width:100%;height:auto;" class="w-auto h-56 mb-24">
            </template>
            
            <!-- 提交按钮 -->
            <van-button class="w-full" style="margin-top:10px;" type="primary" @click="onSubmit">{{ $t('nextStep') }}</van-button>
            
            <!-- 国家选择弹窗组件 -->
            <nationality-list ref='controlChildRef' :title="$t('selectNation')" @getName="getName" v-if="!disabled()"></nationality-list>
        </div>
    </div>
</template>

<script setup>
// 导入步骤指示器组件
import Step from "./step.vue";
// 导入国家选择弹窗组件
import nationalityList from '../authentication/components/nationalityList.vue'
// 导入Vant上传组件
import { Uploader } from 'vant';
// 导入实名认证相关API
import { _applyIdentify, _getIdentify } from '@/service/user.api.js'
// 导入图片上传API
import { _uploadImage } from '@/service/upload.api'
// 导入国家列表数据
import countriesinit from "../authentication/components/countryList";
// 导入自定义输入框组件
import ExInput from "@/components/ex-input/index.vue";
// 导入Vant提示组件
import { showToast } from "vant";
// 导入Vue Router
import { useRouter } from "vue-router";
// 导入Vue响应式API
import { ref, reactive, onMounted } from "vue";
// 导入国际化API
import { useI18n } from 'vue-i18n'

// 初始化国际化和路由
const { locale, t } = useI18n()
const router = useRouter()

// ==================== 响应式数据定义 ====================

// 国家列表数据
const countries = ref(countriesinit)
// 选中的国家名称
const countryName = ref(t('selectNation'))
// 选中的国家代码（如 'af' 代表阿富汗）
const countryCode = ref('af')
// 证件号码
const idnumber = ref('')
// 用户真实姓名
const name = ref('')
// 身份证正面照片文件数组
const frontFile = ref([])
// 身份证反面照片文件数组
const reverseFile = ref([])
// 认证状态：-1=未提交, 0=待审核, 1=审核中, 2=已通过, 3=未通过
const status = ref(-1)
// 图片数组（备用）
const imgs = ref([])
// 状态结果映射数组：[未提交, 审核中, 已通过, 未通过]
const resultArr = ref([
    'small-success_' + t('applynoView'),   // 索引0：未提交状态
    'identifing_' + t('reviewing'),        // 索引1：审核中状态
    'small-success_' + t('passView'),       // 索引2：已通过状态
    'icon-close_' + t('noPassView')        // 索引3：未通过状态
])

// ==================== 生命周期钩子 ====================

/**
 * 组件挂载时调用，获取用户当前认证状态
 */
onMounted(() => {
    fetchInfo()
})

// ==================== 方法定义 ====================

/**
 * 获取用户认证信息
 * 从后端获取当前用户的实名认证状态和已填写的信息
 */
const fetchInfo = () => {
    _getIdentify().then(data => {
        // 更新认证状态
        status.value = data.status
        // 如果不是未提交状态（status !== 0），则填充已有的认证信息
        if (data.status !== 0) {
            // 根据国际化语言获取国家名称
            countryName.value = countries.value[locale.value][data.nationality]['name']
            // 设置国家代码
            countryCode.value = data.nationality
            // 设置证件号码
            idnumber.value = data.idnumber
            // 设置姓名
            name.value = data.name
            // 设置正面照片（包含显示URL和上传URL）
            frontFile.value = data.idimg_1 ? [{ url: data.idimg_1_path, resURL: data.idimg_1 }] : []
            // 设置反面照片（包含显示URL和上传URL）
            reverseFile.value = data.idimg_2 ? [{ url: data.idimg_2_path, resURL: data.idimg_2 }] : []
        }
    })
}

/**
 * 返回上一页
 */
const onClickLeft = () => {
    router.go(-1);
}

/**
 * 判断表单是否禁用
 * @returns {boolean} - true表示禁用（不可编辑），false表示可编辑
 * 状态为 0(待审核)、3(未通过)、-1(未提交) 时可编辑
 */
const disabled = () => {
    return ![0, 3, -1].includes(status.value)
}

/**
 * 点击上传按钮时记录当前上传的类型
 * @param {string} type - 'frontFile' 或 'reverseFile'
 */
const onClickUpload = (type) => {
    curFile.value = type
}

/**
 * 图片读取完成后的处理函数
 * @param {object} file - 上传的文件对象
 * @param {object} targetRef - 目标文件数组的ref（frontFile 或 reverseFile）
 */
const afterRead = (file, targetRef) => {
    // 设置上传状态为上传中
    file.status = 'uploading'
    file.message = t('uploading')
    
    // 调用图片上传API
    _uploadImage(file).then(data => {
        // 上传成功，设置状态为成功
        file.status = 'success'
        file.message = t('uploadSuccess')
        // 保存上传后的图片URL（用于提交给后端）
        file.resURL = data
        // 更新目标文件数组
        targetRef.value = [file]
    }).catch(() => {
        // 上传失败，设置状态为失败
        file.status = 'failed'
        file.message = t('uploadFailed')
    })
}

// ==================== 国家选择弹窗相关 ====================

// 国家选择弹窗组件引用
const controlChildRef = ref(null)

/**
 * 打开国家选择弹窗
 */
const openBtn = () => {
    if (!disabled()) {
        controlChildRef.value.open();
    }
}

/**
 * 获取选中的国家信息
 * @param {object} params - 包含name(国家名称)和code(国家代码)
 */
const getName = (params) => {
    countryName.value = params.name;
    countryCode.value = params.code;
}

// ==================== 表单提交 ====================

/**
 * 提交实名认证表单
 * 包含表单验证和API调用
 */
const onSubmit = () => {
    // 验证国籍是否选择
    if (!countryName.value) {
        showToast(t('selectNation'))
        return
    }
    
    // 验证姓名是否填写
    if (!name.value) {
        showToast(t('entryName'))
        return
    }
    
    // 验证证件号码是否填写
    if (!idnumber.value) {
        showToast(t('entryCredent'))
        return
    }
    
    // 验证图片是否上传完整
    if (!frontFile.value.length || !reverseFile.value.length) {
        showToast(t('uploadComplete'))
        return
    }
    
    // 如果状态不是待审核（已有认证记录），直接跳转到谷歌验证
    if (disabled()) {
        router.push('/gooleVerify');
    } else {
        // 调用实名认证API提交数据
        console.log('[submit] frontFile:', JSON.stringify(frontFile.value), 'reverseFile:', JSON.stringify(reverseFile.value))
        _applyIdentify({
            name: name.value,
            idnumber: idnumber.value,
            frontFile: frontFile.value,
            reverseFile: reverseFile.value,
            countryName: countryCode.value
        }).then(() => {
            // 提交成功提示
            showToast(t('submitSuccess'))
            // 跳转到谷歌验证页面
            router.push('/gooleVerify');
        }).catch(err => {
            // 提交失败，打印错误并显示提示
            console.log(err)
            showToast(t(err.message));
        })
    }
}

</script>
<style lang="scss" scoped>
@import "@/views/authentication/components/intl.css";

.identity {
    width: 100%;
    box-sizing: border-box;
}

.upload-wrap {
    width: 110px;
    height: 110px;
    display: flex;
    justify-content: center;
    align-items: center;
}

input {
    font-size: 18px;
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


.service-text {
    text-decoration: underline;
}

.header {
    display: flex;
    justify-content: space-between;
    padding: 0 13px;
    font-size: 14px;
    height: 50px;
    line-height: 50px;
}

.stepBox {
    padding: 0 15px;
}

.title {
    font-weight: 700;
    font-size: 26px;
    margin-top: 25px;
    margin-bottom: 30px;
}

.city {
    background: $light-grey;
}
</style>
