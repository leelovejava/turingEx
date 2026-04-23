<template>
  <el-dialog
    title="新增"
    :close-on-click-modal="false"
    width="800px"
    :visible.sync="visible"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="100px"
    >
          <el-form-item label="矿机" prop="algorithm">
            <el-select
              class="speaInputTwo"
              v-model="dataForm.algorithm"
              placeholder="请选择"
              @change="changeVal(dataForm.algorithm)"
            >
              <el-option
                v-for="item in algorithms"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
          

          <!---->
          <el-row>
            <el-col :span="11">
              <el-form-item label="投资金额区间" prop="investment_min">
                <el-input disabled v-model="investment_min" placeholder="">
                  <template v-slot:append>
                    <span>USDT</span>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="2">          
              <el-form-item label="— —" prop="">
              </el-form-item></el-col>
            <el-col :span="11">
              <el-form-item label="" prop="investment_max">
                <el-input disabled v-model="investment_max"  placeholder="">
                  <template v-slot:append>
                    <span>USDT</span>
                  </template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <!---->

          <el-form-item label="用户uid" prop="para_uid">
            <el-input
              v-model="dataForm.para_uid"
              placeholder="用户uid"
            ></el-input>
          </el-form-item>
          <el-form-item label="投资金额" prop="para_amount">
            <el-input
              v-model="dataForm.para_amount"
              placeholder="投资金额"
            ><template v-slot:append>
                <span>USDT</span>
              </template></el-input>
          </el-form-item>
     

      <!-- <el-form-item  label="登录人资金密码"  prop="login_safeword">
        <el-input v-model="dataForm.login_safeword" type="password" placeholder="请输入登录人资金密码"></el-input>
      </el-form-item> -->

    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>
<script>
import { encrypt } from "@/utils/crypto";
import { Debounce } from "@/utils/debounce";
export default {
  data() {
    return {
      investment_min:"",
      investment_max:"",
      visible: false,
      dataForm: {
        id: 0,
        roleName: "",
        remark: "",
        name: "",
        state: "",
        computing_power_unit:'',
      },
      all:{},
      algorithms: [
        {
          label: "EtHash",
          value: "EtHash",
        },
        {
          label: "EquiHash",
          value: "EquiHash",
        },
        {
          label: "Kadena",
          value: "Kadena",
        },
        {
          label: "EthHash",
          value: "EthHash",
        },
      ],
      dataRule: {
        algorithm: [
          { required: true, message: "不能为空", trigger: "blur" },
        ],
        para_uid: [
            { required: true, message: '不能为空', trigger: 'blur' },
        ],
        para_amount: [
            { required: true, message: '不能为空', trigger: 'blur' },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    init(row) {
      this.dataForm = {};
      // if(row){
      //   this.dataForm = row;
      // }  
      this.listMiningOrder();
     
    },
    changeVal(val) {
      console.log(val);
      let data = this.all[val];
      this.investment_min = data.investment_min;
      this.investment_max = data.investment_max;
    },
    listMiningOrder(){
      //
      this.algorithms = []
      this.all = {}
      this.$http({
            url: this.$http.adornUrl(`/normal/adminMinerOrderAction!toAddOrder.action`),
            method: "get",
            params: this.$http.adornParams({
            }),
      }).then(({ data }) => {
        
        let list = data.data.miner_list;
        for(let i = 0 ; i < list.length ; i++){
          let data = list[i];
          this.algorithms[i] = {label:data.name,value:data.uuid}

          this.all[data.uuid] = data;
        }
        console.log("listMiningOrder => " + JSON.stringify(this.algorithms));
        this.visible = true;

      });
      //
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/normal/adminMinerOrderAction!addOrder.action`),
            method: "get",
            params: this.$http.adornParams({
              para_uid:this.dataForm.para_uid,
              para_amount:this.dataForm.para_amount,
              para_minerid:this.dataForm.algorithm,
              session_token:"",
            }),
          }).then(({ data }) => {
            console.log("data => " + JSON.stringify(data));
            if(data.code==0){
              //
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.visible = false;
                  this.$emit("refreshDataList");
                },
              });
              //
            }else{
              this.$message({
                message: data.msg,
                type: 'error',
                duration: 1500,
                onClose: () => {
                }
              })
              //
            }

          });
        }
      });
    }),
  },
};
</script>
<style scoped>
.speaInputTwo{
  width: 660px;
}
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
</style>
