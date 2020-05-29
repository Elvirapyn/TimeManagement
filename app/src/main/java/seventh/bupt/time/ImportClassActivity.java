package seventh.bupt.time;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import com.loonggg.weekcalendar.view.WeekCalendar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportClassActivity extends AppCompatActivity {

    private WebView mWebView;
    private Button mButton;
    public DBAdapter dbAdapter;
    private String start_date=null;
    private EditText year;
    private EditText month;
    private EditText day;
    private static Context mContext;
    private int importflag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import);
        dbAdapter = new DBAdapter(ImportClassActivity.this);
        dbAdapter.open();
        mContext = getApplicationContext();
        year=(EditText)findViewById(R.id.year);
        month=(EditText)findViewById(R.id.month);
        day=(EditText)findViewById(R.id.day);
        mButton=(Button)findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importflag=1;
                start_date=year.getText()+"-"+month.getText()+"-"+day.getText();
                mWebView.zoomIn();
            }
        });
        init();
    }

    @SuppressLint({"JavascriptInterface","SetJavaScriptEnabled","AddJavascriptInterface"})
    private void init(){
        mWebView=(WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        // mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.requestFocus();
        mWebView.getSettings().setBuiltInZoomControls(true);

        mWebView.setWebViewClient(new WebViewClient() {

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // 在开始加载网页时会回调
                super.onPageStarted(view, url, favicon);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 拦截 url 跳转,在里边添加点击链接跳转或者操作
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                // 在每一次请求资源时，都会通过这个函数来回调
                return super.shouldInterceptRequest(view, request);
            }
            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                view.loadUrl("javascript:var ifrs=document.getElementsByTagName(\"iframe\");" +
                        "var iframeContent=\"\";" +
                        "for(var i=0;i<ifrs.length;i++){" +
                        "iframeContent=iframeContent+ifrs[i].contentDocument.body.parentElement.outerHTML;" +
                        "}\n" +
                        "var frs=document.getElementsByTagName(\"frame\");" +
                        "var frameContent=\"\";" +
                        "for(var i=0;i<frs.length;i++){" +
                        "frameContent=frameContent+frs[i].contentDocument.body.parentElement.outerHTML;" +
                        "}\n" +
                        "window.local_obj.showSource(document.getElementsByTagName('html')[0].innerHTML + iframeContent + frameContent);");
                super.onScaleChanged(view, oldScale, newScale);
            }
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // 加载错误的时候会回调，在其中可做错误处理，比如再请求加载一次，或者提示404的错误页面
                super.onReceivedError(view, errorCode, description, failingUrl);
                mWebView.reload();
            }
        });
        mWebView.loadUrl("https://vpn.bupt.edu.cn/");
    }

    public final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {

            //解析html
            Document doc = Jsoup.parse(html);
            Element table = doc.getElementById("kbtable");
            if(table==null&&importflag==1){
                importflag=0;
                AlertDialog alertDialog = new AlertDialog.Builder(ImportClassActivity.this)
                        .setTitle("导入课表失败")//标题
                        .setMessage("请打开课表页面后重新导入")//内容
                        .setIcon(R.mipmap.ic_launcher)//图标
                        .create();
                alertDialog.show();
            }
            if(isValidDate(start_date)){//判断输入日期格式合法
                Elements trs = table.select("tr");
                for (int i = 0; i < trs.size() - 1; ++i) {                      //遍历该表格内的所有的<tr> <tr/>
                    Element tr = trs.get(i);                                  // 获取一个tr
                    Elements tds = tr.select("td");                 // 获取该行的所有td节点
                    for (int j = 0; j < tds.size(); ++j) {                   // 选择某一个td节点
                        String info = "周" + (j + 1) + "，第" + (i) + "节：";
                        //Log.i("week", info);
                        //Log.i("table", info);
                        Element td = tds.get(j);
                        // 获取td节点的所有div
                        Elements divs = td.select("div");
                        Elements div = divs.select(".kbcontent");
                        String text = div.get(0).text();
                        //切分
                        String test = text.replace('O', ' ');
                        String[] splits = test.split("\\s+|\t");
                        //for(String ss : splits){
                          //  Log.i("split",ss);
                       // }
                       // Log.i("table",test);
                        //splits=Match(splits);
                        try {
                            Cut(splits,j+1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                AlertDialog alertSuccess = new AlertDialog.Builder(ImportClassActivity.this)
                        .setTitle("导入成功")//标题
                        .setMessage("成功导入课表，请返回日历界面查看吧！")//内容
                        .setIcon(R.mipmap.ic_launcher)//图标
                        .create();
                alertSuccess.show();
            }else{//日期不合法弹窗
                AlertDialog alertDialog1 = new AlertDialog.Builder(ImportClassActivity.this)
                        .setTitle("日期错误")//标题
                        .setMessage("请输入正确的开始日期")//内容
                        .setIcon(R.mipmap.ic_launcher)//图标
                        .create();
                alertDialog1.show();
            }
        }
    }
    public static boolean isValidDate(String str) {
        boolean convertSuccess=true;
        // 指定日期格式为四位年/两位月份/两位日期，注意yyyy/MM/dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
        // 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
            format.setLenient(false);
            format.parse(str);
        } catch (ParseException e) {
            // e.printStackTrace();
// 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess=false;
        }
        return convertSuccess;
    }
    /*返回所有周数+节数的字符串*/
    private String[] Match(String[] splits){
        String[] strs=new String[10];
        int k=0;
        Pattern pattern = Pattern.compile(".*\\(周\\)\\[.*节\\]");
        for(int i=0;i<splits.length;i++){
            Matcher matcher = pattern.matcher(splits[i]);
            int index = 0;
            while (matcher.find()) {
                String res = matcher.group();
                strs[k]=res;
                k++;
            }
        }
        strs[k]="\0";
        return strs;
    }
    private void Cut (String[] splits,int weekday) throws ParseException {
        String week = null;
        int start_week;
        int end_week;
        String desc="";
        /*for(String s:splits){
            desc+=s;
        }*/
        if(splits.length>0)
            desc+=splits[0]+"\n"+splits[splits.length-1];

        String[] matched=Match(splits);



        for (int i = 0; i < matched.length && matched[i] != "\0"; i++) {//遍历取出的时间信息
            //Log.i("week", "splist:______________________");
            //获取信息所在段
            String str = matched[i];
            //Log.i("week", "str:" + str);
            //截取周信息
            int min_bracket_left = str.indexOf("(");
            week = str.substring(0, min_bracket_left);
            //Log.i("week", week);
            int strip = week.indexOf("-");
            if (strip > 0) {//12-18（周）
                start_week = Integer.parseInt(week.substring(0, strip));
                end_week = Integer.parseInt(week.substring(strip + 1));
            } else {//12(周)
                start_week = Integer.parseInt(week);
                end_week = start_week;
            }
           // Log.i("week", "start week:" + start_week);
            //Log.i("week", "end week:" + end_week);
            //将周转换成日期
            //String test_date = "2020-02-24";
            //String date = weekToDate(start_week, start_date, 2);
            //Log.i("week", date);
            //截取节次信息
            int sq_bracket_left;
            int sq_bracket_right;
            int start_class;
            int end_class = 0;
            int[] class_num = new int[10];
            sq_bracket_left = str.indexOf("[");
            sq_bracket_right = str.indexOf("节");
            String time = str.substring(sq_bracket_left + 1, sq_bracket_right);
            strip = time.indexOf("-");
            start_class = Integer.parseInt(time.substring(0, strip));
            while (strip > 0) {
                time = time.substring(strip + 1);
                strip = time.indexOf("-");
            }
            end_class = Integer.parseInt(time);
            String[] start = {"08:00", "08:50", "09:50", "10:40", "11:30", "13:00", "13:50", "14:45", "15:40", "16:35", "17:25", "18:30", "19:20", "20:10"};
            String[] end = {"08:45", "09:35", "10:35", "11:25", "12:15", "13:45", "14:35", "15:30", "16:25", "17:20", "18:10", "19:15", "20:05", "20:55"};
            String startTime = start[start_class - 1];
            String endTime = end[end_class - 1];
            //Log.i("week", "start class:" + start_class);
            //Log.i("week", "end class:" + end_class);
           // Log.i("week", "start time:" + startTime);
            //Log.i("week", "end timme:" + endTime);
            for(int n=start_week;n<=end_week;n++){
                String trans_date=weekToDate(n,start_date,weekday);
                NormalTransaction transaction=new NormalTransaction(trans_date,"Y",desc,startTime,endTime);
                if(isConflict(trans_date,startTime,endTime)==false)
                    dbAdapter.insert(transaction);
            }
            //使得课表更新后立刻在界面上更新,需要测试
           //在一个activity获取另一个activity的控件
           /* LayoutInflater factory=LayoutInflater.from(ImportClassActivity.this);
            View layout=factory.inflate(R.layout.calendar,null);
            WeekCalendar weekCalendar=layout.findViewById(R.id.week_calendar);
            View layout2=factory.inflate(R.layout.view_calender,null);
            GridView gridView=layout2.findViewById(com.loonggg.weekcalendar.R.id.gridview);
            String dayofSelected=weekCalendar.getTheDayOfSelected();
            String[] weekDate=CalendarView.getWeekofDate(dayofSelected);
            NormalTransaction[] new_tasks=dbAdapter.queryWeekData(weekDate);
            ArrayList<HashMap<String, Object>> arrayList= CalendarView.getArrayList(new_tasks);
            GridAdapter gridAdapter = new GridAdapter(this, arrayList, LayoutInflater.from(this));
            gridView.setAdapter(gridAdapter);*/

        }
    }





    /*得到某年某周的第一天*/
    private Calendar getFirstDayOfWeek(String year, int week) {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.YEAR, Integer.valueOf(year));
        c.set (Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.DATE, 1);
        Calendar cal = (GregorianCalendar) c.clone();
        cal.add(Calendar.DATE, (week-1) * 7);
        Calendar day=getFirstDayOfWeek(cal.getTime());
        return day;
    }
    /*取得指定日期所在周的第一天*/
    public Calendar getFirstDayOfWeek(Date date) {
        java.text.SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        return c;
    }
    /* 给出教学周周数，学期开始日期，周几，返回日期*/
    private String weekToDate(int week,String start_date,int day){
        // String start_date = "2020-02-24";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = format.parse(start_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        int start_num=calendar.get(Calendar.WEEK_OF_YEAR);
       // Log.i("parse", String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
        //System.out.println(calendar.get(Calendar.WEEK_OF_YEAR));
        Calendar monsday=getFirstDayOfWeek("2020",start_num+week-1);
        monsday.add(Calendar.DAY_OF_YEAR,day-1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String result = sdf.format(monsday.getTime());
        return result;
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


}