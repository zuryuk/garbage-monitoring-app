package service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import GarbageBin.GarbageBin;
import GarbageBin.GarbageHandler;

public class ArduinoConnection extends Service {
    //Strings
    private static final String ERROR = "ERROR";
    private static final String SUCCESS = "SUCCESS";
    private String url = "";
    //Service
    public static final String BROADCAST_BACKGROUND_SERVICE_RESULT = "com.pierre.biojoux.project.BROADCAST_BACKGROUND_SERVICE_RESULT";
    final GarbageHandler db = new GarbageHandler(this);
    //Variables
    RequestQueue queue;
    private boolean started = false;
    private long wait = 30 * 1000; // 30 mins in ms
    private final IBinder mBinder = new MyBinder();

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

        url ="http://home.tamk.fi/~e4jluukk/datatest/Sample2.html";
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                        public void onResponse(String response) {
                        parse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error);
            }
        });
        queue.add(stringRequest);
    }

    GarbageBin parse(String text){
        String[] split = text.split(";");
        for(int i = 0; i < split.length; i++) {
            System.out.println("value " + i + ": " + split[i]);
        }
        int sensor = Integer.parseInt(split[0]);
        double lat = Double.parseDouble(split[1]);
        double lon = Double.parseDouble(split[2]);
        String status;
        String emptied;

        emptied = SimpleDateFormat.getDateInstance().toString();
        if (sensor < 30) { status = "Full"; }
        else if (sensor < 80 ){ status = "Medium"; }
        else { status = "Empty";}
        GarbageBin bin =  new GarbageBin(sensor, lat, lon, status, emptied, url);
        bin.test();
        db.setBin(bin);
        db.getBin(1).test();
        db.close();
        return bin;
    }
}