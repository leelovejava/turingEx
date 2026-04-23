const data1 = [
  {
    v1: "AUS200",
    v2: "Late open 10:10",
    v3: "Normal Trading",
  },
  {
    v1: "Australia Shares",
    v2: "Closed",
    v3: "Normal Trading",
  },
  {
    v1: "SA40",
    v2: "Normal Trading",
    v3: "Closed",
  },
];
const data2 = [
  {
    v1: "Cocoa, Coffee",
    v2: "Open late at 15:00,close early at 20:30",
    v3: "Closed",
  },
  {
    v1: "Cotton",
    v2: "Close early at 20:30",
    v3: "Closed",
  },
  {
    v1: "OJ",
    v2: "Normal Trading",
    v3: "Closed",
  },
  {
    v1: "Soybeans",
    v2: "Open late at 16:30,close early at 20:05",
    v3: "Closed",
  },
  {
    v1: "Sugar",
    v2: "Normal Trading",
    v3: "Closed",
  },
  {
    v1: "Wheat",
    v2: "Open late at 16:30,close early at 20:45",
    v3: "Closed",
  },
  {
    v1: "Cattle, Corn, LeanHogs, Oats, RghRice, SoyMeal, SoyOil",
    v2: "16:30-20:05",
    v3: "Closed",
  },
  {
    v1: "Lumber",
    v2: "17:00-20:05",
    v3: "Closed",
  },
  {
    v1: "Copper",
    v2: "Close early at 20:45",
    v3: "Close early at 20:00",
  },
  {
    v1: "LDSugar",
    v2: "Normal Trading",
    v3: "Close early at 19:00",
  },
  {
    v1: "Metals",
    v2: "Close early at 20:45",
    v3: "Close early at 20:00",
  },
  {
    v1: "NatGas, SpotCrude, Gasoline",
    v2: "Close early at 20:45",
    v3: "Close early at 20:00",
  },
  {
    v1: "SpotBrent",
    v2: "Close early at 21:00",
    v3: "Close early at 21:30",
  },
  {
    v1: "US30, US500, CN50, SCI25, HK50, SPA35, EUSTX50, UK100, JPN225",
    v2: "Close early at 23:00",
    v3: "Closed",
  },
  {
    v1: "GER40",
    v2: "Normal Trading",
    v3: "Close early at 23:00 ",
  },
  {
    v1: "NAS100, US2000, USDX",
    v2: "Close early at 20:15",
    v3: "Close early at 20:00",
  },
  {
    v1: "VIX",
    v2: "Close early at 20:15",
    v3: "Close early at 18:30",
  },
  {
    v1: "US stocks, ETFs",
    v2: "Normal Trading",
    v3: "Closed",
  },
];
const data3 = [
  {
    v1: "NAS100, US500, US 2000,US30, JPN 225",
    v2: "Opens 08:01 Monday | 08:01-06:59 (closes Saturday 06:55)",
    v3: "(Mon-Thu) 01:01 - 23:59 (Fri) 01:01- 23:55",
  },
  {
    v1: "CN50",
    v2: "Opens 11:01 Monday | 11:01-18:30, 19:01-06: 45 (closes Saturday 06:40)",
    v3: "(Mon-Thu) 04:01-11:30,12:01-23:45 (Fri) 04:01-11:30;12:01-23:40 ",
  },
  {
    v1: "SCI25",
    v2: "Opens 10:31 Monday | 10:31-19:20, 19:51-06: 45 (closes Saturday 06:40)",
    v3: "(Mon-Thu) 03:31-12:20;12:51-23:45 (Fri) 03:31-12:20;12:51-23:40 ",
  },
  {
    v1: "SPA35",
    v2: "Opens 16:01 Monday | 16:01-04:00 (closes Saturday 03:55)",
    v3: "(Mon-Thu) 09:01-21:00, (Fri) 09:01-20:55",
  },
  {
    v1: "UK100",
    v2: "Opens 08:06 Monday | 08:01-06:59 (closes Saturday 05:55)",
    v3: "(Mon) 01:06-23:59 (Tue-Thu) 01:01-23:59 (Fri) 01:01-22:55",
  },
  {
    v1: "EUSTX50",
    v2: "Opens 08:06 Monday | 08:01-10:00, 10:16-06:59 (closes Saturday 05:55)",
    v3: "(Mon-Thu) 01:06-03:00;03:16-23:59 (Tue-Thu) 01:01 - 03:00; 03:16 - 23:59 (Fri) 01:01-03:00;03:16-22:55",
  },
  {
    v1: "HK50",
    v2: "Opens 11:16 Monday | 11:16-14:00;15:01-18:30;19:16-05:00(Saturday close 04:55)",
    v3: "(Mon-Thu)04:16-07:00;08:01-11:30;12:16-22:00 (Fri) 04:16-07:00;08:01-11:30;12:16-21:55 ",
  },
  {
    v1: "GER40",
    v2: "Opens 08:05 Monday | 08:00-10:00, 10:15-06:59 (Saturday close 05:55)",
    v3: "Mon) 01:05-03:00, 03:15-23:59, (Tue-Thu) 01:00-03:00, 03:15-23:59 , (Fri): 01:05-03:00, 03:15-22:55",
  },
  {
    v1: "AUS200",
    v2: "Open 09:50 Monday| 09:50-16:30, 17:10-06:59",
    v3: "(Mon-Fri) 02:50-09:30, 10:10-23:59",
  },
  {
    v1: "FRA40",
    v2: "Open 16:01 Monday| 16:01-05:55",
    v3: "(Mon-Fri) 09:01-22:55",
  },
  {
    v1: "MidDE50",
    v2: "Opens 16:01 Monday | 16:00-next day 05:45 (Saturday close 05:45)",
    v3: "(Mon-Fri) 10:01-18:25",
  },
  {
    v1: "NETH25",
    v2: "Opens 16:01 Monday | 16:00-next day 05:45 (Saturday close 05:45)",
    v3: "(Mon-Fri) 09:01-22:45",
  },
  {
    v1: "NOR25",
    v2: "Opens 17:01 Monday | 17:01-next day 00:15 (Saturday close 00:15)",
    v3: "(Mon-Fri) 10:01-17:15",
  },
  {
    v1: "SWI20",
    v2: "Opens 16:01 Monday | 16:01-next day 05:45 (Saturday close 05:45)",
    v3: "(Mon-Fri) 09:01-22:45",
  },
  {
    v1: "GERTEC30",
    v2: "Opens 17:01 Monday | 17:01-next day 01:25 (Saturday close 01:25)",
    v3: "(Mon-Fri) 10:01-18:25",
  },
  {
    v1: "CHINAH",
    v2: "Opens 11:15 Monday|11:15-14:00,15:00-18:30,19:15-next day 03:00",
    v3: "(Mon-Fri) 04:15-07:00,08:00-11:30,12:15-20:00",
  },
  {
    v1: "SA40",
    v2: "Opens 16:31 Monday | 16:31-next day 01:25 (Saturday close 01:25)",
    v3: "(Mon-Fri) 09:31-18:25",
  },
  {
    v1: "MidDE50",
    v2: "Opens 17:01 Monday | 17:01-next day 01:25 (Saturday close 01:25)",
    v3: "(Mon-Fri) 10:01-18:25",
  },
  {
    v1: "CA60",
    v2: "Opens 23:30 Monday | 23:30-next day 06:10 (Saturday close 06:10)",
    v3: "(Mon-Fri) 16:30-23:10",
  },
  {
    v1: "VIX",
    v2: "Open Monday 08:01 | 08:01-06:59 (closes Saturday 06:55)",
    v3: "(Mon-Thu) 01:01 - 23:59 (Fri) 01:01 - 23:55",
  },
];
const data4 = [
  {
    v1: "ADAUSD, AVAXUSD, BCHUSD,BNBUSD, BTCUSD, DASHUSD, DOGEUSD, DOTUSD, EOSUSD, ETHUSD, LINKUSD, LTCUSD, MATICUSD, SOLUSD, UNIUSD, XAUBTC, XLMUSD, XRPUSD, XTZUSD",
    v2: "(Mon-Thu)00:01-23:59, (Fri)00:01-23:55",
    v3: "00:01-23:59",
    v4: "00:01-23:55",
  },
  {
    v1: "Dash",
    v2: "(Mon-Thu)00:01-23:59, (Fri)00:01-22:55",
    v3: "Closed",
    v4: "--",
  },

  {
    v1: "Crypto10, Crypto20, Crypto30",
    v2: "24/5(Mon-Thu)00:01-23:59, (Fri)00:01-23:55",
    v3: "00:01-23:59",
    v4: "--",
  },
];
const data5 = [
  {
    v1: "Australia Share CFDs",
    v2: "(Mon-Fri)10:00-16:00",
    v3: "(Mon-Fri)03:00-09:00 (stagger start)",
  },
  {
    v1: "UK Share CFDs",
    v2: "Opens 17:01 Monday | 17:01-next day 01:29 (Saturday close 01:25)",
    v3: "(Mon-Thu)10:01-18:29, (Friday) 10:01-18:25",
  },
  {
    v1: "US Share CFDs",
    v2: "Opens23:31|23:31-next day 05:55",
    v3: "(Mon-Fri)16:31 - 22:55",
  },
  {
    v1: "German Share CFDs",
    v2: "Opens 17:01 Monday | 17:01-next day 01:29 (Saturday close 01:25)",
    v3: "(Mon-Thu)10:01-18:29, (Friday) 10:01-18:25",
  },
  {
    v1: "HK Share CFDs",
    v2: "(Mon-Fri)11:31-14:00,15:00-18:00",
    v3: "(Mon-Fri)04:31-07:00,08:00-11:00",
  },
];
const data6 = [
  {
    v1: "Metals/Energy",
    v2: "Opens 08:01 Monday| 08:01 - 06:59 (Saturday close 06:55)",
    v3: "(Mon )01:01 - 23:59, (Fri )01:01 - 23:55",
  },
  {
    v1: "SpotBrent",
    v2: "Opens 08:00 Monday| 07:00 - 07:59, 10:00-06:59 (Saturday close 06:55)",
    v3: "(Mon)01:00-23:59, (Tue-Thu)00:00-00:59, 03:00-23:59 (Fri) 00:00-00:59, 03:00-23:55",
  },
  {
    v1: "Corn, Oats, SoyOil, Soybeans, Wheat",
    v2: "Opens 10:00 Monday | 10:00 - 22:45, 23:30-04:20 (Saturday close 04:20)",
    v3: "(Mon-Fri)03:00-15:45,16:30-21:20",
  },
  {
    v1: "Cattle, LeanHogs",
    v2: "Opens 23:30 Monday | 23:30 - 04:05 (Saturday close 04:05)",
    v3: "(Mon-Fri) 16:30-21:05",
  },
  {
    v1: "Cocoa",
    v2: "Opens 18:45 Monday | 18:45 - 03:30 (Saturday close 03:30)",
    v3: "(Mon-Fri)11:45 - 20:30",
  },
  {
    v1: "Coffee",
    v2: "Opens 18:15 Monday | 18:15 - 03:30 (Saturday close 03:30)",
    v3: "(Mon-Fri )11:15 - 20:30",
  },
  {
    v1: "Cotton",
    v2: "Opens 16:00 Monday | 16:00 - 04:20 (Saturday close 04:20)",
    v3: "(Mon-Fri)09:00 - 21:20",
  },
  {
    v1: "London Sugar",
    v2: "Opens 17:45 Monday | 17:45 - 02:55 (Saturday close 02:55)",
    v3: "(Mon-Fri)10:45 - 19:55",
  },
  {
    v1: "Lumber",
    v2: "Opens 00:00 Monday | 00:00 - 06:05 (Saturday close 06:05)",
    v3: "(Mon-Fri)17:00-23:05",
  },
  {
    v1: "RghRice",
    v2: "Opens 10:00 Monday | 10:00 - 12:00, 23:30-04:20 (Saturday close 04:20 )",
    v3: "(Mon-Fri)03:00-05:00, 16:30-21:20",
  },
  {
    v1: "Sugar",
    v2: "Opens 17:31 Monday | 17:31 - 03:00 (Saturday close 03:00)",
    v3: "(Mon-Fri)10:31 - 20:00",
  },
  {
    v1: "Orange Juice",
    v2: "Opens 22:00 Monday | 22:00 - 04:00 (Saturday close 04:00)",
    v3: "(Mon-Fri)15:00 - 21:00",
  },
];

