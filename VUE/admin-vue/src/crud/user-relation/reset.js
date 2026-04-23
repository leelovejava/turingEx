export const tableOption = {
  searchMenuSpan: 3,
  columnBtn: false,
  border: true,
  selection: false,
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
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '用户',
    prop: 'userName',
    placeholder:'用户名、UID',
    search: true,
    slot:true,
  },{
    label: 'UID',
    prop: 'userCode',
  },{
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
    label: '实名姓名',
    prop: 'realName',
  }, {
    label: '实名认证状态',
    prop: 'realNameAuthority',
    type: 'select',
    dicData: [
      {
        label: '已认证',
        value: true
      }, {
        label: '未认证',
        value: false
      }
    ]
  },{
    prop: 'status',
    label: '审核状态',
    type: 'select',
    dicData: [
      {
        label: '审核中',
        value: 1
      }, {
        label: '审核通过',
        value: 2
      }, {
        label: '审核未通过',
        value: 3
      }
    ]
  }, {
    label: '原因',
    prop: 'msg'
  }, {
    label: '申请时间',
    prop: 'createTime'
  }, {
    label: '操作类型',
    prop: 'operate',
    search: true,
    type: 'select',
    dicData: [
      {
        label: '修改资金密码',
        value: 0
      }, {
        label: '取消谷歌绑定',
        value: 1
      }, {
        label: '取消手机绑定',
        value: 2
      }, {
        label: '取消邮箱绑定',
        value: 3
      }
    ]
  }, {
    label: '用户系统等级',
    prop: 'userLevel'
  }, {
    label: '备注',
    prop: 'remark'
  }]
}
