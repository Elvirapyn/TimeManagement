package seventh.bupt.time;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import seventh.bupt.time.Alarm.AlarmService;
import seventh.bupt.time.Alarm.Todo;

import com.loonggg.weekcalendar.view.WeekCalendar;
public class MainActivity extends FragmentActivity implements View.OnClickListener{
    /*
    郭正鑫
    界面
    */

    public static MainActivity mainActivity;
    //三个Fragment对象
    private CalendarView calView;
    private personal_center per;

    //存放Fragment对象
    private FrameLayout frameLayout;
    //每个选项的控件定义
    private RelativeLayout cal_rl;
    private RelativeLayout per_rl;
    private TextView cal_tv;
    private TextView per_tv;

    //定义fragmentManeger对象管理器
    private FragmentManager fragmentManager;

    /*
    平雅霓
    添加事务
    */
    private Button addNormalTransaction;
    public static final String PREFERENCE_NAME = "SaveSetting";
    public static int MODE = Context.MODE_PRIVATE;
    public DBAdapter dbAdapter;
    //对话框的按钮
    private EditText trsct_description;
    private EditText startDate;
    private EditText startTime;
    private EditText endTime;
    private EditText lastDays;
    // 定义显示时间控件
    private Calendar calendar; // 通过Calendar获取系统时间

    private GridAdapter curAdapter;//获取当前的gridAdapter

    private final String TAG = this.getClass().getSimpleName();

    View popView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager= getSupportFragmentManager();
        initView();//初始化控件
        Choice_menu(0);//初始化时页面加载第一个日历选项

        /*平雅霓part*/
        //添加事务的按钮
        addNormalTransaction = (Button) findViewById(R.id.addTransaction);
        calendar = Calendar.getInstance();
        //打开数据库
        dbAdapter = new DBAdapter(MainActivity.this);
        dbAdapter.open();

        /*
            wqm
            启动时从数据库中读入数据
        */
        loadDataFromDatabase();
       /* WeekCalendar weekCalendar=(WeekCalendar)findViewById(R.id.week_calendar);
        String curDay=weekCalendar.getTheDayOfSelected();*/

