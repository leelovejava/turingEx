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
  emptyBtn:false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '用户名',
    prop: 'userName',
    width:'135',
  }, {
    label: '用户名',
    prop: 'usendh',
    search: true,
    hide:true,
  },{
    label: 'UID',
    prop: 'userCode',
    width:'135',
  },{
    searchslot: true,
    showColumn: false,
    type: 'select',
    label: '操作类型',
    prop: 'ndh',
    search:true,
    hide:true
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
  },{
    label: '账户类型',
    prop: 'role',
    search: true,
    type: 'select',
    hide:true,
  }, {
    label: '日志',
    prop: 'log',
  },{
    label: '日志',
    prop: 'logndh',
    search: true,
    hide:true
  },{
    label: '操作者',
    prop: 'operator',
    width:'135',
  },{
    label: '操作者',
    prop: 'operatorndh',
    search: true,
    hide:true,
  },{
    label: '时间',
    prop: 'createTime',
    width:'135',
  }]
}
