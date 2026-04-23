export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 130,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: 'ID',
    prop: 'uuid'
  }, {
    label: '订单号',
    prop: 'symbol'
  }, {
    label: '用户',
    prop: 'name'
  }, {
    label: '币对',
    prop: 'MinimumTradingVolume'
  }, {
    label: '委托方式',
    prop: 'RandomFactorOfTradingVolume'
  }, {
    label: '委托价格',
    prop: 'CurrencyPricePrecision'
  }, {
    label: '触发价',
    prop: 'CurrencyQuantityPrecision'
  }, {
    label: '委托数量',
    prop: 'MaximumPriceDifferenceBetweenBuyingAndSellingOrders'
  }, {
    label: '已成交数量',
    prop: 'InitialNumberOfOrders'
  }, {
    label: '预期交易额(USDT)',
    prop: 'StepSizeOfPriceChange'
  }, {
    label: '已成交额(USDT)',
    prop: 'OrderInterval'
  },{
    label: '交易进度',
    prop: 'RobotStatus',
    type: 'select',
    dicData: [{
      label: '停止',
      value: 0
    }, {
      label: '启动',
      value: 1
    }]
  },{
    label: '明细',
    prop: 'TransactionEngineStatus',
    type: 'select',
    dicData: [{
      label: '已停止',
      value: 0
    }, {
      label: '运行中',
      value: 1
    }]
  },{
    label: '创建时间',
    prop: 'TransactionEngineStatus',
    type: 'select',
    dicData: [{
      label: '已停止',
      value: 0
    }, {
      label: '运行中',
      value: 1
    }]
  }]
}
