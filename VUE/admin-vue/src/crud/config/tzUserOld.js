export const tableOption = {
  border: true,
  stripe: true,
  menuAlign: "center",
  align: "center",
  searchMenuSpan: 6,
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  searchBtn: true,
  columnBtn: false,
  refreshBtn: true,
  props: {
    label: "label",
    value: "value",
  },
  column: [
    {
      label: "ID",
      prop: "id",
      width: 80,
      search: true,
      searchSpan: 6,
      searchRules: [
        {
          required: true,
          message: "请输入ID",
          trigger: "blur",
        },
      ],
    },
    {
      label: "手机号码",
      prop: "phone",
      width: 150,
      search: true,
    },
    {
      label: "手机号码",
      prop: "phoneAll",
      width: 150,
    },
    {
      label: "邮箱",
      prop: "email",
      width: 200,
      search: true,
    },
  ],
};