<template>
  <div class="w-full otc-select" :class="[isTwo ? 'zindex11' : 'zindex10']">
    <div class="otc-select-wrapper">
      <slot>
        <slot name="other"></slot>
        <!-- :class="{ active: value.title === item.title }" -->
        <div
          class="cursor-pointer item flex items-center"
          v-for="(item, index) in list"
          :key="index"
          @click="handleItemClick(item)"
        >
          <!-- isbg 表示需要币种的icon -->
          <div v-if="isbg" class="flex items-center">
            <img
              v-if="isTwo"
              class="w-22 h-22 mx-10"
              :src="`${
                ConfigURL.HOST_URL
              }/symbol/${item.title?.toLowerCase()}.png`"
              alt=""
            />
            <div v-if="!isTwo" class="currency_symbol">
              <span v-if="item.currencySymbol">{{
                item.currencySymbol.substring(0, 2)
              }}</span>
            </div>
          </div>
          <!-- 一般走这里 -->
          <div class="flex items-center" :class="[!isbg ? 'ml-10' : '']">
            <img
              v-if="isIcon"
              style="height: 20px"
              src="@/assets/images/c2c/want-buy/Group2060(1).png"
            />
            {{ item.title }}
          </div>
        </div>
      </slot>
    </div>
  </div>
</template>

<script>
import ConfigURL from "@/config/index";

export default {
  name: "OtcSelect",
  props: ["list", "value", "isTwo", "isbg", "isIcon"],
  emits: ["itemSelect"],
  data() {
    return {
      ConfigURL,
    };
  },

  methods: {
    handleItemClick(data) {
      this.$emit("itemSelect", data);
    },
  },
};
</script>

<style lang="scss" scoped>
@import "@/assets/css/c2c/init.scss";

.otc-select {
  box-shadow: 0px 0px 4px rgba(0, 0, 0, 0.2);
  border-radius: 5px;
  background: #fff;

  .item {
    &:hover {
      background: #f5f5f5;
    }
  }
}

.zindex10 {
  z-index: 10;
}

.zindex11 {
  z-index: 11;
}
.currency_symbol {
  width: 22px;
  height: 22px;
  background: #d35069;
  font-size: 12px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  margin: 0 10px;
}
</style>
