export const tableOption11 = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 350,
  align: 'center',
  refreshBtn: false,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  menu:false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '日期',
    prop: 'dateTime'
    // search: true
  }, {
    label: '充提',
    prop: 'remark',
    // search: true,
    children: [{
      label: '充值',
      prop: 'age',
      display:false,
      children: [{
        label: 'USDT',
        prop: 'recharge_usdt',
        display:false
      }, {
        label: 'ETH',
        prop: 'recharge_eth',
        display:false
      }, {
        label: 'BTC',
        prop: 'recharge_btc',
        display:false
      }, {
        label: 'USDC',
        prop: 'recharge_usdc',
        display:false
      }]
    }, {
      label: '提现',
      prop: 'phone',
      display:false,
      children: [{
        label: 'USDT',
        prop: 'withdraw',
        display:false
      }, {
        label: 'ETH',
        prop: 'withdraw_eth',
        display:false
      }, {
        label: 'BTC',
        prop: 'withdraw_btc',
        display:false
      },{
        label: 'USDC',
        prop: 'withdraw_usdc',
        display:false
      }]
    }]
  }, {
    label: '赠送(USDT)',
    prop: 'gift_money'
  },{
    label: '充提差额(USDT)',
    prop: 'difference'
  },{
    label: '交易盈亏',
    prop: 'business_profit'
  },{
    label: '手续费',
    prop: 'totle_fee'
    // search: true
  }, {
    label: '收益',
    prop: 'totle_income'
  }]
}
