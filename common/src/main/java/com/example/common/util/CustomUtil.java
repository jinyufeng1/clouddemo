package com.example.common.util;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomUtil {

    /**
     * 时间戳数字转化成格式化字符串
     * @param timestamp 必须以毫秒为单位
     * @return
     */
    public static String transformTimestamp(long timestamp, String dataPattern) {
        Date date = new Date(timestamp);
        // 定义日期格式
        SimpleDateFormat formatter = new SimpleDateFormat(dataPattern);
        // 转换为字符串
        return formatter.format(date);
    }

    /**
     * 判断对象是否为空指针或者所有字段属性都为空指针
     * @param obj
     * @return
     */
    public static boolean isAllFieldsNull(Object obj) {
        if (obj == null) {
            return true;
        }
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null && !"".equals(value)) {
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
