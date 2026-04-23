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
    label: '每张金额',
    prop: 'unitAmount'
  }, {
    label: '每张手续费',
    prop: 'unitFee'
  }, {
    label: '最小变动单位',
    prop: 'pips'
  }, {
    label: '最小变动单位的盈亏金额',
    prop: 'pipsAmount'
  }]
}
