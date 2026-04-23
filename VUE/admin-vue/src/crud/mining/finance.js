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
  emptyBtn:false,
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '产品名称',
    prop: 'name',
    search: true
  }, {
    label: '产品名称(英文)',
    prop: 'name_en',
    search: false
  }, {
    slot:true,
    label: '图片',
    prop: 'methodImg'
  }, {
    label: '周期(天)',
    prop: 'cycle',
    search: false
  }, {
    label: '日利率(%)(显示在APP端给客户看的)',
    prop: 'daily_rate',
    search: false
  }, {
    label: '今日利率(%)(实际结算收益时候的使用的)',
    prop: 'today_rate'
  }, {
    label: '违约结算比例(%)',
    prop: 'default_ratio'
  }, {
    label: '投资金额区间(USDT)',
    prop: 'investment_min'
  }, {
    label: '状态',
    prop: 'state',
    type: 'select',
    dicData:[{
      label:'启用',
      value:'1'
    },{
      label:'停用',
      value:'0'
    }]
  }]
}
