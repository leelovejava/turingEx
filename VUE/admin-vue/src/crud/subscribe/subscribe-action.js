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
    label: 'ID',
    prop: 'userName',
 
  }, {
    label: '活动名称',
    prop: 'userCode',
    search: true
  }, {
    label: '活动图片',
    prop: 'recomUserName'
  }, {
    label: '账户类型',
    prop: 'roleName',
    search: false,
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
    label: '活动状态',
    prop: 'log',
    search: false
  }, {
    label: '一级邀请',
    prop: 'walletType'
  }, {
    label: '活动类型',
    prop: 'amount'
  }, {
    label: '币种数量/价格',
    prop: 'amountBefore'
  }, {
    label: '开始时间',
    prop: 'amountBefore'
  }, {
    label: '结束时间',
    prop: 'amountAfter'
  }, {
    label: '公告链接',
    prop: 'amountAfter'
  }, {
    label: '活动链接',
    prop: 'amountAfter'
  }, {
    label: '进度',
    prop: 'amountAfter'
  }, {
    label: '创建时间',
    prop: 'amountAfter'
  }]
}
