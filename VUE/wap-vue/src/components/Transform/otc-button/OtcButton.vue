<template>
  <div>
    <van-button
        v-show="disabled"
        class="w-full text-white"
        :color="color"
        type="info"
        @click="btnClick"
    >{{ $t(text) }}
    </van-button>
    <van-button
        v-show="!disabled"
        disabled
        :color="disableColor"
        class="w-full"
        style="color: #C0C4CC"
    >{{ $t(text) }}
    </van-button>
  </div>
</template>

<script>
import {
  Button
} from "vant";

export default {
  name: "OtcButton",
  props: {
    disabled: {
      require: true,
    },
    text: {

    },
    color: {
      default: '#1D91FF'
    },
    disableColor: {
      default: '#EAEBEE'
    }
  },
  methods: {
    btnClick() {
      this.$emit('btnClick');
    }
  },
  components: {
    [Button.name]:Button,
  }
}
</script>

<style scoped>

</style>