<template>
  <div class="mod-user">
    <div>
      <div class="user_Box_main user_Top_box">
        <div class="u_Top_tittle">今日数据统计(00:00-24:00)</div>
        <div class="u_Top_text">
          <div class="text_Box">
            <div class="text_mall mall_img">
              <div class="mall_Top">
                <span class="mall_Bg"></span>
                <span class="mall_Text">USDT账户余额</span>
                <span class="mall_Data">今日</span>
              </div>
              <div class="mall_Bottom font_Size20">
                {{ sumUsdtAmount }}
              </div>
            </div>
            <div class="text_mall_tow mall_imgl">
              <span class="mall_Bottom_text">总账户金额：</span>
              <span class="mall_Bottom_num">{{ sumUsdtAmount }}</span>
            </div>
          </div>
          <div class="text_Box">
            <div class="text_mall mall_img_two font_Size16">
              <div class="mall_Top">
                <span class="mall_Bg"></span>
                <span class="mall_Text">充值金额</span>
                <span class="mall_Data">今日</span>
              </div>
              <div class="mall_Bottom font_Size20">
                {{ recharge }}
              </div>
            </div>
            <div class="text_mall_tow mall_imgl_two">
              <span class="mall_Bottom_text">总充值金额：</span>
              <span class="mall_Bottom_num">{{ sumRecharge }}</span>
            </div>
          </div>
          <div class="text_Box">
            <div class="text_mall mall_img_three font_Size16">
              <div class="mall_Top">
                <span class="mall_Bg"></span>
                <span class="mall_Text">提现金额</span>
                <span class="mall_Data">今日</span>
              </div>
              <div class="mall_Bottom font_Size20">
                {{ withdraw }}
              </div>
            </div>
            <div class="text_mall_tow mall_imgl_three">
              <span class="mall_Bottom_text">提现总额：</span>
              <span class="mall_Bottom_num">{{ sumWithdraw }}</span>
            </div>
          </div>
          <div class="text_Box">
            <div class="text_mall mall_img_four font_Size16">
              <div class="mall_Top">
                <span class="mall_Bg"></span>
                <span class="mall_Text">收益</span>
                <span class="mall_Data">今日</span>
              </div>
              <div class="mall_Bottom font_Size20">
                {{ todayTotleIncome }}
              </div>
            </div>
            <div class="text_mall_tow mall_imgl_four">
              <span class="mall_Bottom_text">收益总额：</span>
              <span class="mall_Bottom_num">{{ totleIncome }}</span>
            </div>
          </div>
          <div class="text_Box">
            <div class="text_mall mall_img_five">
              <div class="text_Left_mn">
                <div class="mn_Top">
                  <span class="text_Five_bg"></span>
                  <span class="text_Five_sour">新增用户</span>
                </div>
                <div class="mn_Bottom font_Size20">{{ todayUserCount }}</div>
              </div>
              <div class="text_Middle_mn">
                <div class="mn_Top">
                  <span class="text_Five_bg"></span>
                  <span class="text_Five_sour">今日充值人数</span>
                </div>
                <div class="mn_Bottom font_Size20">
                  {{ todayRechargeUserCount }}
                </div>
              </div>
              <div class="text_Right_mn">今日</div>
            </div>
            <div class="text_mall_tow mall_imgl_five">
              <span class="mall_Bottom_text">总用户数：</span>
              <span class="mall_Bottom_num">{{ allUserCount }}</span>
            </div>
          </div>
        </div>
      </div>
      <div
        class="user_Box_main user_Bottom_box"
        v-if="isAuth('suer:user:tixingall')"
      >
        <div class="u_Top_tittle">
          {{ "待处理提醒" }}
          <span class="font_Size12 bg_Red">{{countNum(".user_user")}}</span>
        </div>
        <div class="u_Bottom_tittle">
          <div
            v-if="isAuth('suer:user:update')"
            class="text_Box2"
            @click="onRechange()"
          >
            <div class="text_moll">
              <img
                style="width: 30px; height: 40px"
                src="../../../assets/img/Vector_2.png"
                alt=""
              />
            </div>
            <div class="text_moll">
              <span class="font_Size14">充值订单</span>
              <span class="font_Size12 bg_Red">{{ countNum(".recharge_blockchain_order_untreated_cout") }}</span>
            </div>
          </div>
          <div
            v-if="isAuth('suer:user:tixian')"
            class="text_Box2"
            @click="onWithdraw()"
          >
            <div class="text_moll">
              <img
                style="width: 30px; height: 40px"
                src="../../../assets/img/Vector_1.png"
                alt=""
              />
            </div>
            <div class="text_moll">
              <span class="font_Size14">提现订单</span>
              <span class="font_Size12 bg_Red">{{ countNum(".withdraw_order_untreated_cout") }}</span>
            </div>
          </div>
          <div
            class="text_Box2"
            v-if="isAuth('suer:user:jichu')"
            @click="onUseBaseButton()"
          >
            <div class="text_moll">
              <img
                style="width: 42px; height: 40px"
                src="../../../assets/img/Vector_3.png"
                alt=""
              />
            </div>
            <div class="text_moll">
              <span class="font_Size14">用户基础认证</span>
              <span class="font_Size12 bg_Red">{{ countNum(".kyc_untreated_cout") }}</span>
            </div>
          </div>
          <div
            class="text_Box2"
            v-if="isAuth('suer:user:gaoji')"
            @click="onUseBaseButtonTwo()"
          >
            <div class="text_moll">
              <img
                style="width: 42px; height: 40px"
                src="../../../assets/img/Vector_4.png"
                alt=""
              />
            </div>
            <div class="text_moll">
              <span class="font_Size14">用户高级认证</span>
              <span class="font_Size12 bg_Red">{{ countNum(".kyc_high_level_untreated_cout") }}</span>
            </div>
          </div>
        </div>
      </div>
      <!-- <div class="user_Box_main user_Bottom_box">
      <div class="u_Top_tittle">订单</div>
      <echarts>
    </echarts>
    </div>
    <div class="user_Box_main user_Bottom_box">
      <div class="bottom_Left_rechats user_Box_main">
        <div class="u_Top_tittle">用户数</div>
        <echarts_two>
      </echarts_two>
      </div> 
      <div class="bottom_Right_rechats user_Box_main">
        <div class="u_Top_tittle">用户交易统计</div>
        <echarts_three>
      </echarts_three>
      </div>
    </div>1 -->
    </div>
  </div>
