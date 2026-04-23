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
    label: '货币',
    prop: 'name',
    search: true,
    hide:true,
  }, {
    label: '货币',
    prop: 'money',
    slot:true,
  },{
    label: '汇入汇出',
    prop: 'outOrIn'
  }, {
    label: '汇率',
    prop: 'rata'
  }]
}
