<template>
  <el-dialog
    :title="name + '简况F10'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="handClose"
    @open="handOpen"
  >
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <el-form :inline="true" label-width="120px">
        <el-form-item label="类型ID">
          <el-input
            disabled
            class="speaInputTwo"
            v-model="dataForm.symbol"
            placeholder="类型ID"
          ></el-input>
        </el-form-item>

        <el-form-item label="风险等级">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.riskLevel"
            placeholder="风险等级"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <!-- <el-form-item label="基础信息">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="基础信息"
          ></el-input>
        </el-form-item> -->
        <el-form-item label="基金类型">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.securityType"
            placeholder="基金类型"
          ></el-input>
        </el-form-item>
        <el-form-item label="业绩比较基准">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.performanceBenchmark"
            placeholder="业绩比较基准"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="成立日">
          <el-date-picker
            class="speaInputTwo"
            v-model="dataForm.foundDate"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间"
          >
          </el-date-picker>
        </el-form-item>

        <el-form-item label="投资类型">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.investmentType"
            placeholder="投资类型"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="上市日期">
          <el-date-picker
            class="speaInputTwo"
            v-model="dataForm.listingDate"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm:ss"
            placeholder="选择日期时间"
          >
          </el-date-picker>
        </el-form-item>

        <el-form-item label="交易方式">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.tradingMethod"
            placeholder="交易方式"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="基金规模">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.fundSize"
            placeholder="基金规模"
          ></el-input>
        </el-form-item>

        <el-form-item label="交易费用">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.transactionFee"
            placeholder="交易费用"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="基金份额">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.fundShares"
            placeholder="基金份额"
          ></el-input>
        </el-form-item>

        <el-form-item label="交易佣金">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.tradingCommission"
            placeholder="交易佣金"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="跟踪指数">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.indexTracking"
            placeholder="跟踪指数"
          ></el-input>
        </el-form-item>

        <el-form-item label="印花税">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.stampDuty"
            placeholder="印花税"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="行业概念">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.belongingConcept"
            placeholder="行业概念"
          ></el-input>
        </el-form-item>

        <el-form-item label="管理费率">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.managementFeeRate"
            placeholder="管理费率"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="基金经理">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.managingDirector"
            placeholder="基金经理"
          ></el-input>
        </el-form-item>

        <el-form-item label="托管费率">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.custodianFeeRate"
            placeholder="托管费率"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="基金公司">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.orgName"
            placeholder="基金公司"
          ></el-input>
        </el-form-item>

        <el-form-item label="投资概念">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.investmentPhilosophy"
            placeholder="投资概念"
          ></el-input>
        </el-form-item>
      </el-form>
      <!-- <el-form :inline="true" label-width="120px">
        <el-form-item label="基金类型">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="基金类型"
          ></el-input>
        </el-form-item>
      </el-form> -->
      <el-form-item label="风险收益特征">
        <el-input
          type="textarea"
          v-model="dataForm.riskCharacteristics"
        ></el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
    <!-- 确认弹窗-end -->
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
      dialogFormVisible: true,
      formLabelWidth: "120px",
      name: "",
      menuList: [],
      menuListTreeProps: {
        label: "name",
        children: "children",
      },
      options: [],
      optionsTwo: [
        {
          label: "机器人刷单",
          value: 1,
        },
        {
          label: "采集",
          value: 2,
        },
      ],
      row: "",
      dataForm: {
        symbol: "", //项目ID
        accountFirm: "", //核数师
        address: "", //地址
        auditOrganization: "", //审计机构
        belongIndustry: "", //所属行业
        belongingConcept: "", //所属概念/行业概念
        board: "", //板块
        bps: "", //每股净资产
        businessScope: "", //经营范围
        chairman: "", //主席
        circulatingAShareMarketCapitalization: "", //流通A市值
        circulatingAShares: "", //流通A股
        commonAcs: "", //总股本
        companyHeadquarters: "", //公司总部
        createBy: "", //地址
        createTime: "", //地址
        custodianFeeRate: "", //托管费用率
        debtRatio: "", //负债率
        delFlag: "", //地址
        dividendRate: "", //股息率
        dpsHkd: "", //每股股息
        empNum: "", //员工人数
        enName: "", //英文名称
        eps: "", //每股收益
        foundDate: "", //成立日期
        fundShares: "", //基金份额
        fundSize: "", //基金规模
        goodwillScale: "", //商誉规模
        grossProfitMargin: "", //毛利率
        hkskMarketCap: "", //港股市值
        hongKongStockCapital: "", //港股股本
        hongKongStockShares: "", //港股股份
        indexTracking: "", //跟踪指数
        investmentPhilosophy: "", //投资理念
        investmentType: "", //投资类型
        isinCode: "", //isin
        issuePrice: "", //发行价格
        issuedCommonShares: "", //发行量
        legalAdvisor: "", //法律顾问
        legalRepresentative: "", //法人代表
        listingDate: "", //上市时间
        loanToValueRatio: "", //质押比例
        mainCorrespondentBank: "", //主要往来银行
        managementFeeRate: "", //管理费用率
        managementTeamSize: "", //管理层人数
        managingDirector: "", //总经理/基金经理
        netFundsRaised: "", //募资净额
        netProfit: "", //净利润
        netProfitRatio: "", //净利率
        onlineDrawingRate: "", //网上中签率
        operateIncome: "", //营业额
        operateIncomeQoq: "", //营业额同比
        orgEmail: "", //电邮地址
        orgFax: "", //传真号码
        orgName: "", //公司名称/基金公司
        orgProfile: "", //公司简介
        orgTel: "", //电话号码
        orgWeb: "", //公司网址
        parValuePerShare: "", //每股面值
        pbTtm: "", //市净率
        peRatioAtIpo: "", //发行市盈率
        peTtm: "", //市盈率
        performanceBenchmark: "", //业绩比较基准
        profitParentCompany: "", //归母净利润同比
        regAddress: "", //注册地址
        region: "", //所属区域
        registeredCapital: "", //注册资本
        riskLevel: "", //风险等级
        riskCharacteristics: "", //风险特征
        roe: "", //净资产收益率
        secretary: "", //公司秘书
        secucode: "", //证券代码
        securityType: "", //证券类型/基金类型
        shareRegistrar: "", //股份过户登记处
        shhareholdersEarnings: "", //股东应占溢利
        skMarketCap: "", //总市值
        stampDuty: "", //印花税
        stockExchange: "", //上市场所
        strCodeA: "", //A股代码
        strNameA: "", //A股简称
        symbol: "", //代码
        totalIncome: "", //收入总额
        totalOperatingRevenue: "", //营业总收入
        tradeUnit: "", //每手股数
        tradingCommission: "", //交易佣金
        tradingMethod: "", //交易方式
        transactionFee: "", //交易费用
        updateBy: "",
        updateTime: "",
        uuid: "",
        weeklyInterestRate: "", //周息率
        yearSettleDay: "", //年结日
        yoyNetProfit: "", //净利润同比
        yoyProfitParentCompany: "", //归母净利润
        yoyProfitShare: "", //占溢利同比
        yoyTotalIncome: "", //收入总额同比
        yoyTotalOperatingRevenue: "", //总营同比
      },
      dataRule: {
        whitePagerAddress: [
          { required: true, message: "白皮书地址不能为空", trigger: "blur" },
        ],
        idCode: [{ required: true, message: "ID不能为空", trigger: "blur" }],
        projectName: [
          { required: true, message: "申购项目名称不能为空", trigger: "blur" },
        ],
        issuePrice: [
          { required: true, message: "发行价不能为空", trigger: "blur" },
        ],
        minQuantity: [
          { required: true, message: "最小申购数量不能为空", trigger: "blur" },
        ],
        maxQuantity: [
          { required: true, message: "最大申购数量不能为空", trigger: "blur" },
        ],
      },
    };
  },
  created() {},
  methods: {
    init(symbol, name) {
      this.dataForm.symbol = symbol || "";
      this.name = name || "";
      // console.log(row);
      // if (row) {
      //   (this.dataForm.id = row.uuid),
      //     (this.dataForm.idCode = row.idCode),
      //     (this.dataForm.projectName = row.projectName),
      //     (this.dataForm.issuePrice = row.issuePrice),
      //     (this.dataForm.currency = row.currency),
      //     (this.dataForm.expectedLaunchTime = row.expectedLaunchTime),
      //     (this.dataForm.subscriptionEndTime = row.subscriptionEndTime),
      //     (this.dataForm.subscriptionStartTime = row.subscriptionStartTime),
      //     (this.dataForm.publishTime = row.publishTime),
      //     (this.dataForm.minQuantity = row.minQuantity),
      //     (this.dataForm.maxQuantity = row.maxQuantity);
      //   this.optionsTwo.value = row.dataType * 1;
      //   this.options.value = row.projectTypeSymbol;
      //   this.dataForm.whitePagerAddress = row.whitePagerAddress;
      // } else {
      //   this.optionsTwo.value = this.optionsTwo[0].value;
      //   this.options.value = this.options[0].value;
      // }
      this.visible = true;
      // this.dialogFormVisible = false
    },
    handOpen() {
      this.queryBySymbol();
    },
    handClose() {
      // this.$data.dataForm = JSON.parse(
      //   JSON.stringify(this.$options.data().dataForm)
      // );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    // 获取项目种类列表
    queryBySymbol(page, params, done) {
      this.$http({
        url: this.$http.adornUrl("/item/itemSummary/queryBySymbol"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              symbol: this.dataForm.symbol,
            },
            params
          ),
          false
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          this.dataForm = data.data;
          console.log(this.dataForm);
        }
        if (done) {
          done();
        }
      });
    },
    dataFormSubmit: Debounce(function () {
      //更新修改
      //start
      this.$refs["dataForm"].validate((valid) => {
        if (valid) {
          this.$http({
            url: this.$http.adornUrl(`/item/itemSummary/save`),
            method: "post",
            data: this.$http.adornData({
              uuid: this.dataForm.uuid,
              symbol: this.dataForm.symbol, // 类型ID
              riskLevel: this.dataForm.riskLevel, // 风险等级
              securityType: this.dataForm.securityType, // 基金类型
              performanceBenchmark: this.dataForm.performanceBenchmark, // 业绩比较基准
              foundDate: this.dataForm.foundDate, //成立日
              investmentType: this.dataForm.investmentType, //投资类型
              listingDate: this.dataForm.listingDate, //	上市日期
              tradingMethod: this.dataForm.tradingMethod, //交易方式
              fundSize: this.dataForm.fundSize, //基金规模
              transactionFee: this.dataForm.transactionFee, //交易费用
              fundShares: this.dataForm.fundShares, //交易份额
              tradingCommission: this.dataForm.tradingCommission, //交易佣金
              //'projectTypeName': this.options.label,    //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
              indexTracking: this.dataForm.indexTracking, //跟踪指数
              stampDuty: this.dataForm.stampDuty, //印花税
              belongingConcept: this.dataForm.belongingConcept, //行业概念
              managementFeeRate: this.dataForm.managementFeeRate, //管理费率
              managingDirector: this.dataForm.managingDirector, //基金经理
              custodianFeeRate: this.dataForm.custodianFeeRate, //托管费率
              orgName: this.dataForm.orgName, //基金公司
              investmentPhilosophy: this.dataForm.investmentPhilosophy, //投资概念
              riskCharacteristics: this.dataForm.riskCharacteristics, //风险收益特征
            }),
          }).then(({ data }) => {
            if (data.code == 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.dialogFormVisible = false;
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
                  this.dialogFormVisible = false;
                  this.visible = false;
                },
              });
            }
          });
        }
      });
    }),
    beforeAvatarUpload(file) {
      // const isJPG = file.type === 'image/jpeg';
      const isLt2M = file.size / 1024 / 1024 < 10;
      if (!isLt2M) {
        this.$message.error("上传图片大小不能超过 10MB!");
      }
      return isLt2M;
    },
  },
};
</script>

<style scoped>
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
.speaInput {
  width: 120px;
}
.speaInputTwo {
  width: 250px;
}
</style>
