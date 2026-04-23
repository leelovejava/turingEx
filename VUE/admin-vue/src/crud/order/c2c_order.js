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
    searchLabelWidth:210,
    menuWidth: 430,
    searchSpan:8,
    searchMenuSpan:8,
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
    column: [
      {
        label: '承兑商昵称、承兑商UID',
        prop: 'c2c_user_code',
        search: true,
        hide:true
      },{
        label: '承兑商用户名、承兑商用户UID',
        prop: 'c2c_user_party_code',
        search: true,
        hide:true
      },{
        label: '订单号',
        prop: 'order_no',
        search: true,
        hide:true
      },{
      label: '用户名、用户UID',
      prop: 'user_code',
      search: true,
      hide:true,
    },{
      label: '用户名',
      prop: 'username',
      solt:true,
    }, {
      label: 'UID',
      prop: 'usercode',
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
    },{
      label: '承兑商昵称',
      prop: 'c2c_user_nick_name'
    },{
      label: '承兑商UID',
      prop: 'uscodemn',
      slot:true,
    },{
      label: '承兑商用户名',
      prop: 'c2c_user_party_name',
      solt:true,
    },{
      label: '承兑商用户UID',
      prop: 'c2cUscodemn',
      slot:true,
    },{
      label: '详情',
      prop: 'paramName3'
    },{
      label: '买卖方式',
      prop: 'direction',
      type:'select',
      search: true,
      dicData:[{
        label:'买入',
        value:'buy'
      },{
        label:'卖出',
        value:'sell'
      }]
    },{
      label: '支付币种',
      prop: 'currency'
    },{
      label: '上架币种',
      prop: 'symbol'
    },{
      label: '币种单价',
      prop: 'symbol_value'
    },{
      label: '币种数量',
      prop: 'coin_amount'
    },{
      label: '支付时效',
      prop: 'expire_time'
    },{
      label: '支付金额',
      prop: 'amount'
    }, {
      label: '支付方式',
      prop: 'method_type_name',
      solt:true,
    },{
      label: '订单类型',
      prop: 'order_type',
      type:'select',
      dicData:[{
        label:'按金额',
        value:'by_amount'
      },{
        label:'按数量',
        value:'by_num'
      }]
    },{
      label: '订单状态',
      prop: 'state',
      type:'select',
      searchValue:'',//搜索的默认值
      search: true,
      dicData:[{
        label:'全部',
        value:''
      },{
        label:'待付款',
        value:'0'
      },{
        label:'已付款',
        value:'1'
      },{
        label:'申诉中',
        value:'2'
      },{
        label:'已完成',
        value:'3'
      },{
        label:'已取消',
        value:'4'
      },{
        label:'已超时',
        value:'5'
      }]
    },{
      label: '下单时间',
      prop: 'create_time'
    },{
      label: '更多信息',
      prop: 'paramName4'
    }]
  }
  