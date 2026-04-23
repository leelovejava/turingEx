export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 130,
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
    label: '交易对ID',
    prop: 'uuid'
  }, {
    label: '交易对名称',
    prop: 'symbol'
  }, {
    label: '交易对',
    prop: 'name'
  }, {
    label: '报价货币',
    prop: 'quoteCurrency'
  }, 
  // {
  //   label: '币种',
  //   prop: 'symbolFullName'
  // }, 
  // {
  //   label: '交易精度',
  //   prop: 'decimals'
  // }, 
  {
    label: '价格精度',
    prop: 'decimals'
  }, {
    label: '最小交易量',
    prop: 'pips'
  }, {
    label: '最小交易额',
    prop: 'pipsAmount'
  }, {
    slot:true,
    label: '前端显示状态',
    prop: 'showStatus'
  }, {
    slot:true,
    label: '交易状态',
    prop: 'tradeStatus'
  }, {
    label: '排序',
    prop: 'sorted'
  },{
    label: '创建时间',
    prop: 'createTime'
  }]
}
