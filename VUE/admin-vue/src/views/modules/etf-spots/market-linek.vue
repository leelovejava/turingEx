<template>
  <el-dialog
    title="当天分时行情K线设置"
    :close-on-click-modal="false"
    :visible.sync="visible"
    width="1200px"
    @close="handClose"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <el-form-item prop="">
        <div class="green">请先生成K线，才能保存行情配置</div>
      </el-form-item>

      <el-row>
        <el-col :span="7">
          <el-form-item label="开始时间" prop="openTimeTs">
            <el-date-picker
              v-model="dataForm.openTimeTs"
              type="date"
              value-format="yyyy-MM-dd"
              :picker-options="startPickerOptions"
              @change="handleStartTimeChange"
              placeholder="选择日期时间"
            >
            </el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="7">
          <el-form-item label="结束时间" prop="closeTimeTs">
            <el-date-picker
              v-model="dataForm.closeTimeTs"
              disabled
              type="date"
              value-format="yyyy-MM-dd"
              :picker-options="endPickerOptions"
              placeholder="选择日期时间"
            >
            </el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="交易对" prop="symbol">
            <el-select
              v-model="options.symbol"
              placeholder="请选择"
              @change="changeVal()"
            >
              <el-option
                v-for="item in options"
                :key="item.symbol"
                :label="item.symbol"
                :value="item.symbol"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="">
            <el-button type="primary" @click="dataFormSubmitOne()"
              >生成K线</el-button
            >
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="7">
          <el-form-item label="今日开盘价" prop="openPrice">
            <el-input
              v-model="dataForm.openPrice"
              placeholder="今日开盘价"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="7">
          <el-form-item label="今日收盘价" prop="closePrice">
            <el-input
              v-model="dataForm.closePrice"
              placeholder="今日收盘价"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="控盘策略">
            <el-select
              v-model="langug.value"
              placeholder="请选择"
              @change="changeVal()"
            >
              <el-option
                v-for="item in langug"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="7">
          <el-form-item label="今日最低价格" prop="low">
            <el-input
              v-model="dataForm.low"
              placeholder="今日最低价格"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="7">
          <el-form-item label="今日最高价格" prop="high">
            <el-input
              v-model="dataForm.high"
              placeholder="今日最高价格"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="5">
          <el-form-item label="今日总成交量" prop="turnoverLow">
            <el-input
              v-model="dataForm.turnoverLow"
              placeholder="最低"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="1">
          <el-form-item label="" prop="">
            <el-input disabled placeholder="~" class="spann"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="5">
          <el-form-item label="" prop="turnoverHigh">
            <el-input
              v-model="dataForm.turnoverHigh"
              placeholder="最高"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="" prop="">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" v-if="isShow" @click="dataFormSubmit()"
          >保存</el-button
        >
      </el-form-item>
    </el-form>

    <div class="mod-subscribe-general">
      <avue-crud
        ref="crud"
        :page.sync="page"
        :data="dataList"
        :option="tableOption"
        @on-load="getDataList"
      >
        <!-- <template slot="menuLeft">
          <el-button
            type="primary"
            icon="el-icon-plus"
            size="small"
            v-if="isAuth('sys:user:save')"
            @click.stop="addOrUpdateHandle(options)"
            >新增</el-button
          >
        </template> -->
        <template slot-scope="scope" slot="openTimeTss">
          <span>{{ formatTimestamm(scope.row.openTimeTs) }}</span>
        </template>
        <template slot-scope="scope" slot="closeTimeTss">
          <span>{{ formatTimestamm(scope.row.closeTimeTs) }}</span>
        </template>
        <template slot-scope="scope" slot="menu">
          <!-- <el-button
            type="primary"
            icon="el-icon-edit"
            size="small"
            v-if="isAuth('sys:user:delete')"
            @click.stop="addOrUpdateHandle(options,scope.row)"
            >编辑</el-button
          > -->
          <el-button
            type="danger"
            icon="el-icon-delete"
            size="small"
            @click.stop="deleteHandle(scope.row.uuid)"
            >删除</el-button
          >
        </template>
      </avue-crud>
      <!-- 新增删除 -->
      <linKeAdd
        v-if="linKeAddVisible"
        ref="linKeAdd"
        @refreshDataList="getDataList"
      ></linKeAdd>
    </div>
    <echarts ref="addOrUpdate"> </echarts>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="visible = false">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { tableOption } from "@/crud/etf-spots/market-linek";
