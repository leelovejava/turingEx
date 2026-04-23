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
  searchLabelWidth:100,
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
    label: '用户名',
    prop: 'clientName',
  },{
    label: '关键词',
    prop: 'keyword',
    search: true,
    placeholder:'UID、IP',
    hide:true,
  },{
    label: '客户值类型',
    prop: 'clientType',
    type: 'select',
    search: true,
    dicData:[{
      label:'所有类型',
      value:''
    },{
      label:'用户UID',
      value:'userCode'
    },{
      label:'ip地址',
      value:'ip'
    }]
  },{
    label: 'UID或IP',
    prop: 'clientKey',
  },{
    label: '风控类型',
    prop: 'type',
    type: 'select',
    search: true,
    dicData:[{
      label:'所有类型',
      value:''
    },{
      label:'断网',
      value:'badnetwork'
    },{
      label:'黑名单',
      value:'black'
    }]
  },{
    label: '状态',
    prop: 'status',
    type: 'select',
    search: true,
    dicData:[{
      label:'全部状态',
      value:-1
    },{
      label:'启用',
      value:1
    },{
      label:'未启用',
      value:0
    }]
  },{
    label: '开始时间',
    prop: 'beginTime',
    type: 'datetime',
    format: "yyyy-MM-dd HH:mm:ss", // 组件展示的日期格式
    valueFormat:"yyyy-MM-dd HH:mm:ss", // 组件vue值
   
  }, {
    label: '结束时间',
    prop: 'endTime',
    type: 'datetime',
    format: "yyyy-MM-dd HH:mm:ss", // 组件展示的日期格式
    valueFormat:"yyyy-MM-dd HH:mm:ss", // 组件vue值
   
  }]
}
