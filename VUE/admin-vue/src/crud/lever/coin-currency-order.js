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
    prop: 'offset',
    type: 'select',
    dicData:[{
      label:'买入',
      value:'open'
    },{
      label:'卖出',
      value:'close'
    },{
      label:'涨',
      value:'buy'
    },{
      label:'跌',
      value:'sell'
    }]
  }, {
    label: '委托张数',
    prop: 'volumeOpen'
  }, {
    label: '杠杆',
    prop: 'leverRate'
  }, {
    label: '报价类型',
    prop: 'symbol'
  }, {
    label: '限价',
    prop: 'unitAmount',
  }, {
    label: '止盈止损',
    prop: 'stopPriceProfit',
  }, {
    label: '状态',
    prop: 'state',
    type: 'select',
    dicData:[{
      label:'已提交',
      value:'submitted'
    },{
      label:'平仓',
      value:'created'
    },{
      label:'已撤销',
      value:'canceled'
    }]
  }, {
    label: '创建时间',
    prop: 'createTime',
    type: "datetime",
    viewDisplay: false,
    format: "yyyy-MM-dd HH:mm:ss", // 组件展示的日期格式
    valueFormat:"yyyy-MM-dd HH:mm:ss", // 组件vue值
  }]
}
