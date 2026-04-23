export const tableOptionData = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 180,
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
    label: '代码',
    prop: 'symbol',
    search:true,
  },{
    label: '时间',
    prop: 'timenumStr',
  },{
    slot: true,
    label: '交割收益',
    prop: 'ndh',
  },{
    label: '最低金额',
    prop: 'unitAmount',
  },{
    label: '收益率基数',
    prop: 'profitRatioCardinality',
  },{
    label: '手续费(%)',
    prop: 'unitFee',
  }]
}
