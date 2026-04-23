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
  refreshBtn: true,
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
    prop: 'userName',
    search:true,
  }, {
    label: 'UID',
    prop: 'userCode',
  }, {
    label: '订单号',
    prop: 'orderNo',
    search:true,
  },{
    label: '账户类型',
    prop: 'roleName',
    search: true,
    type: 'select',
    dicData:[{
      label:'所有账号',
      value:''
    },{
      label:'正式账号',
      value:'MEMBER'
    },{
      label:'演示账号',
      value:'GUEST'
    },{
      label:'试用账号',
      value:'TEST'
    }]
  }, {
    label: '品种',
    prop: 'itemName'
  }, {
    label: '操作',
    prop: 'direction',
    type: 'select',
    dicData:[{
      label:'买入',
      value:'buy'
    },{
      label:'卖出',
      value:'sell'
    }]
  }, {
    label: '成交均价',
    prop: 'tradeAvgPrice'
  }, {
    label: '止盈止损',
    prop: 'stopPriceProfit'
  }, {
    label: '支付单位',
    prop: 'symbol'
  }, {
    label: '剩余/委托金额',
    prop: 'speanMone',
    slot:true
  }, {
    label: '剩余/委托保证金',
    prop: 'speanTwo',
    slot:true
  }, {
    label: '用户钱包余额',
    prop: 'money'
  }, {
    label: '盈亏',
    prop: 'profit'
  }, {
    label: '状态',
    prop: 'state',
    type: 'select',
    dicData:[{
      label:'持仓',
      value:'submitted'
    },{
      label:'平仓',
      value:'created '
    }]
  }, {
    label: '创建时间',
    prop: 'createTime',
    type: "datetime",
    search: true,
    viewDisplay: false,
    format: "yyyy-MM-dd HH:mm:ss", // 组件展示的日期格式
    valueFormat:"yyyy-MM-dd HH:mm:ss", // 组件vue值
  }, {
    label: '平仓时间',
    prop: 'closeTime',
    type: "datetime",
    search: true,
    viewDisplay: false,
    format: "yyyy-MM-dd HH:mm:ss", // 组件展示的日期格式
    valueFormat:"yyyy-MM-dd HH:mm:ss", // 组件vue值
  }]
}
