export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  index: true,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 200,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  emptyBtn: false,
  menu: true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [
    {
      label: '标题',
      prop: 'title',
      search: true
    },
    {
      label: '通知类型',
      prop: 'noticeType',
      search: true
    },
    {
      label: '用户UID',
      prop: 'userCode'
    },
    {
      label: '用户名',
      prop: 'userName'
    },
    {
      label: '状态',
      prop: 'status',
      type: 'select',
      dicData: [
        {
          label: '未读',
          value: 1
        },
        {
          label: '已读',
          value: 2
        }
      ]
    },
    {
      label: '创建时间',
      prop: 'createTime'
    }
  ]
}
