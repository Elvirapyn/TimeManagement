/*
   1.响铃振动
   2.弹出提示框
 */

package seventh.bupt.time.Alarm.view;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import seventh.bupt.time.R;


public class AlertActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    //闹铃以及震动
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;

    private String todo;
    private int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        Intent intent = getIntent();

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
        mediaPlayer.start();
        vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(15000);
        Log.d(TAG,"Start Success");
        /*
        //震动
        else {
            vibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
            vibrator.vibrate(30000);
       */
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭响铃
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    //提示框的onClick方法
    public void stopMedia(View view) {
        vibrator.cancel();
        onDestroy();
    }

}
