<template>
  <div class="mod-orde-withdraw">
    <avue-crud
      ref="crud"
      :page.sync="page"
      :data="dataList"
      :option="tableOption"
      :cell-class-name="addClasscolor"
      @search-change="searchChange"
      @selection-change="selectionChange"
      @refresh-change="refreshChange"
      @on-load="getDataList"
    >
      <template slot="menuLeft">
        <avue-tabs :option="option" @change="handleChange"></avue-tabs>
        <span v-if="type.prop === 'tab1'"></span>
        <span v-else-if="type.prop === 'tab2'"></span>
        <span v-else-if="type.prop === 'tab3'"></span>
        <span v-else-if="type.prop === 'tab4'"></span>
      </template>
      <template slot-scope="scope" slot="address">
        <span
          class="seachButton"
          v-if="isAuth('order:withdraw:mesage')"
          @click.stop="
            seachDress(scope.row.volume, scope.row.address, scope.row.qdcode)
          "
          >查看信息</span
        >
      </template>
      <template slot-scope="scope" slot="userNamesolt">
        <span v-if="isAuth('order:withdraw:username')" @click="searchName(scope.row.userName)" class="seachButton">{{
          scope.row.userName
        }}</span>
      </template>
      <template slot-scope="scope" slot="menu">
        <!-- <el-button
          type="primary"
          icon="el-icon-edit"
          size="small"
          plain
          v-if="isAuth('sys:user:delete') && scope.row.status * 1 == 1&& scope.row.status * 1 !== 2"
          @click.stop="deleteHandle(scope.row.id, scope.row.address)"
          >修改收款地址</el-button
        > -->
        <el-select
          v-if="isAuth('order:withdraw:operate') && scope.row.status * 1 !== 1&& scope.row.status * 1 !== 2"
          v-model="scope.row.select"
          clearable
          placeholder="操作"
          class="celectSpeac"
          @change="
            changeSelet(
              scope.row.id,
              scope.row.address,
              scope.row.select,
              scope.row
            )
          "
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </template>
    </avue-crud>
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdate"
      @refreshDataList="getDataList"
    ></add-or-update>
    <add-other-update
      v-if="addOtherUpdateVisible"
      ref="addOtherUpdate"
      @refreshDataList="getDataList"
    ></add-other-update>
    <consignment-info
      v-if="consignmentInfoVisible"
      ref="consignmentInfo"
    ></consignment-info>
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
import AddOrUpdate from "./withdrawInfo";
import AddOtherUpdate from "./withdrawInfoTwo";
import { tableOption } from "@/crud/shop/withdraw";
import ConsignmentInfo from "./withdraw-info";
export default {
  data() {
    return {
      addOrUpdateVisible: false,
      consignmentInfoVisible: false,
      addOtherUpdateVisible: false,
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      tableOption: tableOption,
      userNamevisible: false,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      options: [
        {
          value: "1",
          label: "通过申请(手动打款)",
        },
        {
          value: "2",
          label: "驳回申请",
        },
        {
          value: "3",
          label: "修改用户地址",
        },
        {
          value: "4",
          label: "备注描述",
        },
      ],
      type: {},
      status: "",
      option: {
        column: [
          {
            label: "全部",
            prop: "",
          },
          {
            label: "未处理",
            prop: "0",
          },
          {
            label: "通过申请",
            prop: "1",
          },
          {
            label: "驳回申请",
            prop: "2",
          },
        ],
      },
      dataForm: {},
      username: {},
      searchParams: {}, // 搜索条件
    };
  },
  components: {
    AddOrUpdate,
    ConsignmentInfo,
    AddOtherUpdate,
  },
  created() {
    this.type = this.option.column[0];
  },
  mounted() {
    this.$bus.$on("updateOfWithdraw", (data) => {
      this.getDataList();
    });
  },
  beforeDestroy() {
    // 组件被销毁了，不能进行数据传输
    // 解绑事件
    this.$bus.$off("updateOfWithdraw");
  },
  methods: {
    getDataList(page, done) {
      const params = {
        current: page == null ? this.page.currentPage : page.currentPage,
        size: page == null ? this.page.pageSize : page.pageSize,
        ...this.searchParams,
      };
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/withdraw/list"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              status: this.status,
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
            },
            params
          )
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
    addClasscolor({ column, row }) {
      //表单样式
      if (
        (column.property === "status" && row.status == "1") ||
        (column.property === "rolename" && row.rolename == "MEMBER")
      ) {
        return "green";
      } else if (column.property === "rolename" && row.rolename == "GUEST") {
        return "yellow";
      } else if (column.property === "amount") {
        return "red";
      }
    },
    seachDress(volume, address, qdcode) {
      this.consignmentInfoVisible = true;
      this.$nextTick(() => {
        this.$refs.consignmentInfo.init(volume, address, qdcode);
      });
    },
    // 撤销操
    deleteHandle(id, dress) {
      this.addOrUpdateVisible = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id, dress);
      });
    },
    // tab切换
    handleChange(column, params, done) {
      this.type = column;
      this.status = column.prop;
      this.getDataList(this.page, params, done);
    },
    // 条件查询
    searchChange(params, done) {
      this.page.currentPage = 1; // 重置当前页为第一页
      this.searchParams = params;
      this.getDataList(this.page, done);
    },
    // 点击刷新按钮
    refreshChange(params, done) {
      this.getDataList(this.page, params, done);
    },
    searchName(name) {
      this.username = name;
      this.userNamevisible = true;
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
    changeSelet(id, dress, val, row) {
      if (val) {
        let m = this.options[val - 1].label; //弹窗标题
        if (val == 1 || val == 2 || val == 4) {
          // 1通过申请 2驳回申请 3修改用户收款地址
          this.addOtherUpdateVisible = true;
          this.$nextTick(() => {
            this.$refs.addOtherUpdate.init(id, val, m);
          });
        } else {
          // 3修改用户收款地址
          this.deleteHandle(id, dress);
        }
        row.select = "";
      }
    },
  },
};
</script>

<style scoped>
/* 以下是下拉框操作按钮样式 */
::v-deep .celectSpeac .el-input__inner {
  background: #1c4efa !important;
  width: 123px;
}
::v-deep .celectSpeac .el-input__inner::placeholder {
  color: #fff;
}
.seachButton {
  cursor: pointer;
  color: rgb(69, 147, 235);
}
.seachButton:hover {
  color: rgb(8, 63, 134);
}
</style>
