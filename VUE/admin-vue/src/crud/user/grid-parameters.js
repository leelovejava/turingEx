export const tableOptionData = {
  searchMenuSpan: 0,
  columnBtn: false,
  border: false,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: false,
  menuAlign: 'center',
  menuWidth: 180,
  align: 'center',
  refreshBtn: false,
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
    label: '层级',
    prop: 'userLevel',
  },{
    label: '用户名',
    prop: 'userName',
  },{
    label: 'UID',
    prop: 'uid',
  },{
    label: '账户类型',
    prop: 'accountType',
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
      label:'试用账号',
      value:'TEST'
    }]
  },
  {
    label: '基础认证',
    prop: 'realNameAuthority',
    type: 'select',
    dicData:[{
      label:'已验证',
      value:true
    },{
      label:'未验证',
      value:false
    }]
  }
  ]
}
