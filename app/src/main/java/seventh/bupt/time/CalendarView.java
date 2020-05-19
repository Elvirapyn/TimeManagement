package seventh.bupt.time;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.loonggg.weekcalendar.view.WeekCalendar;

import java.util.ArrayList;
import java.util.List;

//日历界面

public class CalendarView extends  Fragment {
    //private ImageView add;
    /*private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;*/

    //WeekCalendar weekCalendar;//自定义日历控件


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calendar,container,false);
        return view;
    }
    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        //add = getActivity().findViewById(R.id.add);
        //add.bringToFront();
        //weekCalendar = (WeekCalendar) getActivity().findViewById(R.id.week_calendar);
    }

}

