export const tableOption = {
  searchMenuSpan: 10,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  searchLabelWidth:140,
  menuWidth: 180,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  emptyBtn:false,
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [ {
    label: '用户名',
    prop: 'userNamesolt',
    solt:true,
  },{
    searchslot: true,
    showColumn: false,
    type: 'select', 
    label: '用户名、UID(完整)',
    prop: 'ndhm',
    search:true,
    hide:true
  },{
    label: 'UID',
    prop: 'userCode',
  }, {
    label: '推荐人',
    prop: 'recomUserName'
  },{
    label: '账户类型',
    prop: 'role',
    search: true,
    type: 'select',
    hide:true,
  }, {
    label: '账户类型',
    prop: 'roleName',
    type: 'select',
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
    },{
      label:'代理商',
      value:'AGENT'
    }]
  }, {
    label: '日志',
    prop: 'lognth',
    search: true,
    hide:true,
  }, {
    label: '日志',
    prop: 'log',
  }, {
    label: '币种',
    prop: 'ndh'
    // prop: 'walletType'
  }, {
    label: '金额',
    prop: 'amount'
  }, {
    label: '变更前',
    prop: 'amountBefore'
  }, {
    label: '变更后',
    prop: 'amountAfter'
  }, {
    label: '前端是否显示',
    prop: 'show',
    type: 'select',
    dicData:[{
      label:'是',
      value:'1'
    },{
      label:'否',
      value:'0'
    }]
  }, {
    label: '时间',
    prop: 'createTime',
    type: 'datetime',
    format: "yyyy-MM-dd HH:mm:ss", // 组件展示的日期格式
    valueFormat:"yyyy-MM-dd HH:mm:ss", // 组件vue值
    width:'135',
  },{
    label: '开始时间',
    prop: 'startTimendh',
    type: 'datetime',
    format: "yyyy-MM-dd HH:mm:ss", // 组件展示的日期格式
    valueFormat:"yyyy-MM-dd HH:mm:ss", // 组件vue值
    search:true,
    hide:true
  }, {
    label: '结束时间',
    prop: 'endTimendh',
    type: 'datetime',
    format: "yyyy-MM-dd 23:59:59", // 组件展示的日期格式
    valueFormat:"yyyy-MM-dd 23:59:59", // 组件vue值
    search:true,
    hide:true,
  }]
}
