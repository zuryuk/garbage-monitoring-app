package com.pierre.biojoux.project;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import GarbageBin.GarbageBin;
import GarbageBin.GarbageHandler;

public class MainActivity extends AppCompatActivity {

    private Button buttonCheckStatus;
    private Button buttonViewMap;
    private Button buttonLogOut;
    private ArrayList<GarbageBin> binList = null;
    private GarbageHandler db = new GarbageHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binList = testData();
        db.getBin(1).test();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.pierre.biojoux.project.BROADCAST_BACKGROUND_SERVICE_RESULT");
        Intent intent = new Intent(this, ArduinoConnection.class);
        startService(intent);


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
        GarbageBin B1 = new GarbageBin(1, 3741.414, 1223.14121, "Full", "1.12.2016", "192.168.0.100");
        GarbageBin B2 = new GarbageBin(2, 5610.581771, 01012.222001, "Empty", "30.11.2016", "192.168.0.101");

        ArrayList<GarbageBin> binList = new ArrayList<GarbageBin>();
        binList.add(B1);
        binList.add(B2);
        return binList;
    }
}
