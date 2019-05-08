package com.k2data.kbc.kmx.es.util;

import com.k2data.kbc.kmx.es.EsException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static final String FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
    public static final String FORMAT_YYYY_MM_DD_HH_M_S = "yyyy-MM-dd HH:mm:ss";

    public static Date parse(String dateStr, String format) throws EsException {
        Date result = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            result = sdf.parse(dateStr);
        } catch (Exception e) {
            throw new EsException("日期字符串格式错误，");
        }

        return result;
    }

    public static int getYear(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);

        return ca.get(Calendar.YEAR);
    }

    public static int getMonth(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);

        return ca.get(Calendar.MONTH) + 1;
    }

    public static int getDate(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);

        return ca.get(Calendar.DATE);
    }

    public static int getHour(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);

        return ca.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinute(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);

        return ca.get(Calendar.MINUTE);
    }

    public static int getSecond(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);

        return ca.get(Calendar.SECOND);
    }
}
