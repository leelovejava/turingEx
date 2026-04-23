export const tableOption = {
  detail: true,
  labelWidth: 110,
  group: [
    {
      label: 'APP',
      prop: 'app',
      icon: 'el-icon-edit-outline',
      column: [
        {
          label: '当前版本',
          prop: 'name',
        }
      ]
    }, 
    {
      label: '充值',
      prop: 'chongzhi',
      icon: 'el-icon-edit-outline',
      labelPosition: 'top',
      column: [
        {
          labelWidth: 400,
          label:'充值最低数量，其他币种价值会被换算成USDT判断',
          prop: 'chongzhi1',
        },
        {
          labelWidth: 400,
          label:'充值最高数量，其他币种价值会被换算成USDT判断',
          prop: 'chongzhi2',
        }
      ]
    },
    {
      label: '提现',
      prop: 'tixian',
      icon: 'el-icon-edit-outline',
      labelPosition: 'top',
      column: [
        {
          // labelWidth: 100,
          label:'是否开启基础认证后才能进行提现',
          prop: 'tixian1',
          // prop: 'tixian1',
          type: 'select',
          dicData: [{
            label: '关闭',
            value: 0
          }, {
            label: '开启',
            value: 1
          }]
        },
        {
          // labelWidth: 100,
          label:'提现流水限制是否开启',
          prop: 'tixian2',
          type: 'select',
          dicData: [{
            label: '关闭',
            value: 0
          }, {
            label: '开启',
            value: 1
          }]
        },
        {
          // labelWidth: 100,
          label:'提现限制流水百分比',
          prop: 'tixian3',
        },
        {
          // labelWidth: 100,
          label:'单次USDT提现限额',
          prop: 'tixian4',
        },
        {
          // labelWidth: 100,
          label:'单次BTC提现最低金额',
          prop: 'tixian5',
        },
        {
          // labelWidth: 100,
          label:'单次ETH提现最低金额',
          prop: 'tixian6',
        },
        {
          // labelWidth: 100,
          label:'每日可提现次数，若为0或空则不做限制',
          prop: 'tixian7',
        },
        {
          // labelWidth: 100,
          label:'每日可提现时间段：例如*(06:06:06-18:00:00),若为空则不做限制',
          prop: 'tixian8',
        }
      ]
    },
    {
      label: '',
      prop: 'tksq',
      // icon: 'el-icon-view',
      column: [
        {
          label: '充值',
          prop: 'chongzhi',
          icon: 'el-icon-edit-outline',
          labelPosition: 'top',
          column: [
            {
              labelWidth: 400,
              label:'充值最低数量，其他币种价值会被换算成USDT判断',
              prop: 'chongzhi1',
            },
            {
              labelWidth: 400,
              label:'充值最高数量，其他币种价值会被换算成USDT判断',
              prop: 'chongzhi2',
            }
          ]
        },
        {
          label: '充值',
          prop: 'chongzhi',
          icon: 'el-icon-edit-outline',
          labelPosition: 'top',
          column: [
            {
              labelWidth: 400,
              label:'充值最低数量，其他币种价值会被换算成USDT判断',
              prop: 'chongzhi1',
            },
            {
              labelWidth: 400,
              label:'充值最高数量，其他币种价值会被换算成USDT判断',
              prop: 'chongzhi2',
            }
          ]
        }
      ]
    }, 
    {
      label: '用户信息',
      prop: 'yhxx',
      icon: 'el-icon-edit-outline',
      column: [
        {
          label: '测试长度',
          prop: 'len',
          value: 3,
          maxlength: 5,
        }, {
          label: '测试自定义',
          prop: 'lens',
          value: 3
        }
      ]
    }
  ]
}
