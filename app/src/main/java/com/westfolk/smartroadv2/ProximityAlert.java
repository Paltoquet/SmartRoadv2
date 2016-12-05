package com.westfolk.smartroadv2;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.westfolk.smartroad.R;
import com.westfolk.smartroadv2.interfaces.Observer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Lucas on 28/11/2016.
 */

public class ProximityAlert extends Activity implements Observer {

    private Button proximity;
    private LocationManager locationManager;
    private ProximityReceiver receiver;
    private boolean gps_enabel = false;
    private FileOutputStream fileout;
    private OutputStreamWriter writer;
    private String file_path = "current_log.txt";

    private ArrayList<Checkpoint> checkpoints;
    private int current_checkpoint = 0;
    private int number_of_checkpoint = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity);

        proximity = (Button) findViewById(R.id.proximity);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        receiver = new ProximityReceiver();
        receiver.addObserver(this);
        checkpoints = new ArrayList<Checkpoint>();

        try {
            fileout= openFileOutput(file_path, MODE_PRIVATE);
            writer = new OutputStreamWriter(fileout);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

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

        //TODO : unregisterReceiver()
        proximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add proximity alerte
                Utils utils = new Utils();
                String data = utils.readFile("SmartRoad.json");

                //Setting up My Broadcast Intent
                //Intent intent = new Intent("com.westfolk.smartroadv2.ProximityReceiver");
                try {
                    JSONObject dataJson = new JSONObject(data);
                    JSONArray array = dataJson.getJSONArray("value");
                    int i;
                    for (i = 0; i < array.length(); i++) {
                        JSONObject jsonobject = array.getJSONObject(i);
                        /*
                        double lt = Double.parseDouble(jsonobject.get("lt").toString());
                        double lg = Double.parseDouble(jsonobject.get("lg").toString());
                        */
                        String lt = jsonobject.get("lt").toString();
                        String lg = jsonobject.get("lg").toString();
                        checkpoints.add(new Checkpoint(lt,lg,i));
                        //System.out.println(lt + " "+ lg);

                        //(latitude, longitude, radius, expiration, intent); -1 for no expirtaion
                        //locationManager.addProximityAlert(lt, lg, 50, -1, pi);
                    }
                number_of_checkpoint = i;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(!checkpoints.isEmpty()){
                    Log.i("RecordActivity", "--------- Test ---------");
                    //specify the name of your custom intent action in the manisfest like  BOUGE TON CUL PD
                    Intent intent = new Intent("com.westfolk.smartroadv2.BOUGE_TON_CUL");
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), current_checkpoint, intent, 0);
                    locationManager.addProximityAlert(43.61809495, 7.0661471, 50, -1, pi);
                    //set our receiver to check incoming BOUGE_TON_CUL action
                    IntentFilter filter = new IntentFilter("com.westfolk.smartroadv2.BOUGE_TON_CUL");
                    registerReceiver(receiver,filter);
                }
            }
        });

    }

    @Override
    public void update() {
        Log.i("notified","passage au prochain checkpoint");
        checkpoints.get(current_checkpoint).setDate(new Date());

        //removing current proximity alert
        Intent intent = new Intent("com.westfolk.smartroadv2.BOUGE_TON_CUL");
        PendingIntent remove = PendingIntent.getBroadcast(getApplicationContext(), current_checkpoint , intent, 0);
        locationManager.removeProximityAlert(remove);
        current_checkpoint++;

        //if we are not at the end
        if(current_checkpoint <= number_of_checkpoint){
            Checkpoint checkpoint = checkpoints.get(current_checkpoint);
            //creating new proximity alert
            Intent it = new Intent("com.westfolk.smartroadv2.BOUGE_TON_CUL");
            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), current_checkpoint, it, 0);
            locationManager.addProximityAlert(43.61809495, 7.0661471, 50, -1, pi);
        }
        else{
            Log.i("the end","pd");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            JSONObject res = new JSONObject();
            JSONArray values = new JSONArray();
            JSONObject checkpoint;
            //write the result to file
            for(Checkpoint i : checkpoints){
                checkpoint = new JSONObject();
                try {
                    checkpoint.put("lat",i.getLatitude());
                    checkpoint.put("long",i.getLongitude());
                    checkpoint.put("id",i.getId());
                    checkpoint.put("date",dateFormat.format(i.getDate()));
                    values.put(checkpoint);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                res.put("values",values);
                writer.write(res.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
