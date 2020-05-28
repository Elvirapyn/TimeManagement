package seventh.bupt.time;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import androidx.fragment.app.Fragment;
import com.loonggg.weekcalendar.R;
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

    private ArrayList<HashMap<String, Object>> taskList = new ArrayList<HashMap<String, Object>>();
    private DBAdapter dbAdapter1;
    private GridAdapter gridAdapter;
    private GridView gridView;

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
        final WeekCalendar weekCalendar = getActivity().findViewById(R.id.week_calendar);


        /*System.out.println("被选中的日期为" + curDay);
        for (int i = 0; i < 7; i++) {
            System.out.println("本周内的日期为：" + weekofdate[i]);
        }*/
        //下面的一个任务就是获取当前日期所在的一周内的日期
        //这里是需要改的！！！查询的内容会变

        //获取上方日历栏的当前日期
        String curDay = weekCalendar.getTheDayOfSelected();
        final String[] weekofdate = getWeekofDate(curDay);
        //获取一周内的事务
        final NormalTransaction[] normalTransactions = dbAdapter1.queryWeekData(weekofdate);
        taskList = getArrayList(normalTransactions);
        gridView = (GridView) getActivity().findViewById(R.id.gridview);
        gridAdapter = new GridAdapter(getContext(), taskList, LayoutInflater.from(getContext()));
        gridView.setAdapter(gridAdapter);


        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                NormalTransaction tran=(NormalTransaction)taskList.get(position).get("taskList"+position);
                dbAdapter1.deleteOneData(tran.transactionDate_,tran.startTime_);

               //normalTransactions=dbAdapter1.queryWeekData(getWeekofDate(weekCalendar.getTheDayOfSelected()));
                //for(int i=position;i<normalTransactions.length-1;i++)
                 //   normalTransactions[i]=normalTransactions[i+1];
                //taskList=getArrayList(refreshList);
                //gridAdapter.notifyDataSetChanged();

                /*WeekCalendar weekCalendar=getActivity().findViewById(R.id.week_calendar);
                String[] weekDate=CalendarView.getWeekofDate(weekCalendar.getTheDayOfSelected());
                NormalTransaction[] new_tasks=dbAdapter1.queryWeekData(weekDate);
                ArrayList<HashMap<String, Object>> arrayList=CalendarView.getArrayList(new_tasks);
                gridAdapter.setTaskList(arrayList);*/

                GridView gridView=getActivity().findViewById(R.id.gridview);
                String[] weekDate=CalendarView.getWeekofDate(weekCalendar.getTheDayOfSelected());
                NormalTransaction[] new_tasks=dbAdapter1.queryWeekData(weekDate);
                ArrayList<HashMap<String, Object>> arrayList= CalendarView.getArrayList(new_tasks);
                GridAdapter gridAdapter = new GridAdapter(getContext(), arrayList, LayoutInflater.from(getContext()));
                gridView.setAdapter(gridAdapter);
                /*NormalTransaction[] new_tasks=dbAdapter1.queryWeekData(weekofdate);
                ArrayList<HashMap<String, Object>> arrayList=calView.getArrayList(new_tasks);
                curAdapter.setTaskList(arrayList);*/
                return false;

            }


        });
    }

    public static ArrayList<HashMap<String, Object>> getArrayList(NormalTransaction[] normalTasks) {

        ArrayList<HashMap<String, Object>> taskList_ = new ArrayList<HashMap<String, Object>>();
        if (normalTasks != null) {
            for (int i = 0; i < normalTasks.length; i++) {
                HashMap<String, Object> date_to_task = new HashMap<>();
                date_to_task.put("taskList" + i, (Object) normalTasks[i]);
                taskList_.add(date_to_task);//这是获取到了整个的时间--任务map

            }
            // gridAdapter.notifyDataSetChanged();//实时监控数据变化
        }
        return taskList_;

    }

    public GridAdapter getGridAdapter() {
        return gridAdapter;
    }




    //获取当前日期所在周的所有日期
    public static String[] getWeekofDate(String curDay) {
          String[] weekofDate=new String[7];
          int weekday=GridAdapter.getWeekday(curDay);//此处设置的是周日为0
          Calendar cal=Calendar.getInstance();
          Date curDate=convertDate(curDay);
          cal.set(curDate.getYear()+1900,curDate.getMonth(),curDate.getDate());
          //month从0开始
          //System.out.println("年"+cal.getTime());
          //System.out.println("月"+curDate.getMonth()+1);
          //System.out.println("月"+curDate.getDate());
          DateFormat dateFormat = new SimpleDateFormat("yyyy-M-dd", Locale.ENGLISH);
          cal.add(Calendar.DATE,-weekday-1);//获取当前周的第一天
          weekofDate[0]=dateFormat.format(cal.getTime());
          for(int i=0;i<7;i++){
            cal.add(Calendar.DATE,1);
            weekofDate[i]=dateFormat.format(cal.getTime());
          }
        //Date testdate=convertDate("2020-10-10");
        //System.out.println("测试两位数月份"+dateFormat.format(testdate));
        return weekofDate;
    }

    //将string转化成date，获取年月日
    public static Date convertDate(String dateString) {
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

