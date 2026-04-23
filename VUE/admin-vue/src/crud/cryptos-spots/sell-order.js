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
    prop: 'id'
  }, {
    label: '订单号',
    prop: 'orderNo'
  }, {
    label: '用户',
    prop: 'userName'
  }, {
    label: '币对',
    prop: 'symbolName'
  }, {
    label: '委托方式',
    prop: 'orderPriceType',
    type: 'select',
    dicData: [{
      label: '限价交易',
      value: 'limit'
    }, {
      label: '市价交易',
      value: 'opponent'
    }]
  }, {
    label: '委托价格',
    prop: 'price'
  }, {
    label: '触发价',
    prop: 'closePrice'
  }, {
    label: '委托数量',
    prop: 'volume'
  }, {
    label: '已成交数量',
    prop: 'volume'
  }, {
    label: '预期交易额(USDT)',
    prop: 'StepSizeOfPriceChange'
  }, {
    label: '已成交额(USDT)',
    prop: 'OrderInterval'
  },{
    label: '交易进度',
    prop: 'state',
    type: 'select',
    dicData: [{
      label: '已提交',
      value: 'submitted'
    }, {
      label: '已撤销',
      value: 'canceled'
    }, {
      label: '委托完成',
      value: 'created'
    }]
  },{
    slot:true,
    label: '明细',
    prop: 'TransactionEngineStatus'
  },{
    label: '创建时间',
    prop: 'createTime'
  }]
}
