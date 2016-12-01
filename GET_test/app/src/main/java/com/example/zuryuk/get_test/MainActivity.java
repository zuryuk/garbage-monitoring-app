package com.example.zuryuk.get_test;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.zuryuk.get_test.GarbageBin.GarbageBin;
import com.example.zuryuk.get_test.GarbageBin.GarbageHandler;

public class MainActivity extends AppCompatActivity {
    final GarbageHandler db = new GarbageHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.zuryuk.get_test.BROADCAST_BACKGROUND_SERVICE_RESULT");
        registerReceiver(receiver, filter);

        GarbageBin bin2 = new GarbageBin(1, 2, 3, "dsa", "dsa", "dsa");
        db.setBin(bin2);
        Intent intent = new Intent(this, ArduinoConnection.class);
        startService(intent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
    @Override
    public void onPause()
    {
        super.onPause();
        unregisterReceiver(receiver);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.zuryuk.get_test.BROADCAST_BACKGROUND_SERVICE_RESULT");
        registerReceiver(receiver, filter);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.example.zuryuk.get_test.BROADCAST_BACKGROUND_SERVICE_RESULT")) {
                System.out.println("Broadcast received");
            }
        }
    };
}

