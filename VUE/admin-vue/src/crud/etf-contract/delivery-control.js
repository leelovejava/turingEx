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
    label: '交易对ID',
    prop: 'uuid'
  }, {
    label: '交易对名称',
    prop: 'symbol'
  }, {
    label: '交易对',
    prop: 'name'
  }, {
    label: '报价货币',
    prop: 'name'
  }, {
    label: '币对',
    prop: 'MinimumTradingVolume'
  }, {
    label: '交易精度',
    prop: 'RandomFactorOfTradingVolume'
  }, {
    label: '价格精度',
    prop: 'CurrencyPricePrecision'
  }, {
    label: '最小交易量',
    prop: 'CurrencyQuantityPrecision'
  }, {
    label: '最小交易额',
    prop: 'MaximumPriceDifferenceBetweenBuyingAndSellingOrders'
  }, {
    label: '前端显示状态',
    prop: 'InitialNumberOfOrders'
  }, {
    label: '交易状态',
    prop: 'StepSizeOfPriceChange'
  }, {
    label: '排序',
    prop: 'OrderInterval'
  },{
    label: '创建时间',
    prop: 'RobotStatus'
  }]
}
