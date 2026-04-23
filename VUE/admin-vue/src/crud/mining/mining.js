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
  }, 
  // {
  //   label: '图片',
  //   prop: 'img'
  // }, 
  {
    label: '矿机购买币种',
    prop: 'buy_currencym',
    search: false,
    slot:true,
  }, {
    label: '矿机产出币种',
    prop: 'output_currencym',
    search: false,
    slot:true,
  }, {
    label: '周期(天)',
    prop: 'cycle'
  }, {
    label: '可解锁周期(天)',
    prop: 'cycle_close'
  }, {
    label: '日利率(%)',
    prop: 'show_daily_rate'
  }, {
    label: '今日利率(%)',
    prop: 'daily_rate'
  }, {
    label: '投资金额区间(USDT)',
    prop: 'investment_min'
  }, {
    label: '在售',
    prop: 'on_sale',
    type: 'select',
    dicData:[{
      label:'上架',
      value:'1'
    },{
      label:'下架',
      value:'0'
    }]
  }]
}
