export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false, //开启勾选功能
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 205,
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
      label: '订单号',
      prop: 'orderNo',
      hide:true,
      search: true
    },{
    label: '用户名',
    placeholder:'用户名/UID',
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
    label: '推荐人',
    prop: 'usernameParent',
  },  {
    label: '品种',
    prop: 'symbol',
  },{
    label: '现货操作',
    prop: 'offset',
    type: 'select',
    searchFilterable:true,//select选择框匹配
    searchValue:'',//搜索的默认值
    filterable:true,  //在表单为select状态时 可键盘输入进行筛选选择项
    multiple:false,  //当type为select时，设置是否多选
    dicData: [
      {
        label: '买入',
        value: 'open'
      }, {
        label: '卖出',
        value: 'close'
      }
    ]
  },{
    label: '委托价值',
    prop: 'volume',
  },{
    label: '委托数量',
    prop: 'symbolValue',
  },{
    label: '委托价格',
    prop: 'price',
  },{
    label: '成交数量',
    prop: 'successVolume',
  },{
    label: '报价类型',
    prop: 'orderPriceType',
    type: 'select',
    dicData: [
      {
        label: '限价',
        value: 'limit'
      }, {
        label: '市价',
        value: 'opponent'
      }
    ]
  },{
    label: '成交价格',
    prop: 'closePrice',
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
    label: '创建时间',
    prop: 'createTime',
  },{
    label: '成交时间',
    prop: 'closeTime',
  }]
}
