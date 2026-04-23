<template>
  <div class="mod-transport">
  <el-descriptions title="查询条件"></el-descriptions>
  <el-divider></el-divider>
  <el-form :inline="true"
             :model="dataForm"
             @keyup.enter.native="getDataList(this.page)">

      <el-form-item label="货币代码">
        <el-input v-model="dataForm.orderNumber2"
                  placeholder="货币代码"
                  clearable></el-input>
      </el-form-item>

      <el-form-item>
        <el-button type="primary"
                   icon="el-icon-search"
                   size="small"
                   @click="getDataList()">查询</el-button>
        <!-- <el-button @click="clearDatas()"
                   size="small">清空</el-button> -->
      </el-form-item>
    </el-form>
    <el-divider></el-divider>
    <el-descriptions title="查询结果"></el-descriptions>
    <div class="main">
      <div class="content">
        <div class="tit">
          <el-row style="width:100%">
            <el-col :span="4"><span class="item product">币种名称</span></el-col>
            <el-col :span="3"><span class="item">代码</span></el-col>
            <el-col :span="4"><span class="item">交易对</span></el-col>
            <el-col :span="4"><span class="item">精度(位)</span></el-col>
            <el-col :span="3"><span class="item">交易量倍数</span></el-col>
            <el-col :span="3"><span class="item">借贷利率</span></el-col>
            <el-col :span="3"><span class="item"></span></el-col>
          </el-row>
        </div>
        <div class="prod"
             v-for="order in dataList"
             :key="order.orderId">
          <div class="prod-tit">
            <span>订单编号：{{order.orderNumber}}</span>
            <span>下单时间：{{order.createTime}}</span>
            <!-- <span>买家：19999999999</span>
            <span >联系电话：19999999999</span> -->
          </div>
          <div class="prod-cont">
            <el-row style="width:100%">
              <el-col :span="12">
                <div class="prod-item">
                  <div class="items name"
                       v-for="orderItem in order.orderItems"
                       :key="orderItem.orderItemId">
                    <div class="prod-image">
                      <img :src="resourcesUrl + orderItem.pic"
                           style="height:100px;width: 100px;">
                    </div>
                    <div class="prod-name">
                      <span>{{orderItem.prodName}}</span>
                      <span class="prod-info">{{orderItem.skuName}}</span>
                    </div>
                    <div class="prod-price">
                      <span>￥{{orderItem.price}}</span>
                      <span>×{{orderItem.prodCount}}</span>
                    </div>
                  </div>
                </div>
              </el-col>
              <el-col :span="3"
                      style="height: 100%;">
                <div class="item">
                  <div>
                    <span class="totalprice">￥{{order.actualTotal}}</span>
                    <span v-if="order.freightAmount">（含运费：￥{{order.freightAmount}}）</span>
                    <span>共{{order.productNums}}件</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="3"
                      style="height: 100%;">
                <div class="item">
                  <div>
                    <span v-if="order.payType === 1">微信支付</span>
                    <span v-else-if="order.payType === 2">支付宝</span>
                    <span v-else>手动代付</span>
                  </div>
                </div>
              </el-col>
              <el-col :span="3"
                      style="height: 100%;">
                <div class="item">
                  <span v-if="order.status === 1"
                        size="small"
                        type="danger">待付款</span>
                  <span v-else-if="order.status === 2"
                        size="small"
                        type="danger">待发货</span>
                  <span v-else-if="order.status === 3"
                        size="small"
                        type="danger">待收货</span>
                  <span v-else-if="order.status === 4"
                        size="small"
                        type="danger">待评价</span>
                  <span v-else-if="order.status === 5"
                        size="small"
                        type="danger">成功</span>
                  <span v-else
                        size="small">失败</span>
                </div>
              </el-col>
              <el-col :span="3"
                      style="height: 100%;">
                <div class="item">
                  <div class="operate">
                    <!-- <button onclick="">打印订单</button><br> -->
                    <el-button v-if="isAuth('order:order:update')"
                               type="text"
                               size="small"
                               @click="addOrUpdateHandle(order.orderNumber)">查看</el-button>
                  </div>
                </div>
              </el-col>
            </el-row>
          </div>
          <div class="remark">
            <div class="buyer-remark">
              <span>备注:{{order.remarks}}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- 空 -->
    <div class="empty-tips">暂无数据</div>
    <el-pagination @size-change="sizeChangeHandle"
                   @current-change="currentChangeHandle"
                   :current-page="page.pageIndex"
                   :page-sizes="[10, 20, 50, 100]"
                   :page-size="page.pageSize"
                   :total="page.total"
                   layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
  </div>
</template>

