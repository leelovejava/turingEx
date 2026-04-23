export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 100,
  searchLabelWidth:100,
  searchMenuSpan:3,
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
  column: [{
    label: '订单号(完整)',
    prop: 'order_no_para',
    hide:true,
    search: true
  }, {
    label: '用户名、UID',
    prop: 'name_para',
    hide:true,
    search: true
  },{
    label: '矿机产品名称',
    prop: 'miner_para',
    hide:true,
    search: true,
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
    }]
  },{
    label: '用户',
    prop: 'username',
    // search: true
  }, {
    label: 'UID',
    prop: 'usercode',
    search: false
  }, 
  // {
  //   label: '矿机',
  //   prop: 'rolename',
  //   hide:true,
  //   search: true,
  //   type: 'select',
  //   dicData:[{
  //     label:'选择矿机',
  //     value:''
  //   },{
  //     label:'正式账号',
  //     value:'MEMBER'
  //   },{
  //     label:'演示账号',
  //     value:'GUEST'
  //   },{
  //     label:'试用账号',
  //     value:'TEST'
  //   }]
  // },
  {
    label: '用户类型',
    prop: 'rolename',
    search: true,
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
    }]
  }, {
    label: '订单号',
    prop: 'order_no',
    search: false
  }, {
    label: '产品名称',
    prop: 'miner_name',
    search: false
  }, {
    label: '产品名称(英文)',
    prop: 'miner_name_en'
  }, {
    label: '金额',
    prop: 'amount'
  }, {
    label: '收益',
    prop: 'profit'
  }, {
    label: '买入时间',
    prop: 'create_time'
  }, {
    label: '开始时间',
    prop: 'earn_time'
  }, {
    label: '停止时间',
    prop: 'stop_time'
  }, {
    label: '赎回时间',
    prop: 'close_time'
  }, {
    label: '状态',
    prop: 'state',
    type: 'select',
    dicData:[{
      label:'已赎回',
      value:'2'
    },{
      label:'托管中',
      value:'1'
    },{
      label:'已赎回',
      value:'0'
    }]
  }]
}
