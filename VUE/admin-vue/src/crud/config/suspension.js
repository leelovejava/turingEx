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
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '代码编码',
    prop: 'symbol',
    search: true
  }, {
    label: '名称',
    prop: 'symbolName'
  }, {
    label: '开盘时间',
    prop: 'openBjDate'
  }, {
    label: '停盘时间',
    prop: 'closeBjDate'
  }]
}
