<template>
  <div class="mod-role">
    <!-- tag页 -->
    <!-- <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="ETF" name="1"></el-tab-pane>
      <el-tab-pane label="数字货币" name="2"></el-tab-pane>
      <el-tab-pane label="外汇" name="3"></el-tab-pane>
      <el-tab-pane label="美股" name="4"></el-tab-pane>
    </el-tabs> -->

    <!-- 搜索 -->
    <!-- <el-form :inline="true"
             :model="dataForm"
             @keyup.enter.native="getDataList(this.page)">
      
      <el-form-item label="用户名/UID:">
        <el-input v-model="dataForm.orderNumber2"
                  placeholder="用户名/UID"
                  clearable></el-input>
      </el-form-item>

      <el-form-item label="起始时间:">
        <el-date-picker v-model="dateRange"
                        type="datetimerange"
                        range-separator="至"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        start-placeholder="开始日期"
                        end-placeholder="结束日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary"
                   icon="el-icon-search"
                   size="small"
                   @click="getDataList()">查询</el-button>
        <el-button @click="clearDatas()"
                   size="small">清空</el-button>
      </el-form-item>
    </el-form> -->

    <!-- ETF -->
    <el-form :inline="true"
             :model="dataForm"
             @keyup.enter.native="getDataList(this.page)">
    <el-row>
     <el-col :span = '5'>
      <el-form-item label="开始时间">
        <el-date-picker
        class="speacPick"
      v-model="startTime"
      type="datetime"
      valueFormat="yyyy-MM-dd HH:mm:ss"
      placeholder="选择开始时间">
    </el-date-picker>
      </el-form-item></el-col>
     <el-col :span = '5'>
      <el-form-item  label="结束时间">
        <el-date-picker
        class="speacPick"
      v-model="endTime"
      type="datetime"
      valueFormat="yyyy-MM-dd HH:mm:ss"
      placeholder="选择结束时间">
    </el-date-picker>
      </el-form-item></el-col>
     <el-col :span = '7'>
      <el-form-item >
        <el-button type="primary"
                   icon="el-icon-search"
                   size="small"
                   @click="getDataList()">查询</el-button>
      </el-form-item></el-col>
    </el-row>
  </el-form> 
      
      
    <avue-crud
      ref="crud"
      :data="dataAllList"
      :option="tableOption01"
      @search-change="searchChange"
      @selection-change="selectionChange"
      :cell-class-name="addClasscolor"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <div class="main">
          <el-tabs v-model="activeName2" @tab-click="handleClick2">
            <el-tab-pane label="当天" name="1"></el-tab-pane>
            <el-tab-pane label="当周" name="2"></el-tab-pane>
            <el-tab-pane label="当月" name="3"></el-tab-pane>
            <el-tab-pane label="全部" name="4"></el-tab-pane>
          </el-tabs>
        </div>
        <div class="speaDiv">数据汇总</div>
      </template>
    </avue-crud>
    <!-- 数字货币 -->
    <!-- <avue-crud ref="crud" v-if="activeName == '2'"
            :page.sync="page"
            :data="dataList"
            :option="tableOption01"
            @search-change="searchChange"
            @selection-change="selectionChange"
            @on-load="getDataList">
    </avue-crud> -->
    <!-- 外汇 -->
    <!-- <avue-crud ref="crud" v-if="activeName == '3'"
            :page.sync="page"
            :data="dataList"
            :option="tableOption01"
            @search-change="searchChange"
            @selection-change="selectionChange"
            @on-load="getDataList">
    </avue-crud> -->
    <!-- 美股 -->
    <!-- <avue-crud ref="crud" v-if="activeName == '4'"
            :page.sync="page"
            :data="dataList"
            :option="tableOption01"
            @search-change="searchChange"
            @selection-change="selectionChange"
            @on-load="getDataList">
    </avue-crud> -->

    <!-- ETF 2 -->
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption11"
      @search-change="searchChange"
      @selection-change="selectionChange"
      :cell-class-name="addClasscolor"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <div class="speaDiv">查询结果</div>
      </template>
    </avue-crud>

    <!-- 数字货币 2 -->
    <!-- <avue-crud ref="crud" v-if="activeName2 == '2'"
          :page.sync="page"
          :data="dataList"
          :option="tableOption11"
          @search-change="searchChange"
          @selection-change="selectionChange"
          @on-load="getDataList">

      <template slot="menuLeft">
        <div class="main">
          <el-tabs v-model="activeName2" @tab-click="handleClick2">
            <el-tab-pane label="当天" name="1"></el-tab-pane>
            <el-tab-pane label="当周" name="2"></el-tab-pane>
            <el-tab-pane label="当月" name="3"></el-tab-pane>
            <el-tab-pane label="全部" name="4"></el-tab-pane>
          </el-tabs>
        </div>
      </template>

    </avue-crud> -->

    <!-- 外汇 2 -->
    <!-- <avue-crud ref="crud" v-if="activeName2 == '3'"
          :page.sync="page"
          :data="dataList"
          :option="tableOption11"
          @search-change="searchChange"
          @selection-change="selectionChange"
          @on-load="getDataList">

      <template slot="menuLeft">
        <div class="main">
          <el-tabs v-model="activeName2" @tab-click="handleClick2">
            <el-tab-pane label="当天" name="1"></el-tab-pane>
            <el-tab-pane label="当周" name="2"></el-tab-pane>
            <el-tab-pane label="当月" name="3"></el-tab-pane>
            <el-tab-pane label="全部" name="4"></el-tab-pane>
          </el-tabs>
        </div>
      </template>

    </avue-crud> -->

    <!-- 美股 2 -->
    <!-- <avue-crud ref="crud" v-if="activeName2 == '4'"
          :page.sync="page"
          :data="dataList"
          :option="tableOption11"
          @search-change="searchChange"
          @selection-change="selectionChange"
          @on-load="getDataList">

      <template slot="menuLeft">
        <div class="main">
          <el-tabs v-model="activeName2" @tab-click="handleClick2">
            <el-tab-pane label="当天" name="1"></el-tab-pane>
            <el-tab-pane label="当周" name="2"></el-tab-pane>
            <el-tab-pane label="当月" name="3"></el-tab-pane>
            <el-tab-pane label="全部" name="4"></el-tab-pane>
          </el-tabs>
        </div>
      </template>

    </avue-crud> -->
    <!-- 弹窗, 新增 / 修改 -->
    <!-- <add-or-update v-if="addOrUpdateVisible"
                   ref="addOrUpdate"
                   @refreshDataList="getDataList"></add-or-update> -->
  </div>
