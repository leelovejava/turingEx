<template>
  <el-dialog title="钱包" :close-on-click-modal="false" :visible.sync="visible">
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="80px"
    >
      <avue-crud
        ref="crud"
        :page.sync="page"
        :data="dataList"
        :option="tableOption"
        @search-change="searchChange"
        @selection-change="selectionChange"
        @on-load="getDataList"
      >
        <template slot="menuLeft"> </template>
      </avue-crud>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="visible = false">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { tableOption } from "@/crud/sys/othermanger";
import { treeDataTranslate } from "@/utils";
import { Debounce } from "@/utils/debounce";
export default {
  data() {
    return {
      visible: false,
      tableOption: tableOption,
      userId: "",
      dataList: [],
      menuList: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10, // 每页显示多少条
      },
      dataForm: {
        id: 0,
        roleName: "",
        remark: "",
      },
      dataRule: {
        roleName: [
          { required: true, message: "角色名称不能为空", trigger: "blur" },
          {
            pattern: /\s\S+|S+\s|\S/,
            message: "请输入正确的角色名称",
            trigger: "blur",
          },
        ],
        remark: [
          {
            required: false,
            pattern: /\s\S+|S+\s|\S/,
            message: "输入格式有误",
            trigger: "blur",
          },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(uid) {
      // this.dataForm.id = id || 0
      this.userId = uid || "";
      this.visible = true;
      this.getDataList(this.page)
    },
    // 获取数据列表
    getDataList(page, params, done) {
      this.dataListLoading = true;
      this.$http({
        url: this.$http.adornUrl("/statistics/walletExtendsAll"),
        method: "post",
        data: this.$http.adornData(
          Object.assign(
            {
              userId: this.userId,
            },
            params
          )
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          //usdtArr
          this.dataList = data.data.wallet_data;
          this.page.total = data.data.total;
          let a = data.data.wallet_type_arr.split(",");
          this.usdtArr = a.map((item) => {
            return {
              value: item,
              label: item,
            };
          });
          this.usdtArr.value = this.usdtArr[0].value;
          this.dataListLoading = false;
        }

        if (done) {
          done();
        }
      });
    },
    // 条件查询
    searchChange(params, done) {
      this.getDataList(this.page, params, done);
    },
    // 多选变化
    selectionChange(val) {
      this.dataListSelections = val;
    },
  },
};
</script>
