<template>
  <div class="relative z-10 bg-white">
    <div
      class="relative z-10 w-120 h-46 rounded-md bg-f5 cursor-pointer"
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
          <!-- 币种的图片 -->
          <div class="currencySymbol" v-if="active.currencySymbol">
            {{ active.currencySymbol.substring(0, 2) }}
          </div>
          <span>{{ active.title }}</span>
        </div>
      </div>
      <transition name="el-fade-in-linear">
        <otc-select
          :isTwo="false"
          :isbg="isbg"
          class="absolute top-46 left-0 z-10"
          v-show="show"
          :list="options"
          v-model="active"
          @mouseleave.native="handleMouseOut"
          @itemSelect="handleSelect"
        >
          <template #other>
            <otc-input @input="handleInputCb" :inputValue="value">
              <template #right>
                <img
                  class="w-15 h-15 cursor-pointer"
                  @click="clearData"
                  src="@/assets/images/c2c/want-buy/Group1384.png"
                  alt=""
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
import OtcInput from "@/views/c2c/components/OtcInput.vue";
import OtcSelect from "./OtcSelect.vue";
import { mapState, mapActions } from "pinia";
import Axios from "@/api/c2c.js";
import quotesAxios from "@/api/quotes.js";
export default {
  name: "fiatCurrency",
  props: {
    currencyType: {
      default: 1,
      type: Number,
    },
    isbg: {
      default: false,
      type: Boolean,
    },
    isTwo: {
      default: true,
      type: Boolean,
    },
  },
  data() {
    return {
      value: "",
      show: false,
      active: {}, //选中的币种
      options: [], //列表数据
      allList: [],
    };
  },
  created() {
    if (this.currencyType == 2) {
      this.allList = [];
      this.options = [];
      this.getSymbolList();
    }
  },
  // computed: {
  //   ...mapState({
  //     exchangeRateDataList: (state) => state.exchangeRateData,
  //   }),
  // },
  methods: {
    // ...mapActions(["exchangerateData"]),
    // 获取币种的汇率信息
    getSymbolList() {
      quotesAxios.getExchangerateuserconfig().then((res) => {
        if (res.code == "0") {
          res.data.map((item) => {
            let obj = {
              title: item.currency,
              uuid: item.uuid,
              rata: item.rata,
              currencySymbol: item.currencySymbol,
            };
            this.allList.push(obj);
            this.options.push(obj);
          });

          this.active =
            this.options.filter((it) => it.title?.toUpperCase() === "USD")[0] ||
            this.options[0]; //默认选中USD
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
    z-index: 11;
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
  transform: translateY(-50%);
  width: 10px;
  height: 5px;
}

.currencySymbol {
  width: 22px;
  height: 22px;
  background: #d35069;
  font-size: 12px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  margin-right: 10px;
}
</style>
