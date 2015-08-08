package com.edward.processmanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class BlackName extends ActionBarActivity {
    public ArrayList<String> blackName;
    private ListView blackNameList;
    private ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_name);
        blackName = new ArrayList<String>();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        blackName.clear();
        int size = preference.getInt("Status_size",0);
        for(int i=0;i<size;i++){
            blackName.add(preference.getString("Status_"+i,null));
        }
        blackNameList = (ListView)findViewById(R.id.blackNameList);
        arrayAdapter = new ArrayAdapter<String> (BlackName.this, android.R.layout.simple_list_item_1,blackName);
        blackNameList.setAdapter(arrayAdapter);
        blackNameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                new AlertDialog.Builder(BlackName.this).setMessage("是否从黑名单中删除")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                blackName.remove(position);
                                Toast.makeText(getApplicationContext(),"已删除",Toast.LENGTH_SHORT).show();
                                saveArray();
                                blackNameList.setAdapter(arrayAdapter);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_black_name, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
