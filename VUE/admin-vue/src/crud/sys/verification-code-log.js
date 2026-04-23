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
  refreshBtn: false,
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
    label: '手机号或邮箱号',
    prop: 'target',
    search: true,
    searchLabelWidth:120,
    searchPlaceholder:"手机号或邮箱号",
    searchSpan:120
  }, {
    slot:true,
    label: '验证码',
    prop: 'log'
  }, {
    label: '时间',
    prop: 'createTime'
  }]
}
