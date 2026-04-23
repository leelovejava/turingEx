export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 260,
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
  column: [{
    label: '交易员名称',
    prop: 'name',
  },{
    label: '交易员名称',
    prop: 'name_para',
    hide:true,
    search: true
  }, {
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
  }, {
    label: '累计收益',
    prop: 'profit',
    search: false
  }, {
    label: '累计收益率(%)',
    prop: 'profit_ratio'
  }, {
    label: '累计跟随人数',
    prop: 'follower_sum'
  }, {
    label: '利润分成比例(%)',
    prop: 'profit_share_ratio'
  }, {
    label: '状态',
    prop: 'state',
    type: 'select',
    dicData:[{
      label:'开启带单',
      value:'1'
    },{
      label:'停止带单',
      value:'0'
    },{
      label:'禁止带单',
      value:'2'
    }]
  }, {
    label: '当前带队跟单人数',
    prop: 'follower_now'
  }, {
    label: '当前带队币种',
    prop: 'symbols'
  }, {
    label: '审核',
    prop: 'checked',
    type: 'select',
    dicData:[{
      label:'待审核',
      value:0
    },{
      label:'审核通过',
      value:1
    },{
      label:'审核不通过',
      value:-1
    }]
  }, {
    label: '入驻时间',
    prop: 'create_time'
  }]
}
