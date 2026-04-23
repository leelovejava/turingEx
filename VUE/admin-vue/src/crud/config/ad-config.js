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
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '业务代码',
    prop: 'contentCode'
  }, {
    label: '模块',
    prop: 'model',
    type: 'select',
    dicData: [{
      label: "顶部展示",
      value: "top",
    },
    {
      label: "其他地方展示",
      value: "other",
    },
    {
      label: "弹窗海报",
      value: "poster",
    },]
  },
  {
    searchslot: true,
    showColumn: false,
    type: 'select',
    label: '语言',
    prop: 'ndh',
    search:true,
    hide:true
  }, {
    label: '语言',
    prop: 'languageText'
  }, {
    label: '图片',
    prop: 'image',
    slot:true,
  }, {
    label: '可否点击',
    prop: 'click',
    type: 'select',
    dicData: [{
      label: '是',
      value: 1
    }, {
      label: '否',
      value: 0
    }]
  }, {
    label: '是否展示',
    prop: 'onShow',
    type: 'select',
    dicData: [{
      label: '是',
      value: 1
    }, {
      label: '否',
      value: 0
    }]
  }, {
    label: '排序',
    prop: 'sortIndex'
  }]
}
