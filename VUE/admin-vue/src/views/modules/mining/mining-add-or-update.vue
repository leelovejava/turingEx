<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    width="1100px"
    :visible.sync="visible"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="150px"
    >
      <el-form-item class="titleDivTwo" label="基础信息" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="8">
          <el-form-item label="产品名称(简体中文)" prop="name">
            <el-input
              v-model="dataForm.name"
              placeholder="产品名称(简体中文)"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="产品名称(繁体中文)" prop="name_cn">
            <el-input
              v-model="dataForm.name_cn"
              placeholder="产品名称(繁体中文)"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="产品名称(英文)" prop="name_en">
            <el-input
              v-model="dataForm.name_en"
              placeholder="产品名称(英文)"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="矿机购买币种" prop="buy_currency">
            <el-input
              v-model="dataForm.buy_currency"
              placeholder="矿机购买币种"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="矿机产出币种" prop="output_currency">
            <el-input
              v-model="dataForm.output_currency"
              placeholder="矿机产出币种"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="适用算法" prop="uuid">
            <el-select
              class="speaInputTwo"
              v-model="dataForm.algorithm"
              placeholder="请选择"
              @change="changeVal()"
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
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="网络连接" prop="uuid">
            <el-select
              class="speaInputTwo"
              v-model="dataForm.internet"
              placeholder="请选择"
              @change="changeVal()"
            >
              <el-option
                v-for="item in internets"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="生产厂家" prop="product_factory">
            <el-input
              v-model="dataForm.product_factory"
              placeholder="生产厂家"
            ></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="外籍尺寸" prop="product_size">
            <el-input
              v-model="dataForm.product_size"
              placeholder="外籍尺寸"
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="额定算力" prop="computing_power">
            <el-input
              v-model="dataForm.computing_power"
              placeholder="额定算力"
            ><template v-slot:append>
              <el-select
              style="width:100px"
              class="speaInputTwo"
              v-model="dataForm.computing_power_unit"
              placeholder="请选择"
              @change="changeVal()"
            >
              <el-option
                v-for="item in computing_power_units"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              >
              </el-option>
            </el-select>
              </template></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="官方功耗" prop="power">
            <el-input
              v-model="dataForm.power"
              placeholder="官方功耗"
            ><template v-slot:append>
                <span>W</span>
              </template></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="整机重量" prop="weight">
            <el-input
              v-model="dataForm.weight"
              placeholder="整机重量"
            ><template v-slot:append>
                <span>Kg</span>
              </template></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="11">
          <el-form-item label="工作温度" prop="work_temperature_min">
            <el-input v-model="dataForm.work_temperature_min" placeholder="">
              <template v-slot:append>
                <span>℃</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="2">          
          <el-form-item label="— —" prop="">
          </el-form-item></el-col>
        <el-col :span="11">
          <el-form-item label="" prop="work_temperature_max">
            <el-input v-model="dataForm.work_temperature_max"  placeholder="">
              <template v-slot:append>
                <span>℃</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="11">
          <el-form-item label="工作湿度" prop="work_humidity_min">
            <el-input v-model="dataForm.work_humidity_min" placeholder="">
              <template v-slot:append>
                <span>%RH</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
        <el-col :span="2">          
          <el-form-item label="— —" prop="">
          </el-form-item></el-col>
        <el-col :span="11">
          <el-form-item label="" prop="work_humidity_max">
            <el-input v-model="dataForm.work_humidity_max"  placeholder="">
              <template v-slot:append>
                <span>%RH</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item class="titleDivTwo" label="交易信息" prop="">
      </el-form-item>
      <el-row>
        <el-col :span="11">
          <el-form-item label="投资金额区间" prop="investment_min">
            <el-input v-model="dataForm.investment_min" placeholder="">
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
            <el-input v-model="dataForm.investment_max"  placeholder="">
              <template v-slot:append>
                <span>USDT</span>
              </template>
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8">
          <el-form-item label="可解锁周期" prop="cycle_close">
            <el-input
              v-model="dataForm.cycle_close"
              placeholder="可解锁周期"
            ><template v-slot:append>
                <span>天</span>
              </template></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="日利率" prop="show_daily_rate">
            <el-input
              v-model="dataForm.show_daily_rate"
              placeholder="日利率"
            ><template v-slot:append>
                <span>%</span>
              </template></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="今日利率" prop="daily_rate">
            <el-input
              v-model="dataForm.daily_rate"
              placeholder="今日利率"
            ><template v-slot:append>
                <span>%</span>
              </template></el-input>
          </el-form-item>
        </el-col>
      </el-row>
          <el-form-item label="状态" prop="state">
            <el-select
              class="speaInputTwo"
              v-model="dataForm.state"
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

      <el-form-item  label="登录人资金密码"  prop="login_safeword">
        <el-input v-model="dataForm.login_safeword" type="password" placeholder="请输入登录人资金密码"></el-input>
      </el-form-item>

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
      visible: false,
      dataForm: {
        id: 0,
        roleName: "",
        remark: "",
        name: "",
        state: "",
      },
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
      internets: [
        {
          label: "Ethernet",
          value: "Ethernet",
        },
      ],
      direction: [
        {
          label: "启用",
          value: "1",
        },
        {
          label: "停用",
          value: "0",
        },
      ],
      computing_power_units:[
        {
          label: "MH/s",
          value: "MH/s",
        },
        {
          label: "Ksol/s",
          value: "Ksol/s",
        },
        {
          label: "Th/s",
          value: "Th/s",
        },
        {
          label: "Gh/s",
          value: "Gh/s",
        },
      ],
      dataRule: {
        roleName: [
          { required: true, message: "角色名称不能为空", trigger: "blur" },
        ],
        login_safeword: [
            { required: true, message: '登录人资金密码不能为空', trigger: 'blur' },
        ],
      },
      tempKey: -666666, // 临时key, 用于解决tree半选中状态项不能传给后台接口问题. # 待优化
    };
  },
  methods: {
    info(id,call){
      this.$http({
        url: this.$http.adornUrl(`/normal/adminMinerAction!toUpdate.action`),
        method: "get",
        params: this.$http.adornParams({
          id:id
        }),
      }).then(({ data }) => {
        console.log("data => " + JSON.stringify(data));
        call && call(data)
      });
    },
    init(row) {
      this.dataForm = {};
      if(row){
        this.dataForm = row;
      }  
      console.log("this.dataForm = > " + JSON.stringify(this.dataForm))
      this.info(this.dataForm.id,(data)=>{
        this.dataForm = Object.assign({}, this.dataForm, data.data)
        this.visible = true;
      })
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    // 表单提交
    dataFormSubmit: Debounce(function () {


      // let obj = {
      //   id: this.dataForm.id,
      //   img: this.dataForm.img,
      //   name: 体验矿机 3天
      //   name_cn: 体验礦機 3天
      //   name_en: Super computing power miner 3 days
      //   buyCurrency: usdt
      //   outputCurrency: usdt
      //   algorithm: EtHash
      //   internet: Ethernet
      //   product_factory: 比特大陆
      //   product_size: 245mm*132.5mm*290mm
      //   computing_power: 420.0
      //   computing_power_unit: Ksol/s
      //   power: 1510.0
      //   weight: 5.9
      //   work_temperature_min: 0.0
      //   work_temperature_max: 40.0
      //   work_humidity_min: 5.0
      //   work_humidity_max: 95.0
      //   investment_min: 0.0
      //   investment_max: 0.0
      //   cycle_close: 0
      //   show_daily_rate: 0.0
      //   daily_rate: 0.0
      //   on_sale: 1
      //   login_safeword: 123456
      // }

      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/normal/adminMinerAction!update.action`),
            method: "get",
            params: this.$http.adornParams({
              ...this.dataForm,
              login_safeword:encrypt(this.dataForm.login_safeword)
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
