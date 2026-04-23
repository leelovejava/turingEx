export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: true,
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
  emptyBtn:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: 'ID',
    prop: 'userCode',
  }, {
    label: '申购单号',
    prop: 'orderNo',
    search: true
  }, {
    label: '申购状态',
    prop: 'status',
    type: 'select',
    dicData:[{
      label:'申购中',
      value:1
    },{
      label:'已中签',
      value:2
    },{
      label:'未中签',
      value:3
    }],
    search: true
  }, {
    label: '用户名',
    prop: 'userName',
    search: true
  }, {
    label: 'UID',
    prop: 'userCode',
  }, {
    label: '实名',
    prop: 'realName',
  }, {
    label: '股票名称',
    prop: 'symbolName',
    search: true
  }, {
    label: '股票代码',
    prop: 'symbolCode',
    search: true
  }, {
    label: '申购价格',
    prop: 'subPrice'
  }, {
    label: '申购股数',
    prop: 'subNumber'
  }, {
    label: '中签数量',
    prop: 'winningNumber'
  }, {
    label: '申购需认缴',
    prop: 'requiredSubscribe'
  }, {
    label: '中签应认缴',
    prop: 'requiredNumber'
  }, {
    label: '认缴次数',
    prop: 'userPromiseCount'
  }, {
    label: '已认缴次数',
    prop: 'subscribedCount'
  }, {
    label: '已认缴金额',
    prop: 'subscribedAmount'
  },{
    label: '申购时间',
    prop: 'createTime',
  },]
}
