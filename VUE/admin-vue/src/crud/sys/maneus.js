export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: true,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 350,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  emptyBtn:false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '用户名',
    prop: 'username',
  }, {
    label: '用户名',
    prop: 'usendh',
    search: true,
    hide:true,
  },{
    label: '角色',
    prop: 'roleName'
  }, {
    label: '邮箱',
    prop: 'email'
  }, {
    label: '谷歌验证器',
    prop: 'googleAuthBind',
    type: 'select',
    dicData: [
      {
        label: '未绑定',
        value: false
      }, {
        label: '已绑定',
        value: true
      }
    ]
  }, {
    label: '登录权限',
    prop: 'status',
    type: 'select',
    dicData: [
      {
        label: '禁用',
        value: 0
      }, {
        label: '正常',
        value: 1
      }
    ]
  }, {
    label: '备注',
    prop: 'remarks'
  }]
}
