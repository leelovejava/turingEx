<template>
  <div class="mod-prod">
    <el-form :inline="true"
             :model="dataForm"
             @keyup.enter.native="getDataList(this.page)">
      <el-form-item label="订单号:">
        <el-input v-model="dataForm.orderNumber"
                  placeholder="订单号（完整)"
                  clearable></el-input>
      </el-form-item>
      <el-form-item label="用户名/UID:">
        <el-input v-model="dataForm.orderNumber2"
                  placeholder="用户名（钱包地址）、UID"
                  clearable></el-input>
      </el-form-item>
      <el-form-item label="账号:">
        <template>
          <el-select v-model="dataForm.status"
                     clearable
                     placeholder="请选择">
            <el-option v-for="item in options"
                       :key="item.value"
                       :label="item.label"
                       :value="item.value">
            </el-option>
          </el-select>
        </template>
      </el-form-item>
      <el-form-item>
        <el-button type="primary"
                   icon="el-icon-search"
                   size="small"
                   @click="getDataList()">查询</el-button>
        <el-button @click="clearDatas()"
                   size="small">清空</el-button>
      </el-form-item>
    </el-form>
    <div class="main">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="全部" name="1"></el-tab-pane>
        <el-tab-pane label="计息中" name="2"></el-tab-pane>
        <el-tab-pane label="已结清" name="3"></el-tab-pane>
        <el-tab-pane label="强平结清" name="4"></el-tab-pane>
      </el-tabs>
      <div class="content">
        <div class="tit">
          <el-row style="width:100%">
            <el-col :span="2"><span class="item product">用户</span></el-col>
            <el-col :span="2"><span class="item">UID</span></el-col>
            <el-col :span="2"><span class="item">账户类型</span></el-col>
            <el-col :span="2"><span class="item">订单号</span></el-col>
            <el-col :span="2"><span class="item">订单类型</span></el-col>
            <el-col :span="2"><span class="item">贷款币种 </span></el-col>
            <el-col :span="1"><span class="item">质押率</span></el-col>
            <el-col :span="1"><span class="item">总负债</span></el-col>
            <el-col :span="2"><span class="item">总利息</span></el-col>
            <el-col :span="2"><span class="item">时利率</span></el-col>
            <el-col :span="2"><span class="item">借款周期</span></el-col>
            <el-col :span="2"><span class="item">借款时间</span></el-col>
            <el-col :span="2"><span class="item">到期时间</span></el-col>
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
    <!-- 弹窗, 新增 / 修改 -->
    <add-or-update v-if="addOrUpdateVisible"
                   ref="addOrUpdate"
                   @refreshDataList="getDataList"></add-or-update>
    <consignment-info v-if="consignmentInfoVisible"
                      ref="consignmentInfo"
                      @inputCallback="getWaitingConsignmentExcel"></consignment-info>
  </div>
</template>

<script>
import { tableOption } from '@/crud/prod/prodTag'
import AddOrUpdate from './prodTag-add-or-update'
export default {
  data () {
    return {
      dataForm: {},
      dataForm2:{},
      dataList: [],
      dateRange: [],
      activeName:'1',
      options: [{
        value: 1,
        label: '所有账号'
      },
      {
        value: 2,
        label: '正式账号'
      },
      {
        value: 3,
        label: '演示账号'
      }],
      page: {
        total: 0, // 总页数
        currentPage: 1, // 当前页数
        pageSize: 10 // 每页显示多少条
      },
      dataListLoading: false,
      consignmentInfoVisible: false,
      tableOption: tableOption,
      permission: {
        delBtn: this.isAuth('prod:prodTag:delete')
      },
      addOrUpdateVisible: false
    }
  },
  components: {
    AddOrUpdate
  },
  created () {
  },
  mounted () {
  },
  methods: {
    getDataList (page, params, done) {
      this.dataListLoading = true
      this.$http({
        url: this.$http.adornUrl('/prod/prodTag/page'),
        method: 'get',
        params: this.$http.adornParams(Object.assign({
          current: page == null ? this.page.currentPage : page.currentPage,
          size: page == null ? this.page.pageSize : page.pageSize
        }, params))
      }).then(({ data }) => {
        this.dataList = data.records
        this.page.total = data.total
        this.dataListLoading = false
        if (done) {
          done()
        }
      })
    },
    // 新增 / 修改
    addOrUpdateHandle (id) {
      this.addOrUpdateVisible = true
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id)
      })
    },
    deleteHandle (id) {
      this.$confirm('确定进行删除操作?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$http({
          url: this.$http.adornUrl('/prod/prodTag/' + id),
          method: 'delete',
          data: this.$http.adornData({})
        }).then(({ data }) => {
          this.$message({
            message: '操作成功',
            type: 'success',
            duration: 1500,
            onClose: () => {
              this.getDataList(this.page)
            }
          })
        })
      }).catch(() => { })
    },
    handleClick(tab, event) {
        console.log(tab, event)
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
    /**
     * 刷新回调
     */
    refreshChange () {
      this.getDataList(this.page)
    },
    searchChange (params, done) {
      this.getDataList(this.page, params, done)
    }
  }
}
</script>

<style lang="scss" scoped>
.mod-prod{
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
