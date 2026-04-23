export const tableOption = {
    searchMenuSpan: 6,
    columnBtn: false,
    border: true,
    selection: true,
    selection: false, //开启勾选功能
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
    props: {
      label: 'label',
      value: 'value'
    },
    column: [{
      label: '用户名',
      prop: 'userName',
      search: true
    }, {
      label: 'UID',
      prop: 'userCode',
    }, {
      label: '实名姓名',
      prop: 'realName'
    },{
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
      label: '手机号绑定',
      prop: 'userMobileBind',
      type:'select',
      dicData:[{
        label:'已绑定',
        value:true
      },{
        label:'未绑定',
        value:false
      }]
    }, {
      label: '邮箱绑定',
      prop: 'mailBind',
      type:'select',
      dicData:[{
        label:'已绑定',
        value:true
      },{
        label:'未绑定',
        value:false
      }]
    }, {
      label: '推荐人',
      prop: 'recomUserName'
    },{
      label: '评分',
      prop: 'userLevel',
    }, {
      label: '是否在线',
      prop: 'online',
      type:'select',
      dicData:[{
        label:'在线',
        value:true
      },{
        label:'离线',
        value:false
      }]
    },{
      label: 'IP',
      prop: 'lastIp',
      search: true,
      hide:true,
    },{
      label: '基础认证',
      prop: 'realNameAuthority',
      type:'select',
      dicData:[{
        label:'已认证',
        value:true
      },{
        label:'未认证',
        value:false
      }]
    },{
      label: '跟单状态',
      prop: 'follow',
      type:'select',
      dicData:[{
        label:'未开启',
        value:0
      },{
        label:'开启',
        value:1
      }]
    },{
      label: '注册时间',
      prop: 'createTime'
    }, {
      label: '最后登录时间',
      prop: 'userLasttime'
    },{
      label: 'IP',
      prop: 'userRegip',
    }, {
      label: '备注',
      prop: 'remarks'
    }]
  }
  