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
  emptyBtn:true,
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '用户名',
    prop: 'userName',
    placeholder:'用户名(钱包地址)、UID',
    search: true
  }, {
    label: 'UID',
    prop: 'userCode',
  },{
    label: '账户类型',
    prop: 'roleName',
    type: 'select',
    dicData:[{
      label:'账号',
      value:''
    },{
      label:'正式账号',
      value:'MEMBER'
    },{
      label:'演示账号',
      value:'GUEST'
    },{
      label:'代理商',
      value:'AGENT'
    },{
      label:'试用账号',
      value:'TEST'
    }]
  }, {
    label: '推荐人',
    prop: 'usernameParent',
  }, {
    label: '收益费率',
    prop: 'config',
  }, {
    label: '活动标题',
    prop:'title',
    search: true
  }, {
    slot:true,
    label: '活动标题图片',
    prop: 'img',
  }, {
    label: '用户USDT限制数量',
    prop: 'usdt',
  }, {
    label: '奖励ETH数量',
    prop: 'eth'
  }]
}