</template>

<script>
import { tableOption } from "@/crud/user/user";
import AddOrUpdate from "./user-add-or-update";
import echarts from "./echats_One.vue";
import echarts_two from "./echats_Two.vue";
import echarts_three from "./echats_Three.vue";
// import useBase from'./useBase.vue'
// import useBaseTwo from'./useBaseTwo.vue'
export default {
  name: "chartShow",
  data() {
    return {
      dataList: [],
      dataListLoading: false,
      waitCount: 0, //待处理事件总和
      dataListSelections: [],
      rechargeCount: "", //充值
      withdrawCount: "", //提现
      realNameAuthCount: "", //用户基础认证
      highLevelAuthCount: "", //用户高级认证
      allUserCount: "", //总用户数
      balanceAmount: "", //	充提差额
      recharge: "", //	充值金额
      sumRecharge: "", // 充值总额
      todayUsdtAmount: "", //今日USDT账户余额
      sumUsdtAmount: "", //	USDT账户余额
      todayRechargeUserCount: "", //	今日充值人数
      todayUserCount: "", //	今日新增
      todayTotleIncome: "", //	今日收益
      totleIncome: "", //总收益
      withdraw: "", //	提现金额
      sumWithdraw: "", //总提现额
      addOrUpdateVisible: false,
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
    };
  },
  components: {
    AddOrUpdate,
    echarts,
    echarts_two,
    echarts_three,
  },
  created() {
    this.getDataList(), this.getDataListTwo();
  },
  methods: {
    onUseBaseButton() {
      this.$router.push({ path: "/user-relation-basics" });
    },
    onUseBaseButtonTwo() {
      this.$router.push({ path: "/user-relation-senior" });
    },
    onRechange() {
      this.$router.push({ path: "/order-rechange" });
    },
    onWithdraw() {
      this.$router.push({ path: "/order-withdraw" });
    },
    // 待处理数据
    getDataList() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/home/waitCount"),
        method: "post",
        data: this.$http.adornData(),
      }).then(({ data }) => {
        if (data.code == 0) {
          (this.rechargeCount = data.data.rechargeCount), //充值
            (this.withdrawCount = data.data.withdrawCount), //提现
            (this.realNameAuthCount = data.data.realNameAuthCount), //用户基础认证
            (this.highLevelAuthCount = data.data.highLevelAuthCount); //用户高级认证
          this.waitCount =
            this.rechargeCount +
            this.withdrawCount +
            this.realNameAuthCount +
            this.highLevelAuthCount;
        } else {
          this.$message({
            message: data.msg,
            type: "error",
          });
        }
      });
    },
    // 今日数据统计
    getDataListTwo() {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/home/view"),
        method: "post",
        data: this.$http.adornData(),
      }).then(({ data }) => {
        if (data.code == 0) {
          console.log(data.data);
          console.log(data.data.allUserCount);
          (this.allUserCount = data.data.allUserCount), //总用户数
            (this.balanceAmount = data.data.balanceAmount), //	充提差额
            (this.sumRecharge = data.data.sumRecharge), //	总充值金额
            (this.recharge = data.data.recharge), //	充值金额
            (this.todayUsdtAmount = data.data.todayUsdtAmount), //	今日USDT账户余额
            (this.sumUsdtAmount = data.data.sumUsdtAmount), //	USDT账户余额
            (this.todayRechargeUserCount = data.data.todayRechargeUserCount), //	今日充值人数
            (this.todayUserCount = data.data.todayUserCount), //	今日新增
            (this.totleIncome = data.data.totleIncome), //	收益
            (this.todayTotleIncome = data.data.todayTotleIncome), //	今日收益
            (this.withdraw = data.data.withdraw); //	今日提现金额
          this.sumWithdraw = data.data.sumWithdraw; //	总提现金额
        } else {
          this.$message({
            message: data.msg,
            type: "error",
          });
        }
      });
    },
    // 新增 / 修改
    addOrUpdateHandle(id) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id);
      });
    },
    // 删除
    deleteHandle(id) {
      var ids = id
        ? [id]
        : this.dataListSelections.map((item) => {
            return item.userId;
          });
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/admin/user"),
            method: "delete",
            data: this.$http.adornData(ids, false),
          }).then(({ data }) => {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.getDataList(this.page);
              },
            });
          });
        })
        .catch(() => {});
    },
    // 条件查询
    searchChange(params, done) {
      this.getDataList(this.page, params, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
    countNum(name){
      let num = this.main.tips[name]
      return num;
    }
  },
};
</script>
<style lang="scss" scoped>
.mod-user {
  .user_Box_main {
    width: 100%;
    box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
    margin: 0 auto;
    border-radius: 10px;
    margin-bottom: 20px;
    margin-top: 30px;
  }
  .u_Top_tittle {
    width: 100%;
    height: 60px;
    font-size: 16px;
    line-height: 60px;
    font-weight: 500;
    padding-left: 2%;
    border-bottom: 1px solid #e6e6e6;
  }
  .u_Top_text {
    width: 100%;
    overflow: hidden;
    padding: 45px 0;
  }
  .u_Bottom_tittle {
    width: 60%;
    min-width: 800px;
    display: flex;
    margin-top: 56px;
  }
  .text_Box {
    float: left;
    width: 288px;
    margin: 0 0px 18px 0;
    padding: 0 10px;
  }
  .text_Box2 {
    float: left;
    width: 160px;
    margin: 0 50px 18px 0;
    padding: 0 10px;
  }
  .text_mall {
    display: block;
    height: 110px;
    border-radius: 5px 5px 0px 0px;
    padding: 0 10px;
    color: #fff;
  }
  .text_mall_tow {
    display: block;
    height: 50px;
    border-radius: 0px 0px 5px 5px;
    line-height: 50px;
    padding: 0 10px;
  }
  .mall_Bottom_text {
    float: left;
  }
  .mall_Bottom_num {
    float: right;
  }
  .mall_img {
    background: #ed8a9a;
  }
  .mall_img_two {
    background: #87c1fa;
  }
  .mall_img_three {
    background: #7890f7;
  }
  .mall_img_four {
    background: #80d4ce;
  }
  .mall_img_five {
    background: #eba994;
  }
  .mall_imgl {
    background: #ffe6ea;
    color: #47121a;
  }
  .mall_imgl_two {
    background: #e1f0ff;
    color: #0a1e32;
  }
  .mall_imgl_three {
    background: #e2e8ff;
    color: #10183d;
  }
  .mall_imgl_four {
    background: #d8fffc;
    color: #0b322f;
  }
  .mall_imgl_five {
    background: #ffeae3;
    color: #2f130b;
  }
  .mall_Top {
    height: 40px;
    color: #fff;
    font-size: 14px;
  }
  .mall_Bg {
    float: left;
    background: url(../../../assets/img/us.png) center no-repeat;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background-size: 100%;
    margin-top: 15px;
  }
  .mall_Text {
    float: left;
    margin: 17px 0 0 6px;
  }
  .mn_Top {
    height: 40px;
  }
  .mn_Bottom {
    margin-top: 21px;
  }
  .mall_Data {
    float: right;
    margin-top: 17px;
  }
  .mall_Bottom {
    margin-top: 20px;
  }
  .mn_Bottom {
    max-width: 100px;
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .text_Left_mn {
    float: left;
    height: 110px;
    max-width: 100px;
    text-overflow: ellipsis;
    overflow: hidden;
  }
  .text_Middle_mn {
    float: left;
    height: 110px;
    margin-left: 10px;
  }
  .text_Right_mn {
    float: right;
    height: 110px;
    padding-top: 17px;
  }
  .text_Five_bg {
    float: left;
    background: url(../../../assets/img/imgswt.png) center no-repeat;
    width: 20px;
    height: 20px;
    border-radius: 50%;
    background-size: 100%;
    margin-top: 15px;
  }
  .text_Five_sour {
    float: left;
    margin: 17px 0 0 6px;
  }
  .text_moll {
    display: block;
    margin-bottom: 20px;
    text-align: center;
    height: 40px;
  }
  .font_Size16 {
    font-size: 16px;
  }
  .font_Size20 {
    font-size: 20px;
  }
  .green {
    color: #29be89;
  }
  .red {
    color: red;
  }
  .font_Size14 {
    font-size: 14px;
  }
  .font_Size12 {
    font-size: 12px;
  }
  .bg_Red {
    color: #f2f7ff;
    background: #e05561;
    border-radius: 79px;
    padding: 5px;
    margin-left: 5px;
  }
  .bottom_Left_rechats {
    width: 68%;
    height: 490px;
    float: left;
  }
  .bottom_Right_rechats {
    width: 30%;
    height: 490px;
    float: right;
  }
}
</style>
