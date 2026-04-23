<template>
  <div class="bg-white">
    <div
      class="relative z-10 w-174 h-46 rounded-md bg-f5 cursor-pointer"
      @click.stop="handleMouseOver"
      @mouseleave.stop="handleMouseOut"
    >
      <div class="flex items-center h-full w-full">
        <img
          class="Subtract"
          src="@/assets/images/c2c/want-buy/Subtract.png"
          alt=""
        />
        <div class="ml-11 font-14" v-if="!active">
          {{ $t("message.c2c.suoyoushoukuanfangshi") }}
        </div>
        <div
          v-if="active.title"
          class="flex items-center h-full pl-11 box-border"
        >
          <!-- <img class="w-14 h-14 mr-6" src="active.img" alt="" /> -->
          <span>{{ active.title }}</span>
        </div>
      </div>
      <transition name="el-fade-in-linear">
        <otc-select
          class="absolute z-10 top-46 left-0 pt-10 pb-10"
          v-show="show"
          :list="options"
          v-model="active"
          @itemSelect="itemData"
          :isIcon="true"
          @mouseleave="handleMouseOut"
        >
          <template #other>
            <otc-input @input="handleInputCb" :inputValue="inputValue">
              <template #left>
                <img
                  class="w-18 h-18 cursor-pointer"
                  src="@/assets/images/c2c/want-buy/Union.png"
                  alt=""
                />
              </template>
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

export default {
  name: "PaymentMethod",
  data() {
    return {
      inputValue: "", //输入框的值
      show: false,
      active: "", //选中的支付方式
      options: [], //支付方式
    };
  },
  props: ["payList"],
  created() {
    this.allList = this.options = this.payList;
  },
  methods: {
    handleMouseOver() {
      this.show = true;
    },
    handleMouseOut() {
      this.show = false;
    },
    itemData(val) {
      this.active = val;
      this.clearData();
      this.handleMouseOut();
      this.$emit("selectPayItem", val);
    },
    // 输入银行卡
    handleInputCb(val) {
      this.inputValue = val;
      if (val) {
        this.options = this.allList.filter((item) => {
          if (item.title.includes(val)) {
            return true;
          }
        });
      } else {
        this.options = this.allList;
      }
    },
    clearData() {
      console.log("清除数据");
      this.inputValue = "";
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
    payList(val) {
      this.allList = val;
      this.options = val;
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

  .otc-input {
    width: 152px;
    height: 42px;
    margin: 0 auto 11px;
    padding-top: 15px;

    input {
      padding-left: 35px;
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
</style>
