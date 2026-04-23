export const tableOption = {
  searchMenuSpan: 6,
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
  emptyBtn:false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '用户名',
    prop: 'userName',
  },{
    label: '用户名',
    prop: 'userndh',
    search: true,
    hide:true,
  }, {
    label: '在线状态',
    prop: 'onlineState',
    type: 'select',
    dicData: [{
      label: '下线',
      value: 0
    }, {
      label: '在线',
      value: 1
    }]
  }, {
    label: '最后上线时间',
    prop: 'lastOnlineTime'
  }, {
    label: '最后分配时间',
    prop: 'lastCustomerTime',
  }, {
    label: '谷歌验证器',
    prop: 'googleAuthBind',
    type: 'select',
    dicData: [{
      label: '已绑定',
      value: true
    }, {
      label: '未绑定',
      value: false
    }]
  }, {
    label: '登录权限',
    prop: 'status',
    type: 'select',
    dicData: [{
      label: '开启',
      value: "1"
    }, {
      label: '关闭',
      value: "0"
    }, {
      label: '关闭',
      value: null
    }]
  }, {
    label: '备注',
    prop: 'remarks'
  }]
}
