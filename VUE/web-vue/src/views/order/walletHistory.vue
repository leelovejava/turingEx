<template>
  <div class="right-padding">
    <div class="right-title">{{ $t("message.user.qianbaolishi") }}</div>
    <el-form :inline="true" :model="formInline" class="margin-top20">
      <el-form-item :label="$t('message.user.leixing1')">
        <el-select
          v-model="formInline.category"
          clearable
          :laceholder="$t('message.user.xuanzeleixing')"
        >
          <el-option
            :label="$t('message.user.chongzhi3')"
            value="recharge"
          ></el-option>
          <el-option
            :label="$t('message.user.tixian2')"
            value="withdraw"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('message.user.shijian2')">
        <el-date-picker
          v-model="formInline.start_time"
          type="datetime"
          :placeholder="$t('message.user.xuanzekaishishijian')"
        >
        </el-date-picker>
        <el-date-picker
          v-model="formInline.end_time"
          type="datetime"
          class="margin-left10"
          :placeholder="$t('message.user.xuanzejiehsushijian')"
        >
        </el-date-picker>
      </el-form-item>
      <el-form-item :label="$t('message.user.zichan')">
        <el-select
          v-model="formInline.wallet_type"
          clearable
          :placeholder="$t('message.user.xuanzezichan')"
        >
          <el-option
            v-for="item in assetList"
            :key="item.symbol"
            :label="item.symbol?.toUpperCase()"
            :value="item.symbol"
          >
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item :label="$t('message.user.zhuangtai2')">
        <el-select
          v-model="formInline.status"
          clearable
          :placeholder="$t('message.user.xuanzezhuangtai')"
        >
          <el-option
            :label="$t('message.user.querenzhong2')"
            value="0"
          ></el-option>
          <el-option
            :label="$t('message.user.chengong2')"
            value="1"
          ></el-option>
          <el-option :label="$t('message.user.shibai2')" value="2"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="search">{{
          $t("message.user.chaxun2")
        }}</el-button>
      </el-form-item>
    </el-form>

    <!-- 列表内容 -->
    <div class="margin-top-bottom20">
      <el-table
        :data="tableData"
        class="width100"
        :header-cell-style="getRowClass"
        :row-style="rowStyles"
        :empty-text="$t('message.home.noData')"
      >
        <el-table-column
          prop="createtimestr"
          :label="$t('message.user.shijian3')"
        ></el-table-column>
        <el-table-column
          prop="order_no"
          :label="$t('message.user.dingdanhao2')"
          :width="190"
        ></el-table-column>
        <el-table-column prop="category" :label="$t('message.user.leixing2')">
          <template #default="scope">
            <div v-if="scope.row.category == 'recharge'">
              {{ $t("message.user.chongzhi4") }}
            </div>
            <div v-else>{{ $t("message.user.tixian3") }}</div>
          </template>
        </el-table-column>
        <el-table-column
          prop="amount"
          :label="$t('message.user.shuliang2')"
        ></el-table-column>
        <el-table-column
          prop="wallet_type"
          :label="$t('message.user.zichan3')"
        ></el-table-column>

        <el-table-column prop="status" :label="$t('message.user.zhuangtai5')">
          <template #default="scope">
            <div class="red" v-if="scope.row.status == 0">
              {{ $t("message.user.querenzhong4") }}
            </div>
            <div class="green" v-if="scope.row.status == 1">
              {{ $t("message.user.chenggong5") }}
            </div>
            <div class="red" v-if="scope.row.status == 2">
              {{ $t("message.user.shibai3") }}
            </div>
          </template>
        </el-table-column>
        <el-table-column :label="$t('message.user.caozuo3')">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="checkDetail(scope.row.order_no, scope.row.category)"
              >{{ $t("message.user.xiangqing") }}</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <el-pagination
        class="pagination-box"
        v-model:current-page="pageNum"
        :default-page-size="20"
        layout="total, prev, pager, next, jumper"
        :total="tableLength"
        @current-change="handleCurrentChange"
      />
      <!-- 详情 -->
      <el-dialog
        :title="$t('message.user.xiangqing')"
        v-model="detailDialog"
        width="50%"
        center
      >
        <div class="dialog-style">
          <div class="dialog-content">
            <div>
              <span class="label"
                >{{ $t("message.user.dingdanshijian3") }}:
              </span>
              <span>{{ detailData.create_time }}</span>
            </div>
            <div>
              <span class="label"> {{ $t("message.user.dingdanhao3") }}: </span>
              <span>{{ detailData.order_no }}</span>
            </div>
            <div>
              <span class="label"> {{ $t("message.user.jine2") }}: </span>
              <span>{{ detailData.amount }}</span>
            </div>
            <div>
              <span class="label">{{ $t("message.user.bizhong4") }}: </span>
              <span>{{ detailData.coin }}</span>
            </div>
            <div>
              <span class="label">{{ $t("message.user.shouxufei2") }}: </span>
              <span>{{ detailData.fee }}</span>
            </div>
            <div v-if="category == 'withdraw'">
              <span class="label">{{ $t("message.user.shoukuandizhi") }}:</span>
              <span>{{ detailData.to }}</span>
            </div>
            <div v-if="category == 'recharge'">
              <span class="label">{{ $t("message.user.zhuanchudizhi") }}:</span>
              <span>{{ detailData.from }}</span>
            </div>
            <div>
              <span class="label"> hash{{ $t("message.user.zhi") }}:</span>
              <span>{{ detailData.tx || "--" }}</span>
            </div>
            <div>
              <span class="label"> {{ $t("message.user.zhuangtai3") }}:</span>
              <span
                :style="`color:${detailData.state == 1 ? 'green' : 'red'}`"
                >{{ getStateText(detailData.state) }}</span
              >
            </div>
            <div>
              <span class="label"> {{ $t("message.user.yuanyin") }}:</span>
              <span>{{ detailData?.failure_msg }}</span>
            </div>
          </div>
        </div>
      </el-dialog>
    </div>
  </div>
