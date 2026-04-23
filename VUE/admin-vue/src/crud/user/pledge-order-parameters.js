export const tableOptionData = {
  searchMenuSpan: 0,
  columnBtn: false,
  border: false,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: false,
  menuAlign: 'center',
  menuWidth: 180,
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
    label: '订单类型',
    prop: 'order_type',
    type: 'select',
    dicData:[{
      label:'借款',
      value:1
    },{
      label:'新增质押',
      value:2
    },{
      label:'续借',
      value:3
    },{
      label:'还款',
      value:4
    },{
      label:'强平结清',
      value:5
    }]
  },{
    label: '金额',
    prop: 'loan_amount',
  },{
    label: '质押类型',
    prop: 'pledge_type',
    type: 'select',
    dicData:[{
      label:'币',
      value:1
    },{
      label:'--',
      value:2
    },{
      label:'--',
      value:3
    },{
      label:'--',
      value:4
    },{
      label:'--',
      value:5
    }]
  },{
    label: '质押金额',
    prop: 'pledge_amount',
  },
  {
    label: '质押币种',
    prop: 'pledge_currency'
  },
  {
    label: '时间',
    prop: 'create_time'
  }
  ]
}
