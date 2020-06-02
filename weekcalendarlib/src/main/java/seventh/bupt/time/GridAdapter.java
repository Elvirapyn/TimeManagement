package seventh.bupt.time;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.loonggg.weekcalendar.R;

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
    int parentHeight=3600;
    int parentWidth=957;//屏幕的长宽，到最后展示的时候要根据相关机型进行调整

    float maxHeight[]=new float[100];//获取每个iter内的最后一件事务的长度，作为下一个iter的偏移量

    public GridAdapter(Context context,ArrayList<HashMap<String, Object>> tasks,LayoutInflater mLayoutInflater) {
        myContext = context;
        layoutInflater = mLayoutInflater;
        taskList=tasks;
       // this.notifyDataSetChanged();
        dbAdapter = new DBAdapter(myContext);
        dbAdapter.open();

    }
//!!!!!!!!一定注意在push前把db版本改回来

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

    public void setTaskList(ArrayList<HashMap<String, Object>> changedDataset){
       taskList =changedDataset;
    }


    @SuppressLint("NewApi")
    @Override
    public View getView(int position,  View convertView, final ViewGroup parent) {

        this.notifyDataSetChanged();
        ViewHolder viewHolder;//若子布局比较复杂，则可以使用viewholder来进行设计
        //获取tasklist中的key
        String index="taskList"+position;
        NormalTransaction curTask=(NormalTransaction)taskList.get(position).get(index);
        System.out.println("position为"+ position);
        System.out.println("date为"+curTask.transactionDate_);
        System.out.println(curTask.description_);

        String description = curTask.description_;

        //获取字符串中的开始时间和结束时间，小时和分钟
        Date startTime_=convertTime(curTask.startTime_);
        int startHour=startTime_.getHours();
        int startMinute=startTime_.getMinutes();
        Date endTime_=convertTime(curTask.endTime_);
        int endHour=endTime_.getHours();
        int endMinute=endTime_.getMinutes();


        float duration=(float)endHour-startHour+(float)(endMinute-startMinute)/60;
        System.out.println("DURATION"+duration);
        convertView = layoutInflater.inflate(R.layout.item_grid, parent, false);
        viewHolder = new ViewHolder();
        viewHolder.mTextView = convertView.findViewById(R.id.grid_item);

        //这段代码用来获取控件的实际长宽，先保留，最后调试时用
        parent.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    public void onGlobalLayout() {

                        parentHeight = parent.getHeight();
                        parentWidth = parent.getWidth();
                        System.out.println("刚调整宽度" + parentWidth);
                        System.out.println("刚调整高度" + parentHeight);
                        System.out.println("加载完成后的gridview高度");


                       parent.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });

        float caculatedHeight = (float)parentHeight * duration / 24;//获取到相对的高度


        float bias;
        int iterNum=position/7;//计算第几个循环
        if(position%7==6) //选取每次循环最后一个元素的高度作为下一次迭代的偏移累加高度
           maxHeight[iterNum] = caculatedHeight;
        if(iterNum==0)
             bias=(float)0.0;
        else
             bias=getBias(iterNum);//设置本次迭代的偏移量


        //获取和父部件顶端的距离，注意首个时间点为5:00
        float topPadding=0;
        if(startHour>=5&&startHour<=23) {
           topPadding = ((float) 60.0 * (startHour-5)+ startMinute) / (24 * 60) * parentHeight -  bias;
        }
        else if(startHour<5) {
            topPadding = ((float) 60.0 * (startHour+19) + startMinute) / (24 * 60) * parentHeight - bias;
        }


        int weekday=getWeekday(curTask.transactionDate_);
        convertView.setTranslationX((weekday- position%7) *parentWidth / 7);
        convertView.setTranslationY(topPadding);
        convertView.setLayoutParams(new GridView.LayoutParams(parentWidth / 7, (int)caculatedHeight));
        convertView.setTag(viewHolder);
        viewHolder.mTextView.setText(description);
        viewHolder.mTextView.setBackgroundColor(Color.parseColor(getRandomColor(position)));
        return convertView;

    }

    //将string转化成date，获取小时和分钟
    public Date convertTime(String dateString) {
        Date timeDate=new Date();
        try {
            DateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH); // HH 大写是24小时制
            timeDate= dateFormat.parse(dateString);   // util类型
            //Timestamp dateTime = new Timestamp(timeDate.getTime()); // Timestamp类型, timeDate.getTime()返回一个 long 型
            //System.out.println(dateTime);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return timeDate;
    }



    //获取字符串中的信息是周几，注意周日为0
    public static int getWeekday(String pTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {

            e.printStackTrace();
        }

        return c.get(Calendar.DAY_OF_WEEK)-1;
    }

    //获取随机颜色，后续可以改善
    public String getRandomColor(int position){

        ArrayList<String> randomColor=new ArrayList<String>();
        randomColor.add("#99CCCC");
        randomColor.add("#FFCC99");
        randomColor.add("#FFCCCC");
        randomColor.add("#99CC33");
        randomColor.add("#FFCC00");
        randomColor.add("#99CCCC");
        randomColor.add("#CC99CC");
        randomColor.add("#FF9933");
        randomColor.add("#FF9999");
        randomColor.add("#3399CC");

        return randomColor.get(position%10);


    }
    /*public int getInitialHeight(final View convertView){
        final int height=0;
        convertView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                 int height = convertView.getHeight();

                convertView.getViewTreeObserver().removeOnPreDrawListener(this);
               // Log.d("Debug","addOnPreDrawListener中获取高度："+height);
                return false;
            }
        });
        System.out.println("viewTree获取的高度"+height);
        return height;
    }*/

    //获取每一次迭代的偏移量
    public float getBias(int iterNum){

       float bias=0;
       for(int i=iterNum-1;i>=0;i--)
           bias+=maxHeight[i];
        return bias;
    }

    private final class ViewHolder
    {
        TextView mTextView;
    }

}