const data7 = [
  {
    v1: "EURX",
    v2: "Opens 08:01 Monday | 08:01-06:59",
    v3: "(Mon-Fri) 01:01 - 23:59",
  },
  {
    v1: "JPYX",
    v2: "Opens 08:01 Monday | 08:01-06:59",
    v3: "(Mon-Fri) 01:01 - 23:59",
  },
  {
    v1: "USDX",
    v2: "Opens 10:01 Monday10:01 - 06:59 (Saturday close 06:55)",
    v3: "(Mon-Thu) 03:01 - 23:59, (Fri)03:01-23:55",
  },
];
const common = [
  {
    prop: "v2",
    label: "m2-th-h2",
  },
  {
    prop: "v3",
    label: "m2-th-h3",
  },
];
const list1 = [
  {
    prop: "v1",
    label: "m2-th-h1",
  },
  {
    prop: "v2",
    label: "25/04/2023",
  },
  {
    prop: "v3",
    label: "27/04/2023",
  },
];
const list2 = [
  {
    prop: "v1",
    label: "m2-th-w1",
  },
  {
    prop: "v2",
    label: "11/16/2021",
  },
  {
    prop: "v3",
    label: "11/25/2021",
  },
];
const list3 = [
  {
    prop: "v1",
    label: "m2-th-s3-t1",
  },
  ...common,
];

const list4 = [
  {
    prop: "v1",
    label: "m2-th-s4-t1",
  },
  ...common,
];

const list5 = [
  {
    prop: "v1",
    label: "m2-th-s5-t1",
  },
  {
    prop: "v2",
    label: "m2-th-h2",
  },
  {
    prop: "v3",
    label: "m2-th-h4",
  },
];
const list6 = [
  {
    prop: "v1",
    label: "m2-th-s6-t1",
  },
  ...common,
];

const list7 = [
  {
    prop: "v1",
    label: "m2-th-s7-t1",
  },
  ...common,
];
export const tableData = [data1, data2, data3, data4, data5, data6, data7];
export const tableList = [list1, list2, list3, list4, list5, list6, list7];
