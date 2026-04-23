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
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '用户名',
    prop: 'userName',
    search: true,
    hide:true,
  },
  {
    label: '用户名',
    prop: 'userNamesolt',
    solt:true,
  },
  {
    label: 'UID',
    prop: 'userCode',
    search: true,
  },
  {
    label: '开始时间',
    prop: 'startTime',
    type: "datetime",
    search: true,
    viewDisplay: false,
    hide: true,
    format: "yyyy-MM-dd HH:mm:ss", // 组件展示的日期格式
    valueFormat:"yyyy-MM-dd HH:mm:ss", // 组件vue值
  },
  {
    label: '结束时间',
    prop: 'endTime',
    type: "datetime",
    format: "yyyy-MM-dd HH:mm:ss",
    valueFormat:"yyyy-MM-dd HH:mm:ss",
    hide: true,
    searchClearble:false,
    search: true,
    viewDisplay: false,
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
  }, {
    label: '品种',
    prop: 'symbol',
  },{
    label: '合约方向',
    prop: 'direction',
    type: 'select',
    dicData: [
      {
        label: '多',
        value: 'buy',
      }, {
        label: '空',
        value: 'sell',
      }
    ]
  },{
    label: '成交均价',
    prop: 'tradeAvgPrice',
  },{
    label: '当前价格',
    prop: 'mark_price',
  },{
    label: '止盈止损',
    prop: 'stopProfitLoss',
  },{
    width: "150",
    label: '剩余/委托金额',
    prop: 'vove',
    solt:true,
  },{
    width: "150",
    label: '剩余/委托保证金',
    prop: 'depo',
    solt:true,
  },{
    label: '用户钱包余额',
    prop: 'money',
  },{
    label: '涨幅比(%)',
    prop: 'changeRatio',
    solt:true,
  },{
    label: '盈亏',
    prop: 'profitLoss',
  },{
    label: '状态',
    prop: 'state',
    type: 'select',
    dicData: [
      {
        label: '持仓',
        value: 'submitted',
      }, {
        label: '已撤销',
        value: 'canceled',
      }, {
        label: '已平仓',
        value: 'created',
      }
    ]
  },{
    label: '创建时间',
    prop: 'createTime',
    width:'135',
  }]
}
