export const tableOption = {
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
  column: [
    {
    label: '币种名称',
    prop: 'name',
    search:true,
  },
  // {
  //   searchslot: true,
  //   showColumn: false,
  //   type: 'select',
  //   label: '代码',
  //   prop: 'ndh',
  //   search:true,
  //   hide:true
  // },
  {
    label: '代码',
    prop: 'symbol'
  }, {
    label: '交易对',
    prop: 'symbol',
  },{
    label: '每张金额',
    prop: 'unitAmount'
  },{
    label: '每张手续费',
    prop: 'unitFee'
  }, {
    label: '合约手续费(百分比)',
    prop: 'unitPercentage'
  },{
    label: '最小变动单位',
    prop: 'pips'
  },{
    label: '最小变动单位的盈亏',
    prop: 'pipsAmount'
  }]
}
