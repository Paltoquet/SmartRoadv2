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
import android.widget.Toast;

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
    private Button sendButton;
    private LocationManager locationManager;
    private ProximityReceiver receiver;
    private boolean gps_enabel = false;
    private FileOutputStream fileout;
    private OutputStreamWriter writer;
    private Context context;
    private String file_path = "current_log.txt";
    private JSONObject res;

    private ArrayList<Checkpoint> checkpoints;
    private int current_checkpoint = 0;
    private int number_of_checkpoint = 0;

    private Checkpoint checkpoint;
    private WsClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximity);

        proximity = (Button) findViewById(R.id.proximity);
        sendButton = (Button) findViewById(R.id.send);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        receiver = new ProximityReceiver();
        receiver.addObserver(this);
        checkpoints = new ArrayList<Checkpoint>();
        context = this.getApplicationContext();
        client = new WsClient(getApplicationContext());

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
                public void onLocationChanged(Location location) {}
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
                @Override
                public void onProviderEnabled(String provider) {}
                @Override
                public void onProviderDisabled(String provider) {}
            });
        }

        proximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Launching promimity...", Toast.LENGTH_SHORT).show();

                //Add proximity alerte
                Utils utils = new Utils();
                String data = utils.readFile("SmartRoad.json");

                //Setting up My Broadcast Intent
                try {
                    JSONObject dataJson = new JSONObject(data);
                    JSONArray array = dataJson.getJSONArray("value");
                    int i;
                    for (i = 0; i < array.length(); i++) {
                        JSONObject jsonobject = array.getJSONObject(i);
                        String lt = jsonobject.get("lt").toString();
                        String lg = jsonobject.get("lg").toString();
                        checkpoints.add(new Checkpoint(lt,lg,i));
                    }
                    number_of_checkpoint = i;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(!checkpoints.isEmpty()){
                    //specify the name of your custom intent action in the manisfest like  BOUGE TON CUL PD
                    Intent intent = new Intent("com.westfolk.smartroadv2.BOUGE_TON_CUL");
                    PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), current_checkpoint, intent, 0);

                    checkpoint = checkpoints.get(current_checkpoint);
                    /* Static test 5000 a modifier */
                    locationManager.addProximityAlert(Double.parseDouble(checkpoint.getLatitude()), Double.parseDouble(checkpoint.getLongitude()), 5000, -1, pi);
                    //set our receiver to check incoming BOUGE_TON_CUL action
                    IntentFilter filter = new IntentFilter("com.westfolk.smartroadv2.BOUGE_TON_CUL");
                    registerReceiver(receiver,filter);

                    Toast.makeText(context, "Proximity 1" +"/"+ (number_of_checkpoint) + " ready...", Toast.LENGTH_SHORT).show();
                }
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Send to the server
                client.post("timing", res, new TimingHandler());
            }
        });
    }

    @Override
    public void update() {
        Log.i("ProximityAlert","Passage au prochain checkpoint");
        checkpoints.get(current_checkpoint).setDate(new Date());

        //removing current proximity alert
        Intent intent = new Intent("com.westfolk.smartroadv2.BOUGE_TON_CUL");
        PendingIntent remove = PendingIntent.getBroadcast(getApplicationContext(), current_checkpoint , intent, 0);
        locationManager.removeProximityAlert(remove);
        current_checkpoint++;

        //if we are not at the end
        if(current_checkpoint < number_of_checkpoint){
            checkpoint = checkpoints.get(current_checkpoint);
            //creating new proximity alert
            Intent it = new Intent("com.westfolk.smartroadv2.BOUGE_TON_CUL");
            PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), current_checkpoint, it, 0);

            /*  Static test 5000 a modifier */
            locationManager.addProximityAlert(Double.parseDouble(checkpoint.getLatitude()), Double.parseDouble(checkpoint.getLongitude()), 5000, -1, pi);
            Toast.makeText(context, "Proximity "+ (current_checkpoint + 1) +"/"+ (number_of_checkpoint) +" ready...", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(context, "You arrived !", Toast.LENGTH_SHORT).show();
            Log.i("ProximityAlert","Fin du parcours");
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            res = new JSONObject();
            JSONArray values = new JSONArray();
            JSONObject checkpoint;
            //write the result to file
            for(Checkpoint i : checkpoints){
                checkpoint = new JSONObject();
                try {
                    checkpoint.put("lt",i.getLatitude());
                    checkpoint.put("lg",i.getLongitude());
                    checkpoint.put("id",i.getId());
                    checkpoint.put("date",dateFormat.format(i.getDate()));
                    values.put(checkpoint);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            try {
                res.put("value",values);
                Log.i("ProximityAlert", String.valueOf(res));
                writer.write(res.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
