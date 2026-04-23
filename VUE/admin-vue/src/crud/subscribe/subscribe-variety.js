export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 200,
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
    label: '项目ID',
    prop: 'projectBreedId',
  }, {
    label: '项目名称',
    prop: 'projectName',
    hide:true,
    search:true,
  },{
    label: '项目总类',
    prop: 'transactionPairsSymbol',
  },{
    label: '成分股品种',
    prop: 'relatedStockName',
  }, {
    label: '数据源类别',
    prop: 'dataType',
    type: 'select',
    dicData:[{
      label:'机器人刷单',
      value:1
    },{
      label:'第三方数据采集',
      value:2
    }]
  }, {
    label: '代码',
    prop: 'symboLl',
    slot:true,
  }, {
    label: '交易对',
    prop: 'symboLl',
    slot:true,
  }, {
    label: '简况F10',
    prop: 'amountAfter',
    slot:true,
  }, {
    label: '当前价格',
    prop: 'close',
    slot:true,
  }, {
    label: '今开',
    prop: 'open',
    slot:true,
  }, {
    label: '今收',
    prop: 'close',
    slot:true,
  },{
    label: '成交量',
    prop: 'amount',
    slot:true,
  }]
}
