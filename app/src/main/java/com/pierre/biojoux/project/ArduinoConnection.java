package com.pierre.biojoux.project;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import GarbageBin.GarbageBin;
import GarbageBin.GarbageHandler;

public class ArduinoConnection extends Service {
    //Strings
    private static final String ERROR = "ERROR";
    private static final String SUCCESS = "SUCCESS";
    private static final String url = "https://api.xively.com/v2/feeds/1819176350";
    private static final String apikey = "aLWKMwlAcdlPK9TrLytC1sKrZxWguK2sl0dwHybMFrFTGhvl";
    //Service
    public static final String BROADCAST_BACKGROUND_SERVICE_RESULT = "com.pierre.biojoux.project.BROADCAST_BACKGROUND_SERVICE_RESULT";
    final GarbageHandler db = new GarbageHandler(this);
    //Variables
    private boolean started = false;
    private long wait = 30 * 1000; // 30 mins in ms
    private final IBinder mBinder = new MyBinder();
    RequestQueue queue = null;

    public ArduinoConnection() { //Constructor
    }

    @Override
    public void onCreate() {
        System.out.println("Service started");
        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!started && intent!=null) {
            started = true;
            fetchData();
        }
        return START_STICKY; // On stop start again
    }

    private void broadcastTaskResult(){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(BROADCAST_BACKGROUND_SERVICE_RESULT);
        broadcastIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        started = false;
        super.onDestroy();
    }

    public class MyBinder extends Binder {
        ArduinoConnection getService(){
            return ArduinoConnection.this;
        }
    }
    /************************          METHODS *******************/
    private void fetchData(){

        AsyncTask<Object, Object, String> task = new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object[] params) {
                try {
                    requestData();
                    Thread.sleep(wait);
                } catch (Exception e) {
                    e.printStackTrace();
                    return ERROR;
                }
                return SUCCESS;
            }


            @Override
            protected void onPostExecute(String stringResult) {
                super.onPostExecute(stringResult);
                broadcastTaskResult();
                if(started){
                    fetchData();
                }
            }
        };
        task.execute();
    }


    void requestData() {
        if (queue == null)
            queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GarbageBin bin = parse(response);
                        db.setBin(bin);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("X-ApiKey", apikey);

                return params;
            }};

        queue.add(stringRequest);

    }

    GarbageBin parse(String jsonResponse){
        String statusTxt = "";
        String[] split = null;
        double lat = 0;
        double lon = 0;
        try {
            JSONObject test = new JSONObject(jsonResponse);
            JSONArray array = test.getJSONArray("datastreams");
            String value = array.getJSONObject(0).getString("current_value");
            System.out.println(value);
            split = value.split(",");
            lat = Double.parseDouble(split[0]);
            lon = Double.parseDouble(split[1]);
            int status = array.getJSONObject(1).getInt("current_value");
            System.out.println(status);

            if (status < 30){
                statusTxt = "Empty";
            }
            else if (status < 60){
                statusTxt = "Medium";
            }
            else {
                statusTxt = "Empty";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new GarbageBin(1, lat, lon, statusTxt, "1.1.1970", "192.168.0.100");

    }
}