package seventh.bupt.time;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BasisTimesUtils {
    public static int THEME_DEVICE_DEFAULT_LIGHT = AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;
    public static int THEME_DEVICE_DEFAULT_DARK = AlertDialog.THEME_DEVICE_DEFAULT_DARK;
    public static int THEME_TRADITIONAL = AlertDialog.THEME_TRADITIONAL;
    public static int THEME_HOLO_LIGHT = AlertDialog.THEME_HOLO_LIGHT;
    public static int THEME_HOLO_DARK = AlertDialog.THEME_HOLO_DARK;
    private static DatePickerDialog mDatePickerDialog;//日期选择器

    /**将字符串时间转为Long时间
     * @param time yyyy-MM-dd HH:mm:ss:SSS*/
    public static Long getLongTimeOfSSS(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            Date date = sdf.parse(time);
            return date.getTime();
        } catch (Exception e) {
        }
        return 0L;
    }

    /**将字符串时间转为Long时间
     * @param time yyyy-MM-dd HH:mm:ss*/
    public static Long getLongTime(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(time);
            return date.getTime();
        } catch (Exception e) {
        }
        return 0L;
    }

    /**将字符串时间转为Long时间
     * @param time yyyy-MM-dd*/
    public static Long getLongTimeOfYMD(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(time);
            return date.getTime();
        } catch (Exception e) {
        }
        return 0L;
    }

    /**将Long时间转成String时间
     * @return yyyy-MM-dd HH:mm:ss:SSS*/
    public static String getStringTimeOfSSS(Long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        return sdf.format(date);
    }

    /**将Long时间转成String时间
     * @return yyyy-MM-dd HH:mm:ss*/
    public static String getStringTime(Long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**将Long时间转成String时间
     * @return yyyy-MM-dd*/
    public static String getStringTimeOfYMD(Long time) {
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    /*** 当前的时间(设备)
     * @return yyyy-MM-dd HH:mm:ss:SSS*/
    public static String getDeviceTimeOfSSS() {
        String date = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
            date = df.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            date = new Date().getTime() + "";//当前时间的long字符串
        }
        return date;
    }

    /*** 当前的时间(设备)
     * @return yyyy-MM-dd HH:mm:ss*/
    public static String getDeviceTime() {
        String date = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            date = df.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            date = new Date().getTime() + "";//当前时间的long字符串
        }
        return date;
    }

    /**当前的时间(年月日)
     * @return yyyy-MM-dd*/
    public static String getDeviceTimeOfYMD() {
        String date = "";
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            date = df.format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**显示时间选择器  时分*/
    public static void showTimerPickerDialog(Context context, boolean themeLight, String title, int hourOfDay
            , int minute, boolean is24HourView, final OnTimerPickerListener onTimerPickerListener) {
        int themeId = AlertDialog.THEME_HOLO_LIGHT;//默认白色背景
        if (!themeLight) {
            themeId = AlertDialog.THEME_HOLO_DARK;//黑色背景
        }
        showTimerPickerDialog(context, themeId, title, hourOfDay, minute, is24HourView, onTimerPickerListener);
    }

    /*** 显示时间选择器  时分,分钟显示有间隔*/
    public static void showTimerPickerDialog(Context context, boolean themeLight, String title, int hourOfDay
            , int minute, boolean is24HourView, final OnTimerPickerListener onTimerPickerListener, int interval, String[] name) {
        int themeId = AlertDialog.THEME_HOLO_LIGHT;//默认白色背景
        if (!themeLight) {
            themeId = AlertDialog.THEME_HOLO_DARK;//黑色背景
        }
        showTimerPickerDialog(context, themeId, title, hourOfDay, minute, is24HourView, onTimerPickerListener, interval, name);
    }

    /*** 显示时间选择器, 默认白色背景*/
    public static void showTimerPickerDialog(Context context, String title, int hourOfDay, int minute, boolean is24HourView, final OnTimerPickerListener onTimerPickerListener) {
        showTimerPickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, title, hourOfDay, minute, is24HourView, onTimerPickerListener);
    }

    /*** 显示时间选择器*/
    public static void showTimerPickerDialog(Context context, int themeId, String title, int hourOfDay,
                                             int minute, boolean is24HourView, final OnTimerPickerListener onTimerPickerListener) {

        TimePickerDialog dialog = new TimePickerDialog(context, themeId, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (onTimerPickerListener != null) {
                    onTimerPickerListener.onConfirm(hourOfDay, minute);
                }
            }
        }, hourOfDay, minute, is24HourView);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (onTimerPickerListener != null) {
                    onTimerPickerListener.onCancel();
                }
            }
        });

        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
        dialog.show();
    }


    /**
     * 时间选择器，但是分钟是间隔10分一跳动
     *
     * @param context               上下文，
     * @param themeId               显示样式
     * @param title                 表头
     * @param hourOfDay             小时
     * @param minute                分钟
     * @param is24HourView          是否24小时
     * @param onTimerPickerListener 返回
     */
    public static void showTimerPickerDialog(Context context, int themeId, String title, int hourOfDay,
                                             int minute, boolean is24HourView, final OnTimerPickerListener onTimerPickerListener, int interval, String[] name) {


        //----
        CustomTimePickerDialog dialog = new CustomTimePickerDialog(context, themeId, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (onTimerPickerListener != null) {
                    onTimerPickerListener.onConfirm(hourOfDay, minute);
                }
            }
        }, hourOfDay, minute, is24HourView, interval, name);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (onTimerPickerListener != null) {
                    onTimerPickerListener.onCancel();
                }
            }
        });

        if (!TextUtils.isEmpty(title)) {
            dialog.setTitle(title);
        }
        dialog.show();
    }


    /**
     * 时间选择器监听
     */
    public interface OnTimerPickerListener {
        void onConfirm(int hourOfDay, int minute);

        void onCancel();

    }
}