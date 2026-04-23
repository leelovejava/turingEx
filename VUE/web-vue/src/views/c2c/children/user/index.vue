<template>
  <div class="w-full h-full">
    <otc-order-header />
    <div class="main">
      <el-tabs v-model="activeName">
        <el-tab-pane
          :label="$t('message.c2c.shoukuanfangshi')"
          name="first"
        ></el-tab-pane>
      </el-tabs>
      <div class="tables mb-8 p-12">
        <div class="title">
          {{ $t("message.c2c.shoukuanfangshi") }}
        </div>
        <div class="flex py-4">
          <div class="w-800 text">
            {{ $t("message.c2c.tip154") }}
          </div>
          <div class="add-box">
            <!-- 添加收款方式 -->
            <div class="add-but" @click="OnClickShowAddre">
              {{ $t("message.c2c.tianjiashoukuanfangshi")
              }}<i class="el-icon-plus"></i>
            </div>
            <!-- 收款方式下拉框 -->
            <div class="pay-type-select" v-if="payTypeShow">
              <div
                class="pay-type-select-item"
                v-for="(item, index) in payMethodList"
                :key="index"
                @click="openPay(item)"
              >
                <div class="pay-type-select-item-line"></div>
                {{ item.name }}
              </div>
            </div>
          </div>
        </div>
        <!-- 收款方式列表 -->
        <div
          class="pay-type-item"
          v-for="(item, index) in listTableList"
          :key="index"
        >
          <!-- 头部 -->
          <div class="pay-top">
            <div class="pay-top-line"></div>
            <div>{{ item.methodName }}</div>
            <div class="caozuo">
              <div class="caozuo-item" @click="editData(item)">
                {{ $t("message.user.bianji") }}
              </div>
              <div class="caozuo-item" @click="OnClickDel(item.uuid)">
                {{ $t("message.user.shanchu") }}
              </div>
            </div>
          </div>
          <!-- 表格内容-->
          <div class="pay-bottom" v-if="item.methodType == 2">
            <div class="pay-bottom-item">
              <div class="bottom-title">
                {{ $t("message.user.zhenshixingming") }}
              </div>
              <div class="bottom-text">{{ item.realName }}</div>
            </div>
            <div class="pay-bottom-item">
              <div class="bottom-title">{{ $t("message.user.bizhong") }}</div>
              <div class="bottom-text">{{ item.paramValue1 }}</div>
            </div>
            <div class="pay-bottom-item">
              <div class="bottom-title">{{ $t("message.user.dizhi") }}</div>
              <div class="bottom-text">{{ item.paramValue2 }}</div>
            </div>
            <div class="pay-bottom-item">
              <div class="bottom-title">
                {{ $t("message.user.zhuanzhangwangluo") }}
              </div>
              <div class="bottom-text">{{ item.paramValue3 }}</div>
            </div>
          </div>
          <div class="pay-bottom" v-else>
            <div class="pay-bottom-item">
              <div class="bottom-title">{{ $t("message.user.xingming") }}</div>
              <div class="bottom-text">{{ item.realName }}</div>
            </div>
            <div class="pay-bottom-item" v-if="item.methodType == 1">
              <div class="bottom-title">
                {{ $t("message.user.yinhangkazhanghu") }}
              </div>
              <div class="bottom-text">{{ item.paramValue3 }}</div>
            </div>
            <div class="pay-bottom-item" v-else>
              <div class="bottom-title">{{ item.paramName1 }}</div>
              <div class="bottom-text">{{ item.paramValue1 }}</div>
            </div>
            <div class="pay-bottom-item" v-if="item.methodType == 1">
              <div class="bottom-title">
                {{ $t("message.user.yinhangmingcheng") }}
              </div>
              <div class="bottom-text">{{ item.paramValue1 }}</div>
            </div>
            <div class="pay-bottom-item" v-if="item.methodType == 1">
              <div class="bottom-title">{{ $t("message.user.kaihuhang") }}</div>
              <div class="bottom-text">{{ item.paramValue2 }}</div>
            </div>
            <div class="pay-bottom-item" v-if="item.methodType != 1">
              <div class="bottom-title">
                {{ $t("message.c2c.shoukuanerweima") }}
              </div>
              <!-- TODO 后端没返回 -->
              <div class="bottom-text">
                <img :src="item.qrcodePath" />
              </div>
            </div>
            <div class="pay-bottom-item">
              <div class="bottom-title">{{ $t("message.c2c.beizhu") }}</div>
              <div class="bottom-text">{{ item.remark }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- footer -->
    <footer-view></footer-view>
    <el-dialog v-model="dialogVisible" width="470px">
      <img class="tip-img" src="@/assets/images/c2c/orderSuccess/tip.png" />
      <div class="dig-title">{{ $t("message.user.tip1") }}</div>
      <div class="dig-text">{{ $t("message.user.tip2") }}</div>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="but" @click="dialogVisible = false">{{
            $t("message.user.quxiao")
          }}</el-button>
          <el-button class="but" type="primary" @click="deleteIdFun">{{
            $t("message.user.queding")
          }}</el-button>
        </span>
      </template>
    </el-dialog>
    <!--  -->
    <el-dialog
      v-model="dialogVisible1"
      :title="$t('message.user.tuijianstype')"
      class="pop-box"
      width="670px"
    >
      <div class="title">{{ $t("message.user.tuijianstype") }}</div>
      <div class="pop-pay-list">
        <div class="pop-pay-item">
          <div class="line"></div>
          {{ $t("message.user.yinhangka") }}
        </div>
        <div class="pop-pay-item">
          <div class="line"></div>
          {{ $t("message.user.yinhangka") }}
        </div>
      </div>
      <div class="flex mt-20 items-center">
        <div class="title flex-1">{{ $t("message.user.allPay") }}</div>
        <div style="width: 250px">
          <el-input
            :placeholder="$t('message.user.sousuobizhong')"
            prefix-icon="el-icon-search"
            v-model="input2"
          >
          </el-input>
        </div>
      </div>
      <div class="pop-pay-list">
        <div class="pop-pay-item">
          <div class="line"></div>
          {{ $t("message.user.yinhangka") }}
        </div>
        <div class="pop-pay-item">
          <div class="line"></div>
          {{ $t("message.user.yinhangka") }}
        </div>
      </div>
    </el-dialog>
    <!--  -->
    <el-dialog v-model="dialogVisible2" width="470px">
      <img class="tip-img" src="@/assets/images/c2c/orderSuccess/tip.png" />
      <div class="dig-title">{{ $t("message.user.tip1") }}</div>
      <div class="dig-text">{{ $t("message.user.tip2") }}</div>
      <template #footer>
        <span class="dialog-footer flex justify-center">
          <el-button class="but" @click="dialogVisible2 = false">{{
            $t("message.user.quxiao")
          }}</el-button>
          <el-button class="but" type="primary" @click="deleteIdFun">{{
            $t("message.user.queding")
          }}</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import FooterView from "@/components/layout/footerView.vue";
import OtcOrderHeader from "@/views/c2c/components/otc-order-header/index.vue";
import Axios from "@/api/c2c.js";
import { getParamsLang } from "@/utils/index";

export default {
  name: "user",
  data() {
    return {
      listTableList: [],
      dialogVisible: false,
      dialogVisible1: false,
      activeName: "first",
      payTypeShow: false,
      payMethodList: [], //收款方式
      input2: "",
      deleteId: "",
      dialogVisible2: false,
    };
  },
  components: {
    FooterView,
    OtcOrderHeader,
  },
  mounted() {
    let spToken = localStorage.getItem("spToken");
    if (spToken) {
      this.getPayMethodList();
      this.getUserPayList();
    }
  },
  methods: {
    getParamsLang,
    // 获取该用户支持的收款方式
    getUserPayList() {
      Axios.userC2cPaymentMethodList({ type: 2 }).then((res) => {
        if (res.code == "0") {
          this.listTableList = res.data;
        }
      });
    },

    // 获取收款方式
    getPayMethodList() {
      this.payMethodList = [];
      // type:2表示C2C
      Axios.payMethodList({ type: 2 })
        .then((res) => {
          for (let key in res.data) {
            let obj = {
              name: res.data[key],
              value: key,
            };
            this.payMethodList.push(obj);
          }
        })
        .catch((err) => {
          console.error(err);
        });
    },
    //编辑收款方式
    editData(item) {
      this.$router.push("/addressAddBank?payId=" + item.uuid);
    },
    // 删除
    deleteIdFun() {
      var addParams = {
        id: this.deleteId,
      };
      //  删除
      Axios.delPayConfig(addParams)
        .then((res) => {
          if (res.code == "0") {
            this.dialogVisible2 = false;
            this.getPayMethodList();
            this.getUserPayList();
            this.$message({
              message: this.$t("message.user.shanchuchenggong"),
              type: "success",
            });
          }
        })
        .catch((err) => {
          console.error(err);
        });
    },

    OnClickDel(id) {
      this.deleteId = id;
      this.dialogVisible2 = true;
    },
    openPay(item) {
      this.$router.push("/addressAddBank?type=2&methodId=" + item.value);
    },
    OnClickShowAddre() {
      this.payTypeShow = !this.payTypeShow;
      // this.showAddreChoose = !this.showAddreChoose;
    },
  },
};
</script>

<style scoped lang="scss">
.main {
  width: 1200px;
  background: #fff;
  margin: 0 auto;

  .user-main {
    border-bottom: 1px solid #e1e3e6;
    padding: 40px 0;

    .user-info {
      align-items: center;

      img {
        width: 25px;
        height: 25px;
        margin-right: 10px;
      }

      .user-name {
        font-size: 16px;
        margin-right: 10px;
      }

      .icon {
        font-size: 16px;
        margin-right: 30px;
        color: #727a89;
      }

      .certified {
        height: 30px;
        background: #eff7ff;
        border-radius: 5px;
        padding: 0 10px;
        line-height: 30px;
        color: #1a6ebd;
        font-size: 14px;
      }
    }

    .certified-item {
      align-items: center;
      font-size: 14px;
      margin-right: 30px;

      .success {
        width: 15px;
        height: 15px;
        margin-left: 10px;
      }
    }
  }

  .w-800 {
    width: 800px;
  }

  .user-right {
    .advertiser {
      background: #1d91ff;
      border-radius: 4px;
      height: 40px;
      color: #fff;
      font-size: 14px;
      padding: 0 12px;
      line-height: 40px;
      margin-left: 30px;
    }

    .eye {
      width: 23px;
      height: 23px;
    }

    .moeny-number {
      color: #727a89;
    }
  }

  .egg-item {
    .title {
      color: #727a89;
      font-size: 14px;
    }

    .text {
      font-size: 20px;
      font-weight: bold;

      span {
        font-size: 14px;
        font-weight: initial;
        margin-left: 5px;
      }
    }
  }

  .paytypebut {
    width: 250px;
    height: 40px;
    background: #eaecef !important;
  }

  .pay-top {
    display: flex;
    justify-content: space-between;
    padding: 15px 30px;
    align-items: center;
    background: #fafafa;
    font-size: 16px;
    font-weight: bold;
    position: relative;
  }

  .pay-top-line {
    position: absolute;
    height: 25px;
    border-radius: 10px;
    background: #4ba6eb;
    width: 5px;
    left: 16px;
    top: 14px;
  }

  .pay-type-item {
    border: 1px solid #e7e9eb;
    border-radius: 5px;
    margin-bottom: 40px;
  }

  .caozuo {
    display: flex;
  }

  .caozuo-item {
    margin-left: 20px;
    font-size: 14px;
    font-weight: initial;
    cursor: pointer;
  }

  .pay-bottom {
    padding: 15px 30px;
    display: flex;
  }

  .pay-bottom-item {
    min-width: 210px;
    word-break: break-all;

    word-wrap: break-word;

    white-space: pre-wrap;
  }

  .bottom-title {
    color: #727a89;
    font-size: 14px;
  }

  .bottom-text {
    color: #000000;
    font-size: 14px;
    margin-top: 20px;
  }

  .pay-type-select {
    position: absolute;
    width: 250px;
    top: 42px;
    background: #ffffff;
    box-shadow: 0px 0px 4px rgba(0, 0, 0, 0.2);
    z-index: 10;
  }

  .pay-type-select-item {
    height: 40px;
    padding: 0 30px;
    display: flex;
    align-items: center;
    cursor: pointer;
  }

  .pay-type-select-item-line {
    width: 4px;
    height: 20px;
    background: #4ba6eb;
    border-radius: 6px;
    margin-right: 10px;
  }

  .pay-type-select-item:hover {
    background: #f5f5f5;
  }

  .el-button {
    border: 1px solid #dcdfe6;
  }

  :deep(.el-dialog__header) {
    border-bottom: none !important;
  }

  .dig-title {
    font-size: 24px;
    font-weight: bold;
    text-align: center;
    margin-top: 30px;
  }

  .dig-text {
    font-size: 14px;
    margin-top: 20px;
    text-align: center;
    margin-bottom: 30px;
  }

  .tip-img {
    width: 60px;
    height: 60px;
    margin: 0 auto;
    display: block;
  }

  .dialog-footer {
    display: flex;
  }

  .dialog-footer .but {
    width: 210px;
  }
}

:deep(.el-tabs__active-bar) {
  background-color: #1d91ff !important;
}

.tables {
  background: #ffffff;
  box-shadow: 0px 0px 4px rgba(0, 0, 0, 0.3);
  border-radius: 5px;

  .title {
    font-size: 16px;
    font-weight: bold;
  }

  .text {
    font-size: 14px;
    line-height: 30px;
  }

  .add-box {
    flex: 1;
    display: flex;
    justify-content: flex-end;
    position: relative;

    .add-but {
      width: 250px;
      height: 40px;
      cursor: pointer;
      background: #eaecef;
      border-radius: 3px;
      line-height: 40px;
      text-align: center;
      font-size: 14px;
    }

    .pay-type-select {
      position: absolute;
      width: 250px;
      top: 42px;
      background: #ffffff;
      box-shadow: 0px 0px 4px rgba(0, 0, 0, 0.2);
      z-index: 10;
    }

    .more {
      height: 40px;
      padding: 0 10px;
      color: #000;
      line-height: 40px;
      background: #f5f5f5;
    }

    .pay-type-select-item {
      height: 40px;
      padding: 0 30px;
      display: flex;
      align-items: center;
      cursor: pointer;
    }

    .pay-type-select-item-line {
      width: 4px;
      height: 20px;
      background: #4ba6eb;
      border-radius: 6px;
      margin-right: 10px;
    }

    .pay-type-select-item:hover {
      background: #f5f5f5;
    }
  }
}

.pop-box {
  color: #000000;

  .title {
    font-size: 16px;
    font-weight: bold;
    color: #000000;
  }

  .pop-pay-list {
    display: flex;
    flex-wrap: wrap;
    color: #000000;

    .pop-pay-item {
      font-size: 15px;
      font-weight: bold;
      display: flex;
      align-items: center;
      width: 310px;
      margin-top: 20px;
      cursor: pointer;

      .line {
        background: #2757bb;
        border-radius: 10px;
        width: 3px;
        height: 16px;
        margin-right: 15px;
      }
    }
  }
}
.dig-title {
  font-size: 24px;
  font-weight: bold;
  text-align: center;
  margin-top: 30px;
}

.dig-text {
  font-size: 14px;
  margin-top: 20px;
  text-align: center;
  margin-bottom: 30px;
}

.tip-img {
  width: 60px;
  height: 60px;
  margin: 0 auto;
  display: block;
}
</style>
