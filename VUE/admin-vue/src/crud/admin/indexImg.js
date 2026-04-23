export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false, //开启勾选功能
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 250,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '用户名',
    placeholder:'用户名、UID',
    prop: 'userName',
    search: true,
  }, {
    label: '实名姓名',
    prop: 'realName'
  }, {
    label: 'UID',
    prop: 'userCode'
  }, {
    label: '账号类型',
    prop: 'roleName',
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
    label: '类型',
    prop: 'typeName'
  }, {
    label: '备注',
    prop: 'remark'
  }]
}
