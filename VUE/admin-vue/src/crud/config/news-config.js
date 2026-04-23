export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
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
  emptyBtn:false,
  menu:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '用户名',
    prop: 'userName',
    search: true
  }, 
  {
    label: 'UID',
    prop: 'userCode',
    search: true,
    hide:true,
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
    label: '玩家/代理UID',
    prop: 'userCodeHdc',
    slot:true,
  }, {
    label: '账户类型',
    prop: 'roleName',
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
    label: '推荐人',
    prop: 'recomUserName'
  }, {
    label: '标题',
    prop: 'title',
    search: true,
  }, {
    label: '图片',
    prop: 'httpImgUrl',
    slot:true,
  }, {
    label: '图片跳转链接',
    prop: 'imgJumpUrl'
  }, {
    label: '可否点击',
    prop: 'click',
    type: 'select',
    dicData: [{
      label: '是',
      value: true
    }, {
      label: '否',
      value: false
    }]
  }, {
    label: '是否弹出',
    prop: 'popUp',
    type: 'select',
    dicData: [{
      label: '是',
      value: true
    }, {
      label: '否',
      value: false
    }]
  }, {
    label: '滚动新闻',
    prop: 'indexTop',
    type: 'select',
    dicData: [{
      label: '是',
      value: true
    }, {
      label: '否',
      value: false
    }]
  }, {
    label: '开始时间',
    prop: 'startTime'
  }, {
    label: '结束时间',
    prop: 'endTime'
  }, {
    label: '创建时间',
    prop: 'startTime'
  }, {
    label: '语言',
    prop: 'languageText'
  }]
}
