<template>
  <el-dialog
    :title="type ? '批量公布' : '公布'"
    :close-on-click-modal="false"
    width="600px"
    @close="handClose"
    :visible.sync="visible"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="150px"
    >
    <el-form-item v-if="type" label="公布账号:" prop="ipoStatus">
       <span>{{nameList}}</span>
      </el-form-item>
      <el-form-item label="申购状态" prop="status">
        <el-select
          class="speaInputTwo"
          v-model="dataForm.status"
          placeholder="请选择"
          @change="changeVal()"
        >
          <el-option
            v-for="item in direction"
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
import { Debounce } from "@/utils/debounce";
import { encrypt } from "@/utils/crypto";
export default {
  data() {
    return {
      visible: false,
      imageUrl: "",
      row: "",
      type:true,
      dataForm: {
        status:''
      },
      nameList:'',
      orderNo:[],
      direction: [
        {
          label: "已中签",
          value: 2,
        },
        {
          label: "未中签",
          value: 3,
        },
      ],
      dataRule: {},
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(row) {
      this.dataForm.status = this.direction[0].value
      console.log(this.dataForm.status)
      if (Array.isArray(row)) {
        this.type=true
        this.orderNo = row.map((item) => {
            return item.orderNo;
          });
          let nameAll = row.map((item) => {
            return item.userName;
          });
          this.nameList = nameAll.join(',');
          console.log(this.orderNo)
      //console.log('row 是数组');批量
    } else {
      this.type=false
      this.orderNo = [row.orderNo]
      console.log(this.orderNo)
      //console.log('row 不是数组');单个
    }
      this.row = row || "";
      //this.dataForm = { ...row };
      this.visible = true;
    },
    handClose() {
      this.nameList = ''
      this.orderNo = []
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.row = "";
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl("/applyNewSharesOrder/batchPublish"),
            method: "post",
            data: this.$http.adornData({
              orderNo: this.orderNo, //订单号
              status: this.dataForm.status, //状态
            }),
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
.titleDivTwo {
  height: 40px;
  border-left: 3px solid #1c4efa;
  background: #f4f7ff;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
.speaInputTwo {
  width: 240px;
}
</style>
