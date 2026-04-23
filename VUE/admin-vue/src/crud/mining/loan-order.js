export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 120,
  searchLabelWidth:100,
  searchMenuSpan:3,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  menu:true,
  emptyBtn:false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '订单号',
    prop: 'orderNo',
    hide:true,
    search: true
  },{
    label: '订单号',
    prop: 'uuid'
  },{
    label: '用户名',
    prop: 'userName',
    search: true
  },{
    label: '账户类型',
    prop: 'rolename',
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
    label: '借贷币种',
    prop: 'symbol'
  }, {
    label: '借贷额度',
    prop: 'quota'
  }, {
    label: '借贷利息',
    prop: 'totalInterest'
  },{
    label: '借贷状态',
    slot: true,
    prop: 'state'
  }, {
    label: '借贷天数(天)',
    prop: 'term'
  }, {
    label: '申请时间',
    prop: 'createTime'
  }, {
    label: '还款周期(天)',
    prop: 'repayCycle'
  }, {
    label: '日利率(%)',
    prop: 'dailyRate'
  }, {
    label: '还款方式',
    slot: true,
    prop: 'repayment'
  }, {
    label: '驳回原因',
    prop: 'reason',
  }, {
    label: '放款机构',
    slot: true,
    prop: 'lendingInstitution',
  }]
}
