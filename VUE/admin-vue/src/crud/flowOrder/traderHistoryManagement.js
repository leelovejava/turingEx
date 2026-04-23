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
  searchLabelWidth:100,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  emptyBtn:true,
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [ {
    label: '用户名',
    prop: 'username',
  },{
    label: '用户名',
    prop: 'username_para',
    placeholder:'用户名、UID',
    hide:true,
    search: true
  },{
    label: '用户类型',
    prop: 'rolename',
    type: 'select',
    search: true,
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
    label: 'UID',
    prop: 'usercode',
    search: false
  },{
    label: '交易员名称',
    prop: 'trader_name',
  },{
    label: '交易员名称',
    prop: 'name_para',
    hide:true,
    search: true
  }, {
    label: '品种',
    prop: 'itemname',
    search: false
  }, {
    label: '操作',
    prop: 'direction',
    type: 'select',
    dicData:[{
      label:'多',
      value:'buy'
    },{
      label:'空',
      value:'sell'
    }]
  }, {
    label: '开仓价格',
    prop: 'trade_avg_price'
  }, {
    label: '平仓价格',
    prop: 'CLOSE_AVG_PRICE'
  }, {
    label: '盈亏',
    prop: 'profit'
  }, {
    label: '涨跌幅(%)',
    prop: 'change_ratio'
  }, {
    label: '状态',
    prop: 'state',
    type: 'select',
    dicData:[{
      label:'持仓',
      value:'submitted '
    },{
      label:'平仓',
      value:'created'
    }]
  },  {
    label: '创建时间',
    sortable:true,
    prop: 'create_time'
  }, {
    label: '平仓时间',
    sortable:true,
    prop: 'close_time'
  }]
}
