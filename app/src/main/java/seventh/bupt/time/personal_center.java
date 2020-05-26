package seventh.bupt.time;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;


//个人中心的界面

public class personal_center extends Fragment {
    Button backup;
    Button recoverData;
    Button importClass;
    DBAdapter dbAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.personal_center,container,false);
        return view;
    }
    @Override
    public void onStart(){
        super.onStart();
        backup = (Button) getView().findViewById(R.id.backup);
        importClass=(Button)getView().findViewById(R.id.importClassTable);
        recoverData = (Button) getView().findViewById(R.id.recoverData);
        dbAdapter = new DBAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBackup();
            }
        });
        recoverData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadBackup();
            }
        });
        importClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importClass();
            }
        });
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void importClass() {
        Intent intent = new Intent(getActivity(), ImportClassActivity.class);
        startActivity(intent);
    }

    public void createBackup() {
        // 判断sd卡状态
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            // 得到一个file对象，路径是sd卡的真实路径
            File file = new File("sdcard/Documents/backup.txt");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                NormalTransaction []transactions = dbAdapter.queryAllData();
                if(transactions!=null)
                    for (int i = 0; i < transactions.length; i++)
                        fos.write(transactions[i].backupData().getBytes());
                // 字符写入流
                fos.close();
                Toast.makeText(getActivity().getApplicationContext(), "备份成功！",Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), "备份失败啦。。。",Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "sd卡不可用哟",Toast.LENGTH_LONG).show();
        }
    }

    public void loadBackup() {
        File file = new File("sdcard/Documents/backup.txt");
        if (file.exists()) {
            try {
                // 使用字符读取流
                FileReader fileReader = new FileReader(file);
                // 为什么要用BufferedReader，因为BufferedReader有readLine()的方法
                BufferedReader br = new BufferedReader(fileReader);
                String description;
                while((description=br.readLine()) != null){
                    description =description.trim();
                    String date = br.readLine().trim();
                    String is_notify = br.readLine().trim();
                    String start_time = br.readLine().trim();
                    String end_time = br.readLine().trim();
                    NormalTransaction transaction = new NormalTransaction(date, is_notify, description, start_time, end_time);
                    dbAdapter.insert(transaction);
                }
                fileReader.close();
                br.close();
                Toast.makeText(getActivity().getApplicationContext(), "恢复成功！",Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), "文件不存在！",Toast.LENGTH_LONG).show();
            }
        }
    }
}
