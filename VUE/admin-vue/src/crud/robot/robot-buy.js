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
  menu:false, // 机器人刷单操作按钮暂时隐藏
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '机器人ID',
    prop: 'uuid'
  }, {
    label: '交易对',
    prop: 'symbol'
  }, {
    label: '交易对项目名称',
    prop: 'followMarket'
  }, {
    label: '最低交易量',
    prop: 'minmuanAmount'
  }, {
    slot:true,
    label: '交易量随机因子(%)',
    prop: 'RandomFactorOfTradingVolume'
  }, {
    label: '币种价格精度',
    prop: 'priceDecimals'
  }, {
    label: '币种数量精度',
    prop: 'numDecimals'
  }, {
    label: '买卖盘最高差价',
    prop: 'maxumPriceDiff'
  }, {
    label: '初始订单数',
    prop: 'buyNum'
  }, {
    label: '价格变化步长',
    prop: 'step'
  }, {
    label: '下单时间间隔(s)',
    prop: 'maxmunInterval'
  },{
    label: '机器人状态',
    prop: 'runningStatus',
    type: 'select',
    dicData: [{
      label: '停止',
      value: 0
    }, {
      label: '启动',
      value: 1
    }]
  },{
    label: '交易引擎状态',
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
