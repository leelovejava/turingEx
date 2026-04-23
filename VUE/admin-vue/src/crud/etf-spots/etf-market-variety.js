export const tableOption = {
  searchMenuSpan: 6,
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
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [
    {
      label: 'ID',
      prop: 'uuid'
    },
    {
      label: '交易对',
      prop: 'symbol'
    },
   {
    label: '项目名称',
    prop: 'name',
    search: true
  }, 
  {
    label: '结算币种',
    prop: 'quoteCurrency'
  }, {
    label: '币种价格精度',
    prop: 'decimals'
  },{
    label: '最高买单价',
    prop: 'maxmumPrice'
  },{
    label: '最低卖单价',
    prop: 'minimumPrice'
  },{
    label: '最小下单量',
    prop: 'minimumOrder'
  },{
    label: '最大下单量',
    prop: 'maxmumOrder'
  },{
    label: '交易模块',
    prop: 'type'
  },{
    label: '最小挂单金额',
    prop: 'pipsAmount'
  },{
    label: '币种状态设置',
    slot:true,
    prop:'shezhi'
  },{
    label: '排序',
    prop: 'sorted'
  }]
}
