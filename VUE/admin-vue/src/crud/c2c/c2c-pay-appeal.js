export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  searchSpan:7,
  searchLabelWidth:190,
  searchMenuSpan:11,
  menuWidth: 150,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [ {
    label: '用户名、用户UID',
    prop: 'userCode',
    search: true,
    hide:true,
  },{
    label: '用户名',
    prop: 'user_name',
    slot:true,
  }, {
    label: '用户UID',
    prop: 'user_code',
    search: false
  }, {
    label: '账户类型',
    prop: 'role_name',
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
    label: '承兑商类型',
    prop: 'c2c_user_type',
    type: 'select',
    dicData:[{
      label:'所有承兑商类型',
      value:''
    },{
      label:'后台承兑商',
      value:1
    },{
      label:'用户承兑商',
      value:2
    }]
  }, {
    label: '承兑商类型',
    prop: 'c2cUserType',
    type: 'select',
    search: true,
    hide:true,
    dicData:[{
      label:'所有承兑商类型',
      value:''
    },{
      label:'后台承兑商',
      value:1
    },{
      label:'用户承兑商',
      value:2
    }]
  }, {
    label: '承兑商昵称、承兑商UID',
    prop: 'c2cUserCode',
    search: true,
    hide:true,
  },{
    label: '承兑商用户名、用户UID',
    prop: 'c2cUserPartyCode',
    search: true,
    hide:true,
  },{
    label: '订单号',
    prop: 'orderNo',
    search: true,
    hide:true,
  },{
    label: '承兑商UID',
    prop: 'c2c_user_code',
  }, {
    label: '承兑商用户名',
    prop: 'c2c_user_party_name',
  }, {
    label: '关联订单号',
    prop: 'order_no'
  }, {
    label: '申诉详情',
    prop: 'nth',
    slot:true,
  }, {
    label: '申诉状态',
    prop: 'state',
    type: 'select',
    dicData:[{
      label:'全部',
      value:''
    },{
      label:'已提交',
      value:'0'
    },{
      label:'已处理',
      value:'1'
    }]
  }, {
    label: '申诉状态',
    prop: 'status',
    type: 'select',
    search: true,
    hide:true,
    dicData:[{
      label:'全部',
      value:''
    },{
      label:'已提交',
      value:'0'
    },{
      label:'已处理',
      value:'1'
    }]
  }, {
    label: '创建时间',
    prop: 'create_time'
  }, {
    label: '更新时间',
    prop: 'update_time'
  }]
}
