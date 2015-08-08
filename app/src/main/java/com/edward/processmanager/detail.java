package com.edward.processmanager;

/**
 * Created by Edward on 2014/12/25.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
//import cn.edu.neuq.smarttaskman.*;

public class detail extends Activity{
    //public ActivityManager myActivityManager;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);


        Intent intent = getIntent();

        String processId = intent.getStringExtra("EXTRA_PROCESS_PID");
        String processUid =  intent.getStringExtra("EXTRA_PROCESS_UID");
        String processName = intent.getStringExtra("EXTRA_PROCESS_NAME");
        String processImportance = intent.getStringExtra("EXTRA_PROCESS_IMPORTANCE");
        String processImportanceReasonCode = intent.getStringExtra("EXTRA_PROCESS_IMPORTANCE_REASON_CODE");
        String processImportanceReasonPid = intent.getStringExtra("EXTRA_PROCESS_IMPORTANCE_REASON_PID");
        String processLru = intent.getStringExtra("EXTRA_PROCESS_LRU");
        String[] pkgNameList = intent.getStringArrayExtra("EXTRA_PKGNAMELIST");

        String describe = "";
        switch(Integer.parseInt(processImportance)){
            case 400:
                describe = "IMPORTANCE_BACKGROUND";
                break;
            case 500:
                describe = "IMPORTANCE_EMPTY";
                break;
            case 100:
                describe = "IMPORTANCE_FOREGROUND";
                break;
            case 130:
                describe = "IMPORTANCE_PERCEPTIBLE";
                break;
            case 300:
                describe = "IMPORTANCE_SERVICE";
                break;
            case 200:
                describe = "IMPORTANCE_VISIBLE";
                break;
            default:
                describe = "ERROR";
                break;
        }

        //TextView detail = (TextView)findViewById(R.id.detail);
        TextView tv1 = (TextView)findViewById(R.id.processId);
        tv1.setText(processId);
        TextView tv2 = (TextView)findViewById(R.id.processUid);
        tv2.setText(processUid);
        TextView tv3 = (TextView)findViewById(R.id.processName);
        tv3.setText(processName);
        TextView tv4 = (TextView)findViewById(R.id.processImportance);
        tv4.setText(processImportance + "\n" +describe);
        TextView tv5 = (TextView)findViewById(R.id.processImportanceReasonCode);
        tv5.setText(processImportanceReasonCode);
        TextView tv6 = (TextView)findViewById(R.id.processImportanceReasonPid);
        tv6.setText(processImportanceReasonPid);
        TextView tv7 = (TextView)findViewById(R.id.processLru);
        tv7.setText(processLru);
        TextView tv8 = (TextView)findViewById(R.id.pkgNameList);
        String packageName = "";
        for(String item : pkgNameList)
            packageName += item +"\n";
        tv8.setText(packageName);
    }
}
