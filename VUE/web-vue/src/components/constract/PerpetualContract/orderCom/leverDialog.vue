<template>
  <div>
    <el-dialog
      :title="$t('message.home.tiaozhengganggan')"
      class="dialog-box"
      v-model="isShow"
      :append-to-body="true"
      width="450px"
      :before-close="handleClose"
    >
      <div class="body-wrapper">
        <!-- 币种 -->
        <div class="coin-info">
          <span class="name"
            >{{ symbol.toUpperCase() }}{{ $t("message.home.yongxu") }}</span
          ><span class="margin-mode">{{ $t("message.home.quancang") }}</span>
        </div>
        <!-- 杠杆 -->
        <div>
          <div>{{ $t("message.home.gangganbeishu") }}</div>
          <el-input-number
            class="input-lever"
            v-model="inputLever"
            :min="0"
            :max="getMaxLever"
            @change="inputChange"
            @input="inputNumber"
          />
          <div class="slider">
            <div
              class="slider-item"
              :class="getActive(item)"
              :style="{ width: `${100 / lever_rate.length}%` }"
              v-for="(item, index) in lever_rate"
              :key="index"
            >
              <div class="step">
                <div class="step-line"></div>
                <div class="step-round" @click="sliderChangeRate(item)"></div>
              </div>
              <div class="text">{{ item }}</div>
            </div>
          </div>
        </div>
        <!-- 信息 -->
        <div class="submit-info">
          <!-- 最大可开 -->
          <div class="submit-info-item">
            <div>{{ $t("message.home.kekaizhangshu") }}:</div>

            <div>
              {{ getMaxAmountByLever(sessionObj?.volume)
              }}{{ $t("message.home.zhang") }}
            </div>
          </div>
          <!-- 保证金 -->
          <div class="submit-info-item">
            <div>{{ $t("message.home.baozhengjin") }}:</div>
            <div>1000.00 {{ unit }}</div>
          </div>
        </div>

        <!-- 按钮 -->
        <div class="btns">
          <el-button v-click="handleSubmit" class="btn-submit">
            {{ $t("message.home.queren") }}
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { mapState, mapActions, mapStores } from "pinia";
import { useUserStore } from "@/store/user";
export default {
  name: "leverDialog",
  props: {
    current_lever: {
      type: [Number,String],
    }, //当前的杠杆
    symbol: {
      type: String,
      default: "--",
    },
    lever_rate: {
      type: Array,
      default: function () {
        return ["1"];
      }, // 支持的杠杆 ['1X','2X',...]
    },
    sessionObj: {
      type: Object,
      default: function () {
        return {};
      },
    },
    unit: {
      type: String,
    },
  },
  data() {
    return {
      isShow: false,
      inputLever: this.current_lever*1,
      sliderLever: this.current_lever,
      maxLever: 1,
      marks: (val) => val % lever_rate.length === 0, //marks的取值是后端来定的，表示分成多少份
    };
  },
  watch: {
    // 默认值为传入的current_lever
    current_lever(val) {
      this.inputLever = val*1;
      this.sliderLever = val;
    },
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
    getMaxLever: function () {
      let len = this.lever_rate.length;
      if (len) {
        const maxValue = Math.max(...this.lever_rate);
        return Math.max(maxValue, 1);
      }
      return 1;
    },
  },
  methods: {
    getActive(val) {
      return this.sliderLever == val ? "active-step" : "";
    },
    getMaxAmountByLever(val) {
      if (!val) {
        return `-- ${unit}`;
      } else {
        return parseInt(val / 1);
        // if (this.lever_rate.length > 0) {
        //   return parseInt(val / this.inputLever);
        // } else if (this.lever_rate.length == 0) {
        //   return parseInt(val / 1);
        // }
      }
    },
    handleClose() {
      this.isShow = false;
      this.inputLever = this.lever_rate[0]*1;
      this.sliderLever = this.lever_rate[0];
    },
    open() {
      this.isShow = true;
    },
    // input
    inputChange(value) {
      this.inputLever = value*1;
      this.sliderLever = value;
    },
    inputNumber(){},
    // 进度条设置
    sliderChangeRate(value) {
      this.inputLever = Number(value);
      this.sliderLever = Number(value);
    },
    handleSubmit() {
      this.$parent.changeLever(this.inputLever);
      this.handleClose();
    },
  },
};
</script>
<style lang="scss">
.dialog-box {
  background: #1f2328 !important;
  .el-dialog__title {
    color: #fff;
  }
  .el-dialog__header {
    padding: 22px 10px;
  }
  .el-input-number .el-input__wrapper {
    background: #2c3138;
  }
  .el-button:hover {
    background: #4373df;
  }
  // 输入框
  .input-lever {
    width: 100%;
  }

  .el-input__wrapper {
    box-shadow: none;
    .el-input__inner {
      background-color: #2c3138;
      border: none;
    }
  }

  .el-input-number__decrease,
  .el-input-number__increase {
    background-color: #2c3138;
    color: #868e9b;
    font-size: 20px;
    font-weight: 600;
  }

  .el-input-number__decrease {
    border-right: none;
  }
  .el-input-number__increase {
    border-left: none;
  }
}
</style>
<style lang="scss" scoped>
:deep(.el-overlay-dialog) {
  background: rgba(0, 0, 0, 0.6);
}

.coin-info {
  color: #fff;
}
.submit-info {
}
.submit-info-item {
  color: #707a8a;
  height: 32px;
  line-height: 32px;
  display: flex;
  justify-content: space-between;
}
.margin-mode {
  margin: 0 8px;
  display: inline-block;
  padding: 4px;
  background: #392525;
  color: #e05561;
}
.btns {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}
.btn-submit {
  width: 400px;
  background: #4373df;
  color: #fff;
}

.slider {
  display: flex;
  width: 100%;
  margin-left: 4px;
}
.slider-item:last-child .step .step-line {
  width: 0px;
}

.step {
  position: relative;
  width: 100%;
  color: #404040;
  border-color: #404040;
  margin-top: 10px;
  margin-bottom: 4px;
  cursor: pointer;
}
.active-step .step-round {
  box-shadow: 0 0 0 3px #3498db;
  background-color: #3498db;
}

.step-line {
  position: absolute;
  background-color: #404040;
  border-color: inherit;
  height: 2px;
  top: 5px;
  left: 0px;
  right: 0px;
}

.step-round {
  position: relative;
  z-index: 1;
  border-radius: 50%;
  width: 12px;
  height: 12px;
  background: #484d56;
}

.text-wrapper {
  height: 30px;
  line-height: 30px;
}
/* 1x 2x 的样式 */
.text-wrapper span.text {
  display: inline-block;
}
</style>
