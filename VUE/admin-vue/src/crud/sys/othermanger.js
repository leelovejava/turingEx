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
  menu:false,
  props: {
    label: 'label',
    value: 'value'
  },
  column: [{
    label: '币种',
    prop: 'wallettype',
  }, {
    label: '可用金额',
    prop: 'amount'
  }, {
    label: '锁定金额',
    prop: 'lock_amount'
  }, {
    label: '冻结金额',
    prop: 'freeze_amount'
  }]
}
