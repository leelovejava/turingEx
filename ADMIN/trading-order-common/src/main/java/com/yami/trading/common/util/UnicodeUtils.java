package com.yami.trading.common.util;


public class UnicodeUtils {

    public static void main(String[] args)  {
        String s = "💰💰";
        System.out.println(s+"=="+gbEncoding(s));
        System.out.println(gbEncoding(s) + " --转换成中文是："+decodeUnicode(gbEncoding(s)));

        //System.out.println(gbEncoding(s) + " --转换成中文是："+decodeUnicode("\\u7b80\\u4ecb"));
    }

    /*
     * 文本转unicode编码
     */
    public static String gbEncoding(final String gbString) {
        char[] utfBytes = gbString.toCharArray();
        String unicodeBytes = "";
        for (int i = 0; i < utfBytes.length; i++) {
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() <= 2) {
                hexB = "00" + hexB;
            }
            unicodeBytes = unicodeBytes + "\\u" + hexB;
        }
        return unicodeBytes;
    }
    /*
     * unicode编码还原
     */
    public static String decodeUnicode(final String dataStr) {
    	if(dataStr==null||!dataStr.startsWith("\\u")) {
    		return dataStr;
    	}
        int start = 0;
        int end = 0;
        final StringBuffer buffer = new StringBuffer();
        while (start > -1) {
            end = dataStr.indexOf("\\u", start + 2);
            String charStr = "";
            if (end == -1) {
                charStr = dataStr.substring(start + 2, dataStr.length());
            } else {
                charStr = dataStr.substring(start + 2, end);
            }
            char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
            buffer.append(new Character(letter).toString());
            start = end;
        }
        return buffer.toString();
    }

}