</template>

<script>
import { tableOption01 } from "@/crud/report/all-filling0-1";
// import { tableOption02 } from '@/crud/report/all-filling0-2'
// import { tableOption03 } from '@/crud/report/all-filling0-3'
// import { tableOption04 } from '@/crud/report/all-filling0-4'

import { tableOption11 } from "@/crud/report/all-filling1-1";
// import { tableOption12 } from '@/crud/report/all-filling1-2'
// import { tableOption13 } from '@/crud/report/all-filling1-3'
// import { tableOption14 } from '@/crud/report/all-filling1-4'

import AddOrUpdate from "./all-filling-add-or-update";
export default {
  data() {
    return {
      dateRange: [],
      dataAllList: [],
      dataList: [],
      startTime:'',
      endTime:'',
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      tableOption01: tableOption01,
      // tableOption02: tableOption02,
      // tableOption03: tableOption03,
      // tableOption04: tableOption04,
      tableOption11: tableOption11,
      // tableOption12: tableOption12,
      // tableOption13: tableOption13,
      // tableOption14: tableOption14,
      activeName: "1", //选项卡
      activeName2: "4",
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataForm: {},
      searchParams: {}, // 搜索条件
    };
  },
  components: {
    AddOrUpdate,
  },
  methods: {
    // 获取数据列表
    getDataList(page,params, done) {
       let patTime
       if(this.startTime){
        patTime = ''
       }else{
        patTime = this.getTag2()
       }
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/statistics/userAll"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              startTime:this.startTime,
              endTime:this.endTime,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
              //paraTime: '',
              paraTime: patTime,
            },
            params
          )
        ),
        // method: 'post',
        // data:this.$http.adornData({
        //       'current': this.page.currentPage,
        //       'size': this.page.pageSize,
        //       "paraTime": this.getTag2(),
        //       'startTime': this.dateRange === null ? null : this.dateRange[0], // 开始时间
        //       'endTime': this.dateRange === null ? null : this.dateRange[1] // 结束时间
        //       // "startTime": this.dataForm.startTime,
        //       // "endTime": this.dataForm.endTime
        // })
      }).then(({ data }) => {
        if (data.code == 0) {
          let totleSum = [];
          totleSum.push(data.data.sumData);
          this.dataAllList = totleSum;
          this.dataList = data.data.list.records;
          this.page.total = data.data.list.total;
        } else {
          this.$message({
            message: data.msg,
            type: "error",
          });
        }
        this.dataListLoading = false;
        if (done) {
          done();
        }
      });
    },
    addClasscolor({ column, row }) {
      //表单样式
      if (
        (column.property === "gift_money" && row.gift_money * 1 > 0) ||
        (column.property === "difference" && row.difference * 1 > 0) ||
        (column.property === "business_profit" &&
          row.business_profit * 1 > 0) ||
        (column.property === "totle_fee" && row.totle_fee * 1 > 0) ||
        (column.property === "totle_income" && row.totle_income * 1 > 0)
      ) {
        return "green";
      } else if (
        (column.property === "gift_money" && row.gift_money * 1 < 0) ||
        (column.property === "difference" && row.difference * 1 < 0) ||
        (column.property === "business_profit" &&
          row.business_profit * 1 < 0) ||
        (column.property === "totle_income" && row.totle_income * 1 < 0)
      ) {
        return "red";
      } else {
        return "";
      }
    },
    // 条件查询
    searchChange(params, done) {
      this.page.currentPage = 1; // 重置当前页为第一页
      this.getDataList(this.page, params,done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
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
            return item.roleId;
          });
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: this.$http.adornUrl("/sys/role"),
            method: "delete",
            data: this.$http.adornData(ids, false),
          }).then(({ data }) => {
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.getDataList();
              },
            });
          });
        })
        .catch(() => {});
    },
    // 选项卡
    handleClick(tab, event) {
      this.startTime == '',
      this.endTime == ''
      //console.log(this.activeName)
      if (this.activeName == 1) {
        this.state = "submitted";
      } else {
        this.state = "";
      }
      this.getDataList();
    },
    handleClick2(tab, event) {
      this.startTime = '',
      this.endTime = ''
      this.paraTime = this.getTag2();
      //console.log(this.activeName2)
      // if (this.activeName2 == 1) {
      //   this.state = "";
      // } else if (this.activeName2 == 2) {
      //   this.state = "submitted";
      // } else {
      //   this.state = "created";
      // }
      this.getDataList();
    },
    getTag2() {
      //当天 day 当周 week 当月 month 全部 all
      let m = {
        1: "day",
        2: "week",
        3: "month",
        4: "all",
      };
      return m[this.activeName2];
    },
  },
};
</script>
<style scoped>
.speaDiv {
  margin-top: 20px;
  font-size: 16px;
}
.speacPick{
  
}
</style>
