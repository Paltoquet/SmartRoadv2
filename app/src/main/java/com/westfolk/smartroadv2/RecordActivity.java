package com.westfolk.smartroadv2;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.westfolk.smartroad.R;

public class RecordActivity extends ActionBarActivity {

    /*
    We use a timerTask to record the different gps location
    A BlockingQueue is used to communicate with our TimerTask and tell it when to stop
     */
    private Button launch;
    private Button stop;
    private boolean gps_enabel = false;
    private LocationManager locationManager;
    private Timer timer;
    private BlockingQueue<String> queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        launch = (Button) findViewById(R.id.launchrecord);
        stop = (Button) findViewById(R.id.stoprecord);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        queue = new ArrayBlockingQueue<String>(1024);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gps_enabel = true;
        }
        //quand on appuie sur start
        launch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer ==null) {
                    queue.clear();
                    timer = new Timer();
                    //create a RecordTask which will save the longitude and latitude
                    TimerTask task = new RecordTask(locationManager, getApplicationContext(),queue,"record.txt");
                    //call the run method of the Tash each 2000ms
                    timer.schedule(task, 0, 2000);
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kill the timer if it's running
                if(timer!=null){
                    //timer.cancel();
                    queue.add("stop");
                    timer = null;
                }
            }
        });
    }
}
