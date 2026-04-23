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
    label: 'ID',
    prop: 'idCode',
  }, 
  // {
  //   searchslot: true,
  //   showColumn: false,
  //   label: '项目名称',
  //   type: 'select',
  //   prop: 'ndh',
  //   search:true,
  //   hide:true
  // },
  {
    label: '项目总类',
    prop: 'projectTypeName',
  },{
    label: '项目名称',
    prop: 'projectName',
    search:true,
    
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
    label: '发行价',
    prop: 'issuePrice'
  }, {
    label: '接受申购的币种',
    prop: 'currency'
  }, {
    label: '预计上线时间',
    prop: 'expectedLaunchTime'
  }, {
    label: '开始申购时间',
    prop: 'subscriptionStartTime'
  }, {
    label: '结束申购时间',
    prop: 'subscriptionEndTime'
  }, {
    label: '公布结果时间',
    prop: 'publishTime'
  }, {
    label: '最小申购数量',
    prop: 'minQuantity'
  }, {
    label: '最大申购数量',
    prop: 'maxQuantity'
  }, {
    label: '简况F10',
    prop: 'amountAfter',
    slot:true,
  }, {
    label: '白皮书地址',
    prop: 'whitePagerAddress'
  }]
}
