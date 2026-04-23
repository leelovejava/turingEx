export const tableOption = {
  searchMenuSpan: 6,
  searchLabelWidth:120,
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
    label: 'ID',
    prop: 'purchasingId',
  }, {
    label: 'UID',
    prop: 'uuid',
  }, {
    label: '用户名',
    prop: 'userName',
    placeholder:'用户名UID',
    search: true,
  }, {
    label: '认购付款币种数量',
    prop: 'payCurrencyQuantity',
    search: false,
  }, {
    label: '支付币种',
    prop: 'payCurrency',
  }, {
    label: '申购时间',
    prop: 'subscriptionStartTime'
  }, {
    label: '所属项目种类',
    prop: 'projectTypeName'
  }, {
    label: '申购项目名称',
    prop: 'projectName'
  }, {
    label: '申购项目币种数量',
    prop: 'currencyQuantity'
  }]
}
