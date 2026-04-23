<template>
  <div class="buy-input relative">
    <img class="search-icon absolute left-6" src="../../../../../assets/image/payment/search.png" alt="" />
    <input class="w-full box-border pl-20" :readonly="readonly" type="text" :value="value" :placeholder="placeholder"
      @input="changeVal" @focus="focus" />
  </div>
</template>

<script>
export default {
  name: 'BuyInput',
  props: ['value', 'placeholder', 'isReadonly'],
  data() {
    return {
      readonly: false
    }
  },
  mounted() {
    if (this.isReadonly) {
      this.readonly = this.isReadonly
    } else {
      this.readonly = false
    }
  },
  methods: {
    focus(e) {
      this.$emit('focus', e.target.value)
    },
    changeVal(e) {
      this.$emit('input', e.target.value)
    },
  },
}
</script>

<style lang="scss" scoped>
input {
  border: none;
  outline: none;
}

.buy-input {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  font-size: 30px;

  img {
    top: 50%;
    transform: translateY(-50%);
  }

  input {
    width: 100%;
    height: 100%;
    border-radius: 50px;
    height: 80px;
    background: $input_background;
    color: $text_color;
  }
}

.search-icon {
  width: 40px;
  height: 40px;
}
</style>
