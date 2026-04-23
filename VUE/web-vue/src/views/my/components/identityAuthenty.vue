<template>
  <!-- 0:未认证 1：已提交申请，未审核 2:认证成功 3:认证失败  -->
  <div>
    <!--未认证或已认证时 -->
    <el-dialog
      class="my_security"
      :title="$t('message.user.shenfenrenzheng')"
      v-model="visible"
      :close-on-click-modal="false"
      @close="handleClose"
      width="480px"
      center
    >
      <div class="flex_center">
        <!-- 未审核 -->
        <div v-if="identifyStatus == 1" class="status_show">
          <img src="@/assets/myImages/checking.png" width="50" height="50" />
          <div class="state_tip">{{ $t("message.user.daishenhe") }}</div>
        </div>
        <!-- 认证成功 -->
        <div v-if="identifyStatus == 2" class="status_show">
          <img
            src="@/assets/myImages/icon-image/account-security/success-green.png"
            width="50"
            height="50"
          />
          <div class="state_tip">{{ $t("message.user.yirenzheng") }}</div>
        </div>
        <!-- 认证失败 -->
        <div v-if="identifyStatus == 3" class="red status_show">
          <img src="@/assets/myImages/failed.png" width="50" height="50" />
          <div class="state_tip">{{ $t("message.user.shenheweitongguo") }}</div>
          <div>{{ $t("message.user.yuanyin") }}:{{ msg }}</div>
        </div>
      </div>
      <!-- 信息编写 -->
      <div>
        <!-- 国籍选择 -->
        <div class="label">{{ $t("message.user.guoji") }}</div>
        <vue3-country-intl
          v-model="nationality"
          :disabled="disabled"
          :showAreaCode="false"
          type="country"
          :useChinese="isUseChinese"
          :placeholder="$t('message.user.qingxuanzeguoji')"
          :noDataText="$t('message.user.nationNodata')"
        >
        </vue3-country-intl>
        <!-- 姓名 -->
        <div class="label label_top">{{ $t("message.user.xingming") }}</div>
        <el-input
          v-model="name"
          :disabled="disabled"
          :placeholder="$t('message.user.qsr_xingming')"
        ></el-input>
        <!-- 身份证号码 -->
        <div class="label label_top">
          {{ $t("message.user.zhengjianhao") }}
        </div>
        <el-input
          v-model="idNumber"
          :disabled="disabled"
          :placeholder="$t('message.user.qsr_zengjianhaoma')"
        ></el-input>
        <!-- 图片上传 -->
        <div class="label label_top">
          {{ $t("message.user.shimingrenzhengtupianshangchuan") }}
        </div>
        <div class="image_upload">
          <!-- 正面 -->
          <div class="text-center">
            <el-upload
              class="avatar-uploader"
              :headers="uploadHeader"
              :action="`${nowUrl}/api/api/uploadFile`"
              accept=".jpg,.jpeg,.png,.gif.JPG,.JPEG,.PNG,.GIF"
              :show-file-list="false"
              :on-success="handelDoucumentsFront"
              :on-error="onErrorUpload"
              :before-upload="beforeFrontUpload"
              :disabled="disabled"
            >
              <img v-if="oneImg" :src="oneImg" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon">
                <Plus />
              </el-icon>
            </el-upload>
            <p>{{ $t("message.user.zhengjianzhengmian") }}</p>
          </div>
          <div class="text-center" style="margin: 0 10px">
            <el-upload
              class="avatar-uploader"
              :headers="uploadHeader"
              :action="`${nowUrl}/api/api/uploadFile`"
              accept=".jpg,.jpeg,.png,.gif.JPG,.JPEG,.PNG,.GIF"
              :show-file-list="false"
              :on-success="handelDoucumentsBack"
              :on-error="onErrorUpload"
              :before-upload="beforeBackUpload"
              :disabled="disabled"
            >
              <img v-if="twoImg" :src="twoImg" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon">
                <Plus />
              </el-icon>
            </el-upload>
            <p>{{ $t("message.user.zhengjianfanmian") }}</p>
          </div>
          <div class="text-center">
            <el-upload
              class="avatar-uploader"
              :headers="uploadHeader"
              :action="`${nowUrl}/api/api/uploadFile`"
              accept=".jpg,.jpeg,.png,.gif.JPG,.JPEG,.PNG,.GIF"
              :show-file-list="false"
              :on-success="holdIdCardSuccess"
              :on-error="onErrorUpload"
              :before-upload="beforeAvatarUpload"
              :disabled="disabled"
            >
              <img v-if="threeImg" :src="threeImg" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon">
                <Plus />
              </el-icon>
            </el-upload>
            <p>{{ $t("message.user.shouchizhengjianzhao") }}</p>
          </div>
        </div>
        <!-- 操作按钮 -->
        <div class="bind_btn">
          <!-- 未认证 -->
          <el-button
            type="primary"
            @click="applyBtn"
            v-if="identifyStatus == 0"
            >{{ $t("message.user.shenqingrenzheng") }}</el-button
          >
          <!-- 未审核 -->
          <el-button
            type="primary"
            v-if="identifyStatus == 1"
            disabled="true"
            >{{ $t("message.home.yitijiao") }}</el-button
          >
          <!-- 认证失败 -->
          <el-button
            type="primary"
            @click="applyBtn"
            v-if="identifyStatus == 3"
            >{{ $t("message.user.chongxinrenzheng") }}</el-button
          >
          <!-- 认证成功 不需要按钮-->
          <!-- <el-button type="primary" @click="handleClose" v-if="identifyStatus == 2">{{ $t("message.user.queren")
          }}</el-button> -->
        </div>
      </div>
    </el-dialog>

    <!--提交成功弹窗 -->
    <template v-if="submitDialogShow">
      <el-dialog
        class="my_security"
        :title="$t('message.user.shenfenrenzheng')"
        v-model="visible"
        :close-on-click-modal="false"
        width="480px"
        center
      >
        <div class="text-center">
          <img
            src="@/assets/myImages/icon-image/account-security/success-green.png"
            width="85px"
            height="85px"
          />
          <p style="font-size: 20px">
            <b>{{ $t("message.user.tijiaochenggong") }}</b>
          </p>
          <p>
            {{ $t("message.user.chang14") }}
          </p>
        </div>
        <div class="bind_btn">
          <el-button type="primary" @click="handleClose">{{
            $t("message.user.queren")
          }}</el-button>
        </div>
      </el-dialog>
    </template>
  </div>
