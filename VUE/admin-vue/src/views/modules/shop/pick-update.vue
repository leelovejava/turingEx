<template>
  <el-dialog
    title="订单场控"
    :close-on-click-modal="false"
    :visible.sync="visible"
    append-to-body
    @close="handClose"
    width="700px"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      
      <el-form-item label="场控选择" prop="symbol">
        <el-select
        class="speacSelect"
          v-model="options.value"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          >
          </el-option>
        </el-select>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { treeDataTranslate } from "@/utils";
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    return {
      visible: false,
      menuList: [],
      id:'',
      row: "",
      options: [
        {
          value: "profit",
          label: "盈利",
        },
        {
          value: "loss",
          label: "亏损",
        }
      ],
      langug: [], // 语言
      dataForm: {
        closeTimeTs: "",
        openTimeTs: "",
        high: "",
        low: "",
        turnover: "",
        uuid: "",
      },

      dataRule: {
        // sorted: [{ required: true, message: "排序不能为空", trigger: "blur" }],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(id) {
      this.id = id || ''
      this.visible = true;
    },
    changeVal(val) {
      this.$forceUpdate();
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
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
        this.$refs["dataForm"].validate((valid) => {
          if (valid) {
            this.$http({
              // 修改
              url: this.$http.adornUrl(`/normal/adminFuturesOrderAction!/orderProfitLoss.action`),
              method: "get",
              params: this.$http.adornParams(Object.assign({
                orderNo:this.id,
                profitLoss:this.options.value,
              })),
            }).then(({ data }) => {
              if (data.code == 0) {
                this.$message({
                  message: "操作成功",
                  type: "success",
                  duration: 1500,
                  onClose: () => {
                    this.visible = false;
                    this.$emit("refreshDataList");
                  },
                });
              } else {
                this.$message({
                  message: data.msg,
                  type: "error",
                  duration: 1500,
                  onClose: () => {
                    this.visible = false;
                  },
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
.speacSelect{
  width: 400px;
}
</style>