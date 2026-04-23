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
    label: '名称',
    prop: 'name'
  }, {
    label: '代码',
    prop: 'symbol',
    search: true
  }, {
    label: '交易对',
    prop: 'symbol'
  }, {
    label: '精度(位)',
    prop: 'decimals'
  }, {
    label: '交易量倍数',
    prop: 'multiple'
  }]
}
