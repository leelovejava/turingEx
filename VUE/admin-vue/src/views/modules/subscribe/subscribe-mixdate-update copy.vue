<template>
  <el-dialog
    :title="!dataForm.id ? '新增申购管理' : '修改申购管理'"
    :close-on-click-modal="false"
    :visible.sync="visible"
    @close="handClose"
  >
    <!-- 此页面为ETF10的简况页面 含股票简况 暂时备用    -->
    <!-- 此页面为ETF10的简况页面 含股票简况 暂时备用    -->
    <!-- 此页面为ETF10的简况页面 含股票简况 暂时备用    -->
    <!-- 此页面为ETF10的简况页面 含股票简况 暂时备用    -->
    <!-- 此页面为ETF10的简况页面 含股票简况 暂时备用    -->
    <!-- 此页面为ETF10的简况页面 含股票简况 暂时备用    -->
    <!-- 此页面为ETF10的简况页面 含股票简况 暂时备用    -->
    <el-form
      :model="dataForm"
      :rules="dataRule"
      ref="dataForm"
      @keyup.enter.native="dataFormSubmit()"
      label-width="120px"
    >
      <el-form-item label="最新指标">
        <div class="romtitle">最新指标</div>
      </el-form-item>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="类型ID">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_min"
            placeholder="类型ID"
          ></el-input>
        </el-form-item>
        <el-form-item label="净资产收益率">
          <el-input
            class="speaInput"
            v-model="dataForm.roe"
            placeholder="净资产收益率"
          ></el-input>
        </el-form-item>
        <el-form-item label="净利润同比">
          <el-input
            class="speaInput"
            v-model="dataForm.yoyNetProfit"
            placeholder="净利润同比"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="最新指标">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_min"
            placeholder="最新指标"
          ></el-input>
        </el-form-item>

        <el-form-item label="总股本">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_max"
            placeholder="总股本"
          ></el-input>
        </el-form-item>
        <el-form-item label="净利率">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_max"
            placeholder="净利率"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="市盈率(TTM)">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_min"
            placeholder="市盈率(TTM)"
          ></el-input>
        </el-form-item>

        <el-form-item label="流通A股">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_max"
            placeholder="流通A股"
          ></el-input>
        </el-form-item>
        <el-form-item label="负债率">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_max"
            placeholder="负债率"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="每股收益">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_min"
            placeholder="每股收益"
          ></el-input>
        </el-form-item>

        <el-form-item label="质押比例">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_max"
            placeholder="质押比例"
          ></el-input>
        </el-form-item>
        <el-form-item label="总市值(元)">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_max"
            placeholder="总市值（元）"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="营业总收入(元)">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_min"
            placeholder="营业总收入（元）"
          ></el-input>
        </el-form-item>

        <el-form-item label="市净率">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_max"
            placeholder="市净率"
          ></el-input>
        </el-form-item>
        <el-form-item label="流通A市值(元)">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_max"
            placeholder="流通A市值（元）"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="净利润（元）">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_min"
            placeholder="净利润（元）"
          ></el-input>
        </el-form-item>

        <el-form-item label="每股净资产(元)">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_max"
            placeholder="每股净资产(元)"
          ></el-input>
        </el-form-item>
        <el-form-item label="商誉规模">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_max"
            placeholder="商誉规模"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="毛利率">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_min"
            placeholder="毛利率"
          ></el-input>
        </el-form-item>

        <el-form-item label="总营同比">
          <el-input
            class="speaInput"
            v-model="dataForm.recharge_limit_max"
            placeholder="总营同比"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form-item label="公司资料">
        <div class="romtitle">公司资料</div>
      </el-form-item>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="公司名称">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="公司名称"
          ></el-input>
        </el-form-item>

        <el-form-item label="总A股代码">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="总A股代码"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="所属区域">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="所属区域"
          ></el-input>
        </el-form-item>

        <el-form-item label="所属行业">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="所属行业"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="所属概念">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="所属概念"
          ></el-input>
        </el-form-item>

        <el-form-item label="董事长">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="董事长"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="法人代表">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="法人代表"
          ></el-input>
        </el-form-item>

        <el-form-item label="总经理">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="总经理"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="董秘">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="董秘"
          ></el-input>
        </el-form-item>

        <el-form-item label="成立日期">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="成立日期"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="注册资本(元)">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="注册资本(元)"
          ></el-input>
        </el-form-item>

        <el-form-item label="员工人数">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="员工人数"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="管理层人数">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="管理层人数"
          ></el-input>
        </el-form-item>

        <el-form-item label="审计机构">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="审计机构"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="法律顾问">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="法律顾问"
          ></el-input>
        </el-form-item>

        <el-form-item label="联系电话">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="联系电话"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="公司邮箱">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="公司邮箱"
          ></el-input>
        </el-form-item>

        <el-form-item label="公司网址">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="公司网址"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="办公地址">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="办公地址"
          ></el-input>
        </el-form-item>

        <el-form-item label="注册地址">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="注册地址"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="公司简介">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="公司简介"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form-item label="发行相关">
        <div class="romtitle">发行相关</div>
      </el-form-item>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="上市日期">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="上市日期"
          ></el-input>
        </el-form-item>

        <el-form-item label="发行量（股）">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="发行量（股）"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="发行价格">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="发行价格"
          ></el-input>
        </el-form-item>

        <el-form-item label="募资净额（元）">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="募资净额（元）"
          ></el-input>
        </el-form-item>
      </el-form>
      <el-form :inline="true" label-width="120px">
        <el-form-item label="发行市盈率">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_min"
            placeholder="发行市盈率"
          ></el-input>
        </el-form-item>

        <el-form-item label="网上中签率">
          <el-input
            class="speaInputTwo"
            v-model="dataForm.recharge_limit_max"
            placeholder="网上中签率"
          ></el-input>
        </el-form-item>
      </el-form>
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
        custodianFeeRate: "", //负债率
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
        riskCharacteristics: "", //风险等级
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
  created() {
    this.getSymbol();
  },
  methods: {
    init(arr, row) {
      this.options = arr || [];
      this.row = row || "";
      console.log(row);
      if (row) {
        (this.dataForm.id = row.uuid),
          (this.dataForm.idCode = row.idCode),
          (this.dataForm.projectName = row.projectName),
          (this.dataForm.issuePrice = row.issuePrice),
          (this.dataForm.currency = row.currency),
          (this.dataForm.expectedLaunchTime = row.expectedLaunchTime),
          (this.dataForm.subscriptionEndTime = row.subscriptionEndTime),
          (this.dataForm.subscriptionStartTime = row.subscriptionStartTime),
          (this.dataForm.publishTime = row.publishTime),
          (this.dataForm.minQuantity = row.minQuantity),
          (this.dataForm.maxQuantity = row.maxQuantity);
        this.optionsTwo.value = row.dataType * 1;
        this.options.value = row.projectTypeSymbol;
        this.dataForm.whitePagerAddress = row.whitePagerAddress;
      } else {
        this.optionsTwo.value = this.optionsTwo[0].value;
        this.options.value = this.options[0].value;
      }
      this.visible = true;
      // this.dialogFormVisible = false
    },
    handClose() {
      this.$data.dataForm = JSON.parse(
        JSON.stringify(this.$options.data().dataForm)
      );
      this.$nextTick(() => {
        this.$refs["dataForm"].clearValidate(); // 清除表单验证
      });
      this.optionsTwo.value = "";
      this.options.value = "";
    },
    changeVal(val) {
      this.$forceUpdate();
    },
    // 获取项目种类列表
    getSymbol(page, params, done) {
      this.$http({
        url: this.$http.adornUrl("/item/itemSummary/queryBySymbol"),
        method: "get",
        params: this.$http.adornParams(
          Object.assign(
            {
              symbol: ".DJI",
            },
            params
          ),
          false
        ),
      }).then(({ data }) => {
        if (data.code == 0) {
          console.log(data);
        }
        if (done) {
          done();
        }
      });
    },
    dataFormSubmit: Debounce(function () {
      if (this.row) {
        //更新修改
        //start
        this.$refs["dataForm"].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/purchasing/update`),
              method: "post",
              data: this.$http.adornData({
                id: this.dataForm.id,
                currency: this.dataForm.currency, //接受申购币种
                dataType: this.optionsTwo.value, //数据源类别
                expectedLaunchTime: this.dataForm.expectedLaunchTime, //预计上线时间
                idCode: this.dataForm.idCode, //	项目ID
                issuePrice: this.dataForm.issuePrice, //发行价
                maxQuantity: this.dataForm.maxQuantity, //最大申购数量
                minQuantity: this.dataForm.minQuantity, //最小申购数量
                projectName: this.dataForm.projectName, //申购项目名称
                projectTypeSymbol: this.options.value, //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
                //'projectTypeName': this.options.label,    //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
                publishTime: this.dataForm.publishTime, //公布时间
                subscriptionEndTime: this.dataForm.subscriptionEndTime, //结束申购时间
                subscriptionStartTime: this.dataForm.subscriptionStartTime, //开始申购时间
                whitePagerAddress: this.dataForm.whitePagerAddress, //白皮书地址
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
        //end
      } else {
        //新增
        //start
        this.$refs["dataForm"].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/purchasing/add`),
              method: "post",
              data: this.$http.adornData({
                currency: this.dataForm.currency, //接受申购币种
                dataType: this.optionsTwo.value, //数据源类别
                expectedLaunchTime: this.dataForm.expectedLaunchTime, //预计上线时间
                idCode: this.dataForm.idCode, //	项目ID
                issuePrice: this.dataForm.issuePrice, //发行价
                maxQuantity: this.dataForm.maxQuantity, //最大申购数量
                minQuantity: this.dataForm.minQuantity, //最小申购数量
                projectName: this.dataForm.projectName, //申购项目名称
                projectTypeSymbol: this.options.value, //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
                //'projectTypeName': this.options.label,    //项目总类 1 全球ETF 2 能源ETF 3 黄金ETF 4.人工智能ETF
                publishTime: this.dataForm.publishTime, //公布时间
                subscriptionEndTime: this.dataForm.subscriptionEndTime, //结束申购时间
                subscriptionStartTime: this.dataForm.subscriptionStartTime, //开始申购时间
                whitePagerAddress: this.dataForm.whitePagerAddress, //白皮书地址
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
        //end
      }
    }),
    handleAvatarSuccess(res, file) {
      // this.dataForm.methodImg = res.data.path
      //{"data":{"path":"null/2023-04-29/d3f084ea-391f-4ec9-a2dd-f9393221f58f.png",
      //"httpUrl":"https://trading-order-test.s3.amazonaws.com/null/2023-04-29/d3f084ea-391f-4ec9-a2dd-f9393221f58f.png"},"code":0,"msg":"","succeed":false}
      console.log(file);
      this.dataForm.imageUrl = URL.createObjectURL(file.raw); //显示地址
      this.dataForm.imgUrl = res.data.path; //接口传递
      console.log(this.dataForm.imageUrl);
      // if(res.code == 0){
      //   this.dataForm.image = res.data.httpUrl
      // }
    },
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
  width: 150px;
}
.speaInputTwo {
  width: 250px;
}
.romtitle {
  font-size: 18px;
  color: black;
}
</style>
