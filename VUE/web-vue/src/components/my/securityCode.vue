<template>
  <!-- 数字输入框组件 -->
  <div class="code-num-box">
    <div class="code-input-main">
      <input
        v-for="(c, index) in ct"
        class="code-input-main-item"
        :key="index"
        type="number"
        v-model="ct[index]"
        ref="input"
        @input="
          (e) => {
            onInput(e.target.value, index);
          }
        "
        @keydown.delete="
          (e) => {
            onKeydown(e.target.value, index);
          }
        "
        @focus="onFocus"
        :disabled="loading"
      />
    </div>
    <div class="clean">
      <span @click.stop="onClean" class="mouse-cursor">{{
        $t("message.user.qingchu")
      }}</span>
    </div>
  </div>
</template>

<script>
export default {
  name: "VueVercode",
  emits: ["getGoogleCode"],
  data() {
    return {
      ct: ["", "", "", "", "", ""],
      seat: 0,
      loading:false
    };
  },
  mounted() {
    this.resetCaret();
  },
  computed: {
    ctSize() {
      return this.ct.length;
    },
    cIndex() {
      let i = this.ct.findIndex((item) => item === "");
      i = (i + this.ctSize) % this.ctSize;
      return i;
    },
    lastCode() {
      return this.ct[this.ctSize - 1];
    },
  },

  watch: {
    cIndex() {
      this.resetCaret();
    },
    lastCode(val) {
      if (val) {
        this.$refs.input[this.ctSize - 1].blur();
        this.$emit("getGoogleCode", this.ct);
      }
    },
  },

  methods: {
    onInput(val, index) {
      this.msg = "";
      val = val.replace(/\s/g, "");
      if (index == this.ctSize - 1) {
        this.ct[this.ctSize - 1] = val[0]; // 最后一个码，只允许输入一个字符。
      } else if (val.length > 1) {
        let i = index;
        for (i = index; i < this.ctSize && i - index < val.length; i++) {
          this.ct[i] = val[i];
        }
        this.resetCaret();
      }
    },
    // 重置光标位置。
    resetCaret() {
      this.$refs.input[this.ctSize - 1].focus();
    },
    onClean() {
      //清除
      this.ct = ["", "", "", "", "", ""];
      this.seat = 0;
    },
    onFocus() {
      // 监听 focus 事件，将光标重定位到“第一个空白符的位置”。
      let index = this.ct.findIndex((item) => item === "");
      index = (index + this.ctSize) % this.ctSize;
      this.$refs.input[index].focus();
    },
    onKeydown(val, index) {
      if (val === "") {
        // 删除上一个input里的值，并对其focus。
        if (index > 0) {
          this.ct[index - 1] = "";
          this.$refs.input[index - 1].focus();
        }
      }
    },
  },
};
</script>
<style scoped>
.code-num-box {
  display: flex;
}
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
  -webkit-appearance: none !important;
  margin: 0;
}

.code-input-main {
  width: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: row;
  justify-content: space-around;
  position: relative;
  padding: 20px 0 0;
}

.code-input-input {
  height: 45px;
  width: 100%;
  position: absolute;
  border: none;
  outline: none;
  color: transparent;
  background-color: transparent;
  text-shadow: 0 0 0 transparent;
  caret-color: transparent;
  opacity: 0;
}

.code-input-main-item {
  width: 45px;
  height: 45px;
  line-height: 45px;
  margin-right: 6px;
  padding: 0;
  opacity: 0.8;
  border: solid #ccc 1px;
  text-align: center;
  font-size: 14px;
  color: #323232;
  border-radius: 3px;
}

.clean {
  width: 100%;
  box-sizing: border-box;
  text-align: right;
  color: #0e88ef;
  font-size: 12px;
  padding: 0 10px;
  font-size: 16px;
  position: relative;
  top: 30px;
}
</style>
