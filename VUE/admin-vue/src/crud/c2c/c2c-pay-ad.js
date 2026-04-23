export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  searchSpan:7,
  searchLabelWidth:160,
  searchMenuSpan:3,
  menuWidth: 240,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  emptyBtn:false,
  delBtn: false,
  viewBtn: false,
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '承兑商昵称',
    prop: 'nick_name',
  }, {
    label: '承兑商头像',
    prop: 'img',
    solt:true,
  }, {
    label: '用户名',
    prop: 'user_name',
    search: false
  }, {
    label: '承兑商UID',
    prop: 'c2c_user_code',
    placeholder:'	承兑商昵称、承兑商UID',
    search: true
  }, {
    label: '承兑商类型',
    prop: 'c2c_user_type',
    type: 'select',
    search: true,
    dicData:[{
      label:'后台承兑商',
      value:1
    },{
      label:'用户承兑商',
      value:2
    }]
  }, {
    label: '用户名UID',
    prop: 'user_code',
    placeholder:'	承用户名、用户UID',
    search: true,
  }, {
    label: '买卖方式',
    prop: 'direction',
    type: 'select',
    search: true,
    dicData:[{
      label:'买入',
      value:'buy'
    },{
      label:'卖出',
      value:'sell'
    }]
  }, {
    label: '支付币种',
    prop: 'currency',
  }, {
    label: '上架币种',
    prop: 'symbol',
  }, {
    label: '支付方式',
    prop: 'pay_typeM',
    solt:true,
  },  {
    searchslot: true,
    showColumn: false,
    type: 'select',
    label: '支付币种',
    prop: 'nnh',
    search:true,
    hide:true
  }, {
    searchslot: true,
    showColumn: false,
    type: 'select',
    label: '上架币种',
    prop: 'nmh',
    search:true,
    hide:true
  },{
    label: '币种单价',
    prop: 'symbol_value'
  }, {
    label: '币种数量',
    prop: 'coin_amount'
  }, {
    label: '单笔订单限额',
    prop: 'investment_max'
  }, {
    label: '保证金',
    prop: 'deposit_open'
  }, {
    label: '是否上架',
    prop: 'on_sale',
    type: 'select',
    dicData:[{
      label:'下架',
      value:0
    },{
      label:'上架',
      value:1
    }]
  }, {
    label: '创建时间',
    prop: 'create_time'
  }, {
    label: '更多信息',
    prop: 'nth',
    solt:true,
  },{
    label: '备注',
    prop: 'remark'
  }]
}
