package seventh.bupt.time;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class GridAdapter extends BaseAdapter {
    private DBAdapter dbAdapter;
    private ArrayList<HashMap<String, Object>> taskList ;
    private LayoutInflater layoutInflater;
    private Context myContext;
    int parentHeight=4200;
    int parentWidth=1296;
    public GridAdapter(Context context,ArrayList<HashMap<String, Object>> tasks,LayoutInflater mLayoutInflater) {
        myContext = context;
        layoutInflater = mLayoutInflater;
        taskList=tasks;
        dbAdapter = new DBAdapter(myContext);
        dbAdapter.open();
        NormalTransaction a1=new NormalTransaction("2017-07-11 ","N","java学习","15:00","15:30");
        NormalTransaction a2=new NormalTransaction("2017-07-12 ","N","神经网络学习","8:00","12:30");
        NormalTransaction a3=new NormalTransaction("2017-07-13 ","N","写实验报告","14:00","17:00");
        NormalTransaction a4=new NormalTransaction("2017-07-14 ","N","交周报","17:00","18:00");
        //dbAdapter.insert(e);
        /*NormalTransaction a=new NormalTransaction("2017-07-07 12:00:00","no","学英语","3");
        NormalTransaction b=new NormalTransaction("2017-07-07 15:00:00","no","学语文","2");
        NormalTransaction c=new NormalTransaction("2017-07-07 17:00:00","no","学数学","3");*/
       /* dbAdapter.insert(a1);
        dbAdapter.insert(a2);
        dbAdapter.insert(a3);
        dbAdapter.insert(a4);*/
        /*dbAdapter.insert(a);
        dbAdapter.insert(b);
        dbAdapter.insert(c);*/
    }


    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= getCount()) {
            return null;
        }
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        ViewHolder viewHolder;//若子布局比较复杂，则可以使用viewholder来进行设计

        NormalTransaction[] noramalTasks = dbAdapter.queryAllData();
        System.out.println("position为"+ position);
        System.out.println("length为"+ noramalTasks.length);
        System.out.println(noramalTasks[position].description_);

        String description = noramalTasks[position].description_;
        Date startTime_=convertTime(noramalTasks[position].startTime_);
        int startHour=startTime_.getHours();
        int startMinute=startTime_.getMinutes();

        Date endTime_=convertTime(noramalTasks[position].endTime_);
        int endHour=endTime_.getHours();
        int endMinute=endTime_.getMinutes();

        //  Date curDate=convertDate();

        float duration=(float)endHour-startHour+(float)(endMinute-startMinute)/60;
        convertView = layoutInflater.inflate(R.layout.item_grid, parent, false);

        viewHolder = new ViewHolder();
        viewHolder.mTextView = convertView.findViewById(R.id.grid_item);

        parent.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    public void onGlobalLayout() {

                        parentHeight = parent.getHeight();
                        parentWidth = parent.getWidth();
                        System.out.println("刚调整宽度" + parentWidth);
                        System.out.println("刚调整高度" + parentHeight);

                       /* getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);*/
                    }
                });


        System.out.println("DURATION"+duration);
        float caculatedHeight = (float)parentHeight * duration / 24;//获取到相对的高度
        int bias=(position/7)*3000/24;
        float topPadding=0;
        if(startHour>=5&&startHour<=23) {
           topPadding = ((float) 60.0 * (startHour-5)+ startMinute) / (24 * 60) * 4200 - (float) bias;
        }
        else if(startHour<5) {
            topPadding = ((float) 60.0 * (startHour+19) + startMinute) / (24 * 60) * 4200 - (float) bias;
        }
        // System.out.println("starthour="+startHour);
        //System.out.println("endhour="+endHour);

        //System.out.println("minute="+startMinute);
        //获取和父部件顶端的距离
        // System.out.println("调用高度"+topPadding);
        int weekday=getWeekday(noramalTasks[position ].transactionDate_);
        //System.out.println("!!!!weekday"+position+"    "+weekday);//weekday应该是0---6
        //convertView.setLeft(0);
        convertView.setTranslationX((weekday- position%7) *parentWidth / 7);
        //convertView.setX();
        //convertView.setTranslationX((weekday - 1) *parentWidth / 7);
        //System.out.println("偏移宽度"+((daystest[position]- position%7) *parentWidth / 7));
        convertView.setTranslationY(topPadding);

        convertView.setLayoutParams(new GridView.LayoutParams(parentWidth / 7, (int)caculatedHeight));
        //System.out.println("矩阵高度"+caculatedHeight);
        convertView.setTag(viewHolder);
            /*else {

                viewHolder = (ViewHolder) convertView.getTag();
            }*/
        viewHolder.mTextView.setText(description);
        viewHolder.mTextView.setBackgroundColor(Color.parseColor(getRandomColor()));
        return convertView;
        // }
    }

    public Date convertTime(String dateString) {
        Date timeDate=new Date();
        try {
            DateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH); // hh 也可以换成 kk
            //dateFormat.setLenient(false);
            timeDate= dateFormat.parse(dateString);   // util类型
            Timestamp dateTime = new Timestamp(timeDate.getTime()); // Timestamp类型, timeDate.getTime()返回一个 long 型
            System.out.println(dateTime);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return timeDate;
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

    public int getWeekday(String pTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {

            e.printStackTrace();
        }

        return c.get(Calendar.DAY_OF_WEEK)-1;
    }

    public String getRandomColor(){
        // int randomColor=(int) (Math.random() * 15);
        //15种不同颜色的列表
        ArrayList<String> randomColor=new ArrayList<String>();
        randomColor.add("#FFE4E1");//浅粉色
        randomColor.add("#EEAEEE");//浅紫色
        randomColor.add("#FFEFDB");//淡黄色
        randomColor.add("#E0FFFF");//浅蓝色
        randomColor.add("#9AFF9A");//草绿色
        randomColor.add("#87CEFF");//天蓝色
        randomColor.add("#7FFFD4");//青色
        randomColor.add("#EEAD0E");//橙色

        return randomColor.get((int) (Math.random() * 8));


    }


    private final class ViewHolder
    {
        TextView mTextView;
    }

}
