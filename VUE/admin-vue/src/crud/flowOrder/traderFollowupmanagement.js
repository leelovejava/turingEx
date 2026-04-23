export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: false,
  indexLabel: "序号",
  stripe: true,
  menuAlign: "center",
  menuWidth: 200,
  align: "center",
  refreshBtn: true,
  searchSize: "mini",
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  emptyBtn: true,
  menu: false,
  props: {
    label: "label",
    value: "value",
  },
  column: [
    {
      label: "名称",
      prop: "username",
    },
    {
      label: "名称",
      prop: "name_para",
      hide: true,
      search: true,
    },
    {
      label: "用户名",
      prop: "username",
    },
    {
      label: "用户名",
      prop: "username_para",
      placeholder: "用户名、UID",
      hide: true,
      search: true,
    },
    {
      label: "用户类型",
      prop: "rolename_para",
      type: "select",
      hide: true,
      search: true,
      dicData: [
        {
          label: "所有账号",
          value: "",
        },
        {
          label: "正式账号",
          value: "MEMBER",
        },
        {
          label: "演示账号",
          value: "GUEST",
        },
        {
          label: "试用账号",
          value: "TEST",
        },
      ],
    },
    {
      label: "用户类型",
      prop: "rolename",
      type: "select",
      dicData: [
        {
          label: "所有账号",
          value: "",
        },
        {
          label: "正式账号",
          value: "MEMBER",
        },
        {
          label: "演示账号",
          value: "GUEST",
        },
        {
          label: "试用账号",
          value: "TEST",
        },
      ],
    },
    {
      label: "UID",
      prop: "user_code",
      search: false,
    },
    {
      label: "累计收益",
      prop: "profit",
      search: false,
    },
    {
      label: "累计金额",
      prop: "amount_sum",
    },
    {
      label: "入驻时间",
      prop: "create_time",
    },
  ],
};
