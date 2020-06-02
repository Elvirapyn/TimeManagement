package seventh.bupt.time;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CustomTimePickerDialog extends TimePickerDialog
{
    final OnTimeSetListener mCallback;
    TimePicker mTimePicker;
    final int increment; //调整跳过的滚动距离，10的话就是每十个一显示
    //    private String[] name=new String[]{"00","10","20","30","40","50"};
    private String[] name;//与间隔对应的数据
    public CustomTimePickerDialog(Context context, int themeResId, OnTimeSetListener callBack,
                                  int hourOfDay, int minute, boolean is24HourView, int interval, String[] name)
    {

        super(context,themeResId, callBack, hourOfDay, minute, is24HourView);

        this.mCallback = callBack;
        this.increment = interval;
        this.name=name;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mCallback != null && mTimePicker!=null) {
            mTimePicker.clearFocus();
            mCallback.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                    mTimePicker.getCurrentMinute()*increment);
        }
    }

    @Override
    protected void onStop()
    {
        // override and do nothing
    }

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            Class<?> rClass = Class.forName("com.android.internal.R$id");
            Field timePicker = rClass.getField("timePicker");
            this.mTimePicker = (TimePicker)findViewById(timePicker.getInt(null));
            Field m = rClass.getField("minute");

            NumberPicker mMinuteSpinner = (NumberPicker)mTimePicker.findViewById(m.getInt(null));
            mMinuteSpinner.setMinValue(0);
            mMinuteSpinner.setMaxValue((60/increment)-1);//显示计算

            List<String> displayedValues = new ArrayList<String>();
            for(int i=0;i<60;i+=increment)//循环，处理的是关于返回值，必须要与你自己需要间隔的对等
            {
                displayedValues.add(String.format("%02d", i));
            }
            mMinuteSpinner.setDisplayedValues(name);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

