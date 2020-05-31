package seventh.bupt.time.Alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import java.util.Calendar;

public class AlarmService extends Service {

    //从整秒开始
    // 每分钟检测一次，是否有task到达规定时间
    //如果有，弹出顶部弹窗
    private final String TAG = this.getClass().getSimpleName();

    private Context context;
    public AlarmBinder alarmBinder;

    //true：添加alarm
    // false：删除alarm
    private boolean isSetAlarm;

    //保存闹钟时间信息
    private String[] date_alarm;
    private String[] time_alarm;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private String todo;
    private int code;
    private String time;
    private String date;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        System.out.println();
        //解析intent中传递的data信息
        date_alarm = intent.getStringExtra("date").split("-");
        year = Integer.parseInt(date_alarm[0]);
        month = Integer.parseInt(date_alarm[1]);
        day = Integer.parseInt(date_alarm[2]);
        time_alarm = intent.getStringExtra("time").split(":");
        hour = Integer.parseInt(time_alarm[0]);
        minute = Integer.parseInt(time_alarm[1]);

        todo = intent.getStringExtra("todo");
        code = intent.getIntExtra("remindTypeCode", 0);
        time = intent.getStringExtra("time");
        date = intent.getStringExtra("date");

        isSetAlarm = intent.getBooleanExtra("isSetAlarm", true);
        Log.d(TAG, "onStartCommand: " + todo + code);
        alarmBinder = new AlarmBinder();

        //true：添加alarm
        // false：删除alarm
        if (isSetAlarm) {
            alarmBinder.setAlarm(todo, code, time, date);
        } else {
            alarmBinder.cancelAlarm(todo, intent.getStringExtra("date"), intent.getStringExtra("time"), code);
        }

        //onDestroy();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //闹钟管理
    class AlarmBinder extends Binder {
        private AlarmManager alarmManager;
        private Calendar calendar;
        private Intent intent;
        private PendingIntent pendingIntent;
        private int alarmCount = 10;

       //添加闹钟
        public void setAlarm(String todo, int code, String time,String date) {
            //todo:事务名称  code：提醒类型（可以有响铃+振动） 实现时未分类，使用了响铃+振动
            alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            calendar = Calendar.getInstance();
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

            //设置闹钟
            intent = new Intent("android.intent.action.SET_TIMER");
            intent.putExtra("todo", todo);
            intent.putExtra("remindTypeCode", code);
            intent.putExtra("time", time);
            intent.putExtra("date", date);
            Log.d(TAG, "setAlarm: " + intent.getStringExtra("todo") + intent.getIntExtra("remindTypeCode", 0));
            calendar.set(year, (month-1), day, hour, minute, 0);

            //getBroadcast：打开一个广播组件，向BroadcastReceiver广播
            // pendingIntent保证在app down的时候也可以执行闹钟  （异步执行）
            pendingIntent = PendingIntent.getBroadcast(context, alarmCount++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent. FLAG_INCLUDE_STOPPED_PACKAGES);

            //让定时任务的触发时间从1970年1月1日0点开始算起，但会唤醒CPU  第二个参数：距离目标时间的差值
            //int time = 20 * 1000;
            //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + time, pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            Log.d(TAG,calendar.getTime()+"!!!!!!!!!");
            intent = null;
            pendingIntent = null;
            Log.d(TAG,"Send Broadcast Success");
        }

        //删除闹钟：响铃结束+手动删除
        public void cancelAlarm(String todo, String date, String time, int code) {
            alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("todo", todo);
            intent.putExtra("date", date);
            intent.putExtra("time", time);
            intent.putExtra("remindTypeCode", code);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "deleteAlarm: " + intent.getStringExtra("todo") + intent.getIntExtra("remindTypeCode", 0));
        }
    }

}
