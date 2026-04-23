export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 230,
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
    label: '跟随交易员名称',
    prop: 'trader_name',
  },{
    label: '跟随交易员名称',
    prop: 'name_para',
    hide:true,
    search: true
  }, {
    label: '固定张数或比例/值',
    prop: 'followType',
   slot:true
  }, {
    label: '最大持仓张数',
    prop: 'volume_max'
  }, {
    label: '跟随收益',
    prop: 'profit'
  }, {
    label: '跟随本金',
    prop: 'amount_sum'
  }, {
    label: '状态',
    prop: 'state',
    type: 'select',
    dicData:[{
      label:'跟随',
      value:'1'
    },{
      label:'取消跟随',
      value:'2'
    }]
  }, {
    label: '跟随时间',
    prop: 'create_time'
  }]
}
