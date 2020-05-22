/*
    接收广播
    执行操作：1.响铃振动
              2.弹窗
 */
package seventh.bupt.time.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import seventh.bupt.time.Alarm.view.AlertActivity;


public class AlarmReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: " + intent.getStringExtra("todo") + intent.getIntExtra("remindTypeCode", 0));
        Log.d(TAG, "onReceive:  提醒");

        //唤醒AlertActivity，弹出弹窗、响铃和振动
        Intent toAlertAct = new Intent(context, AlertActivity.class);

        //向AlertActivity传参
        toAlertAct.putExtra("todo", intent.getStringExtra("todo"));
        toAlertAct.putExtra("remindTypeCode", intent.getIntExtra("remindTypeCode", 0));
        Log.d(TAG, "onReceive: " + intent.getStringExtra("todo") + intent.getIntExtra("remindTypeCode", 0));
        //创建新的alarm
        toAlertAct.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(toAlertAct);
    }
}
