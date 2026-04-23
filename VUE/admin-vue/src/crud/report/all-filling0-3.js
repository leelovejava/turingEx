export const tableOption03 = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 350,
  align: 'center',
  refreshBtn: false,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  menu:false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '充提',
    prop: 'remark',
    // search: true,
    children: [{
      label: '充值',
      prop: 'age',
      display:false,
      children: [{
        label: 'USDT',
        prop: 'age',
        display:false
      }, {
        label: 'ETH',
        prop: 'phone',
        display:false
      }, {
        label: 'BTC',
        prop: 'phone',
        display:false
      }]
    }, {
      label: '提现',
      prop: 'phone',
      display:false,
      children: [{
        label: 'USDT',
        prop: 'age',
        display:false
      }, {
        label: 'ETH',
        prop: 'phone',
        display:false
      }, {
        label: 'BTC',
        prop: 'phone',
        display:false
      }]
    }]
  }, {
    label: '赠送(USDT)',
    prop: 'createTime'
  },{
    label: '充提差额(USDT)',
    prop: 'createTime'
  },{
    label: '交易盈亏',
    prop: 'createTime'
  },{
    label: '手续费',
    prop: 'createTime'
    // search: true
  }, {
    label: '总收益',
    prop: 'createTime'
  }]
}
