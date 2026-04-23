export const tableOption = {
  searchMenuSpan: 6,
  searchLabelWidth:110,
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
  emptyBtn:true,
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '股票简称',
    prop: 'name',
    search: true
  },{
    label: '申购代码(字母)',
    prop: 'productName',
    search: true
  }, {
    label: '发行价',
    prop: 'marketPrice',
  }, {
    label: '承销价',
    prop: 'underwritingPrice',
  }, {
    label: '用户申购额度',
    prop: 'defaultLimit',
  }, {
    label: '发行总数',
    prop: 'subscribeTotalNumber'
  }, {
    label: '已申购数',
    prop: 'appliedSubscribeNumber'
  }, {
    label: '抽签日期',
    prop: 'drawDate'
  }, {
    label: '开放申购日',
    prop: 'startSubscribeDate'
  }, {
    label: '截止申购日',
    prop: 'endSubscribeDate'
  }, {
    label: '发券日期',
    prop: 'issuanceDate'
  }, {
    label: '发行市场',
    prop: 'ipoStatus',
    type: 'select',
    dicData:[{
      label:'待上市',
      value:1
    },{
      label:'已上市',
      value:2
    }],
    search: true
  }, {
    label: '更新时间',
    prop: 'updateTime'
  },{
    label: '锁定时间(天)',
    prop: 'lockDay',
  },{
    label: '权重',
    prop: 'weight',
  }, {
    label: '状态',
    prop: 'status',
    type: 'select',
    dicData:[{
      label:'未开始',
      value:1
    },{
      label:'开放中',
      value:2
    },{
      label:'已结束',
      value:3
    }],
    search: true
  }]
}
