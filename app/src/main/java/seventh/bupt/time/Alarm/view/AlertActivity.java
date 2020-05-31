/*
   1.响铃振动
   2.弹出提示框
 */

package seventh.bupt.time.Alarm.view;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import seventh.bupt.time.Alarm.AlarmService;
import seventh.bupt.time.Alarm.Todo;
import seventh.bupt.time.MainActivity;
import seventh.bupt.time.R;


public class AlertActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();
    public static AlertActivity alertActivity;

    //闹铃以及震动
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private Todo td;
    private String todo;
    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertActivity = this;
        Intent intent = getIntent();

        td = new Todo();
        td.setTime(intent.getStringExtra("time"));
        td.setDate(intent.getStringExtra("date"));
        td.setTodo(intent.getStringExtra("todo"));
        td.setCode(intent.getIntExtra("remindTypeCode",0));

        //解析接收到的消息 包括事务名称和响铃类型
        todo = intent.getStringExtra("todo");
        code = intent.getIntExtra("remindTypeCode", 0);
        Log.d(TAG, "onCreate: " + todo + code);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //显示事件提醒框
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                Util util=new Util();
                util.showTips(AlertActivity.this,todo);
            }
        });
        //if (code == 0) {
        //响铃+震动30s
        mediaPlayer = MediaPlayer.create(this, R.raw.bell);
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

        //停止服务
        stopService(td);
        mediaPlayer.start();
        vibrator.vibrate(6000);
        Log.d(TAG,"Start Success");
        /*
        //震动
        else {
            vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(30000);
       */

    }

    public void stop(){
        //关闭振动
        if(vibrator!=null){
            vibrator.cancel();
        }
        //关闭响铃
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Log.d(TAG, "stop: " + todo + code);
    }

    //提示框的onClick方法
    public void stopMedia(View view) {
        //关闭振动
        if(vibrator!=null){
            vibrator.cancel();
        }
        //关闭响铃
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        MainActivity.mainActivity.onRestart();
        Log.d(TAG, "stopService: " + todo + code);
    }

    //停止服务
    public void stopService(Todo todo){
        Intent intentToAlarmService = new Intent(this, AlarmService.class);
        intentToAlarmService.putExtra("todo", todo.getTodo());
        intentToAlarmService.putExtra("date", todo.getDate());
        intentToAlarmService.putExtra("time", todo.getTime());
        intentToAlarmService.putExtra("remindTypeCode", todo.getCode());
        intentToAlarmService.putExtra("isSetAlarm", false);

        this.startService(intentToAlarmService);
        Log.d(TAG, "onClick: " + "删除成功");
        Log.d(TAG,"Date:"+intentToAlarmService.getStringExtra("date"));
    }

}