       mainActivity = this;
    }
    @Override
    protected void onStart() {
        super.onStart();
        addNormalTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
                //dbAdapter.deleteAllData();
                NormalTransaction []transactions = dbAdapter.queryAllData();
                if(transactions!=null)
                    for (int i = 0; i < transactions.length; i++)
                        System.out.println(transactions[i].toString());
            }
        });

    }

    //初始化控件
    private void initView() {
        cal_tv =(TextView) findViewById(R.id.cal_tv);
        per_tv=(TextView) findViewById(R.id.per_tv);
        cal_rl=(RelativeLayout) findViewById(R.id.cal_rl);
        per_rl=(RelativeLayout) findViewById(R.id.per_rl);
        cal_rl.setOnClickListener(MainActivity.this);
        per_rl.setOnClickListener(MainActivity.this);
    }
    //底部导航栏的点击
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cal_rl:
                Choice_menu(0);
                break;
            case R.id.per_rl:
                Choice_menu(1);
                break;
            default:
                break;
        }
    }

    //设置点击菜单的处理事件
    private void Choice_menu(int index) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        hindFragments(fragmentTransaction);//隐藏所有fragment
        switch (index){
            case 0:
                //若cal为空，创建一个并添加到界面上
                if(calView ==null){
                    calView = new CalendarView();
                    fragmentTransaction.add(R.id.content, calView);
                    //点击后可以显示当前的事务列表
                    /*WeekCalendar weekCalendar=findViewById(R.id.week_calendar);
                    GridView gridView=findViewById(com.loonggg.weekcalendar.R.id.gridview);
                    String[] weekDate=CalendarView.getWeekofDate(weekCalendar.getTheDayOfSelected());
                    NormalTransaction[] new_tasks=dbAdapter.queryWeekData(weekDate);
                    ArrayList<HashMap<String, Object>> arrayList= CalendarView.getArrayList(new_tasks);
                    GridAdapter gridAdapter = new GridAdapter(gridView.getContext(), arrayList, LayoutInflater.from(gridView.getContext()));
                    gridView.setAdapter(gridAdapter);*/

                    //curAdapter=calView.getGridAdapter();
                }else{
                    //不为空，显示
                    fragmentTransaction.show(calView);
                    //点击后可以显示当前的事务列表
                    WeekCalendar weekCalendar=findViewById(R.id.week_calendar);
                    GridView gridView=findViewById(com.loonggg.weekcalendar.R.id.gridview);
                    String[] weekDate=CalendarView.getWeekofDate(weekCalendar.getTheDayOfSelected());
                    NormalTransaction[] new_tasks=dbAdapter.queryWeekData(weekDate);
                    ArrayList<HashMap<String, Object>> arrayList= CalendarView.getArrayList(new_tasks);
                    GridAdapter gridAdapter = new GridAdapter(gridView.getContext(), arrayList, LayoutInflater.from(gridView.getContext()));
                    gridView.setAdapter(gridAdapter);

                }
                break;
            case 1:
                //若per为空，创建一个并添加到界面上
                if(per==null){
                    per = new personal_center();
                    fragmentTransaction.add(R.id.content,per);
                }else{
                    //不为空，显示
                    fragmentTransaction.show(per);
                }
                break;
        }
        //fragmentTransaction.commit();//提交
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void hindFragments(FragmentTransaction fragmentTransaction) {
        if(calView !=null){
            fragmentTransaction.hide(calView);
        }
        if(per != null){
            fragmentTransaction.hide(per);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        //dbAdapter.close();
    }
    @Override
    public void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
        Choice_menu(0);//初始化时页面加载第一个日历选项
        //dbAdapter.close();
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        dbAdapter.close();
    }
    /*
    平雅霓part
    添加事务*/
    //初始化并弹出对话框方法
    private void showDialog() {
        // 弹出自定义dialog
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
        View view = inflater.inflate(R.layout.add_transaction_dialog, null);

        // 对话框
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();

        // 设置宽度为屏幕的宽度
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth()); // 设置宽度
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);

        LinearLayout dialoglayout = view.findViewById(R.id.dialoglayout);
        dialoglayout.setBackgroundResource(R.drawable.round_white_corner);

        //获取对话框的组件id
        Button btn_click_ok = view.findViewById(R.id.click_ok);
        btn_click_ok.setBackgroundResource(R.drawable.round_text);
        trsct_description = (EditText) view.findViewById(R.id.transaction_desctiption);
        startDate = (EditText) view.findViewById(R.id.startDate);
        startDate.setBackgroundResource(R.drawable.round_text);
        startTime = (EditText) view.findViewById(R.id.startTime);
        startTime.setBackgroundResource(R.drawable.round_text);
        endTime = (EditText) view.findViewById(R.id.endTime);
        endTime.setBackgroundResource(R.drawable.round_text);
        lastDays = (EditText) view.findViewById(R.id.lastDays);
        lastDays.setBackgroundResource(R.drawable.round_text);
        final CheckBox cb = (CheckBox) view.findViewById(R.id.isNotify);

        //设置初始化日期
        Date date = new java.util.Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(date);
        startDate.setText("   " + dateString);
        startTime.setText("   " + "00:00");
        endTime.setText("   " + "00:00");

        //初始化持续天数
        lastDays.setText("   1");

        //开始日期点击事件
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDate(v);
            }
        });
        btn_click_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = startDate.getText().toString();
                dialog.dismiss();
            }
        });

        //开始事件的点击事件
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setstartTimes(24, 15);
            }
        });

        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setendTimes(24, 15);
            }
        });
        btn_click_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int conflictflag=0;
                //弹出的冲突对话框
                AlertDialog.Builder conflictDialog = new AlertDialog.Builder(MainActivity.this);
                conflictDialog.setMessage("时间上有冲突啦！请重新选择！");
                conflictDialog.setPositiveButton("确定", null);
                final AlertDialog alertConflictDialog = conflictDialog.create();

                String is_notify;        //是否闹钟提醒
                if (cb.isChecked()) {
                    is_notify = "Y";
                } else is_notify = "N";
                String description = trsct_description.getText().toString();   //事务描述
                String date = startDate.getText().toString().trim();    //开始日期
                int year = Integer.parseInt(date.substring(0, 4));      //年
                int month = Integer.parseInt(date.substring(5, 7));     //月
                int day = Integer.parseInt(date.substring(8, 10));      //日
                String start_time = startTime.getText().toString().trim();    //开始时间
                String end_time = endTime.getText().toString().trim();        //结束时间
                int last_days = Integer.parseInt(lastDays.getText().toString().trim());    //持续天数

                ArrayList<String> dateList =new ArrayList<String>();     //日期列表
                for (int d = 0; d < last_days; d++) {
                    //大月
                    if ((month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) && day < 31) {
                        String newDate = year + "-" + month + "-" + day;
                        dateList.add(newDate);
                        day++;
                        continue;
                    } else if ((month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) && day == 31) {
                        if (month != 12) {
                            String newDate = year + "-" + month + "-" + day;
                            dateList.add(newDate);
                            month++;
                            day = 1;
                            continue;
                        } else {
                            String newDate = year + "-" + month + "-" + day;
                            dateList.add(newDate);
                            year++;
                            month = 1;
                            day = 1;
                            continue;
                        }
                    }

                    //小月
                    if ((month == 4 || month == 6 || month == 9 || month == 11) && day < 30) {
                        String newDate = year + "-" + month + "-" + day;
                        dateList.add(newDate);
                        day++;
                        continue;
                    } else if ((month == 4 || month == 6 || month == 9 || month == 11) && day == 30) {
                        String newDate = year + "-" + month + "-" + day;
                        dateList.add(newDate);
                        month++;
                        day = 1;
                        continue;
                    }
                    //2月
                    if (month == 2) {
                        //闰年
                        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
                            if (day != 29) {
                                String newDate = year + "-" + month + "-" + day;
                                dateList.add(newDate);
                                day++;
                                continue;
                            } else {
                                String newDate = year + "-" + month + "-" + day;
                                dateList.add(newDate);
                                month++;
                                day = 1;
                                continue;
                            }
                        }
                        //平年
                        else {
                            if (day != 28) {
                                String newDate = year + "-" + month + "-" + day;
                                dateList.add(newDate);
                                day++;
                                continue;
                            } else {
                                String newDate = year + "-" + month + "-" + day;
                                dateList.add(newDate);
                                month++;
                                day = 1;
                                continue;
                            }
                        }
                    }
                }

                //检测是否有冲突
                for(int i=0;i<dateList.size();i++){
                    String dateListItem = dateList.get(i);
                    String num[] = dateListItem.split("-");
                    String newdateItem = num[0];
                    if(Integer.parseInt(num[1])/10==0)newdateItem = newdateItem +"-0"+num[1];  //月份是一位数
                    else  newdateItem = newdateItem + "-"+num[1];
                    if(Integer.parseInt(num[2])/10==0)newdateItem = newdateItem +"-0"+num[2];  //月份是一位数
                    else  newdateItem = newdateItem + "-"+num[2];
                    dateList.set(i,newdateItem);
                    if(isConflict(dateList.get(i),start_time,end_time)) {
                        conflictflag=1;
                        break;
                    }
                }

                //没有冲突则插入数据库
                if(conflictflag==0){
                    for(int i=0;i<dateList.size();i++){
                        NormalTransaction transaction = new NormalTransaction(dateList.get(i), is_notify, description, start_time, end_time);
                        dbAdapter.insert(transaction);//在这里添加数据，按理说应该调用gridadapter中的东西
                        /*
                            wqm
                            为新建的事务创建提醒
                         */
                        Todo todo = new Todo();
                        todo.setCode(0);
                        todo.setTodo(transaction.description_);
                        todo.setDate(transaction.transactionDate_);
                        todo.setTime(transaction.startTime_);
                        setService(todo,true);

                        //添加数据后实时更新
                        /*curAdapter=calView.getGridAdapter();
                        curAdapter.notifyDataSetChanged();
                        WeekCalendar weekCalendar=findViewById(R.id.week_calendar);
                        String[] weekDate=CalendarView.getWeekofDate(weekCalendar.getTheDayOfSelected());
                        NormalTransaction[] new_tasks=dbAdapter.queryWeekData(weekDate);
                        ArrayList<HashMap<String, Object>> arrayList=CalendarView.getArrayList(new_tasks);
                        curAdapter.setTaskList(arrayList);*/
                        WeekCalendar weekCalendar=findViewById(R.id.week_calendar);
                        GridView gridView=findViewById(com.loonggg.weekcalendar.R.id.gridview);
                        String[] weekDate=CalendarView.getWeekofDate(weekCalendar.getTheDayOfSelected());
                        NormalTransaction[] new_tasks=dbAdapter.queryWeekData(weekDate);
                        ArrayList<HashMap<String, Object>> arrayList= CalendarView.getArrayList(new_tasks);
                        GridAdapter gridAdapter = new GridAdapter(gridView.getContext(), arrayList, LayoutInflater.from(gridView.getContext()));
                        gridView.setAdapter(gridAdapter);

                    }
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "添加成功啦！！",Toast.LENGTH_LONG).show();
                }
                //有冲突弹出提示框
                if(conflictflag==1){
                    alertConflictDialog.show();
                }
            }
        });
    }

    //时间比较函数
    public int compareTime(String time1,String time2){
        //time1大返回1，time2大返回2,时间相等返回0
        int hour1 = Integer.parseInt(time1.substring(0,2));
        int minite1 = Integer.parseInt(time1.substring(3,5));
        int hour2 = Integer.parseInt(time2.substring(0,2));
        int minite2 = Integer.parseInt(time2.substring(3,5));
        if(hour1>hour2)return 1;
        else if(hour1==hour2) {
            if (minite1 > minite2) return 1;
            else if (minite1 < minite2) return 2;
            else return 0;
        }
        else {
            return 2;
        }
    }

    //检测冲突函数
    public boolean isConflict(String date,String startTime, String endTime){
        NormalTransaction []transactions = dbAdapter.queryDateData(date);
        if(transactions!=null)
            for(int i=0;i<transactions.length;i++){
                if((compareTime(startTime,transactions[i].startTime_)==2)
                        &&(compareTime(endTime,transactions[i].startTime_)==1&&
                        (compareTime(endTime,transactions[i].endTime_)==2)))return true;
                if(compareTime(startTime,transactions[i].startTime_)<=1&&
                        (compareTime(endTime,transactions[i].endTime_)==2
                                ||compareTime(endTime,transactions[i].endTime_)==0))return true;
                if(compareTime(startTime,transactions[i].startTime_)==1&&
                        compareTime(endTime,transactions[i].endTime_)==1&&
                        (compareTime(startTime,transactions[i].endTime_)==2))return true;
                if((compareTime(startTime,transactions[i].startTime_)==2||compareTime(startTime,transactions[i].startTime_)==0)
                        &&(compareTime(endTime,transactions[i].endTime_)<=1))return true;
            }
        return false;
    }

    //日期选择器
    @SuppressLint("NewApi")
    public void selectDate(View v) {
        int themeResId = 2;
        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, themeResId, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 此处得到选择的时间，可以进行你想要的操作
                if(monthOfYear/10==0&&dayOfMonth/10==0)
                    startDate.setText("   " + year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth);
                else if(monthOfYear/10==0)
                    startDate.setText("   " + year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth);
                else if(dayOfMonth/10==0)
                    startDate.setText("   " + year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth);
                else startDate.setText("   " + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH));
        DatePicker datePicker = dialog.getDatePicker();
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    //开始时间选择器
    public void setstartTimes(final int hour_day, final int minute) {
        String[] name = new String[]{"00", "05","10","15","20","25","30","35","40","45","50","55"};
        BasisTimesUtils.showTimerPickerDialog(MainActivity.this, 2, "请选择时间", hour_day, minute, true,
                new BasisTimesUtils.OnTimerPickerListener() {
                    @Override
                    public void onConfirm(int hourOfDay, int minute) {
                        String months;
                        String dayOfMonths;
                        if (hourOfDay < 10) {
                            months = "0" + hourOfDay;
                        } else {
                            months = hourOfDay + "";
                        }
                        if (minute < 10) {
                            dayOfMonths = "0" + minute;
                        } else {
                            dayOfMonths = "" + minute;
                        }
                        String times = "   " + months + ":" + dayOfMonths;
                        startTime.setText(times);
                        endTime.setText(times);
                    }

                    @Override
                    public void onCancel() {

                    }
                }, 5, name);
    }

    //结束时间选择器
    public void setendTimes(final int hour_day, final int minute) {
        //弹出的冲突对话框
        AlertDialog.Builder conflictDialog = new AlertDialog.Builder(MainActivity.this);
        conflictDialog.setMessage("结束时间应大于开始时间！");
        conflictDialog.setPositiveButton("确定", null);
        final AlertDialog alertdialog= conflictDialog.create();

        String[] name = new String[]{"00", "05","10","15","20","25","30","35","40","45","50","55"};
        BasisTimesUtils.showTimerPickerDialog(MainActivity.this, 2, "请选择时间", hour_day, minute, true,
                new BasisTimesUtils.OnTimerPickerListener() {
                    @Override
                    public void onConfirm(int hourOfDay, int minute) {
                        String months;
                        String dayOfMonths;
                        if (hourOfDay < 10) {
                            months = "0" + hourOfDay;
                        } else {
                            months = hourOfDay + "";
                        }
                        if (minute < 10) {
                            dayOfMonths = "0" + minute;
                        } else {
                            dayOfMonths = "" + minute;
                        }
                        String times = "   " + months + ":" + dayOfMonths;
                        //检测结束时间是否大于开始时间
                        if(isEndTimeRight(startTime.getText().toString().trim(),times.trim()))
                            endTime.setText(times);
                        else alertdialog.show();
                    }

                    @Override
                    public void onCancel() {

                    }
                }, 5, name);
    }

    //检测结束时间是否符合要求
    public boolean isEndTimeRight(String startTime,String endTime){
        if(compareTime(startTime,endTime)==2)return true;
        return false;
    }

    /*
        wqm:
        加载数据库中信息，恢复alarm服务
     */
    public void setService(Todo todo,boolean isSetAlarm){
        Intent intentToAlarmService = new Intent(this, AlarmService.class);
        intentToAlarmService.putExtra("todo", todo.getTodo());
        intentToAlarmService.putExtra("date", todo.getDate());
        intentToAlarmService.putExtra("time", todo.getTime());
        intentToAlarmService.putExtra("remindTypeCode", todo.getCode());
        intentToAlarmService.putExtra("isSetAlarm", isSetAlarm);

        this.startService(intentToAlarmService);
        Log.d(TAG, "onClick: " + "添加成功");
        Log.d(TAG,"Date:"+intentToAlarmService.getStringExtra("date"));
    }

    public void loadDataFromDatabase() {
        Log.d(TAG, "查询数据库");

        NormalTransaction[] noramalTasks = dbAdapter.queryNotify();
        //遍历normalTasks 启动alarm
        //TEST DATA  此处应改为下方从SQL数据库查询数据
        Todo todo;
        if(noramalTasks!=null) {
            for (NormalTransaction cursor : noramalTasks) {
                todo = new Todo();
                todo.setTodo(cursor.description_);
                todo.setDate(cursor.transactionDate_);
                todo.setTime(cursor.startTime_);
                todo.setCode(0);
                Log.d(TAG, "load(): " + todo.getTodo());
                //判断是否设置闹钟
                if(isSetAlarm(cursor.startTime_,cursor.transactionDate_)&cursor.isNotify_=="Y"){
                    setService(todo, true);
                    Log.d(TAG, "Time:" + cursor.startTime_);
                }
                else Log.d(TAG, "Not Set " + cursor.startTime_);
            }
        }
    }

    /*
        //删除alarm时需要执行的操作，添加在数据库的delete语句后面 transaction为要删除的对象
        Todo todo = new Todo();
        todo.setCode(0);
        todo.setTodo(transaction.description_);
        todo.setDate(transaction.transactionDate_);
        todo.setTime(transaction.startTime_);
        setService(todo,false);
     */
    public boolean isSetAlarm(String time, String date){
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

        String[] date_alarm,time_alarm;
        int year, month, day, hour, minute;
        date_alarm = date.split("-");
        year = Integer.parseInt(date_alarm[0]);
        month = Integer.parseInt(date_alarm[1]);
        day = Integer.parseInt(date_alarm[2]);
        time_alarm = time.split(":");
        hour = Integer.parseInt(time_alarm[0]);
        minute = Integer.parseInt(time_alarm[1]);
        calendar.set(year, (month-1), day, hour, minute, 0);
        long alarmMillis = calendar.getTimeInMillis();

        Calendar sys_calendar = Calendar.getInstance();
        long sysMillis = sys_calendar.getTimeInMillis();

        Log.d(TAG, "AlarmMillis: " + alarmMillis+"   SysMillis: "+sysMillis);

        if(alarmMillis>=sysMillis)
            return true;
        else return false;

    }
}
