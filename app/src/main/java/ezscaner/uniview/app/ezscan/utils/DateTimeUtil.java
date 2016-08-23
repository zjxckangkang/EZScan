package ezscaner.uniview.app.ezscan.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public final class DateTimeUtil {

    private static final long mOneDateMseconds = 24 * 60 * 60 * 1000;
    public static final String FORMATSSS = "yyyyMMddHHmmss.SSS";


    /**
     * 返回13位时间
     *
     * @param time
     * @return
     */
    public static long str2ts(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // TODO: 2016/7/7  空指针判断
        return date.getTime();
    }

    /**
     * 按照formatString整理time，输出long类型
     *
     * @param time
     * @param formatString
     * @return
     */
    public static long str2ts(String time, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString, Locale.getDefault());
        Date date = null;
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }

        return date.getTime();
    }


    /**
     * 豪秒转日期和时间
     *
     * @param strSeconds 秒字符串
     * @param dateFormat
     * @return 日期和时间
     */
    public static String ts2str(long ts) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        Date date = new Date();
        date.setTime(ts);
        return format.format(date);
    }

    /**
     * 日期转秒
     *
     * @param strDate 日期 yyyy年MM月dd日 HH:mm:ss
     * @return 豪秒
     */
    public static long getMSecondsByDate(String strDate, SimpleDateFormat format) {
        Date date = null;
        try {
            date = format.parse(strDate);
            return date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;

    }

    /**
     * 豪秒转日期和时间
     *
     * @param strSeconds 秒字符串
     * @param dateFormat
     * @return 日期和时间
     */
    public static String getStrDateAndTimeByMSeconds(long strSeconds, String dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.getDefault());
        Date date = new Date();
        date.setTime(strSeconds);
        return format.format(date);
    }

    /**
     * 豪秒转日期
     *
     * @param strSeconds 秒字符串
     * @param dateFormat 日期和时间中间空格隔开 比如：2014-07-18 12:12:12
     * @return 日期(2014-07-18)
     */
    public static String getStrDateByMSeconds(long strSeconds, String dateFormat) {
        String date = getStrDateAndTimeByMSeconds(strSeconds, dateFormat);
        return date.split(" ")[0];
    }

    public static int getHourByMSeconds(long strSeconds) {
        Date date = new Date();
        date.setTime(strSeconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public static int getMinuteByMSeconds(long strSeconds) {
        Date date = new Date();
        date.setTime(strSeconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    public static int getSecondByMSeconds(long strSeconds) {
        Date date = new Date();
        date.setTime(strSeconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.SECOND);
    }

    public static String getDayByTime(String datetime) {
        return datetime.split(" ")[0];
    }

    /**
     * 时间比较
     *
     * @param time1
     * @param time2
     * @param formatString
     * @return
     */
    public static int compareTime(String time1, String time2, String formatString) {
        long t1 = str2ts(time1, formatString);
        long t2 = str2ts(time2, formatString);

        if (t1 == t2) {
            return 0;
        } else if (t1 < t2) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * @param time1 时间1
     * @param time2 时间2
     * @return 相等时返回0；time1<time2时返回-1；否则返回1
     */
    public static int compareTime(String time1, String time2) {
        long t1 = str2ts(time1);
        long t2 = str2ts(time2);

        if (t1 == t2) {
            return 0;
        } else if (t1 < t2) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * @param strDate 比较输入日期时间
     * @param format  时间格式
     * @return 输入日期时间是否是大于今天晚上12:59:59的时间：如果大于，返回true，否则返回false
     */
    public static boolean isMornThanToday(String strDate, SimpleDateFormat format) {
        Date date = null;
        try {
            date = format.parse(strDate + " 00:00:00");
            long datem = date.getTime();
            date = new Date();
            String nowdate = format.format(date).split(" ")[0];
            nowdate = nowdate + " 23:59:59";
            date = format.parse(nowdate);
            long now = date.getTime();
            return datem >= now;
        } catch (ParseException e) {
            e.printStackTrace();

        }

        return true;
    }

    public static boolean isMornThanToday(long datem, SimpleDateFormat format) {
        Date date = null;
        try {
            date = new Date();
            String nowdate = format.format(date).split(" ")[0];
            nowdate = nowdate + " 23:59:59";
            date = format.parse(nowdate);
            long now = date.getTime();

            return datem >= now;
        } catch (ParseException e) {
            e.printStackTrace();

        }

        return true;
    }

    /**
     * 在给定的日期加几天后的日期
     *
     * @param day      2014-08-09
     * @param addcount 1
     * @return 返回加addcount天后的日期
     */
    public static String getDayAddByInput(String day, int addcount) {
        if (null == day) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cd = Calendar.getInstance();
        try {
            cd.setTime(sdf.parse(day));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        cd.add(Calendar.DATE, addcount);// 增加一天
        return sdf.format(cd.getTime());
    }

    /**
     * @param date   当前日期(2014-07-22)
     * @param format 日期格式(2014-07-22 12:00:00)
     * @return 返回当前天的昨天毫秒值
     */
    public static long getBDateByCurrentDate(String date, SimpleDateFormat format) {
        date = date + " 12:00:00";
        long now = getMSecondsByDate(date, format);
        return now - mOneDateMseconds;
    }

    /**
     * @param date   当前日期(2014-07-22)
     * @param format 日期格式(2014-07-22 12:00:00)
     * @return 返回当前天的明天毫秒值
     */
    public static long getFDateByCurrentDate(String date, SimpleDateFormat format) {
        date = date + " 12:00:00";
        long now = getMSecondsByDate(date, format);
        return now + mOneDateMseconds;
    }

    /**
     * @param date1 开始时间 格式 XXXX-XX-XX
     * @param date2 结束时间开始时间 格式 XXXX-XX-XX
     * @return
     */
    public static int compareDate(String date1, String date2) {
        int r = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            r = format.parse(date1).compareTo(format.parse(date2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

    public static void setCalendar(Calendar calendar, String time) {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        calendar = Calendar.getInstance();

        Date date;
        try {
            date = mFormat.parse(time);
            calendar.setTime(date);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param calendar
     * @return 格式yyyy-MM-dd的时间字符串
     */
    public static String getDateTime(Calendar calendar) {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = calendar.getTime();
        String time = mFormat.format(date);
        return time;
    }

    /**
     * @return Returns  a millisecond value.
     */
    public static long getTime() {
        return new Date().getTime();
    }

    /**
     * @param Date
     * @return 格式yyyy-MM-dd的时间字符串
     */
    public static String getDateTime(Date date) {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String time = mFormat.format(date);
        return time;
    }

    /**
     * @param Date
     * @return 格式yyyy-MM的时间字符串
     */
    public static String getDateTime_Month(Date date) {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String time = mFormat.format(date);
        return time;
    }

    /**
     * @param calendar
     * @return 格式yyyy-MM-dd的时间字符串
     */
    public static String getCurrentDateTime() {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String time = mFormat.format(date);
        return time;
    }

    /**
     * @param calendar
     * @return 格式yyyy-MM-dd的时间字符串
     */
    public static int getTwoWeekOffset(String startTime, String endTime) {
        long startTimeDayCount = getDayCount(startTime);
        long endTimeDayCount = getDayCount(endTime);

        int offset = (int) (endTimeDayCount - startTimeDayCount) / 7;

        return offset;
    }

    public static long getDayCount(String time) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        Date date;

        try {
            date = mFormat.parse(time);
            calendar.setTime(date);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int currentDateOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        long dayCount = calendar.getTimeInMillis() / 86400000 - currentDateOfWeek;

        return dayCount;
    }

    public static int getTwoMonthOffset(Date startTime, Date endTime) {
        int startYear = startTime.getYear();
        int startMonth = startTime.getMonth() + 1;

        int endtYear = endTime.getYear();
        int endMonth = endTime.getMonth() + 1;

        int offset = (endtYear - startYear) * 12 + (endMonth - startMonth);

        return offset;
    }

}
