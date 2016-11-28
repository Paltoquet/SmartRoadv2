package com.westfolk.smartroadv2;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.westfolk.smartroad.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Lucas on 28/11/2016.
 */

public class ProximityAlert extends ActionBarActivity {

    private Button proximity;
    private LocationManager locationManager;
    private boolean gps_enabel = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity);

        proximity = (Button) findViewById(R.id.proximity);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


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

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.i("RecordActivity", "--------- Test ---------");
                locationManager.addProximityAlert(43.61809495, 7.0661471, 10, -1, pi);

                IntentFilter filter = new IntentFilter("com.westfolk.smartroadv2.ProximityReceiver");
                registerReceiver(new ProximityReceiver(), filter);
            }
        });

    }

}
