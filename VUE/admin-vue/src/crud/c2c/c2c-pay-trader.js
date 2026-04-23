export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  searchLabelWidth:160,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 240,
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
    label: '承兑商昵称/承兑商UID',
    placeholder:'承兑商昵称/承兑商UID',
    prop: 'c2cUserId',
    search: true,
    hide:true,
  },{
    label: '承兑商类型',
    prop: 'c2cUserType',
    search: true,
    hide:true,
    type: 'select',
    dicData:[{
      label:'所有承兑商类型',
      value:''
    },{
      label:'后台承兑商',
      value:1
    },{
      label:'用户承兑商',
      value:2
    }]
  },{
    label: '承兑商用户名/用户UID',
    placeholder:'承兑商用户名/用户UID',
    prop: 'c2cUserPartyId',
    search: true,
    hide:true,
  },{
    label: 'C2C管理员用户名',
    placeholder:'C2C管理员用户名',
    prop: 'c2cManagerName',
    search: true,
    hide:true,
  },{
    label: '管理员',
    prop: 'manager_name',
  }, {
    label: '昵称',
    prop: 'nick_name',
  }, {
    label: '用户',
    prop: 'user_name'
  }, {
    label: '承兑商头像',
    prop: 'img',
    solt:true,
  }, {
    label: 'UID',
    prop: 'c2c_user_code',
  }, {
    label: '类型',
    prop: 'c2c_user_type',
    type: 'select',
    dicData:[{
      label:'后台承兑商',
      value:1
    },{
      label:'用户承兑商',
      value:2
    }]
  }, {
    label: '用户UID',
    prop: 'user_code'
  }, {
    label: '保证金/剩余(USDT)',
    prop: 'nth',
    solt:true,
  }, {
    label: '买交易量',
    prop: 'buy_amount'
  }, {
    label: '卖交易量',
    prop: 'sell_amount'
  }, {
    label: '总交易所量',
    prop: 'total_amount'
  }, {
    label: '交易人数',
    prop: 'exchange_users'
  }, {
    label: '成单数(买)',
    prop: 'buy_success_orders'
  }, {
    label: '成单数(卖)',
    prop: 'sell_success_orders'
  }, {
    label: '总成单数',
    prop: 'total_success_orders'
  }, {
    label: '创建时间',
    prop: 'create_time'
  }, {
    label: '更新时间',
    prop: 'update_time'
  }, {
    label: '备注',
    prop: 'remark'
  }]
}
