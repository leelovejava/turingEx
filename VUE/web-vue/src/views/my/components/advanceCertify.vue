<!-- 高级认证 -->
<template>
  <div>
    <!-- 状态为1时：已提交申请，未审核， 0:未认证，3:认证失败， 2:认证成功
            未认证或已认证时显示 -->
    <template v-if="identifyStatus != 1">
      <el-dialog
        class="my_security"
        :title="$t('message.user.gaojirenzheng')"
        v-model="visible"
        :close-on-click-modal="false"
        @close="handClose"
        width="480px"
        center
      >
        <!-- 已认证 -->
        <div v-if="identifyStatus == 2" class="status_show">
          <img
            src="@/assets/myImages/icon-image/account-security/success-green.png"
            width="50"
            height="50"
          />
          <div class="state_tip">{{ $t("message.user.yirenzheng") }}</div>
        </div>
        <!-- 未认证 -->
        <div>
          <!-- 真实姓名带入 -->
          <div class="label">{{ $t("message.user.gongzuodizhi") }}</div>
          <el-input
            v-model="work_place"
            :disabled="disabled"
            :placeholder="$t('message.user.qsr_nindegongzuodizhi')"
          ></el-input>

          <div class="label label_top">
            {{ $t("message.user.jiatingdizhi") }}
          </div>
          <el-input
            v-model="home_place"
            :disabled="disabled"
            :placeholder="$t('message.user.qsr_nindejiatingdizhi')"
          ></el-input>

          <div class="label label_top">
            {{ $t("message.user.yubenrenguanxi") }}
          </div>
          <el-input
            v-model="relatives_relation"
            :disabled="disabled"
            :placeholder="$t('message.user.yubenrenguanxibitian')"
          ></el-input>

          <div class="label label_top">
            {{ $t("message.user.qinshuxingming") }}
          </div>
          <el-input
            v-model="relatives_name"
            :disabled="disabled"
            :placeholder="$t('message.user.qsr_qinshuxingmingbitian')"
          ></el-input>

          <div class="label label_top">
            {{ $t("message.user.qingshudizhi") }}
          </div>
          <el-input
            v-model="relatives_place"
            :disabled="disabled"
            :placeholder="$t('message.user.qsr_qinshudizhibitian')"
          ></el-input>

          <div class="label label_top">
            {{ $t("message.user.qinshudianhua") }}
          </div>
          <el-input
            v-model="relatives_phone"
            :disabled="disabled"
            :placeholder="$t('message.user.qsr_qinshudianhuabitian')"
          ></el-input>

          <div class="bind_btn">
            <el-button
              type="primary"
              @click="applyBtn"
              v-if="identifyStatus != 2"
              >{{ $t("message.user.shenqingrenzheng") }}</el-button
            >
          </div>
        </div>
      </el-dialog>
    </template>
    <template v-if="identifyStatus == 1">
      <el-dialog
        class="my_security"
        :title="$t('message.user.gaojirenzheng')"
        v-model="visible"
        :close-on-click-modal="false"
        width="480px"
        center
      >
        <div class="text-center">
          <img
            src="@/assets/myImages/icon-image/account-security/success-green.png"
            width="50px"
            height="50px"
          />
          <p style="font-size: 20px">
            <b>{{ $t("message.user.tijiaochenggong") }}</b>
          </p>
          <p>
            {{ $t("message.user.chang14") }}
          </p>
        </div>
        <div class="bind_btn">
          <el-button type="primary" @click="handClose">{{
            $t("message.user.queren")
          }}</el-button>
        </div>
      </el-dialog>
    </template>
  </div>
</template>
<script>
import Axios from "@/api/my.js";
export default {
  emits: ["changeModalShow"],
  props: {
    isShowAdvance: {
      // 弹窗是否展示
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      work_place: "",
      home_place: "",
      relatives_relation: "",
      relatives_name: "",
      relatives_place: "",
      relatives_phone: "",
      identifyStatus: "",
      disabled: false,
    };
  },
  computed: {
    visible: {
      get() {
        return this.isShowAdvance;
      },
      set(val) {
        this.$emit("changeModalShow", false, "isShowAdvance");
      },
    },
  },
  mounted() {
    // if (this.$store.state.token) {
    this.getIdentify();
    // }
  },
  methods: {
    //获取高级认证信息
    getIdentify() {
      Axios.getHighIdentify().then((res) => {
        const {
          status,
          workPlace,
          homePlace,
          relativesRelation,
          relativesName,
          relativesPlace,
          relativesPhone,
        } = res.data || {};
        this.disabled = status == 2;
        this.identifyStatus = status;

        this.work_place = workPlace;
        this.home_place = homePlace;
        this.relatives_relation = relativesRelation;
        this.relatives_name = relativesName;
        this.relatives_place = relativesPlace;
        this.relatives_phone = relativesPhone;
      });
    },
    //申请认证
    applyBtn() {
      if (
        !this.work_place ||
        !this.home_place ||
        !this.relatives_relation ||
        !this.relatives_name ||
        !this.relatives_place ||
        !this.relatives_phone
      ) {
        ElMessage.warning(this.$t("qsr_wanzhengdexinxi"));
        return;
      }

      Axios.applyHigh({
        work_place: this.work_place,
        home_place: this.home_place,
        relatives_relation: this.relatives_relation,
        relatives_name: this.relatives_name,
        relatives_place: this.relatives_place,
        relatives_phone: this.relatives_phone,
      }).then((res) => {
        if (res.code == "0") {
          ElMessage.success(this.$t("message.user.tijiaochenggong"));
          this.handClose();
          this.getIdentify();
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
@import url("@/assets/css/my/security.css");
.status_show {
  display: flex;
  flex-direction: column;
  align-items: center;
}
</style>
