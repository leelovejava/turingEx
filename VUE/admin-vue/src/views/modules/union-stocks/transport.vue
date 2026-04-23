<template>
  <div class="mod-etf-transport">
    <el-tabs v-model="stockName" @tab-click="stockHandleClick">
      <el-tab-pane v-if="isAuth('union-stocks:us')" name="US-stocks">
        <span slot="label">
          美股
          <el-badge :value="countNum('.us-spots-us-transport')" v-if="countNum('.us-spots-us-transport') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:hk')" name="HK-stocks">
        <span slot="label">
          港股
          <el-badge :value="countNum('.hk-stocks-transport')" v-if="countNum('.hk-stocks-transport') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:tw')" name="TW-stocks">
        <span slot="label">
          台股
          <el-badge :value="countNum('.tw-stocks-transport')" v-if="countNum('.tw-stocks-transport') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:a')" name="A-stocks">
        <span slot="label">
          A股
          <el-badge :value="countNum('.A-stocks-transport')" v-if="countNum('.A-stocks-transport') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:jp')" name="JP-stocks">
        <span slot="label">
          日股
          <el-badge :value="countNum('.jp-stocks-transport')" v-if="countNum('.jp-stocks-transport') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:india')" name="INDIA-stocks">
        <span slot="label">
          印度股
          <el-badge :value="countNum('.INDIA-stocks-transport')" v-if="countNum('.INDIA-stocks-transport') > 0"></el-badge>
        </span>
      </el-tab-pane>
      <el-tab-pane v-if="isAuth('union-stocks:uk')" name="UK-stocks">
        <span slot="label">
          英股
          <el-badge :value="countNum('.UK-stocks-transport')" v-if="countNum('.UK-stocks-transport') > 0"></el-badge>
        </span>
      </el-tab-pane>
    </el-tabs>
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="当前单" name="1"></el-tab-pane>
      <el-tab-pane label="历史单" name="2"></el-tab-pane>
    </el-tabs>
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="activeName === '1' ? tableOptionCurrent : tableOptionHistory"
      :cell-class-name="addClasscolor"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @refresh-change="refreshChange"
    >
      <template slot-scope="scope" slot="userNamesolt">
        <span @click="searchName(scope.row.userName)" class="seachButton">{{
          scope.row.userName
        }}</span>
      </template>
      <!-- （剩余/委托金额） -->
      <template slot-scope="scope" slot="vove">
        <span class="green">{{ scope.row.volumeUnitAmount }}</span
        >/<span>{{ scope.row.volumeOpenUnitAmount }}</span>
      </template>
      <template slot-scope="scope" slot="plost">
        <span :class="scope.row.profitLoss * 1 >= 0 ? isgreen : isred">{{
          scope.row.profitLoss
        }}</span>
      </template>
      <!-- （剩余/委托保证金） -->
      <template slot-scope="scope" slot="depo">
        <span class="green">{{ scope.row.deposit }}</span
        >/<span>{{ scope.row.depositOpen }}</span>
      </template>
      <template slot-scope="scope" slot="closTime">
        <span v-if="scope.row.closeTime">{{
          new Date(scope.row.closeTime * 1000).toLocaleString()
        }}</span>
      </template>
      <template slot="menuLeft">
        <div class="cheBoxMain" v-if="activeName == 1">
          <div style="margin-bottom: 10px">调整行情</div>
          <div class="chekClect">
            <el-select
              v-model="dataForm.symbol"
              clearable
              placeholder="请选择币种"
            >
              <el-option
                v-for="item in option[this.stockName]"
                :key="item.symbol"
                :label="item.name"
                :value="item.symbol"
              >
              </el-option>
            </el-select>
          </div>
          <div class="chekClect" style="margin-top: 3px">
            <el-button
              type="primary"
              icon="el-icon-edit"
              size="small"
              v-if="isAuth('union-spots:operate')"
              @click="addOrUpdateHandle(dataForm.symbol)"
              >调整</el-button
            >
          </div>
        </div>
      </template>
      <template slot="menuLeft">
        <div class="main">
          <el-tabs
            v-if="activeName == '2'"
            v-model="activeName2"
            @tab-click="handleClick2"
          >
            <el-tab-pane label="全部" name="1"></el-tab-pane>
            <el-tab-pane label="持仓" name="2"></el-tab-pane>
            <el-tab-pane label="已平仓" name="3"></el-tab-pane>
          </el-tabs>
        </div>
      </template>
      <template
        slot-scope="scope"
        slot="menu"
        v-if="scope.row.state == 'submitted'"
      >
        <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          v-if="isAuth('union-spots:operate')"
          @click.stop="deleteHandle(scope.row.orderNo)"
          >平仓</el-button
        >
      </template>
    </avue-crud>
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="refreshChange"
    ></add-or-update>
    <el-dialog
      title="完整用户名(完整钱包地址)"
      :close-on-click-modal="false"
      :visible.sync="userNamevisible"
    >
      <el-form :model="dataForm" ref="dataForm">
        <el-form-item label="" prop="">
          <div style="font-size: 20px">
            <span>{{ username }}</span
            ><a
              class="seachButton"
              target="_blank"
              :href="'https://etherscan.io/address/' + username"
              >在Etherscan上查看</a
            >
          </div>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="userNamevisible = false">取消</el-button>
        <el-button type="primary" @click="userNamevisible = false"
          >确定</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { tableOption as tableOptionCurrent } from "@/crud/shop/transportm";
