package com.edward.processmanager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Debug;
import android.preference.PreferenceManager;
import android.text.format.Formatter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final int EXIT = 0x113;
    private static final int TASK = 0x117;
    private TextView proNum;

    private TextView leftMem;
    private ListView proList;
    private String leftMemSize;
    private ArrayList<String> arrayListPro;
    public ArrayList<String> blackName;
    private ArrayAdapter<String> arrayAdapter;
    public ActivityManager myActivityManager;
    public List<ActivityManager.RunningAppProcessInfo> mRunningPros;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        proNum = (TextView)findViewById(R.id.proNum);
        leftMem = (TextView)findViewById(R.id.leftMem);
        proList = (ListView)findViewById(R.id.proList);
        myActivityManager =(ActivityManager)getSystemService(Activity.ACTIVITY_SERVICE);
        upDateMemInfo();
        Button refresh = (Button)findViewById(R.id.refresh);
        Button kill_all = (Button)findViewById(R.id.kill_all);
        Button testService =(Button)findViewById(R.id.testService);
        Button blackNameBn = (Button)findViewById(R.id.blackNameBn);
        blackName = new ArrayList<String>();
        loadArray(this);
        getRunningProcessInfo();
        getRunningProcessInfo();
        final Intent intent = new Intent(this,MyService.class);
        final Intent intent1 = new Intent(this,BlackName.class);
        proList.setOnItemClickListener(new OnItemClickListener(){
            public void onItemClick(AdapterView<?> arg0, View arg1,  final int position, long arg3) {
                new AlertDialog.Builder(MainActivity.this).setMessage("是否杀死该进程")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String processName = mRunningPros.get(position).processName;
                                myActivityManager.killBackgroundProcesses(processName);
                                getRunningProcessInfo() ;
                                upDateMemInfo();
                                makeToastSimple("已杀死进程"	+ processName, true);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel() ;
                    }
                }).create().show() ;

            }
        });

        proList.setOnItemLongClickListener(new ListView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, final View view,
                                           final int position, long arg3) {
                final String processName = mRunningPros.get(position).processName;
                android.content.DialogInterface.OnClickListener listener1 = new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            myActivityManager.killBackgroundProcesses(processName);
                            getRunningProcessInfo() ;
                            upDateMemInfo();
                            makeToastSimple("已杀死进程"	+ processName, true);
                        }else if (which==1){
                            check_detail(position);
                        }
                        else if(which==2){
                            blackName.add(processName);
                            saveArray();
                            Toast.makeText(getApplicationContext(),blackName.get(blackName.size()-1)+"已加入黑名单",Toast.LENGTH_SHORT).show();
                        }
                    }
                };
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("操作")
                        .setItems(R.array.operation,listener1)
                        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel() ;
                            }
                        }).show();
                return true;
            }
        });

        kill_all.setOnClickListener(new OnClickListener()
        {
            public void onClick(View source)
            {
                for (ActivityManager.RunningAppProcessInfo amPro : mRunningPros){
                    String processName = amPro.processName;
                    myActivityManager.killBackgroundProcesses(processName);
                }
                getRunningProcessInfo();
                upDateMemInfo();
                makeToastSimple("一键清理结束，当前可用内存为" + leftMemSize, true);
            }
        });
        refresh.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View source)
            {
                getRunningProcessInfo();
                makeToast("已刷新");
                upDateMemInfo();
            }
        });
        testService.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(intent);
            }
        });
        blackNameBn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent1);
            }
        });

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
         saveArray();
    }


    public void getRunningProcessInfo(){
        myActivityManager = (ActivityManager)MainActivity.this.getSystemService(ACTIVITY_SERVICE);
        proNum.setText(myActivityManager.getRunningAppProcesses().size()+"");
        arrayListPro = new ArrayList<String>();
        mRunningPros = myActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo amPro : mRunningPros){

            int[] myMempid = new int[] {amPro.pid};
            Debug.MemoryInfo[] memoryInfo = myActivityManager.getProcessMemoryInfo(myMempid);
            double memSize = memoryInfo[0].dalvikPrivateDirty/1024.0;
            int temp = (int)(memSize*100);
            memSize = temp/100.0;
            String suggestion = "";
            if(amPro.importance==400||amPro.importance==500){
                suggestion = "建议添加到黑名单";
            }else {
                suggestion = "不建议添加到黑名单";
            }


            String ProInfo="";
            ProInfo +="Name:"+ amPro.processName
                    + "\nID:" + amPro.pid
                    + "\nMemory:" + memSize + "MB"
                    + "\n建议:" + suggestion;
            arrayListPro.add(ProInfo);
        }

        arrayAdapter = new ArrayAdapter<String> (MainActivity.this, android.R.layout.simple_list_item_1, arrayListPro);
        proList.setAdapter(arrayAdapter);

    }

    public void upDateMemInfo(){
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        myActivityManager.getMemoryInfo(memoryInfo) ;
        long memSize = memoryInfo.availMem ;
        leftMemSize = Formatter.formatFileSize(getBaseContext(), memSize);
        leftMem.setText(leftMemSize);
    }

    public void makeToast(String str){
        Toast toast = Toast.makeText(MainActivity.this,str, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        ImageView image = new ImageView(MainActivity.this);
        image.setImageResource(R.drawable.ic_launcher);
        LinearLayout ll = new LinearLayout(MainActivity.this);
        ll.addView(image);
        ll.addView(toastView);
        toast.setView(ll);
        toast.show();
    }

    public void makeToastSimple(String str,boolean sel){
        Toast toast = Toast.makeText(MainActivity.this,str,
                sel?Toast.LENGTH_SHORT:Toast.LENGTH_LONG);
        toast.show();
    }

    public void check_detail(int position){
        ActivityManager.RunningAppProcessInfo processInfo =  mRunningPros.get(position);
        Intent intent = new Intent(this, detail.class);
        intent.putExtra("EXTRA_PROCESS_PID", processInfo.pid + "");
        intent.putExtra("EXTRA_PROCESS_UID", processInfo.uid + "");
        intent.putExtra("EXTRA_PROCESS_NAME", processInfo.processName);
        intent.putExtra("EXTRA_PROCESS_IMPORTANCE", processInfo.importance + "");
        intent.putExtra("EXTRA_PROCESS_IMPORTANCE_REASON_CODE", processInfo.importanceReasonCode + "");
        intent.putExtra("EXTRA_PROCESS_IMPORTANCE_REASON_PID", processInfo.importanceReasonPid + "");
        intent.putExtra("EXTRA_PROCESS_LRU", processInfo.lru + "");
        intent.putExtra("EXTRA_PKGNAMELIST", processInfo.pkgList) ;
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, TASK, 0, "查看正在运行的任务");
        menu.add(0, EXIT, 0, "退出");
        return super.onCreateOptionsMenu(menu);
    }

    public void show_tasks(){
        Intent intent = new Intent(this, getTask.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi){
        switch (mi.getItemId()){
            case TASK:
                show_tasks();
                break;
            case EXIT:
                finish();
                break;
        }
        return true;
    }
    public boolean saveArray() {
        SharedPreferences preferences =PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEdit =preferences.edit();
        mEdit.putInt("Status_size",blackName.size());
        for(int i=0;i<blackName.size();i++){
            mEdit.remove("Status_"+i);
            mEdit.putString("Status_"+i,blackName.get(i));
        }
        return mEdit.commit();
    }
    public void loadArray(Context context){
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        blackName.clear();
        int size = preference.getInt("Status_size",0);
        for(int i=0;i<size;i++){
            blackName.add(preference.getString("Status_"+i,null));
        }
    }
    /*private void StopProcess(String processname){
        Process sh = null;
        DataOutputStream os = null;
        try{
            sh = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(sh.getOutputStream());
            final String Command = "am force-stop "+processname + "\n";
            os.writeBytes(Command);
            os.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
        try{
            sh.waitFor();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }*/

}