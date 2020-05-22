package seventh.bupt.time;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.loonggg.weekcalendar.view.WeekCalendar;
import com.loonggg.weekcalendar.entity.CalendarData;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

//日历界面

public class CalendarView extends  Fragment {
    //private ImageView add;
    /*private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;*/
    private ArrayList<HashMap<String, Object>> taskList = new ArrayList<HashMap<String, Object>>();
    private DBAdapter dbAdapter1;// =getActivity().getSharedPreferences("lesson", Context.MODE_PRIVATE);
    private GridAdapter gridAdapter;
    private GridView gridView;
    //WeekCalendar weekCalendar;//自定义日历控件




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState) {
        super.onActivityCreated(saveInstanceState);
        //add = getActivity().findViewById(R.id.add);
        //add.bringToFront();
        //weekCalendar = (WeekCalendar) getActivity().findViewById(R.id.week_calendar);

        dbAdapter1 = new DBAdapter(getContext());
        dbAdapter1.open();
        WeekCalendar weekCalendar = (WeekCalendar) getActivity().findViewById(R.id.week_calendar);
        //weekCalendar.
        String curDay = weekCalendar.getTheDayOfSelected();

        System.out.println("被选中的日期为" + curDay);
        final String[] weekofdate = getWeekofDate(curDay);
        for (int i = 0; i < 7; i++) {
            System.out.println("本周内的日期为：" + weekofdate[i]);
        }
        //下面的一个任务就是获取当前日期所在的一周内的日期


        //  gridAdapter.notifyDataSetChanged();
       /* NormalTransaction[] noramalTasks = dbAdapter1.queryAllData();//这一句是获取所有数据的
        NormalTransaction a = new NormalTransaction("2017-07-07 ", "N", "学英语", "6:00", "8:00");
        NormalTransaction b = new NormalTransaction("2017-07-07 ", "N", "学数学", "10:00", "11:00");
        NormalTransaction c = new NormalTransaction("2017-07-08 ", "N", "学语文", "9:00", "11:00");
        NormalTransaction d = new NormalTransaction("2017-07-09 ", "N", "学音乐", "10:00", "11:00");

        for (int i = 0; i < noramalTasks.length; i++) {

            String date = noramalTasks[i].transactionDate_;
            String description = noramalTasks[i].description_;
            String startDatetime = noramalTasks[i].transactionDate_ + noramalTasks[i].startTime_;
            //Timestamp time = Timestamp.valueOf("2017-06-07 17:00:00");
            HashMap<String, Object> date_to_task = new HashMap<>();
            //date_to_task.put("date",date);
            //date_to_task.put(startDatetime,description);
            date_to_task.put("taskList" + i, (Object) noramalTasks[i]);
            taskList.add(date_to_task);//这是获取到了整个的时间--任务map

            gridView = (GridView) getActivity().findViewById(R.id.gridview);


            // gridAdapter.notifyDataSetChanged();//实时监控数据变化

        }*/
        //这里是需要改的！！！查询的内容会变
        int weekday = getWeekday(curDay);//获取到了周几
        //NormalTransaction[] normalTransactions=dbAdapter1.queryAllData();
        final NormalTransaction[] normalTransactions = dbAdapter1.queryWeekData(weekofdate);
        taskList = getArrayList(normalTransactions);//此处已经修改了tasklist的值
        gridView = (GridView) getActivity().findViewById(R.id.gridview);

        gridAdapter = new GridAdapter(getContext(), taskList, LayoutInflater.from(getContext()));

        gridView.setAdapter(gridAdapter);
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                NormalTransaction tran=(NormalTransaction)taskList.get(position).get("taskList"+position);
                dbAdapter1.deleteOneData(tran.transactionDate_,tran.startTime_);
                for(int i=position;i<normalTransactions.length-1;i++)
                    normalTransactions[i]=normalTransactions[i+1];
                taskList=getArrayList(normalTransactions);
                gridAdapter.notifyDataSetChanged();
                /*NormalTransaction[] new_tasks=dbAdapter1.queryWeekData(weekofdate);
                ArrayList<HashMap<String, Object>> arrayList=calView.getArrayList(new_tasks);
                curAdapter.setTaskList(arrayList);*/
                return false;

            }


        });
    }

    public ArrayList<HashMap<String, Object>> getArrayList(NormalTransaction[] normalTasks) {
        //  NormalTransaction[] noramalTasks=dbAdapter1.queryAllData();//这一句是获取所有数据的
        NormalTransaction a = new NormalTransaction("2017-07-07 ", "N", "学英语", "6:00", "8:00");
        NormalTransaction b = new NormalTransaction("2017-07-07 ", "N", "学数学", "10:00", "11:00");
        NormalTransaction c = new NormalTransaction("2017-07-08 ", "N", "学语文", "9:00", "11:00");
        NormalTransaction d = new NormalTransaction("2017-07-09 ", "N", "学音乐", "10:00", "11:00");
        ArrayList<HashMap<String, Object>> taskList_ = new ArrayList<HashMap<String, Object>>();
        if(normalTasks!=null)
        for (int i = 0; i < normalTasks.length; i++) {

            String date = normalTasks[i].transactionDate_;
            String description = normalTasks[i].description_;
            String startDatetime = normalTasks[i].transactionDate_ + normalTasks[i].startTime_;
            //Timestamp time = Timestamp.valueOf("2017-06-07 17:00:00");
            HashMap<String, Object> date_to_task = new HashMap<>();
            //date_to_task.put("date",date);
            //date_to_task.put(startDatetime,description);
            date_to_task.put("taskList" + i, (Object) normalTasks[i]);
            taskList_.add(date_to_task);//这是获取到了整个的时间--任务map




            // gridAdapter.notifyDataSetChanged();//实时监控数据变化

        }

                   return taskList_;
    }


    public GridAdapter getGridAdapter() {
        return gridAdapter;
    }
    public int getWeekday(String pTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {

            e.printStackTrace();
        }

        return c.get(Calendar.DAY_OF_WEEK)-1;//周日为0
    }

    public String[] getWeekofDate(String curDay) {//获取当前日期所在周的所有日期
          String[] weekofDate=new String[7];
          int weekday=getWeekday(curDay);//此处设置的是周日为0
          Calendar cal=Calendar.getInstance();
          Date curDate=convertDate(curDay);
          cal.set(curDate.getYear()+1900,curDate.getMonth(),curDate.getDate());
          //month从0开始
          System.out.println("年"+cal.getTime());
          //System.out.println("月"+curDate.getMonth()+1);
          //System.out.println("月"+curDate.getDate());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);
        Date testdate=convertDate("2020-10-10");
        System.out.println("测试两位数月份"+dateFormat.format(testdate));
        cal.add(Calendar.DATE,-weekday-1);//获取当前周的第一天
        weekofDate[0]=dateFormat.format(cal.getTime());
          //获取当前选中的日期
          for(int i=0;i<7;i++){

              cal.add(Calendar.DATE,1);
              weekofDate[i]=dateFormat.format(cal.getTime());


          }
         // cal.set()
        return weekofDate;
    }
    public Date convertDate(String dateString) {
        Date timeDate=new Date();
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH); // hh 也可以换成 kk
            //dateFormat.setLenient(false);
            timeDate= dateFormat.parse(dateString);   // util类型
            Timestamp dateTime = new Timestamp(timeDate.getTime()); // Timestamp类型, timeDate.getTime()返回一个 long 型
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return timeDate;
    }

}

