package com.yami.trading.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtil {

    // 调整单个对象的BigDecimal属性精度
    public static void adjustBigDecimalPrecision(Object obj, int precision) {
        if (obj == null) return;

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.getType() == BigDecimal.class) {
                try {
                    field.setAccessible(true);
                    BigDecimal originalValue = (BigDecimal) field.get(obj);
                    if (originalValue != null) {
                        BigDecimal adjustedValue = originalValue.setScale(precision, RoundingMode.HALF_UP);
                        field.set(obj, adjustedValue);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 调整对象列表中每个对象的BigDecimal属性精度
    public static void adjustBigDecimalPrecisionForList(List<?> objList, int precision) {
        if (objList == null) return;
        for (Object obj : objList) {
            adjustBigDecimalPrecision(obj, precision);
        }
    }

    public static void main(String[] args) {
        // 测试
        List<MyClass> list = new ArrayList<>();
        list.add(new MyClass(new BigDecimal("12345.67890123456789012345")));
        list.add(new MyClass(new BigDecimal("98765.43210987654321")));

        adjustBigDecimalPrecisionForList(list, 8);

        for (MyClass obj : list) {
            System.out.println(obj.getDecimalValue());
        }
        // 输出:
        // 12345.67890123
        // 98765.43210988
    }
}

class MyClass {
    private BigDecimal decimalValue;

    public MyClass() {
    }

    public MyClass(BigDecimal decimalValue) {
        this.decimalValue = decimalValue;
    }

    public BigDecimal getDecimalValue() {
        return decimalValue;
    }

    public void setDecimalValue(BigDecimal decimalValue) {
        this.decimalValue = decimalValue;
    }
}
