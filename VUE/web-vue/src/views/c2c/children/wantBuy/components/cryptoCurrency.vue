<template>
  <div class="relative bg-white">
    <div
      class="relative w-120 h-46 rounded-md bg-f5 cursor-pointer"
      @click="handleMouseOver"
      @mouseleave="handleMouseOut"
    >
      <div class="flex items-center h-full w-full">
        <img
          class="Subtract"
          src="@/assets/images/c2c/want-buy/Subtract.png"
          alt=""
        />
        <div class="flex items-center h-full pl-11 box-border">
          <img
            :src="`${
              ConfigURL.HOST_URL
            }/symbol/${active.title?.toLowerCase()}.png`"
            width="22"
            class="mr-8"
          />
          <span>{{ active.title }}</span>
        </div>
      </div>
      <transition name="el-fade-in-linear">
        <otc-select
          :isbg="isbg"
          :isTwo="true"
          class="absolute top-46 left-0"
          v-show="show"
          :list="options"
          v-model="active"
          @mouseleave="handleMouseOut"
          @itemSelect="handleSelect"
        >
          <template #other>
            <otc-input @input="handleInputCb" :inputValue="value">
              <template #right>
                <img
                  class="w-15 h-15 cursor-pointer"
                  @click="clearData"
                  src="@/assets/images/c2c/want-buy/Group1384.png"
                />
              </template>
            </otc-input>
          </template>
        </otc-select>
      </transition>
    </div>
  </div>
</template>

<script>
import ConfigURL from "@/config/index";
import OtcInput from "@/views/c2c/components/OtcInput.vue";
import OtcSelect from "./OtcSelect.vue";
import { mapState } from "pinia";
import Axios from "@/api/c2c.js";
export default {
  name: "cryptoCurrency",
  props: {
    currencyType: {
      default: 1,
      type: Number,
    },
    isbg: {
      default: false,
      type: Boolean,
    },
  },
  data() {
    return {
      ConfigURL,
      value: "",
      show: false,
      active: {},
      options: [],
      allList: [],
    };
  },
  created() {
    this.getSymbolList();
  },
  // computed: {
  //   ...mapState({
  //     exchangeRateData: (state) => state.exchangeRateData,
  //   }),
  // },
  methods: {
    getSymbolList() {
      Axios.c2cSymbolList().then((res) => {
        if (res.code == "0") {
          for (const key in res.data) {
            let obj = {
              title: res.data[key],
              currency_symbol: key,
            };
            this.allList.push(obj);
            this.options.push(obj);
          }
          this.active = this.options[0]; //默认选中第一个，btc
          this.$emit("selectItem", this.active);
        }
      });
    },
    handleMouseOver() {
      this.show = true;
    },
    handleMouseOut() {
      this.show = false;
    },
    handleSelect(val) {
      this.active = val;
      this.show = false;
      this.clearData();
      this.handleMouseOut();
      this.$emit("selectItem", val);
    },
    handleInputCb(val) {
      this.value = val;
      if (val) {
        this.options = this.options.filter((item) => {
          return item.title.indexOf(val) != -1;
        });
      } else {
        this.options = this.allList;
      }
    },
    clearData() {
      this.value = "";
      this.options = this.allList;
    },
  },
  watch: {
    active: {
      immediate: true,
      handler() {
        this.show = false;
      },
    },
  },
  components: {
    OtcInput,
    OtcSelect,
  },
};
</script>

<style lang="scss" scoped>
@import "@/assets/css/c2c/init.scss";
:deep {
  .otc-select-wrapper {
    padding-bottom: 6px;

    .item {
      height: 36px;
    }
  }

  .otc-select {
    z-index: 10;
  }

  .otc-input {
    width: 98px;
    height: 42px;
    margin: 0 auto 11px;
    padding-top: 15px;

    input {
      padding-left: 6px;
    }
  }
}

.Subtract {
  position: absolute;
  top: 50%;
  right: 12px;
  z-index: 100;
  transform: translateY(-50%);
  width: 10px;
  height: 5px;
}
</style>
