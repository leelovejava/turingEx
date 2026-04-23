<template>
  <div class="nav-info">
    <div
      class="nav-center"
      :class="$route.path == '/c2c/fastBuy' ? 'justify-center' : ''"
    >
      <div class="nav-tab">
        <span
          class="lf40"
          :class="[$route.path == '/c2c/fastBuy' ? 'active' : '']"
          @click="openUrl('/c2c/fastBuy')"
          >{{ $t("message.c2c.kuaijiequ") }}</span
        >
        <span
          :class="[$route.path == '/c2c/wantBuy' ? 'active' : '']"
          @click="openUrl('/c2c/wantBuy')"
          >{{ $t("message.c2c.zixuanqu") }}</span
        >
      </div>
      <div class="nav-right">
        <!-- 订单 -->
        <div
          class="nav-item"
          :class="[$route.path == '/c2c/order' ? 'nav_active' : '']"
          @click.stop="show = !show"
          style="margin-right: 30px; position: relative"
        >
          <img
            v-if="$route.path != '/c2c/order'"
            src="@/assets/images/c2c/orderSuccess/Subtract.png"
            alt=""
          />
          <img
            v-if="$route.path == '/c2c/order'"
            src="@/assets/images/c2c/order/Group1582.png"
            alt=""
          />
          <span class="nav-t">{{ $t("message.c2c.dingdan") }}</span>
          <span class="br50" v-if="orderList.length > 0">{{
            orderList.length
          }}</span>
          <div v-if="show" class="order-pop">
            <div class="flex items-center h-full font-14 text-white">
              <transition name="el-fade-in-linear">
                <div class="w-full bg-white">
                  <div
                    class="flex items-center justify-between px-4 box-border border-b-1px font-16 font-medium text-black"
                  >
                    <span @click="openUrl('/c2c/order?id=0')">{{
                      $t("message.c2c.jinxingzhong")
                    }}</span>
                    <span @click="openUrl('/c2c/order?id=1')">{{
                      $t("message.c2c.quanbudingdan")
                    }}</span>
                  </div>
                  <!-- 有订单 -->
                  <div v-if="orderList.length > 0" class="p-4 text-black">
                    <div
                      class="order-header-drop"
                      v-for="(item, index) in orderList"
                      :key="index"
                      @click.stop="openOrder(item)"
                    >
                      <div
                        class="flex justify-between items-center font-18 font-medium"
                      >
                        <div>
                          <span
                            :style="{
                              color:
                                item.direction === 'buy'
                                  ? '#4EA372'
                                  : '#E05461',
                            }"
                            >{{
                              item.direction === "buy"
                                ? $t("message.c2c.goumai")
                                : $t("message.c2c.chushou")
                            }}</span
                          >
                          <!-- 币种 -->
                          <span class="ml-1 text-black">{{ item.symbol }}</span>
                        </div>
                        <div class="flex items-center">
                          <span
                            class="text-underline color-1A6 font-14 cursor-pointer"
                            >{{ $t("message.c2c.weifukuan") }}</span
                          >
                          <i
                            class="relative top-2 el-icon-arrow-right font-semibold font-16"
                            style="color: rgb(114, 122, 136)"
                          ></i>
                        </div>
                      </div>
                      <div
                        class="flex items-center justify-between list-item-center"
                      >
                        <div class="font-14 text-black">
                          <span>{{ $t("message.c2c.jiage") }}</span>
                          <span>{{ item.symbol_value }}</span>
                          <span>{{ item.currency }}</span>
                        </div>
                        <div class="font-12 color-B8B">
                          {{ item.create_time }}
                        </div>
                      </div>
                      <div
                        class="flex items-center justify-between list-item-center"
                      >
                        <div class="font-semibold font-16">
                          <span>{{ $t("message.c2c.shuliang") }}</span>
                          <span class="mx-1">{{ item.coin_amount }}</span>
                          <span>{{ item.symbol }}</span>
                        </div>
                        <div class="font-medium font-14">
                          <span class="mr-1">{{ getDecimal(item) }}</span>
                          <span>{{ item.currency }}</span>
                        </div>
                      </div>
                      <div
                        class="flex items-center justify-between list-item-center mt-4"
                      >
                        <div class="font-14 flex items-center">
                          <img
                            class="user-head-img"
                            :src="item.c2c_user_head_img"
                          />
                          <span>{{ item.c2c_user_nick_name }}</span>
                        </div>
                      </div>
                    </div>
                  </div>
                  <!-- 没有订单 -->
                  <div
                    class="flex flex-col items-center pb-35 pt-29"
                    v-if="orderList == 0"
                  >
                    <img
                      class="relative noOrder"
                      src="@/assets/images/c2c/order/noData.png"
                    />
                    <span class="mt-12 font-14" style="color: #484d56">{{
                      $t("message.home.zanwujilu")
                    }}</span>
                  </div>
                </div>
              </transition>
            </div>
          </div>
        </div>
        <!-- C2C用户中心 -->
        <div
          class="nav-item"
          :class="[$route.path == '/c2c/user' ? 'nav_active' : '']"
          @click="openUrl('/c2c/user')"
          style="margin-right: 50px"
        >
          <img
            v-if="$route.path != '/c2c/user'"
            src="@/assets/images/c2c/orderSuccess/Group.png"
            alt=""
          />
          <img
            v-if="$route.path == '/c2c/user'"
            src="@/assets/images/c2c/order/Group1911-1.png"
            alt=""
          />
          <span class="nav-t">{{ $t("message.c2c.yonghuzhongxin") }}</span>
        </div>
        <!-- 更多 -->
        <div class="nav-item">
          <img
            src="@/assets/images/c2c/orderSuccess/Group1580.png"
            class="more-img"
          />
          <el-dropdown
            style="width: 100px; background: #14151a"
            trigger="click"
            @command="openDown"
            popper-class="poperWrapper"
          >
            <span class="text-white mr-4">
              {{ $t("message.c2c.gengduo") }}<el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  + {{ $t("message.c2c.shoukuanfangshi") }}</el-dropdown-item
                >
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Axios from "@/api/c2c.js";
import { ArrowDown } from "@element-plus/icons-vue";

