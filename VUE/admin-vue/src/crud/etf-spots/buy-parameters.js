export const tableOptionData = {
  searchMenuSpan: 0,
  columnBtn: false,
  border: false,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: false,
  menuAlign: 'center',
  menuWidth: 180,
  align: 'center',
  refreshBtn: false,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  menu:false,
  search:false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '币对',
    prop: 'symbol',
  },{
    label: '买单订单号',
    prop: 'orderNo',
  },{
    label: '卖家订单号',
    prop: 'relationOrderNo',
  },{
    label: '成交价',
    prop: 'price',
  },{
    label: '交易量',
    prop: 'volume',
  },{
    label: '交易额度',
    prop: 'volume',
  },{
    label: '买手续费',
    prop: 'fee',
  },{
    label: '卖手续费',
    prop: 'fee',
  }]
}
