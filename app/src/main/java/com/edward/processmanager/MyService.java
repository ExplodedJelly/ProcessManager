package com.edward.processmanager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    public ArrayList<String> blackName;
    public ActivityManager myActivityManagerService;
    @Override
    public IBinder onBind(Intent arg0){
        return  null;
    }
    @Override
    public void onCreate(){
        super.onCreate();
        blackName = new ArrayList<String>();
        myActivityManagerService =(ActivityManager)getSystemService(Activity.ACTIVITY_SERVICE);
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);//把保存的黑名单信息读取出来。
        blackName.clear();
        int size = preference.getInt("Status_size",0);
        for(int i=0;i<size;i++){
            blackName.add(preference.getString("Status_"+i,null));
        }
        Toast.makeText(this,"Service is Started",Toast.LENGTH_LONG).show();
    }
    @Override
    public int onStartCommand(Intent intent, int flag, int startid){
        Toast.makeText(this, "Service is Running", Toast.LENGTH_LONG).show();
        final Handler myHandler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==0x1233)
                {
                    for(int i=0;i<blackName.size();i++) {
                        myActivityManagerService.killBackgroundProcesses(blackName.get(i));
                    }
                }
            }
        };
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                myHandler.sendEmptyMessage(0x1233);
            }
        },0,1800000);
        return START_STICKY;

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
