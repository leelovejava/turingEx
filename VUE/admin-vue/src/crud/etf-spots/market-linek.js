export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 150,
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
    label: '交易对',
    prop: 'symbol',
  }
  // {
  //   label: '交易对项目名称',
  //   prop: 'name',
  // },{
  //   label: '交易量(USDT)',
  //   prop: 'turnover',
  // }
  ,
  {
    label: '当前价格',
    prop: 'openPrice',
  },{
    label: '最高价格',
    prop: 'high',
  },{
    label: '最低价格',
    prop: 'low',
  },{
    label: '开盘价',
    prop: 'openPrice',
  },{
    label: '收盘价',
    prop: 'closePrice',
  },{
    label: '开始时间',
    prop: 'openTimeTss',
    slot:true,
  },{
    label: '结束时间',
    prop: 'closeTimeTss',
    slot:true,
  }]
}