import linKeAdd from "./linKen-add-or-undata";
import { treeDataTranslate } from "@/utils";
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
import echarts from "./echats_One.vue";
export default {
  data() {
    return {
      visible: false,
      linKeAddVisible: false,
      menuList: [],
      dateRange: [],
      isShow: false,
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      startPickerOptions: {
        disabledDate: this.disabledStartDate, // 禁用不满足条件的开始日期
        disabledHours: this.disabledStartHours, // 禁用不满足条件的开始小时
        disabledMinutes: this.disabledStartMinutes, // 禁用不满足条件的开始分钟
        shortcuts: [
          {
            text: "明天",
            onClick: () => {
              const tomorrow = new Date();
              tomorrow.setDate(tomorrow.getDate() + 1);
              tomorrow.setHours(4, 0, 0, 0);
              this.dataForm.openTimeTs = tomorrow;
              this.handleStartTimeChange();
            },
          },
        ],
      },
      endPickerOptions: {
        disabledDate: this.disabledEndDate, // 禁用不满足条件的结束日期
      },
      tableOption: tableOption,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataList: [],
      row: "", //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
      options: [],
      linArr: [], //K线数组
      timeArr: [],
      langug: [
        {
          label: "跟随大盘",
          value: "1",
        },
        {
          label: "自定义趋势",
          value: "2",
        },
      ], // 控盘策略
      dataForm: {
        closePrice: "",
        closeTimeTs: "",
        createBy: "",
        createDate: "",
        createTime: "",
        high: "",
        low: "",
        openPrice: "",
        openTimeTs: "",
        robot_model_uuid: "",
        strategy: "",
        symbol: "",
        turnoverHigh: "",
        turnoverLow: "",
        updateBy: "",
        updateDate: "",
        updateTime: "",
        uuid: "",
      },
      dataRule: {
        openTimeTs: [
          { required: true, message: "开始时间不能为空", trigger: "blur" },
        ],
        closeTimeTs: [
          { required: true, message: "结束时间不能为空", trigger: "blur" },
        ],
        openPrice: [
          { required: true, message: "今日开盘价不能为空", trigger: "blur" },
        ],
        closePrice: [
          { required: true, message: "今日收盘价不能为空", trigger: "blur" },
        ],
        low: [
          { required: true, message: "今日最低价不能为空", trigger: "blur" },
        ],
        high: [
          { required: true, message: "今日最高价不能为空", trigger: "blur" },
        ],
        turnoverLow: [
          { required: true, message: "最低不能为空", trigger: "blur" },
        ],
        turnoverHigh: [
          { required: true, message: "最高不能为空", trigger: "blur" },
        ],
        quoteCurrency: [
          { required: true, message: "结算币种不能为空", trigger: "blur" },
        ],
        unitFee: [
          { required: true, message: "手续费不能为空", trigger: "blur" },
        ],
        // sorted: [{ required: true, message: "排序不能为空", trigger: "blur" }],
        // sorted: [{ required: true, message: "排序不能为空", trigger: "blur" }],
        // sorted: [{ required: true, message: "排序不能为空", trigger: "blur" }],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  components: {
    echarts,
    linKeAdd,
  },
  methods: {
    init(arr) {
      this.options = arr || [];
      this.options.symbol = this.options[0].symbol;
      this.langug.value = this.langug[0].value;
      this.visible = true;
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    addOrUpdateHandle(arr, row) {
      this.linKeAddVisible = true;
      this.$nextTick(() => {
        this.$refs.linKeAdd.init(arr, row);
      });
    },
    disabledStartDate(time) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return time.getTime() <= today.getTime(); // 开始时间只能选择明天及以后的日期
    },
    disabledStartHours() {
      const currentHour = new Date().getHours();
      return Array.from({ length: 24 }, (_, i) => i).filter(
        (hour) => hour < 4 || hour > 21 || (hour === 21 && currentHour >= 30) // 开始时间的小时部分只能选择凌晨4点到晚上21点半
      );
    },
    disabledStartMinutes(hour) {
      const currentHour = new Date().getHours();
      if (hour === 4 && currentHour < 30) {
        // 如果选择的小时是4点且当前小时小于30，则禁用分钟选择
        return Array.from({ length: 60 }, (_, i) => i).filter(
          (minute) => minute !== 0
        );
      } else if (hour === 21 && currentHour >= 30) {
        // 如果选择的小时是21点且当前小时大于等于30，则禁用分钟选择
        return Array.from({ length: 60 }, (_, i) => i).filter(
          (minute) => minute !== 0
        );
      } else {
        // 其他情况不禁用分钟选择
        return [];
      }
    },
    disabledEndDate(time) {
      return true; // 禁用结束时间选择
    },
    handleStartTimeChange() {
      // 设置默认的结束时间为开始时间的后一天
      const startDate = new Date(this.dataForm.openTimeTs);
      const endDate = new Date(startDate.getTime() + 8.64e7); // 一天的毫秒数
      this.dataForm.closeTimeTs = endDate;
    },
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
      this.langug.value = "";
      this.options.value = "";
      this.isShow = false;
    },
    // 删除
    deleteHandle(id) {
      var ids = id
        ? [id]
        : this.dataListSelections.map((item) => {
            return item.orderId;
          });
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl(`/etf/klineConfig/delete?ids=` + ids),
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
    // 获取数据列表
    getDataList(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/etf/klineConfig/pageList"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              type: "indices", //type:'forex', //forex->外汇,commodities->大宗商品，指数/ETF->indices, A-stocks->A股, HK-stocks->港股.US-stocks->美股，cryptos->虚拟货币
              // itemId: this.dataForm.id,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            params
          )
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          console.log("data => " + data);
          this.dataList = data.data.records;
          this.page.total = data.data.total;
        } else {
          this.$message({
            message: data.msg,
            type: "error",
            duration: 1000,
            onClose: () => {},
          });
        }

        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },

    formatTimestamp(timestamp) {
      const date = new Date(timestamp);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");
      const seconds = String(date.getSeconds()).padStart(2, "0");
      const milliseconds = String(date.getMilliseconds()).padStart(3, "0");

      const formattedDate = `${year}-${month}-${day} ${hours}:${minutes}:${seconds} ${milliseconds}`;
      return formattedDate;
    },
    formatTimestamm(timestamp) {
      const date = new Date(timestamp);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");
      const seconds = String(date.getSeconds()).padStart(2, "0");
      const milliseconds = String(date.getMilliseconds()).padStart(3, "0");

      const formattedDate = `${year}-${month}-${day}`;
      return formattedDate;
    },
    // K线
    dataFormSubmitOne: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        let a = new Date(this.dataForm.openTimeTs);
        let b = new Date(this.dataForm.closeTimeTs);
        let aa = a.getTime();
        let bb = b.getTime();
        if (valid) {
          this.$http({
            // 生成K线
            url: this.$http.adornUrl(`/etf/klineConfig/init`),
            method: "post",
            data: this.$http.adornData({
              closePrice: this.dataForm.closePrice,
              // closeTimeTs: bb,
              high: this.dataForm.high,
              low: this.dataForm.low,
              openPrice: this.dataForm.openPrice,
              openTimeTs: aa,
              symbol: this.options.symbol,
              turnoverHigh: this.dataForm.turnoverHigh,
              turnoverLow: this.dataForm.turnoverLow,
              strategy: this.langug.value,
              // uuid: ""
            }),
          }).then(({ data }) => {
            if (data.code == 0) {
              this.dataForm.uuid = data.data.modelId;
              this.timeArr = Object.values(
                data.data.klineList.reduce((res, item) => {
                  //res[item.ts] = [item.ts];
                  res[item.ts] = [this.formatTimestamp(item.ts)];
                  return res;
                }, [])
              );
              this.linArr = Object.values(
                data.data.klineList.reduce((res, item) => {
                  let arr = [item.open, item.close, item.low, item.high];
                  res.push(arr);
                  return res;
                }, [])
              );
              this.$nextTick(() => {
                this.$refs.addOrUpdate.init(this.timeArr, this.linArr);
              });
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {},
              });
              this.isShow = true;
            } else {
              this.$message({
                message: data.msg,
                type: "error",
                duration: 1500,
                onClose: () => {},
              });
            }
          });
        }
      });
    }),

    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        let a = new Date(this.dataForm.openTimeTs);
        let b = new Date(this.dataForm.closeTimeTs);
        let aa = a.getTime();
        let bb = b.getTime();
        if (valid) {
          this.$http({
            // 修改
            url: this.$http.adornUrl(`/etf/klineConfig/save`),
            method: "post",
            data: this.$http.adornData({
              closePrice: this.dataForm.closePrice,
              // closeTimeTs: bb,
              high: this.dataForm.high,
              low: this.dataForm.low,
              openPrice: this.dataForm.openPrice,
              openTimeTs: aa,
              symbol: this.options.symbol,
              turnoverHigh: this.dataForm.turnoverHigh,
              turnoverLow: this.dataForm.turnoverLow,
              strategy: this.langug.value,
              robot_model_uuid: this.dataForm.uuid,
            }),
          }).then(({ data }) => {
            if (data.code == 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.getDataList();
                  this.$emit("refreshDataList");
                },
              });
            } else {
              this.$message({
                message: data.msg,
                type: "error",
                duration: 1500,
                onClose: () => {},
              });
            }
          });
        }
      });
    }),
  },
};
</script>
<style scoped>
.spann {
  width: 40px;
  margin-left: -60px;
}
</style>
