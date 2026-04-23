export const tableOption = {
  searchMenuSpan: 10,
  searchLabelWidth:100,
  columnBtn: false,
  border: true,
  selection: false, //开启勾选功能
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 250,
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
  }, {
    label: '开始时间',
    prop: 'startTime',
    type: "datetime",
    placeholder:'创建时间_开始日期',
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
    placeholder:'创建时间_结束日期',
    format: "yyyy-MM-dd HH:mm:ss",
    valueFormat:"yyyy-MM-dd HH:mm:ss",
    hide: true,
    searchClearble:false,
    search: true,
    viewDisplay: false,
  }, {
    label: '推荐人',
    prop: 'recomUserName'
  },{
    label: '充值币链',
    prop: 'blockchainName',
  },{
    label: '币链地址',
    prop: 'channelAddress',
  },{
    label: '充值数量',
    prop: 'channelAmount',
  },{
    slot:true,
    label: '充值凭证',
    prop: 'img',
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
        label: '成功',
        value: 1
      }, {
        label: '失败',
        value: 2
      }
    ]
  },{
    label: '驳回原因',
    prop: 'description',
  },{
    label: '创建时间',
    prop: 'createTime',
  },{
    label: '审核时间',
    prop: 'reviewTime',
  }]
}
