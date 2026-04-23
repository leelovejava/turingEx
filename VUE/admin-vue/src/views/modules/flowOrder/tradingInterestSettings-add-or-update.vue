<template>
  <el-dialog
    :title="!id ? '新增' : '修改'"
    :close-on-click-modal="false"
    width="800px"
    @close="handClose"
    :visible.sync="visible"
  >
    <el-scrollbar class="vertical-scrollbar">
      <div class="scroll-content">
        <el-form
          :model="dataForm"
          :rules="dataRule"
          ref="dataForm"
          @keyup.enter.native="dataFormSubmit()"
          label-width="100px"
        >
          <el-form-item label="跟单天数" prop="days">
            <el-input
              type="number"
              v-model="dataForm.days"
              placeholder="跟单天数"
            >
            </el-input>
          </el-form-item>
          <el-form-item label="杠杆" prop="level">
            <el-input type="number" v-model="dataForm.level" placeholder="杠杆">
            </el-input>
          </el-form-item>
          <el-form-item label="小时利率" prop="dayRate">
            <el-input
              type="number"
              v-model="dataForm.dayRate"
              placeholder="小时利率"
            >
            </el-input>
          </el-form-item>
        </el-form>
      </div>
    </el-scrollbar>
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
      dataForm: {
        uuid: "",
        days: "",
        level: "",
        dayRate: "",
      },
      id: "",
      dataRule: {
        days: [
          { required: true, message: "跟单天数不能为空", trigger: "blur" },
        ],
        level: [{ required: true, message: "杠杆不能为空", trigger: "blur" }],
        dayRate: [
          { required: true, message: "小时利率不能为空", trigger: "blur" },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(row) {
      if (row) {
        this.id = row.uuid;
        this.dataForm = row;
      }
      this.visible = true;
    },
    handClose() {
      this.id = "";
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    changeVal(val) {
      this.$forceUpdate();
    },

    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          let url = "";
          if (this.id) {
            //更新
            url = `/normal/adminTraderDaysSetting!update.action`;
          } else {
            //新增
            url = `/normal/adminTraderDaysSetting!add.action`;
          }
          this.$http({
            url: this.$http.adornUrl(url),
            method: "post",
            data: this.$http.adornData(this.dataForm),
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
.vertical-scrollbar .scroll-content {
  max-height: 600px;
  overflow-y: auto;
  overflow-x: hidden;
}
.vertical-scrollbar .scroll-content::-webkit-scrollbar {
  width: 4px;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-thumb {
  background-color: #888;
  border-radius: 4px;
}

.vertical-scrollbar .scroll-content::-webkit-scrollbar-thumb:hover {
  background-color: #555;
}
.spainut {
  width: 360px;
}
</style>
