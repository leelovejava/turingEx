<template>
  <div class="w-full h-full otc-input">
    <div class="relative w-full h-full">
      <div class="absolute left flex items-center">
        <slot name="left"></slot>
      </div>
      <slot>
        <input
          class="w-full h-full pl-17 font-14 box-border"
          @focus="focus"
          @blur="blur"
          type="text"
          :placeholder="placeholder"
          @input="changeVal"
          :value="inputValue"
        />
      </slot>
      <div class="absolute right flex items-center">
        <slot name="right"></slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "OtcInput",
  props: ["placeholder", "inputValue", "isNumber"],
  data() {
    return {
      showValue: "",
    };
  },
  watch: {
    inputValue(val) {
      console.log("变化");
      this.showValue = val;
    },
  },
  methods: {
    focus(e) {
      e.target.classList.add("focus");
      this.$emit("focus");
    },
    blur(e) {
      e.target.classList.remove("focus");
      this.$emit("blur");
    },
    changeVal(e) {
      e.stopPropagation();
      if (this.isNumber) {
        e.target.value = e.target.value.replace(/[^\.\d]/g, "");
      }
      this.$emit("input", e.target.value);
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@/assets/css/c2c/init.scss";
input {
  outline: none;
  border: 1px solid #f5f5f5;
  border-radius: 5px;

  &::placeholder {
    color: #babfc6;
  }
}

.focus {
  border: 1px solid $blue;
}

.left,
.right {
  top: 50%;
  transform: translateY(-50%);
}

.left {
  left: 13px;
}

.right {
  right: 15px;
}
</style>