export default {
  name: "otcOrderHeader",
  components: { ArrowDown },
  props: {
    state: {
      type: Number,
    },
  },
  watch: {
    // 每次切换都要更新列表
    state(val) {
      if (val != 0) {
        this.$nextTick(()=>{
          this.getOrderList();
        })
      }
    },
  },
  data() {
    return {
      show: false,
      list: [
        {
          title: this.$t("message.c2c.dingdan"),
          path: "/c2c/order",
        },
        {
          title: this.$t("message.c2c.yonghuzhongxin"),
        },
      ],
      orderList: [],
    };
  },
  mounted() {
    this.$nextTick(()=>{
      this.getOrderList();
    })
  },
  methods: {
    handleMouseOver() {
      this.show = true;
    },
    handleMouseOut() {
      this.show = false;
    },
    openUrl(val) {
      this.$router.push(val);
    },
    openDown() {
      this.$router.push("/c2c/user");
    },
    getDecimal(item) {
      return (
        parseFloat(item.symbol_value) * parseFloat(item.coin_amount)
      ).toFixed(5);
      // if (item.symbol == "BTC" || item.symbol == "ETH") {
      //   return (
      //     parseFloat(item.symbol_value) * parseFloat(item.coin_amount)
      //   ).toFixed(5);
      // } else {
      //   return parseFloat(item.symbol_value) * parseFloat(item.coin_amount);
      // }
    },
    getOrderList() {
      let obj = {
        page_no: 1,
        state: 0,
      };
      // 获取用户订单
      Axios.userc2cOrderList(obj).then((res) => {
        if (res.code == "0") {
          this.orderList = res.data;
        }
      });
    },
    openOrder(item) {
      this.show = false;
      this.$router.push(
        "/c2c/orderSuccess?id=" +
          item.order_no +
          "&isBuy=" +
          (item.direction == "buy" ? 1 : 2)
      );
    },
  },
};
</script>

<style lang="scss" scoped>
.nav-info {
  background: #14151a;
  .nav-center {
    width: 1200px;
    height: 50px;
    margin: 0 auto;
    line-height: 50px;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    position: relative;
    .nav-tab {
      color: #868e9b;
      font-size: 14px;
      cursor: pointer;
      display: flex;
      span {
        display: block;
        margin-bottom: 5px;
      }
      .lf40 {
        margin-right: 40px;
      }
      .active {
        border-bottom: 2px solid #1d91ff;
        color: #fff !important;
      }
    }
    .nav-right {
      position: absolute;
      right: -20px;
      top: 0;
      display: flex;
      cursor: pointer;

      .nav-item {
        display: flex;
        align-items: center;
        font-size: 14px;
        color: #fff;
        .more-img {
          width: 2.4px;

          margin-right: 11px;
        }
        .noOrder {
          left: -9px;
          width: 60px;
          height: 60px;
        }

        img {
          width: 12px;
          height: 14px;
        }

        .nav-t {
          margin: 0 5px;
        }

        .br50 {
          width: 18px;
          height: 18px;
          line-height: 18px;
          border-radius: 50%;
          background: #e05561;
          font-size: 12px;
          text-align: center;
        }
      }
    }
  }
}

:deep {
  .otc-select {
    z-index: 10;
    width: 415px;
    top: 50px;
    left: -80px;
  }
}

.order-header-drop {
  margin-bottom: 25px;
  &:last-child {
    margin-bottom: 0;
  }
}

.nav_active {
  border-bottom: 3px solid #1d91ff;
  .nav-t {
    color: #1d91ff;
    font-weight: bold;
  }
}

.order-pop {
  width: 415px;
  position: absolute;
  top: 57px;
  left: 0;
  max-height: 500px;
  overflow-y: auto;
  z-index: 10;
  box-shadow: 0 0px 2px 2px #e5e7ed;

  .border-b-1px {
    border: 1px solid #eaecef;
  }

  .color-1A6 {
    text-decoration-line: underline;
    color: #1a6ebd;
  }

  .color-B8B {
    color: #bbb;
  }

  .list-item-center {
    height: 30px;
  }

  .user-head-img {
    width: 20px;
    height: 20px;
    margin-right: 5px;
  }
}
</style>
<style lang="scss">
.poperWrapper {
  border: 1px solid #24282d !important ;
  .el-popper__arrow {
    display: none;
  }

  .el-dropdown-menu {
    background: #24282d !important;
  }

  .el-dropdown-menu__item,
  .el-dropdown-menu__item:not(.is-disabled):focus {
    color: #fff;
    background: #24282d !important;
  }

  .el-dropdown-menu__item:hover {
    background: #24282d !important;
    color: #fff;
  }
}
</style>
