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
  emptyBtn:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [ {
    label: '用户名',
    prop: 'userName',
    placeholder:'请输入用户名(钱包地址)、UID',
    search: true,
  },{
    label: 'UID',
    prop: 'userCode',
  },{
    label: '账号类型',
    prop: 'roleName',
    type: 'select',
    search:true,
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
    label: '币种',
    prop: 'coinSymbol',
  },{
    label: '地址',
    prop: 'address',
    placeholder:'请输入充值地址(完整)',
    search: true,
  }]
}
