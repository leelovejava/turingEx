package com.yami.trading.common.constants;

import java.util.HashMap;
import java.util.Map;

public class MessageConstants {
    public  static  final  String LANGUAGE_EN="en";
    public  static  final  String LANGUAGE_ZH_CN="zh-CN";




    public  static  final  String HINT_TEXT="withdraw_value_limitation";

    public static Map<String, String> MESSAGE = new HashMap<String, String>();


    static {
        MESSAGE.put(HINT_TEXT+LANGUAGE_EN, "Currently, transaction {0} is still required to withdraw coins");
        MESSAGE.put(HINT_TEXT+LANGUAGE_ZH_CN,  "当前还需交易{0},才可提币");
    };
}
