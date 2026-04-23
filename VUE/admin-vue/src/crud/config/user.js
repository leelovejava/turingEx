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
  emptyBtn:false,
  // emptyFn: this.handleEmpty, // 清空按钮的回调函数
  menu:true,
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
    searchslot: true,
    showColumn: false,
    type: 'select',
    label: '语言',
    prop: 'ndh',
    search:true,
    hide:true
  },{
    label: '语言',
    prop: 'languageText'
  }, {
    label: '业务代码',
    prop: 'contentCode',
    search: true
  }]
}
