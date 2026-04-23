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
    label: '项目ID',
    prop: 'userName',
    search: true
  }, {
    label: '项目种类',
    prop: 'userCode',
    search: false
  }, {
    label: '相关股票品种',
    prop: 'recomUserName'
  }, {
    label: '数据源类别',
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
    label: '代码',
    prop: 'log',
    search: false
  }, {
    label: '交易对',
    prop: 'walletType'
  }, {
    label: '简况F10',
    prop: 'amount'
  }, {
    label: '当前价格',
    prop: 'amountBefore'
  }, {
    label: '今开',
    prop: 'amountBefore'
  }, {
    label: '今收',
    prop: 'amountAfter'
  }, {
    label: '成交量',
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
