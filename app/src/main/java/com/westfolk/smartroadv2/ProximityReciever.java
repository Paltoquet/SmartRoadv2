package com.westfolk.smartroadv2;

/**
 * Created by Lucas on 27/11/2016.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;


public class ProximityReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // Key for determining whether user is leaving or entering
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        //Gives whether the user is entering or leaving in boolean form
        boolean state = intent.getBooleanExtra(key, false);

        Log.i("ProximityReciever", "Draykoon le nuub");

        if(state){
            // Call the Notification Service or anything else that you would like to do here
            Log.i("ProximityReciever", "Welcome to my Area");
            Toast.makeText(context, "Welcome to my Area", Toast.LENGTH_SHORT).show();
        }else{
            //Other custom Notification
            Log.i("MyTProximityRecieverag", "Thank you for visiting my Area,come back again !!");
            Toast.makeText(context, "Thank you for visiting my Area,come back again !!", Toast.LENGTH_SHORT).show();
        }
    }
}