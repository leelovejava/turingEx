<template>
  <div id="c2c-order">
    <otc-order-header />
    <!-- 头部tab-->
    <div class="w-full h-12 box-shadow bg-white">
      <div class="main-div h-full">
        <div
          class="items-center flex h-full lh-60 font-500 font-16 cursor-pointer color-727"
        >
          <!-- 进行中 -->
          <div
            class="mr-8"
            :class="{ active: active === 0 }"
            @click="selectIndex(0)"
          >
            {{ $t("message.c2c.jinxingzhong") }}
          </div>
          <!-- 全部订单-->
          <div :class="{ active: active === 1 }" @click="selectIndex(1)">
            {{ $t("message.c2c.quanbudingdan") }}
          </div>
        </div>
      </div>
    </div>
    <!--  全部订单的筛选框 -->
    <div v-show="active === 1" class="flex items-center main-div bg-white">
      <!-- 所有币种 -->
      <div class="mr-8">
        <p class="mb-4 font-12 font-500 color-727">
          {{ $t("message.c2c.bizhong") }}
        </p>
        <el-select v-model="currency" :placeholder="$t('message.c2c.quanbu')">
          <el-option
            v-for="(item, index) in currencyOptions"
            :key="index"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </div>
      <!-- 订单类型 -->
      <div class="mr-8">
        <p class="mb-4 font-12 font-500 color-727">
          {{ $t("message.c2c.dingdanleixing") }}
        </p>
        <el-select
          v-model="orderType"
          :placeholder="$t('message.c2c.qingxuanze')"
        >
          <el-option
            v-for="(item, index) in orderTypeOptions"
            :key="index"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </div>
      <!-- 状态 -->
      <div class="mr-8">
        <p class="mb-4 font-12 font-500 color-727">
          {{ $t("message.c2c.zhuangtai") }}
        </p>
        <el-select
          v-model="orderState"
          :placeholder="$t('message.c2c.qingxuanze')"
        >
          <el-option
            v-for="(item, index) in orderStateOptions"
            :key="index"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </div>
      <!-- 日期 -->
      <div class="mr-8">
        <p class="mb-4 font-12 font-500 color-727">
          {{ $t("message.c2c.riqi") }}
        </p>
        <el-date-picker
          :value-format="'yyyy-MM-dd'"
          v-model="date"
          type="daterange"
          :range-separator="$t('message.c2c.zhi')"
          :start-placeholder="$t('message.c2c.kaishishijian')"
          :end-placeholder="$t('message.c2c.jieshushijian')"
        >
        </el-date-picker>
      </div>
      <!-- 查询 -->
      <span
        class="relative top20 mr-8 font-14 font-600 color-1A6 cursor-pointer"
        @click="Inquire"
        >{{ $t("message.c2c.chaxun") }}</span
      >
      <!-- 重置 -->
      <span
        class="relative top20 font-14 font-600 color-1A6 cursor-pointer"
        @click="initSelectData"
        >{{ $t("message.c2c.chongzhishaixuan") }}</span
      >
    </div>

    <div style="background: rgb(249, 249, 250)" :style="contentHeight">
      <!-- 表格头部 -->
      <div ref="content" class="main-div">
        <div
          class="flex justify-between items-center h-12 border-b-1px color-788 font-14"
        >
          <div class="flex-1">{{ $t("message.c2c.leixingbizhong") }}</div>
          <div class="flex-1">{{ $t("message.c2c.zonge") }}</div>
          <div class="flex-1">{{ $t("message.c2c.jiage") }}</div>
          <div class="flex-1">{{ $t("message.c2c.shuliang") }}</div>
          <div class="flex-1">{{ $t("message.c2c.duifangnicheng") }}</div>
          <div class="flex-1">{{ $t("message.c2c.zhuangtai") }}</div>
          <div class="flex-1">{{ $t("message.c2c.caozuo") }}</div>
        </div>
      </div>
      <!--表格内容-->
      <order-table class="main-div" :orderDataList="orderList" />
      <!--分页-->
      <Pagination
        :noPre="noPre"
        :noNext="noNext"
        :pageNum="page_no"
        @changePageNum="changePageNum"
      />
    </div>
  </div>
</template>

