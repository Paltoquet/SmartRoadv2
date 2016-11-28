package com.westfolk.smartroadv2;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.loopj.android.http.RequestParams;
import com.westfolk.smartroad.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecordActivity extends ActionBarActivity {

    /*
    We use a timerTask to record the different gps location
    A BlockingQueue is used to communicate with our TimerTask and tell it when to stop
     */
    private Button launch;
    private Button stop;
    private Button send_info;
    private Button proximity;
    private boolean gps_enabel = false;
    private LocationManager locationManager;
    private WsClient client;
    private Timer timer;
    private BlockingQueue<String> queue;
    TimerTask task;
    Context context;

    //Intent Action
    String ACTION_FILTER = "com.westfolk.smartroadv2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        launch = (Button) findViewById(R.id.launchrecord);
        stop = (Button) findViewById(R.id.stoprecord);
        send_info = (Button) findViewById(R.id.send);
        proximity = (Button) findViewById(R.id.proximity);
        client = new WsClient(getApplicationContext());
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        queue = new ArrayBlockingQueue<String>(1024);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gps_enabel = true;
            Log.i("gps","true");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        //quand on appuie sur start
        launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer ==null) {
                    queue.clear();
                    timer = new Timer();
                    //create a RecordTask which will save the longitude and latitude
                    task = new RecordTask(locationManager, getApplicationContext(),queue,"record.txt");
                    //call the run method of the Tash each 30sec
                    timer.schedule(task, 0, 5000);
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kill the timer if it's running
                if(timer!=null){
                    queue.add("stop");
                    timer.cancel();
                    timer.purge();
                    timer = null;
                    task.run();
                }
            }
        });
        send_info.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try {
                    JSONObject obj =read_record("record.txt");
                    client.post("checkpoint",obj,new CheckpointHandler());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        proximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //Add proximity alerte
                Utils utils = new Utils();
                String data = utils.readFile("SmartRoad.json");

                //Setting up My Broadcast Intent
                Intent intent = new Intent("com.westfolk.smartroadv2.ProximityReceiver");
                PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), -1, intent, 0);

                try {
                    JSONObject dataJson = new JSONObject(data);
                    JSONArray array = dataJson.getJSONArray("value");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonobject = array.getJSONObject(i);
                        //System.out.println(jsonobject);

                        double lt = Double.parseDouble(jsonobject.get("lt").toString());
                        double lg = Double.parseDouble(jsonobject.get("lg").toString());
                        //System.out.println(lt + " "+ lg);

                        //(latitude, longitude, radius, expiration, intent); -1 for no expirtaion
                        locationManager.addProximityAlert(lt, lg, 50, -1, pi);
                    }
                    Log.i("RecordActivity", "Ajout de proximity alerte");
                    //locationManager.addProximityAlert(43.58686952, 6.96030132, 10, -1, pi);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Log.i("RecordActivity", "--------- Test ---------");
                //locationManager.addProximityAlert(43.61809495, 7.0661471, 10, -1, pi);

                IntentFilter filter = new IntentFilter("com.westfolk.smartroadv2.ProximityReceiver");
                registerReceiver(new ProximityReceiver(), filter);
            }
        });

    }

    /*
        Build the JSON we will use to send to our server forom the log file
        Could be more efficient without the file but it's more convenient, it keeps a log
   */
    public JSONObject read_record(String file_path) throws IOException, JSONException {
        FileInputStream fileIn= null;
        JSONObject result = new JSONObject();
        JSONArray tab = new JSONArray();
        fileIn = this.openFileInput(file_path);
        BufferedReader InputRead= new BufferedReader(new InputStreamReader(fileIn));
        String line;
        while((line=InputRead.readLine())!=null){
            Log.i("fichier", line);
            JSONObject elem = new JSONObject();
            String parts[] = line.split(":");
            elem.put("lt",parts[1].split(";")[0]);
            elem.put("lg",parts[2]);
            tab.put(elem);
        }
        result.put("value", tab);
        Log.i("JSON", result.toString());
        return result;
    }
}
