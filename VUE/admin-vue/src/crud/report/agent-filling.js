export const tableOption = {
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
    label: '用户',
    prop: '',
 
    children: [{
      label: '用户名',
      prop: 'username',
      search: true,
      placeholder:"用户名、UID",
    }, {
      label: 'UID',
      prop: 'UID',
      display:false
    }, {
      label: '用户数',
      prop: 'reco_member',
      slot:true,
    }, {
      label: '代理数',
      prop: 'reco_agent',
      display:false
    }]
  },{
    label: '开始时间',
    prop: 'startTime',
    type: "datetime",
    search: true,
    viewDisplay: false,
    hide: true,
    format: "yyyy-MM-dd HH:mm:ss", // 组件展示的日期格式
    valueFormat:"yyyy-MM-dd HH:mm:ss", // 组件vue值
  },
  {
    label: '结束时间',
    prop: 'endTime',
    type: "datetime",
    format: "yyyy-MM-dd HH:mm:ss",
    valueFormat:"yyyy-MM-dd HH:mm:ss",
    hide: true,
    searchClearble:false,
    search: true,
    viewDisplay: false,
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
    },{
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
    label: '交易盈亏(ETF)',
    prop: 'business_profit'
  },{
    label: '手续费(ETF)',
    prop: 'totle_fee'
    // search: true
  }, {
    label: '总收益(ETF)',
    prop: 'totle_income'
  }]
}
