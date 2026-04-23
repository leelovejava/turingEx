<template>
  <div>
    <!-- status 1为审核中，2为审核通过，3为审核未通过 -->
    <template v-if="status != 1">
      <el-dialog
        class="my_security"
        :title="$t('message.user.rengongchongzhi')"
        v-model="visible"
        :close-on-click-modal="false"
        @close="handClose"
        width="480px"
        center
      >
        <div class="label">
          {{ $t("message.user.shimingrenzhengtupianshangchuan") }}
        </div>
        <div class="image_upload">
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
            >
              <img v-if="idFrontImg" :src="oneImg" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
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
            >
              <img v-if="idBackImg" :src="twoImg" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
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
            >
              <img v-if="handheldPhoto" :src="threeImg" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <p>{{ $t("message.user.shouchizhengjianzhao") }}</p>
          </div>
        </div>
        <!-- radio选择 -->
        <el-radio-group v-model="radioType" class="radio_group label_top">
          <el-radio :label="0">{{
            $t("message.user.chongzhizijinmima")
          }}</el-radio>
          <el-radio :label="1">{{
            $t("message.user.chongzhigugeyouxiang")
          }}</el-radio>

          <el-radio :label="3">{{
            $t("message.user.chongzhiyouxiang")
          }}</el-radio>
        </el-radio-group>
        <!-- 重置资金密码 -->
        <div v-if="radioType == 0">
          <div class="label label_top">{{ $t("message.user.zijinmima") }}</div>
          <el-input
            type="password"
            v-model="safePassword"
            :placeholder="$t('message.user.qsr_zijinmima6wei')"
            show-password
          ></el-input>

          <div class="label label_top">
            {{ $t("message.user.querenxinmima") }}
          </div>
          <el-input
            type="password"
            v-model="newSafePassword"
            :placeholder="$t('message.user.qingquerenxinmima')"
            show-password
          ></el-input>
        </div>
        <!-- 重置其他 -->
        <div class="label label_top">
          {{ $t("message.user.beizhuxinxi") }}
        </div>
        <el-input
          v-model="note"
          :placeholder="$t('message.user.qsr_beizhuxinxi')"
        ></el-input>

        <div class="bind_btn">
          <el-button type="primary" @click="confirmBtn">{{
            $t("message.user.shenqingrenzheng")
          }}</el-button>
        </div>
      </el-dialog>
    </template>
    <template v-if="status == 1">
      <el-dialog
        class="my_security"
        :title="$t('message.user.rengongchongzhi')"
        v-model="visible"
        :close-on-click-modal="false"
        width="480px"
        center
      >
        <div class="text-center">
          <img
            src="@/assets/myImages/sucess-yellow.png"
            width="100px"
            height="100px"
          />
          <p style="font-size: 20px">
            <b>{{ $t("message.user.tijiaochenggong") }}</b>
          </p>
          <p>
            {{ $t("message.user.chang14") }}
          </p>
        </div>
      </el-dialog>
    </template>
  </div>
</template>

<script>
import Axios2 from "@/api/my.js";
import { Plus } from "@element-plus/icons-vue";
import { signatureGenerate } from "@/utils/signatureUtil";
import { ElMessage, radioButtonProps } from "element-plus";
import { nowUrl } from "@/utils";
export default {
  components: { Plus },
  emits: ["changeModalShow"],
  props: {
    isShowManual: {
      // 弹窗是否展示
      type: Boolean,
      default: false,
    },
    status: {
      type: Number,
      default: null,
    },
    selectType: {
      type: Number,
      default: null,
    },
  },
  data() {
    return {
      nowUrl,
      safePassword: "",
      newSafePassword: "",
      note: "",
      radioType: 0, //默认资金密码
      idnumber: "", //证件号码
      idFrontImg: "", //证件正面
      idBackImg: "", //证件背面
      handheldPhoto: "", //手持身份证
      oneImg: "",
      twoImg: "",
      threeImg: "",
      // status:null, //审核状态
      //
      uploadHeader: {
        tissuePaper: "",
        sign: "",
        systemRandom: "",
      },
    };
  },
  watch: {
    // 如果有传值，则用传值的，没有的话用户可以自由选择
    selectType(newVal) {
      if (newVal) {
        this.radioType = newVal;
      }
    },
  },
  computed: {
    visible: {
      get() {
        return this.isShowManual;
      },
      set(val) {
        this.$emit("changeModalShow", false, "isShowManual");
      },
    },
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
    //人工重置
    confirmBtn() {
      if (this.radioType == 0 && !this.safePassword && !this.newSafePassword) {
        ElMessage.warning(this.$t("message.user.shangchuan4"));
        return;
      }

      if (this.radioType != 0 && !this.note) {
        ElMessage.warning(this.$t("message.user.shangchuan5"));
        return;
      }

      if (!this.idFrontImg && !this.idBackImg && !this.handheldPhoto) {
        ElMessage.warning(this.$t("message.user.shangchuan6"));
        return;
      }

      if (this.newSafePassword != this.safePassword) {
        ElMessage.warning(this.$t("message.user.liangcimimabuyizhi"));
        return;
      }
      Axios2.setSafewordApply({
        idcard_path_front: this.idFrontImg,
        idcard_path_back: this.idBackImg,
        idcard_path_hold: this.handheldPhoto,
        operate: this.radioType,
        safeword: this.safePassword,
        safeword_confirm: this.newSafePassword,
        remark: this.note,
      }).then((res) => {
        if (res.code == "0") {
          ElMessage.success(this.$t("message.user.tijiaochenggong"));
          this.$parent.getManualInfo();
          this.handClose();
        }
      });
    },
    handClose() {
      this.visible = false;
      this.safePassword = "";
      this.newSafePassword = "";
      this.note = "";
    },
  },
};
</script>

<style>
@import url("@/assets/css/my/security.css");
.text-center {
  text-align: center;
}
.radio_group {
  display: block;
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
</style>
