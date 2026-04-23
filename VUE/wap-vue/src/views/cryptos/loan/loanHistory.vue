<template>
  <div id="loanHistoryPage">
    <div class="loanHistory">
      <assets-head :title="$t('借贷记录')" />
      <!--      Tab区域-->
      <div class="tabMenu flex justify-start items-center">
        <div class="tabList">
          <div class="tab" :class="item.id === activeTabIndex ? 'activeTab' : ''" v-for="item in tabMenuList"
            :key="item.id" @click="changeTab(item)">
            <span>{{ item.menuName }}</span>
          </div>
        </div>
      </div>
      <!--   container -->
      <div class="container px-8">
        <!--      列表区域-->
        <div v-if="loanOrderList && loanOrderList.length > 0">
          <div class="historyData flex justify-between py-10 border-b-color textColor" v-for="item in loanOrderList"
            :key="item.id">
            <div class="left">
              <!--            <div class="mb-8">{{ $t('借贷人') }}: 张三</div>-->
              <div class="mb-8">
                <span>{{ $t("借款金额") }}: </span>
                <span :class="item.status === 5 ? 'text-red' : 'text-green'">{{ item.status === 5 ? "-" : "+" + item.quota
                }}
                  {{ item.symbol }}</span>
              </div>
              <div class="mb-8">{{ $t("备注") }}: {{ item.reason }}</div>
              <div v-if="item.state === 2">
                {{ $t("剩余天数") }}: {{ item.remainQuota }}
              </div>
            </div>
            <div class="right text-center text-white">
              <div class="loanBtn mb-5" @click="$router.push('/customerService')" v-if="item.state === 2">
                {{ $t("申请还款") }}
              </div>
              <div class="loanBtn mb-5" :style="{ background: loanBgText(item.state) }" v-else>
                {{ loanText(item.state) }}
              </div>
              <div class="loanBtn" style="background: #00a7ee" @click="getLoanDetail(item.uuid)">
                {{ $t("详情") }}
              </div>
            </div>
          </div>
        </div>
        <!--      无数据-->
        <div class="flex items-center flex-col" style="margin-top: 198px" v-else>
          <div class="noDataImg mb-6">
            <img src="@/assets/image/loan/noData.png" alt="nodData" />
          </div>
          <p class="textColor1">{{ $t("暂无数据") }}</p>
        </div>
      </div>

      <van-popup v-model:show="isDetail" class="w-full rounded-xl" style="width: 94%">
        <div class="px-8 py-10">
          <div class="text-center text-36 font-bold py-5">
            {{ $t("订单详情") }}
          </div>
          <div class="loanList">
            <div class="flex justify-between py-8 border-b-color">
              <div>
                <span class="grayText">{{ $t("期望借款金额") }}</span>
              </div>
              <div class="flex align-center">
                <!--            <input class="font-semibold textColor border-none text-right mr-3 mainBackground" v-model="loanAmount"  disabled="disabled" @input="inputAmunt" />-->
                <span class="mr-3 font-semibold">{{ loanData.quota || 0 }}</span>
                <span class="font-semibold">USDT</span>
              </div>
            </div>
            <div class="flex justify-between py-8 border-b-color">
              <div>
                <span class="grayText">{{ $t("还款周期") }}</span>
              </div>
              <div class="flex align-center text-right">
                <div class="font-semibold">
                  <span>{{ loanData.term }} {{ $t("Day") }}</span>
                </div>
              </div>
            </div>
            <div class="flex justify-between py-8 border-b-color">
              <div>
                <span class="grayText">{{ $t("日利率") }}</span>
              </div>
              <div class="flex align-center">
                <span class="font-semibold">{{ loanData.dailyRate * 1 * 100 || 0 }}%</span>
              </div>
            </div>
            <div class="flex justify-between py-8 border-b-color">
              <div>
                <span class="grayText">{{ $t("还款方式") }}</span>
              </div>
              <div class="flex align-center">
                <span class="font-semibold text-right">{{ $t("到期一次还款") }}</span>
              </div>
            </div>
            <div class="flex justify-between py-8 border-b-color">
              <div>
                <span class="grayText">{{ $t("利息") }}</span>
              </div>
              <div class="flex align-center">
                <span class="font-semibold">{{ loanData.totalInterest || 0 }} USDT</span>
              </div>
            </div>
            <div class="flex justify-between py-8 border-b-color">
              <div>
                <span class="grayText">{{ $t("放款机构") }}</span>
              </div>
              <div class="flex align-center">
                <span class="font-semibold">{{ loanData.lendingName }}</span>
              </div>
            </div>
            <div class="flex justify-between py-8 border-b-color">
              <div>
                <span class="grayText">{{ $t("状态") }}</span>
              </div>
              <div class="flex align-center">
                <span class="font-semibold" :style="{ color: loanBgText(loanData.state) }">{{
                  loanText(loanData.state)
                }}</span>
              </div>
            </div>
            <div class="flex justify-between py-8 border-b-color" v-if="loanData.state === 2">
              <div>
                <span class="grayText">{{ $t("剩余天数") }}</span>
              </div>
              <div class="flex align-center">
                <span class="font-semibold">{{ loanData.remainQuota }}</span>
              </div>
            </div>
            <div class="flex justify-between py-8">
              <div>
                <span class="grayText">{{ $t("订单时间") }}</span>
              </div>
              <div class="flex align-center">
                <span class="font-semibold">{{ dataTimeEx(loanData.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </van-popup>
    </div>
  </div>
</template>

<script>
import AssetsHead from "@/components/Transform/assets-head/index.vue";
import { _loanOrderList, _loanOrderDetail } from "@/service/fund.api";
import { Popup } from "vant";
import { dataTimeEx } from "@/utils/utis";
export default {
  name: "loanHistory",
  mounted() {
    this.getLoanOrderList();
  },
  methods: {
    //订单详情
    getLoanDetail(orderNo) {
      _loanOrderDetail(orderNo).then((res) => {
        this.loanData = res;
        this.isDetail = true;
      });
    },
    getLoanOrderList() {
      _loanOrderList().then((res) => {
        this.loanOrderList = res.sort((a, b) => b.createTime - a.createTime);
        this.loanAllOrderList = this.loanOrderList;
      });
    },
    loanBgText(val) {
      const bg = { 0: "#BBA635", 1: "#bba635", 2: "#00a7ee", 3: "#E35461", 4: "#b0b4bc" };
      // console.log('this.btnBg',bg[val])
      return bg[val];
    },
    loanText(val) {
      const loanStatus = {
        1: this.$t("审批中"),
        3: this.$t("审批失败"),
        2: this.$t("还款中"),
        4: this.$t("已逾期"),
        5: this.$t("已还款"),
      };
      return loanStatus[val] || this.$t("审批中");
    },
    changeTab(item) {
      this.activeTabIndex = item.id;
      if (item.id === 0) {
        this.loanOrderList = this.loanAllOrderList;
      } else {
        this.loanOrderList = this.loanAllOrderList.filter(
          (data) => data.state === item.id
        );
      }
    },
  },
  computed: {},
  data() {
    return {
      activeTabIndex: 0,
      isData: true,
      loanStatus: 0,
      btnBg: "",
      tabMenuList: [
        { id: 0, menuName: this.$t("全部") },
        { id: 1, menuName: this.$t("审批中") },
        { id: 3, menuName: this.$t("审批失败") },
        { id: 2, menuName: this.$t("还款中") },
        { id: 5, menuName: this.$t("已还款") },
      ],
      loanOrderList: [],
      loanAllOrderList: [],
      isDetail: false, //详情弹窗
      loanData: {},
      dataTimeEx, //时间戳格式化
    };
  },
  components: {
    AssetsHead,
    [Popup.name]: Popup,
  },
};
</script>

<style  lang="scss" scoped>
#loanHistoryPage {
  font-size: 30px;

  .loanHistory {
    width: 100%;
    box-sizing: border-box;
  }

  //隐藏滚动条
  .tabMenu::-webkit-scrollbar {
    display: none;
  }

  .tabMenu {
    border-top: 1px solid $border_color;
    border-bottom: 1px solid $border_color;
    padding: 30px;
    overflow: auto hidden;
    touch-action: auto;
    scrollbar-width: none;
    -ms-overflow-style: none;
    box-sizing: border-box;

    .tabList {
      white-space: nowrap;
      padding-right: 30px;
      touch-action: auto;

      .tab {
        display: inline-block;
        color: #999999;
        height: 60px;
        line-height: 60px;
        text-align: center;
        transition: 0.5s;
        margin-right: 52px;
        border-radius: 34px;
        touch-action: auto;
        -webkit-backface-visibility: hidden;
        backface-visibility: hidden;
        border: 1px solid transparent;

        &.ani {
          transform: translate3d(0, 0, 0);
        }
      }
    }

    .activeTab {
      border-radius: 32px;
      padding: 0 36px;
      -webkit-backface-visibility: hidden !important;
      backface-visibility: hidden !important;
      border: 1px solid $active_line !important;
      color: $color_main !important;
    }
  }

  .noDataImg {
    width: 165px;
    height: 160px;

    img {
      width: 100%;
      height: 100%;
    }
  }

  .loanBtn {
    padding: 14px 30px;
    background: #004aee;
    border-radius: 10px;
  }
}
</style>
