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
    label: '订单号',
    prop: 'orderNo',
    hide:true,
    search: true
  },{
    label: '用户名',
    prop: 'userName',
    placeholder:'用户名、UID',
    search: true,
    hide:true,
  },{
    label: '用户名',
    prop: 'userNamesolt',
    slot:true,
  },{
    label: '实名姓名',
    prop: 'realName'
  },{
    label: 'UID',
    prop: 'userCode',
  },{
    type:'select',
    label: '账户类型',
    prop: 'rolename',
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
    label: '推荐人',
    prop: 'userNameParent'
  },{
    label: '提现币链',
    prop: 'method',
  },{
    label: '提现数量',
    prop: 'volume',
  },{
    label: '到账数量',
    prop: 'amount',
  },{
    label: '状态',
    prop: 'status',
    type: 'select',
    dicData: [
      {
        label: '处理中',
        value: 0
      },
      {
        label: '已处理',
        value: 1
      }, {
        label: '驳回',
        value: 2
      }
    ]
  },{
    slot:true,
    label: '提现地址',
    prop: 'address',
  },{
    label: '驳回原因',
    prop: 'failureMsg',
  },{
    label: '创建时间',
    prop: 'createTime',
  },{
    label: '审核时间',
    prop: 'reviewTime',
  },{
    label: '备注',
    prop: 'remarks',
  }]
}
