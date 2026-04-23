export const tableOption = {
    searchMenuSpan: 6,
    columnBtn: false,
    border: true,
    selection: true,
    selection: false, //开启勾选功能
    searchLabelWidth:120,
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
    props: {
      label: 'label',
      value: 'value'
    },
    column: [{
      label: '用户名',
      placeholder:'用户名、用户UID',
      prop: 'userName',
      search: true
    },
    {
      searchslot: true,
      showColumn: false,
      type: 'select',
      label: '支付方式类型',
      prop: 'ndh',
      search:true,
      hide:true
    },{
      label: '用户UID',
      prop: 'userCode',
    },{
      label: '支付方式类型',
      prop: 'methodTypeName',
    }, {
      label: '支付方式名称',
      prop: 'methodName',
      search: true
    }, {
      slot:true,
      label: '支付方式图片',
      prop: 'methodImg'
    },{
      label: '真实姓名',
      prop: 'realName'
    },{
      slot:true,
      label: '参数名1:参数值1',
      prop: 'npOne'
    },{
      slot:true,
      label: '参数名2:参数值2',
      prop: 'npTwo'
    },{
      slot:true,
      label: '参数名3:参数值3',
      prop: 'npThree'
    },{
      slot:true,
      label: '更多参数',
      prop: 'moreNp'
    },{
      slot:true,
      label: '支付二维码',
      prop: 'qrcode'
    },{
      label: '备注',
      prop: 'remark'
    },{
      label: '创建时间',
      prop: 'createTime'
    }, {
      label: '更新时间',
      prop: 'updateTime'
    }]
  }
  