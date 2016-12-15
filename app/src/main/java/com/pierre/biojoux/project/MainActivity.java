package com.pierre.biojoux.project;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.content.BroadcastReceiver;

import java.util.ArrayList;

import GarbageBin.GarbageBin;
import GarbageBin.GarbageHandler;

public class MainActivity extends AppCompatActivity {

    private Button buttonCheckStatus;
    private Button buttonViewMap;
    private Button buttonLogOut;
    private ArrayList<GarbageBin> binList = null;
    private GarbageHandler db = new GarbageHandler(this);
    IntentFilter filter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binList = testData();
        filter = new IntentFilter();
        filter.addAction("com.pierre.biojoux.project.BROADCAST_BACKGROUND_SERVICE_RESULT");
        Intent intent = new Intent(this, ArduinoConnection.class);
        startService(intent);
        registerReceiver(receiver, filter);


        buttonCheckStatus = (Button) findViewById(R.id.buttonCheckStatus);
        buttonCheckStatus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkStatus();
            }
        });

        buttonViewMap = (Button) findViewById(R.id.buttonViewMap);
        buttonViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMap();
            }
        });

        buttonLogOut = (Button) findViewById(R.id.buttonLogOut);
        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }

    private void checkStatus(){
        Intent intent = new Intent(this, StatusActivity.class);
        intent.putExtra("binList", binList);
        startActivity(intent);
    }
    private void viewMap(){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("binList", binList);
        intent.putExtra("doIt", "ManyGarbageBins");
        startActivity(intent);
    }


    private void logOut(){
        finish();
    }

    private ArrayList<GarbageBin> testData(){
        //5610.581771,01012.222001
        GarbageBin B1 = new GarbageBin(1, 5610.60000, 01012.2000, "Empty", "2.1.1970", "N/A");
        GarbageBin B2 = new GarbageBin(2, 5610.581771, 01012.222001, "Full", "1.1.1970", "N/A");

        ArrayList<GarbageBin> binList = new ArrayList<GarbageBin>();
        binList.add(B1);
        binList.add(B2);
        System.out.println(binList.get(1).getCoord());
        return binList;
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.pierre.biojoux.project.BROADCAST_BACKGROUND_SERVICE_RESULT")) {
                System.out.println("Broadcast received");
                binList.set(1, db.getBin(1));
            }
        }
    };
}
