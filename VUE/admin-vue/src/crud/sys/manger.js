export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: true,
  selection: false, //开启勾选功能
  searchLabelWidth:130,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 170,
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
    label: '用户名(钱包地址)',
    prop: 'userName',
    placeholder:'用户名、UID(钱包地址)',
    solt:true,
    search: true
  }, {
    label: 'UID',
    prop: 'userCode'
  }, {
    label: '实名姓名',
    prop: 'realName'
  },{
    label: '注册时间',
    prop: 'createTime'
  }, {
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
    label: '手机号',
    prop: 'userMobile',
    search: true
  }, {
    label: '邮箱',
    prop: 'userMail',
    search: true
  }, {
    label: 'USDT账户余额',
    prop: 'moneyslot',
    slot:true,
  }, {
    label: '资产',
    prop: 'property',
    slot:true,
  }, {
    label: '提现限制流水',
    prop: 'withdrawLimitAmount'
  }, {
    label: '用户当前流水',
    prop: 'withdrawLimitNowAmount'
  }]
}
