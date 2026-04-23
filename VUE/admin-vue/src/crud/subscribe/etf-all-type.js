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
    label: '承兑商昵称',
    prop: 'userName',
    search: true
  }, {
    label: '承兑商头像',
    prop: 'userCode',
    search: false
  }, {
    label: '承兑商UID',
    prop: 'recomUserName'
  }, {
    label: '承兑商类型',
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
    label: '用户名',
    prop: 'log',
    search: false
  }, {
    label: '用户名UID',
    prop: 'walletType'
  }, {
    label: '买卖方式',
    prop: 'amount'
  }, {
    label: '支付币种',
    prop: 'amountBefore'
  }, {
    label: '上架币种',
    prop: 'amountBefore'
  }, {
    label: '支付方式',
    prop: 'amountAfter'
  }, {
    label: '币种单价',
    prop: 'amountAfter'
  }, {
    label: '币种数量',
    prop: 'amountAfter'
  }, {
    label: '单笔订单限额',
    prop: 'amountAfter'
  }, {
    label: '保证金',
    prop: 'amountAfter'
  }, {
    label: '是否上架',
    prop: 'amountAfter'
  }, {
    label: '创建时间',
    prop: 'amountAfter'
  }, {
    label: '备注',
    prop: 'amountAfter'
  }]
}
