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
    search: true
  },{
    label: '订单',
    prop: 'orderNo',
    hide:true,
    search: true
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
    label: 'UID',
    prop: 'userCode',
    search: true
  },  {
    label: '品种',
    prop: 'symbolName',
  },{
    label: '操作',
    prop: 'direction',
    type: 'select',
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
    label: '委托张数',
    prop: 'volumeOpen',
  },{
    label: '杠杆',
    prop: 'leverRate',
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
    label: '限价',
    prop: 'price',
  },{
    label: '止盈止损',
    prop: 'stopProfitLoss',
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
  },]
}
