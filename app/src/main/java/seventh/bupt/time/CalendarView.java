package seventh.bupt.time;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.loonggg.weekcalendar.view.WeekCalendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        NormalTransaction[] normalTransactions=dbAdapter1.queryAllData();
        taskList=getArrayList(normalTransactions);//此处已经修改了tasklist的值
        gridView = (GridView) getActivity().findViewById(R.id.gridview);
        gridAdapter = new GridAdapter(getContext(), taskList, LayoutInflater.from(getContext()));
        gridView.setAdapter(gridAdapter);
    }

    public ArrayList<HashMap<String, Object>> getArrayList(NormalTransaction[] normalTasks) {
        //  NormalTransaction[] noramalTasks=dbAdapter1.queryAllData();//这一句是获取所有数据的
        NormalTransaction a = new NormalTransaction("2017-07-07 ", "N", "学英语", "6:00", "8:00");
        NormalTransaction b = new NormalTransaction("2017-07-07 ", "N", "学数学", "10:00", "11:00");
        NormalTransaction c = new NormalTransaction("2017-07-08 ", "N", "学语文", "9:00", "11:00");
        NormalTransaction d = new NormalTransaction("2017-07-09 ", "N", "学音乐", "10:00", "11:00");
        ArrayList<HashMap<String, Object>> taskList_ = new ArrayList<HashMap<String, Object>>();
        if(normalTasks!=null) {
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
        }
        return taskList_;
    }


    public GridAdapter getGridAdapter() {
        return gridAdapter;
    }
}

