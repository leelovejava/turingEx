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
    label: '产品名称',
    prop: 'symbol',
    search: true,
    hide:true,
  }, {
    label: '产品名称',
    prop: 'name',
  },{
    label: '原值',
    prop: 'newPrice'
  }, {
    label: '调整后',
    prop: 'afterValue'
  }]
}
