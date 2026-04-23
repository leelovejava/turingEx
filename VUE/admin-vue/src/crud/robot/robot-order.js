export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 200,
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
    label: '订单号',
    prop: 'OrderNo'
  }, {
    label: '用户ID',
    prop: 'userId'
  }, {
    label: '交易对',
    prop: 'symbol'
  }, {
    label: '项目名称',
    prop: 'name'
  }, {
    label: '委托量(USDT)',
    prop: 'EntrustedQuantity'
  }, {
    label: '成交量(USDT)',
    prop: 'turnover'
  }, {
    label: '挂单类型',
    prop: 'OrderType',
    type: 'select',
    dicData: [{
      label: '市价',
      value: "1"
    }, {
      label: '限价',
      value: "2"
    }]
  }, {
    label: '挂单方向',
    prop: 'OrderDirection',
    type: 'select',
    dicData: [{
      label: '买入',
      value: "1"
    }, {
      label: '卖出',
      value: "2"
    }]
  }, {
    label: '挂单价格',
    prop: 'ListingPrice'
  }, {
    label: '挂单时间',
    prop: 'RegistrationTime'
  }, {
    label: '挂单状态',
    prop: 'OrderStatus',
    type: 'select',
    dicData: [{
      label: '交易中',
      value: "1"
    }, {
      label: '已完成',
      value: "2"
    }, {
      label: '已撤单',
      value: "3"
    }]
  }]
}