</template>

<script>
import Axios from "@/api/my.js";
import { ElMessage } from "element-plus";
import { Plus } from "@element-plus/icons-vue";
import { getStorage, setStorage, getBrowserLang } from "@/utils/index";
import { nowUrl } from "@/utils";
import { signatureGenerate } from "@/utils/signatureUtil";
export default {
  components: { Plus },
  emits: ["changeModalShow"],
  props: {
    isShowIdentity: {
      // 弹窗是否展示
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      submitDialogShow: false, //提交成功的弹窗
      msg: "", //错误提示
      nowUrl,
      identifyStatus: "", //认证状态
      nationality: "", //国籍
      name: "", //姓名
      idNumber: "", //证件号码
      // 提交给后端的图片data
      idFrontImg: "", //证件正面
      idBackImg: "", //证件背面
      handheldPhoto: "", //手持身份证
      // http
      oneImg: "",
      twoImg: "",
      threeImg: "",
      disabled: false,
      //
      uploadHeader: {
        tissuePaper: "",
        sign: "",
        systemRandom: "",
      },
    };
  },
  computed: {
    isUseChinese: function () {
      return getStorage("lang") === "zh-CN";
    },
    visible: {
      get() {
        return this.isShowIdentity;
      },
      set(val) {
        this.$emit("changeModalShow", false, "isShowIdentity");
      },
    },
  },
  mounted() {
    //调用身份认证信息方法
    this.spToken = localStorage.getItem("spToken");
    if (this.spToken) {
      this.getIdentify();
    }
  },
  methods: {
    // 正面  （每次取的时间戳和随机数必须不一样）
    beforeFrontUpload(file) {
      const { timestamp, signature, systemRandom } = signatureGenerate();
      this.uploadHeader.tissuePaper = timestamp;
      this.uploadHeader.sign = signature;
      this.uploadHeader.systemRandom = systemRandom;
      this.init(file);
    },
    // 反面
    beforeBackUpload(file) {
      const { timestamp, signature, systemRandom } = signatureGenerate();
      this.uploadHeader.tissuePaper = timestamp;
      this.uploadHeader.sign = signature;
      this.uploadHeader.systemRandom = systemRandom;
      this.init(file);
    },
    //手持
    beforeAvatarUpload(file) {
      const { timestamp, signature, systemRandom } = signatureGenerate();
      this.uploadHeader.tissuePaper = timestamp;
      this.uploadHeader.sign = signature;
      this.uploadHeader.systemRandom = systemRandom;
      this.init(file);
    },
    init(file) {
      let types = [
        "image/jpeg",
        "image/jpg",
        "image/gif",
        "image/bmp",
        "image/png",
      ];
      const isImage = types.includes(file.type);
      const isJPG = file.type === "image/jpeg";
      const isLt5M = file.size / 1024 / 1024 < 5;

      if (!isImage) {
        ElMessage.error(this.$t("message.user.shangchuan1"));
      }
      if (!isLt5M) {
        ElMessage.error(this.$t("message.user.shangchuan2"));
      }
      return isImage && isLt5M;
    },
    //上传正面
    handelDoucumentsFront(res, file) {
      const { path, httpUrl } = res.data;
      this.oneImg = httpUrl;
      this.idFrontImg = path;
    },
    //上传背面
    handelDoucumentsBack(res, file) {
      const { path, httpUrl } = res.data;
      this.twoImg = httpUrl;
      this.idBackImg = path;
    },
    //手持身份证
    holdIdCardSuccess(res, file) {
      const { path, httpUrl } = res.data;
      this.threeImg = httpUrl;
      this.handheldPhoto = path;
    },
    onErrorUpload() {
      ElMessage.warning(this.$t("message.user.shangchuan3"));
    },
    //获取用户实名认证信息
    getIdentify() {
      Axios.getIdentify({}).then((res) => {
        const {
          status,
          nationality,
          msg,
          name,
          idNumber,
          idimg_1_path,
          idimg_2_path,
          idimg_3_path,
          idBackImg,
          handheldPhoto,
          idFrontImg,
        } = res.data || {};
        this.identifyStatus = status;
        this.disabled = status == 1 || status == 2;
        // if (status == 2 || status == 3) {
        this.nationality = nationality;
        this.name = name;
        this.idNumber = idNumber;
        this.oneImg = idimg_1_path;
        this.twoImg = idimg_2_path;
        this.threeImg = idimg_3_path; //带url的
        // 已经有的
        this.idFrontImg = idFrontImg;
        this.idBackImg = idBackImg;
        this.handheldPhoto = handheldPhoto;
        this.msg = msg;
        // }
      });
    },

    //申请认证
    applyBtn() {
      if (!this.nationality || !this.name || !this.idNumber) {
        ElMessage.warning(this.$t("qsr_wanzhengdexinxi"));
        return;
      }

      if (!this.oneImg || !this.twoImg || !this.threeImg) {
        ElMessage.warning(this.$t("message.user.shangchuan6"));
        return;
      }
      Axios.apply({
        nationality: this.nationality,
        idNumber: this.idNumber,
        idName: "id/passpost",
        name: this.name,
        idFrontImg: this.idFrontImg,
        idBackImg: this.idBackImg,
        handheldPhoto: this.handheldPhoto,
      }).then((res) => {
        if (res.code == "0") {
          this.visible = false;
          // 出来一个弹窗，显示已经提交成功
          this.submitDialogShow = true;
          this.$parent.getIdentifyInfo();
        }
      });
    },
    handleClose() {
      this.visible = false;
    },
  },
};
</script>

<style scoped>
@import url("@/assets/css/my/security.css");

.flex_center {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.state_tip {
  margin-top: 10px;
  font-weight: 600;
}

.image_upload {
  display: flex;
  justify-content: space-between;
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 110px;
  height: 110px;
  line-height: 110px;
  text-align: center;
}

.avatar {
  width: 110px;
  height: 110px;
  display: block;
}

.status_show {
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
