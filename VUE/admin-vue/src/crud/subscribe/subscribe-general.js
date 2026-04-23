export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false, //开启勾选功能
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
    label: '项目ID',
    prop: 'uuid',
  }, {
    label: '项目名称',
    prop: 'projectName',
    search: true,
  },{
    label: '项目总类',
    prop: 'projectTypeSymbol',
  },{
    label: '相关股票品种',
    prop: 'relatedStockVarieties',
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
    prop: 'code'
  }, {
    label: '交易对',
    prop: 'transactionPairsSymbol'
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
    prop: 'turnover'
  }]
}
