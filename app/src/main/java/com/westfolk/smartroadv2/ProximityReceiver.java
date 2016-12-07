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

import com.westfolk.smartroadv2.interfaces.Observable;
import com.westfolk.smartroadv2.interfaces.Observer;


public class ProximityReceiver extends BroadcastReceiver implements Observable {


    private Observer activity;

    @Override
    public void onReceive(Context context, Intent intent) {

        // Key for determining whether user is leaving or entering
        String key = LocationManager.KEY_PROXIMITY_ENTERING;

        //Gives whether the user is entering or leaving in boolean form
        boolean state = intent.getBooleanExtra(key, false);

        if(state){
            // Call the Notification Service or anything else that you would like to do here
            Log.i("ProximityReceiver", "Welcome to my Area");
            Toast.makeText(context, "Current proximity reached !", Toast.LENGTH_SHORT).show();
            notifyChanges();
        }
    }

    @Override
    public void notifyChanges() {
        activity.update();
    }

    @Override
    public void addObserver(Observer obs) {
        activity = obs;
    }
}