import { tableOption as tableOptionHistory } from "@/crud/shop/transportHistory";
import AddOrUpdate from "./transport-add-or-update";
import { getStockName } from "./config";
import { isAuth } from '@/utils'
export default {
  data() {
    return {
      dateRange: [],
      option: {},
      dataForm: {
        transName: "",
      },
      state: "submitted",
      activeName: "1", //选项卡
      activeName2: "1",
      dataList: [],
      username: "",
      userNamevisible: false,
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      timer: null, // 定时器变量
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      tableOptionCurrent: tableOptionCurrent,
      tableOptionHistory: tableOptionHistory,
      stocksMap:{}
    };
  },
  components: {
    AddOrUpdate,
  },
  created() {
    this.stockName = getStockName();
    if(this.$store.state.common.stocksValue && this.$store.state.common.stocksValue !=""){
      this.stockName = this.$store.state.common.stocksValue;
      this.$store.commit("common/updateStocksValue", "")
    }
    let arr = ["union-stocks:us","union-stocks:hk","union-stocks:tw","union-stocks:a","union-stocks:jp","union-stocks:india","union-stocks:uk"];
    let name = ["US-stocks","HK-stocks","TW-stocks","A-stocks","JP-stocks","INDIA-stocks","UK-stocks"];

    for(let i=0 ; i < arr.length ; i++){
      if(isAuth(arr[i])){
        this.getAction(name[i]);
      }
    }
    this.stocksMap = {};
  },
  mounted() {
    this.$bus.$on('update-transport', (data)=>{
      this.stockName = data;
    })
  },
  // 在页面销毁前销毁定时器
  beforeDestroy() {
    this.$bus.$off('update-transport')
    this.stopTimer();
  },
  // mounted() {
  //   this.startTimer();
  // },
  beforeRouteLeave(to, from, next) {
    console.log('停止定时器')
    // 在路由离开之前停止定时器
    this.stopTimer();
    next();
  },
  beforeRouteEnter(to, from, next) {
    console.log('启动定时器')
    // 在路由进入之前执行，通过 next 回调传递实例给回调函数
    next(vm => {
      vm.startTimer(); // 通过回调函数重新启动定时器
    });
  },
  computed: {},
  methods: {
    // 启动定时器
    startTimer() {
      this.timer = setInterval(() => {
        this.getDataList(this.page);
      }, 5000); // 5秒钟执行一次
    },
    // 停止定时器
    stopTimer() {
      clearInterval(this.timer);
    },
    addClasscolor({ column, row }) {
      //表单样式
      if (
        (column.property === "direction" && row.direction == "buy") ||
        (column.property === "profitLoss" && row.profitLoss * 1 > 0) ||
        (column.property === "roleName" && row.roleName == "MEMBER") ||
        (column.property === "volume" && row.volume * 1 >= 0) ||
        (column.property === "vnvu" && row.vnvu) ||
        (column.property === "dd" && row.dd)
      ) {
        return "green";
      } else if (
        (column.property === "direction" && row.direction == "sell") ||
        (column.property === "profitLoss" && row.profitLoss * 1 < 0)
      ) {
        return "red";
      } else if (column.property === "roleName" && row.roleName == "GUEST") {
        return "yellow";
      }
    },
    // 获取数据列表
    getDataList(page, params, done) {
      page = page === undefined ? this.page : page;
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl(
          "/normal/adminContractOrderAction!/list.action"
        ),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              type: this.stockName, //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
              userName: this.dataForm.orderNumber2,
              orderNo: this.dataForm.orderNumber,
              roleName: this.dataForm.value,
              state: this.state,
              // startTime: this.dateRange === null ? null : this.dateRange[0], // 开始时间
              // endTime: this.dateRange === null ? null : this.dateRange[1], // 结束时间
            },
            params
          ),
          false
        ),
      }).then(({ data }) => {
        this.dataList = data.data.records;
        this.page.total = data.data.total;
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    searchName(name) {
      this.username = name;
      this.userNamevisible = true;
    },
    // 清除数据
    clearDatas() {
      this.dataForm = {};
      this.dateRange = [];
    },
    // 每页数
    sizeChangeHandle(val) {
      this.page.pageSize = val;
      this.page.currentPage = 1;
      this.getDataList(this.page);
    },
    // 当前页
    currentChangeHandle(val) {
      this.page.currentPage = val;
      this.getDataList(this.page);
    },
    // 选项卡
    handleClick(tab, event) {
      //console.log(this.activeName)
      if (this.activeName == 1) {
        this.state = "submitted";
        this.startTimer()
      } else {
        this.state = "created";
        this.stopTimer()
      }
      this.getDataList();
    },
    handleClick2(tab, event) {
      //console.log(this.activeName2)
      if (this.activeName2 == 1) {
        this.state = "";
      } else if (this.activeName2 == 2) {
        this.state = "submitted";
      } else {
        this.state = "created";
      }
      this.getDataList();
    },
    // 新增 / 修改
    addOrUpdateHandle(id) {
      if (this.dataForm.symbol) {
        this.addOrUpdateVisible = true;
        this.$nextTick(() => {
          this.$refs.addOrUpdate.init(id);
        });
      } else {
        this.$message({
          message: "请先选择币种",
          type: "error",
        });
      }
    },
    // 平仓
    deleteHandle(orderNo) {
      this.$confirm("您确定要进行平仓操作吗", "是否确认平仓", {
        //系统管理用户是否已绑定
        distinguishCancelAndClose: true,
        confirmButtonText: "取消",
        cancelButtonText: "平仓",
        type: "success",
      })
        .then(() => {})
        .catch((action) => {
          if (action === "cancel") {
            this.$http({
              url: this.$http.adornUrl(
                "/normal/adminContractOrderAction!/close.action"
              ),
              method: "get",
              params: this.$http.adornParams({
                orderNo: orderNo,
              }),
            }).then(({ data }) => {
              this.$message({
                message: "平仓成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.getDataList();
                },
              });
            });
          } else {
          }
        });
    },
    //行情数据
    getAction(name) {
      this.$http({
        url: this.$http.adornUrl("/normal/adminItemAction!/list"),
        method: "get",
        params: this.$http.adornParams({
          type: name, //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
          current: 1,
          size: 1000000
        }),
      }).then(({ data }) => {
        if (data.data.records) {
          this.option[name] = data.data.records.map((item, index) => {
            return Object.assign({}, { symbol: item.symbol, name: item.name });
          });
        }
      });
    },
    // 条件查询
    searchChange(params, done) {
      this.getDataList(this.page, params, done);
    },
    // 刷新回调用
    refreshChange() {
      this.page = this.$refs.crud.$refs.tablePage.defaultPage;
      this.getDataList(this.page);
      this.dataListSelections = [];
      this.$refs.crud.selectClear();
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
    stockHandleClick(tab, event){

      // if(this.stocksMap[tab.name]!=1){
      //   this.stocksMap[tab.name]==1
      //   this.getAction();
      // }
      
      this.page.currentPage = 1; // 重置当前页为第一页
      this.getDataList();

    },
    countNum(type){
      if(!this.menuMap){
        // console.log(name + "-->");
        return 0;
      }
      // let type = this.menuMap[name];
      let num = this.main.tips[type]
      if(isNaN(num)){
        num = 0
      }
      // console.log(name + "-->" + type + "-->" + num);
      return num;
    },
  },
};
</script>
<style></style>
<style lang="scss" scoped>
.mod-etf-transport {
  .cheBoxMain {
    overflow: hidden;
    padding: 50px 20px;
  }
  .chekClect {
    float: left;
    margin-right: 20px;
  }
}
.isred {
  width: 20px;
  height: 10px;
  color: white;
  background: red;
  display: block;
}
.isgreen {
  width: 20px;
  height: 10px;
  color: white;
  background: green;
  display: block;
}
</style>
