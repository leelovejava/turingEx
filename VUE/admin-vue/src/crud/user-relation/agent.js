export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
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
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '用户名',
    prop: 'userName',
    search: true,
    hide:true,
  }, {
    label: '用户名',
    prop: 'userNamen',
    slot:true,
  }, {
    label: '切换视图',
    prop: 'viewType',
    type: 'select',
    search: true,
    hide:true,
    dicData: [
    {
      label: '层级视图',
      value: 'level',
    }, {
      label: '列表视图',
      value: 'list'
    }]
  },{
    label: 'UID',
    prop: 'userCode',
  }, {
    label: '上级推荐人',
    prop: 'userNameParent'
  }, {
    label: '分享地址',
    prop: 'shareUrl',
  }, {
    label: '备注',
    prop: 'remarks',
  }]
}
