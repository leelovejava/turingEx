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
  searchLabelWidth:100,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  emptyBtn:false,
  menu:false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '订单号',
    prop: 'orderNo1',
    search: true,
    hide:true,
  }, {
    label: '用户',
    prop: 'username',
    solt:true,
  },{
    label: '用户名、UID',
    prop: 'userParam',
    search: true,
    hide:true,
  }, {
    label: 'UID',
    prop: 'usercode',
    search: false
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
    }],
    hide:true,
  }, {
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
    label: '订单号',
    prop: 'orderNo'
  }, {
    label: '订单类型',
    prop: 'orderType',
    search: false,
    type: 'select',
    dicData:[{
      label:'借款',
      value:1
    },{
      label:'新增质押',
      value:2
    },{
      label:'续借',
      value:3
    },{
      label:'还款',
      value:4
    },{
      label:'强平结清',
      value:5
    }]
  }, {
    label: '借款金额',
    prop: 'loanAmount'
  }, {
    label: '订单状态',
    prop: 'state',
    type: 'select',
    dicData:[{
      label:'全部',
      value:''
    },{
      label:'计息中',
      value:1
    },{
      label:'已结清',
      value:2
    },{
      label:'强平结清',
      value:3
    }]
  }, {
    label: '贷款币种',
    prop: 'loanCurrency'
  }, {
    label: '质押率',
    prop: 'pledgeRate'
  }, {
    label: '总负债',
    prop: 'debtAmount'
  }, {
    label: '总利息',
    prop: 'interestAmount'
  }, {
    label: '时利率',
    prop: 'hourlyRate'
  }, {
    label: '借款周期',
    prop: 'loanCycle'
  }, {
    label: '借款时间',
    prop: 'createTime'
  }, {
    label: '到期时间',
    prop: 'expireTime'
  }]
}
