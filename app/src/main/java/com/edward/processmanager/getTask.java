package com.edward.processmanager;

/**
 * Created by Edward on 2014/12/25.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class getTask extends Activity
{
    private Button refresh;
    private ListView proList;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> arrayListPro;
    private ActivityManager myActivityManager;
    private final int maxNum = 50;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        refresh = (Button)findViewById(R.id.Refresh);
        proList = (ListView)findViewById(R.layout.list);

        refresh.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getRunningTaskInfo();
                Toast toast = Toast.makeText(getTask.this,"已刷新", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    public void getRunningTaskInfo(){

        myActivityManager = (ActivityManager)getTask.this.getSystemService(ACTIVITY_SERVICE);

        arrayListPro = new ArrayList<String>();
        List<ActivityManager.RunningTaskInfo> mRunningTasks = myActivityManager.getRunningTasks(maxNum);

        for (ActivityManager.RunningTaskInfo amTask : mRunningTasks)
            arrayListPro.add(amTask.baseActivity.getClassName()+ "(ID=" + amTask.id +")");
        arrayAdapter = new ArrayAdapter<String> (getTask.this, android.R.layout.simple_list_item_1, arrayListPro);

        proList.setAdapter(arrayAdapter);
    }
}