</template>
<script>
import Axios2 from "@/api/wallet.js";
import { mapState } from "pinia";
import { useUserStore } from "@/store/user";
export default {
  name: "exchangeHistory",
  data() {
    return {
      formInline: {
        category: "",
        start_time: "",
        end_time: "",
        wallet_type: "",
        status: "",
      },
      assetList: [], //资产列表数组
      tableData: [], //列表数组
      total: 0,
      pageNum: 1,
      tableLength: 0,
      detailDialog: false,
      detailData: {},
      category: "",
    };
  },
  computed: {
    ...mapState(useUserStore, ["existToken"]),
  },
  mounted() {
    if (this.existToken) {
      this.getList();
      this.getAssets();
    }
  },

  methods: {
    getStateText(state) {
      const map = {
        0: this.$t("message.user.querenzhong3"),
        1: this.$t("message.user.chengoong"),
        2: this.$t("message.user.shibai5"),
      };
      return map[state];
    },
    getAssets() {
      Axios2.url("api/item!list.action").then((res) => {
        this.assetList = res.data;
      });
    },
    //获取列表数据
    getList() {
      Axios2.url("api/wallet/records.action", {
        category: this.formInline.category,
        start_time: this.formInline.start_time,
        end_time: this.formInline.end_time,
        wallet_type: this.formInline.wallet_type,
        status: this.formInline.status,
        page_no: this.pageNum,
      }).then((res) => {
        this.tableData = res.data;
        this.tableLength = res.data.length;
      });
    },
    search() {
      this.getList();
    },
    handleCurrentChange(val) {
      this.pageNum = val;
      this.getList();
    },

    //给表头设置背景颜色
    getRowClass({ rowIndex, columnIndex }) {
      if (rowIndex == 0) {
        return { background: "#f8f8f9" };
      }
    },
    //行高修改，需以对象形式返回
    rowStyles({ row, rowIndex }) {
      let styleJson = {
        height: "50px",
      };
      return styleJson;
    },
    OnClickPre() {
      this.$router.go(-1);
    },
    checkDetail(order_no, category) {
      this.detailDialog = true;
      this.category = category;
      let apiUrl = "";
      // 充值详情
      if (category == "recharge") {
        apiUrl = "api/rechargeBlockchain/get";
      } else {
        apiUrl = "api/withdraw/get";
      }

      Axios2.url(apiUrl, {
        order_no: order_no,
      }).then((res) => {
        this.detailData = res.data;
      });
    },
  },
};
</script>
<style scoped>
.label {
  width: 40%;
  text-align: right;
  display: inline-block;
}

.dialog-style {
  display: block;
}
</style>
