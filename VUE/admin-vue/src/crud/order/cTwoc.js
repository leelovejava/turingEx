import { Select } from "element-ui";
import  http  from "@/utils/httpRequest";
export const tableOption = {
    searchMenuSpan: 10,
    searchLabelWidth:120,
    columnBtn: false,
    border: true,
    selection: true,
    selection: false, //开启勾选功能
    index: false,
    indexLabel: '序号',
    stripe: true,
    menuAlign: 'center',
    menuWidth: 150,
    align: 'center',
    refreshBtn: true,
    searchSize: 'mini',
    addBtn: false,
    editBtn: false,
    delBtn: false,
    viewBtn: false,
    emptyBtn:false,
    props: {
      label: 'label',
      value: 'value'
    },
    column: [
      {
        searchslot: true,
        showColumn: false,
        type: 'select',
        label: '支付方式类型',
        prop: 'ndh',
        // dicUrl:http.adornUrl('/paymentMethodConfig/getC2cPaymentMethodType'),
        // dicMeathod:'get',
        search:true,
        hide:true
      },
      {
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
      label: '参数名1',
      prop: 'paramName1'
    },{
      label: '参数名2',
      prop: 'paramName2'
    },{
      label: '参数名3',
      prop: 'paramName3'
    },{
      label: '参数名4',
      prop: 'paramName4'
    },{
      label: '参数名5',
      prop: 'paramName5'
    },{
      label: '参数名6',
      prop: 'paramName6'
    },{
      label: '参数名7',
      prop: 'paramName7'
    },{
      label: '参数名8',
      prop: 'paramName8'
    },{
      label: '参数名9',
      prop: 'paramName9'
    },{
      label: '参数名10',
      prop: 'paramName10'
    },{
      label: '参数名11',
      prop: 'paramName11'
    },{
      label: '参数名12',
      prop: 'paramName12'
    },{
      label: '参数名13',
      prop: 'paramName13'
    },{
      label: '参数名14',
      prop: 'paramName14'
    },{
      label: '参数名15',
      prop: 'paramName15'
    },{
      label: '创建时间',
      prop: 'createTime'
    }, {
      label: '更新时间',
      prop: 'updateTime'
    }]
  }
  