export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false, //开启勾选功能
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
  menu:true,
  emptyBtn:false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
      label: '订单号',
      prop: 'orderNo',
      hide:true,
      search: true
    },{
    label: '用户名',
    prop: 'userName',
    search: true
  },{
    label: 'UID',
    prop: 'userCode',
  },{
    type:'select',
    label: '账户类型',
    prop: 'roleName',
    searchFilterable:true,//select选择框匹配
    search: true,
    searchValue:'',//搜索的默认值
    filterable:true,  //在表单为select状态时 可键盘输入进行筛选选择项
    multiple:false,  //当type为select时，设置是否多选
    dicData:[{
      label:'所有账号',
      value:''
    },{
      label:'正式账号',
      value:'MEMBER'
    },{
      label:'演示账号',
      value:'GUEST'
    },{
      label:'试用账号',
      value:'TEST'
    }]
  },  {
    label: '品种',
    prop: 'symbol',
  },{
    label: '合约方向',
    prop: 'direction',
    type: 'select',
    searchFilterable:true,//select选择框匹配
    search: true,
    searchValue:'',//搜索的默认值
    filterable:true,  //在表单为select状态时 可键盘输入进行筛选选择项
    multiple:false,  //当type为select时，设置是否多选
    dicData: [
      {
        label: '多',
        value: 'buy'
      }, {
        label: '空',
        value: 'sell'
      }
    ]
  },{
    label: '合约时间',
    prop: 'timenum',
    slot:true,
  },{
    label: '购买金额',
    prop: 'volume',
  },{
    searchslot: true,
    showColumn: false,
    type: 'select',
    label: '交割币种',
    prop: 'ndh',
    search:true,
    hide:true
  },{
    label: '下单金额',
    prop: 'tradeAvgPrice',
    search: true,
  },{
    label: '结算价',
    prop: 'closeAvgPrice',
  },{
    label: '手续费(USDT)',
    prop: 'fee',
  },{
    label: '盈亏',
    prop: 'profit',
  },{
    label: '购买时间',
    prop: 'createTimeTss',
    slot:true,
  },{
    label: '交割时间',
    prop: 'settlementTimes',
    slot:true,
  },{
    label: '状态',
    prop: 'state',
    type: 'select',
    dicData: [
      {
        label: '已提交',
        value: 'submitted'
      }, {  
        label: '已撤销',
        value: 'canceled'
      }, {
        label: '委托完成',
        value: 'created'
      }
    ]
  },{
    label: '订单盈亏控制情况(优先级高于交割场控设置)',
    prop: 'profitLosssStr',
  },]
}
