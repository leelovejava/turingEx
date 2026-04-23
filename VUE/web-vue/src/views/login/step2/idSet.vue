<!-- 实名验证 -->
<template>
  <div>
    <div style="margin-top: 50px">
      <Steps :stepIndex="2" />
      <main class="main css-6q12pm">
        <div class="css-108ucgm">
          <div class="binance-row css-16kn2us">
            <div class="binance-col css-1wz0uwi" style="margin-left: -12px">
              <div class="css-cz4mv2">
                <div class="css-6x3lo1">
                  <div class="css-vurnku">
                    <div class="css-ojbpe7">
                      {{ $t("message.user.shimingrenzheng") }}
                    </div>

                    <div class="css-vurnku">
                      <!-- 信息编写 -->
                      <div>
                        <!-- 国籍选择 -->
                        <div class="label">{{ $t("message.user.guoji") }}</div>

                        <vue3-country-intl
                          v-model="nationality"
                          :disabled="identifyStatus == 1"
                          :showAreaCode="false"
                          type="country"
                          :useChinese="isUseChinese"
                          :placeholder="$t('message.user.qingxuanzeguoji')"
                          :noDataText="$t('message.user.nationNodata')"
                        />

                        <!-- 姓名 -->
                        <div class="label label_top">
                          {{ $t("message.user.xingming") }}
                        </div>
                        <el-input
                          :disabled="identifyStatus == 1"
                          v-model="name"
                          :placeholder="$t('message.user.qsr_xingming')"
                        ></el-input>
                        <!-- 身份证号码 -->
                        <div class="label label_top">
                          {{ $t("message.user.zhengjianhao") }}
                        </div>
                        <el-input
                          v-model="idNumber"
                          :disabled="identifyStatus == 1"
                          :placeholder="$t('message.user.qsr_zengjianhaoma')"
                        ></el-input>
                        <!-- 图片上传 -->
                        <div class="label label_top">
                          {{
                            $t("message.user.shimingrenzhengtupianshangchuan")
                          }}
                        </div>
                        <div class="image_upload">
                          <div class="text-center">
                            <el-upload
                              class="avatar-uploader"
                              :action="`${nowUrl}/api/api/uploadFile`"
                              :headers="uploadHeader"
                              accept=".jpg,.jpeg,.png,.gif.JPG,.JPEG,.PNG,.GIF"
                              :show-file-list="false"
                              :on-success="handelDoucumentsFront"
                              :on-error="onErrorUpload"
                              :before-upload="beforeFrontUpload"
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
                              :action="`${nowUrl}/api/api/uploadFile`"
                              :headers="uploadHeader"
                              accept=".jpg,.jpeg,.png,.gif.JPG,.JPEG,.PNG,.GIF"
                              :show-file-list="false"
                              :on-success="handelDoucumentsBack"
                              :on-error="onErrorUpload"
                              :before-upload="beforeBackUpload"
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
                              :action="`${nowUrl}/api/api/uploadFile`"
                              :headers="uploadHeader"
                              accept=".jpg,.jpeg,.png,.gif.JPG,.JPEG,.PNG,.GIF"
                              :show-file-list="false"
                              :on-success="holdIdCardSuccess"
                              :on-error="onErrorUpload"
                              :before-upload="beforeAvatarUpload"
                            >
                              <img
                                v-if="threeImg"
                                :src="threeImg"
                                class="avatar"
                              />
                              <el-icon v-else class="avatar-uploader-icon">
                                <Plus />
                              </el-icon>
                            </el-upload>
                            <p>{{ $t("message.user.shouchizhengjianzhao") }}</p>
                          </div>
                        </div>
                        <!-- 拍摄示例 -->
                        <div class="label label_top">
                          {{ $t("paisheshili") }}
                        </div>
                        <img
                          style="width: 100%"
                          src="@/assets/images/login/shili.png"
                        />
                      </div>
                      <!-- 绑定和跳过 -->
                      <button
                        data-bn-type="button"
                        @click="applyBtn"
                        class="css-1bsmpdm"
                      >
                        {{ $t("message.user.bangding") }}
                      </button>
                      <div
                        @click="jumpOver"
                        style="
                          text-align: center;
                          padding: 20px 0;
                          color: #868d9a;
                          cursor: pointer;
                        "
                      >
                        {{ $t("tiaoguo") }}
                      </div>
                    </div>
                    <div class="css-vurnku"></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>
<script>
import { signatureGenerate } from "@/utils/signatureUtil";
import { Plus } from "@element-plus/icons-vue";
import Steps from "../components/steps.vue";
import Axios from "@/api/my.js";
import { nowUrl } from "@/utils";
export default {
  name: "idSet",
  components: {
    Steps,
    Plus,
  },
  data() {
    return {
      nowUrl,
      msg: "", //错误提示
      identifyStatus: "", //认证状态
      nationality: "", //国籍
      name: "", //姓名
      idNumber: "", //证件号码

      // 提交给后端的图片data
      idFrontImg: "", //证件正面
      idBackImg: "", //证件背面
      handheldPhoto: "", //手持身份证
      //上传之后后展示的图片httpurl
      oneImg: "",
      twoImg: "",
      threeImg: "",
      //
      uploadHeader: {
        tissuePaper: "",
        sign: "",
        systemRandom: "",
      },
    };
  },
  mounted() {
    let interimToken = localStorage.getItem("spToken");
    if (interimToken) {
      this.getIdentify();
    }
  },
  computed: {},
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
      console.log("图片大小", file.size / 1024 / 1024);
      if (!isImage) {
        this.$message.error(this.$t("message.user.shangchuan1"));
      }
      if (!isLt5M) {
        this.$message.error(this.$t("message.user.shangchuan2"));
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
      this.$message.warning(this.$t("message.user.shangchuan3"));
    },
    jumpOver() {
      this.$router.push("/secureBind"); //安全绑定
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
        } = res.data || {};
        this.identifyStatus = status;
        // if (status == 2 || status == 3) {
        this.nationality = nationality;
        this.name = name;
        this.idNumber = idNumber;
        this.oneImg = idimg_1_path;
        this.twoImg = idimg_2_path;
        this.threeImg = idimg_3_path;
        this.msg = msg;
        // }
      });
    },

    //申请认证
    applyBtn() {
      if (!this.nationality || !this.name || !this.idNumber) {
        this.$message.warning(this.$t("qsr_wanzhengdexinxi"));
        return;
      }

      if (!this.idFrontImg || !this.idBackImg || !this.handheldPhoto) {
        this.$message.warning(this.$t("message.user.shangchuan6"));
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
          this.$message.success(this.$t("message.user.bangdingchenggong"));
          this.$router.push("/secureBind"); //安全绑定
        }
      });
    },
    handClose() {
      this.visible = false;
    },
  },
};
</script>
<style scoped>
@import url("@/assets/css/login/register.css");
@import url("@/assets/css/my/security.css");
.el-input {
  height: 52px;
}

.el-input--suffix >>> .el-input__inner {
  padding-right: 30px;
  outline: none;
  border: none;
}

.flex_center {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.state_tip {
  margin-top: 10px;
  font-weight: 600;
}

/* 图片上传 */
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