<script>
import { tableOption } from '@/crud/shop/transport'
import AddOrUpdate from './agent-filling-report-add-or-update'
export default {
  data () {
    return {
      dateRange: [],
      option1: [{
        value: 1,
        label: '所有合约'
      },
      {
        value: 2,
        label: '正式账号合约'
      },
      {
        value: 3,
        label: '演示账号合约'
      },
      {
        value: 4,
        label: '试用账号合约'
      }],
      option2: [{
        value2: 1,
        label2: 'DOGE/USTD'
      },
      {
        value2: 2,
        label2: 'MLN/USTD'
      },
      {
        value2: 3,
        label2: 'QTUM/USTD'
      },
      {
        value2: 4,
        label2: 'BTC/USTD'
      }],
      dataForm: {
        transName: ''
      },
      activeName: '1', //选项卡
      activeName2:'1',
      dataList: [],
      dataListLoading: false,
      dataListSelections: [],
      addOrUpdateVisible: false,
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10 // 每页显示多少条
      },
      tableOption: tableOption
    }
  },
  components: {
    AddOrUpdate
  },
  methods: {
    // 获取数据列表
    getDataList (page, params, done) {
      page = (page === undefined ? this.page : page)
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/order/order/page'),
        method: 'get',
        params: this.$http.adornParams(
          Object.assign(
            {
              current: page == null ? this.page.currentPage : page.currentPage,
              size: page == null ? this.page.pageSize : page.pageSize,
              'orderNumber': this.dataForm.orderNumber,
              'status': this.dataForm.status,
              'startTime': this.dateRange === null ? null : this.dateRange[0], // 开始时间
              'endTime': this.dateRange === null ? null : this.dateRange[1] // 结束时间
            },
            params
          ), false
        )
      }).then(({ data }) => {
        this.dataList = data.records
        this.page.total = data.total
        this.dataListLoading = false
        if (done) {
          done()
        }
      })
    },
    // 清除数据
    clearDatas () {
      this.dataForm = {}
      this.dateRange = []
    },
    // 每页数
    sizeChangeHandle (val) {
      this.page.pageSize = val
      this.page.currentPage = 1
      this.getDataList(this.page)
    },
     // 当前页
    currentChangeHandle (val) {
      this.page.currentPage = val
      this.getDataList(this.page)
    },
    // 选项卡 
    handleClick(tab, event) {
        console.log(tab, event)
      },
    handleClick2(tab, event) {
        console.log(tab, event)
    },
    // 新增 / 修改
    addOrUpdateHandle (id) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id)
      })
    },
    // 删除
    deleteHandle (id) {
      var ids = id ? [id] : this.dataListSelections.map(item => { return item.transportId })
      this.$confirm(
        `确定进行[${id ? '删除' : '批量删除'}]操作?`,
        '提示',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        this.$http({
          url: this.$http.adornUrl('/shop/transport'),
          method: 'delete',
          data: this.$http.adornData(ids, false)
        }).then(({data}) => {
          this.$message({
            message: '操作成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              // this.getDataList(this.page)
              this.refreshChange()
            }
          })
        })
      }).catch((e) => {
        console.log('e: ', e)
      })
    },

    // 条件查询
    searchChange (params, done) {
      this.getDataList(this.page, params, done)
    },
    // 刷新回调用
    refreshChange () {
      this.page = this.$refs.crud.$refs.tablePage.defaultPage
      this.getDataList(this.page)
      this.dataListSelections = []
      this.$refs.crud.selectClear()
    },
    // 多选变化
    selectionChange (val) {
      console.log('val: ', val)
      this.dataListSelections = val
    }
  }
}
</script>
<style lang="scss" scoped>
.mod-transport {
  .tit {
    display: flex;
    height: 45px;
    align-items: center;
    background: #323338;
    color: #FFFFFF;
  }
  .tit .item {
    padding: 0 10px;
    width: 10%;
    text-align: center;
  }
  .tit .product {
    width: 25%;
  }
  .prod-tit {
    padding: 10px;
    background: #f8f8f9;
    border-left: 1px solid #dddee1;
    border-top: 1px solid #dddee1;
    border-right: 1px solid #dddee1;
  }
  .prod-tit span {
    margin-right: 15px;
  }
  .prod-cont {
    display: flex;
    border-top: 1px solid #dddee1;
    border-bottom: 1px solid #dddee1;
    border-left: 1px solid #dddee1;
    color: #495060;
  }
  .prod-cont .item {
    display: flex;
    display: -webkit-flex;
    align-items: center;
    justify-content: center;
    padding: 10px;
    // width: 10%;
    border-right: 1px solid #dddee1;
    text-align: center;
    height: 100%;
  }
  .prod-cont .item span {
    display: block;
  }
  .prod-cont .prod-item {
    // width: 38%;
    display: flex;
    flex-direction: column;
    border-right: 1px solid #dddee1;
  }
  .prod-name {
    width: 55%;
    text-align: left;
  }
  .prod-price {
    position: absolute;
    right: 40px;
    text-align: right;
  }
  .prod-price span {
    display: block;
    margin-bottom: 10px;
  }
  .prod-name .prod-info {
    display: block;
    color: #80848f;
    margin-top: 30px;
  }
  .prod-cont .items.name {
    display: flex;
    position: relative;
    padding: 20px;
    // height: 100px;
    border-bottom: 1px solid #dddee1;
  }
  .prod-cont .items.name:last-child {
    border-bottom: none;
  }
  .prod-image {
    margin-right: 20px;
    width: 100px;
    height: 100px;
  }
  .prod-image img {
    width: 100px;
    height: 100px;
  }
  .item span {
    display: block;
    margin-bottom: 10px;
  }
  .item .operate {
    color: #2d8cf0;
  }
  .item .totalprice {
    color: #c00;
  }
  .prod .remark {
    width: 100%;
    height: 50px;
    line-height: 50px;
    background-color: #e8f7f6;
    border-left: 1px solid #dddee1;
    border-right: 1px solid #dddee1;
    border-bottom: 1px solid #dddee1;
    margin-bottom: 20px;
  }
  .buyer-remark {
    padding: 0 20px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }

  .empty-tips {
    display: block;
    width: 100%;
    text-align: center;
    margin: 50px 0;
    color: #999;
  }
}
.el-col{
  height: 48px;
  border-right: 1px solid #D8D9DE;
  line-height: 48px;
  text-align: center;
}
</style>
