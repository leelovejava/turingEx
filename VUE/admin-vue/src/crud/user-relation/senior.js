export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 220,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  menu:true,
  selection: false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '用户名',
    prop: 'userName',
    search: true,
  }, {
    label: 'UID',
    prop: 'userCode',
    search: false
  }, {
    label: '推荐人',
    prop: 'recomUserName'
  }, {
    label: '账户类型',
    prop: 'roleName',
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
    label: '亲属姓名',
    prop: 'relativesName',
    search: false
  }, {
    label: '亲属关系',
    prop: 'relativesRelation'
  }, {
    label: '绑定电话',
    prop: 'relativesPhone'
  },{
    label: '认证状态',
    prop: 'status',
    type: 'select',
    dicData: [{
      label: '已申请未审核',
      value: 0
    }, {
      label: '审核中',
      value: 1
    }, {
      label: '审核通过',
      value: 2
    }, {
      label: '未通过',
      value: 3
    }]
  }, {
    label: '客户提交时间',
    prop: 'createTime',
    type: 'datetime',
    format: "yyyy-MM-dd HH:mm:ss", // 组件展示的日期格式
    valueFormat:"yyyy-MM-dd HH:mm:ss", // 组件vue值
  }, {
    label: '审核操作时间',
    prop: 'operationTime',
    type: 'datetime',
    format: "yyyy-MM-dd HH:mm:ss", // 组件展示的日期格式
    valueFormat:"yyyy-MM-dd HH:mm:ss", // 组件vue值
  }, {
    label: '原因',
    prop: 'msg'
  }]
}
