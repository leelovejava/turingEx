export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: true,
  index: false,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 200,
  searchLabelWidth:120,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  emptyBtn:true,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: 'ID',
    prop: 'userCode',
  }, {
    label: '用户',
    prop: 'userName',
    search: true
  }, {
    label: '名称',
    prop: 'name',
    search: true
  },{
    label: '产品代码(数字)',
    prop: 'productCode',
    search: true
  }, {
    label: '产品代码(字母)',
    prop: 'productName',
    search: true
  }, {
    label: '中签应认缴',
    prop: 'requiredNumber',
  }, {
    label: '申购需认缴',
    prop: 'requiredSubscribe',
  }, {
    label: '中签数量',
    prop: 'winningNumber',
  }, {
    label: '申购价格',
    prop: 'subPrice',
  }, {
    label: '认缴股数',
    prop: 'deductNumber',
  }, {
    label: '认缴金额',
    prop: 'deductUsdt',
  }, 
  // {
  //   label: '类型',
  //   prop: 'userName',
  // }, 
  {
    label: '状态',
    prop: 'status',
    type: 'select',
    dicData:[{
      label:'待确认',
      value:1
    },{
      label:'已认缴',
      value:2
    }],
    search: true
  },
   {
    label: '创建时间',
    prop: 'createTime',
  }, 
  // {
  //   label: '到期时间',
  //   prop: 'symbolName',
  // }
]
}