<script>
import { mapState } from "pinia";
import FooterView from "@/components/layout/footerView.vue";
import Pagination from "@/components/common/pagination.vue";
import OtcOrderHeader from "@/views/c2c/components/otc-order-header/index.vue";
import OrderTable from "./orderTable.vue";
import Axios from "@/api/c2c.js";
export default {
  name: "order",
  data() {
    return {
      active: 1,
      contentTop: 0, // content距离顶部的高度
      date: "",
      currency: "",
      currencyOptions: [
        {
          value: 0,
          label: this.$t("message.c2c.suoyoubizhong"),
        },
      ],
      orderType: "",
      orderTypeOptions: [
        {
          value: 0,
          label: this.$t("message.c2c.goumaichushou"),
        },
        {
          value: "buy",
          label: this.$t("message.c2c.goumai"),
        },
        {
          value: "sell",
          label: this.$t("message.c2c.chushou"),
        },
      ],
      orderState: -1,
      orderStateOptions: [
        {
          value: -1,
          label: this.$t("message.c2c.suoyouzhuangtai"),
        },
        {
          value: 0,
          label: this.$t("message.c2c.weifukuan"),
        },
        {
          value: 1,
          label: this.$t("message.c2c.yifukuan"),
        },
        {
          value: 2,
          label: this.$t("message.c2c.shenshuzhong"),
        },
        {
          value: 3,
          label: this.$t("message.c2c.yiwancheng"),
        },
        {
          value: 4,
          label: this.$t("message.c2c.yiquxiao"),
        },
        {
          value: 5,
          label: this.$t("message.c2c.yichaoshi"),
        },
      ],
      orderList: [],
      page_no: 1,
      noNext: false,
      noPre: false,
      totalPageNum: 0,
    };
  },
  created() {
    this.initSelectData();
    this.getSymbolList();
  },
  mounted() {
    this.contentTop = this.$refs.content.getBoundingClientRect().top;
    if (this.$route.query.id) {
      this.active = this.$route.query.id;
    } else {
      this.active = 0;
    }
    this.selectIndex(parseInt(this.active));
  },
  watch: {
    "$route.query.id"(val) {
      this.selectIndex(parseInt(val));
    },
  },
  methods: {
    //重置
    initSelectData() {
      this.currency = 0;
      this.orderType = 0;
      this.orderState = -1;
      this.date = "";
      this.page_no = 1;
      this.orderList = [];
      this.getList();
    },
    //查询
    Inquire() {
      this.orderList = [];
      this.page_no = 1;
      this.getList();
    },
    //分页
    changePageNum(type) {
      if (type == "next") {
        console.log(this.page_no, this.totalPageNum);
        if (this.page_no > this.totalPageNum) {
          return;
        }
        if (!this.noNext) {
          this.page_no = this.page_no + 1;
        }
      } else {
        if (!this.noPre && this.page_no > 1) {
          this.page_no = this.page_no - 1;
        }
      }
      this.getList();
    },
    getSymbolList() {
      Axios.c2cSymbolList().then((res) => {
        if (res.code == "0") {
          for (const key in res.data) {
            let obj = {
              label: res.data[key],
              value: key,
            };
            this.currencyOptions.push(obj);
          }
        }
      });
    },
    // 选中
    selectIndex(val) {
      this.active = val;
      this.currency = 0;
      this.orderType = 0;
      this.orderState = -1;
      this.date = "";
      this.page_no = 1;
      this.orderList = [];
      this.getList();
    },
    // 获取订单
    getList() {
      let obj = {
        page_no: this.page_no,
      };
      if (this.active == 0) {
        obj.state = 0;
      }
      if (this.orderType != 0) {
        obj.direction = this.orderType;
      }
      if (this.orderState != -1) {
        obj.state = this.orderState;
      }
      if (this.date) {
        obj.startTime = this.date[0];
        obj.endTime = this.date[1];
      }
      // 获取用户订单
      Axios.userc2cOrderList(obj).then((res) => {
        if (res.code == "0") {
          this.orderList = res.data;
          if (this.currency != 0) {
            this.orderList = this.orderList.filter((item) => {
              console.log(this.currency.toUpperCase(), item.symbol);
              return this.currency.toUpperCase() == item.symbol;
            });
          }
          this.totalPageNum = parseInt(res.data.length / 10);
          const total = Math.ceil(res.data.length / 10);
          this.noNext = this.pageNum > total || this.pageNum == total;
        }
      });
    },
  },
  computed: {
    // ...mapState('otcOrder', ['orderList']),
    contentHeight() {
      return {
        "min-height": `calc(100vh - ${this.contentTop}px)`,
      };
    },
  },
  components: {
    OtcOrderHeader,
    OrderTable,
    FooterView,
    Pagination,
  },
};
</script>

<style lang="scss" scoped>
.top20 {
  top: 20px;
}
.box-shadow {
  box-shadow: 0px 2px 6px rgba(0, 0, 0, 0.1);
}

:deep {
  .el-input__inner {
    background-color: #fff !important;
  }
  .el-select {
    width: 145px;

    .el-input__inner:focus {
      border-color: #dcdfe6;
    }

    .el-input.is-focus {
      .el-input__inner {
        border-color: #1d91ff;
      }
    }
  }
  .el-range-editor.is-active {
    border-color: #409eff;
  }
}

.el-select-dropdown__item {
  height: 40px !important;
  line-height: 40px !important;
  font-size: 14px;
  font-weight: 500;
  color: #000;
}

.active {
  position: relative;
  color: #000;

  &:after {
    content: "";
    position: absolute;
    left: 0;
    top: 34px;
    width: 100%;
    height: 3px;
    background: #1d91ff;
  }
}

.color-1A6 {
  color: #1a6ebd;
}

.color-727 {
  color: #78808e;
}

.main-div {
  width: 1200px;
  margin: 24px auto;
}
</style>
