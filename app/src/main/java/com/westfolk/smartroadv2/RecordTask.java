package com.westfolk.smartroadv2;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;

/**
 * Created by thibault on 25/10/2016.
 */
public class RecordTask extends TimerTask {

    private Context context;
    //private WsClient client;
    private LocationManager locationManager;
    private BlockingQueue<String> queue;
    private String file_path;
    private FileOutputStream fileout;
    private OutputStreamWriter writer;

    public RecordTask(LocationManager lm,Context _context,BlockingQueue<String> _queue,String path){

        /*
            we use the queue to communicate with the UI Thread
            we wrote in a file "path" our different gps measure
         */
        context = _context;
        queue = _queue;
        file_path = path;
        //client = new WsClient();
        locationManager = lm;

        try {
            fileout= context.openFileOutput(file_path, context.MODE_PRIVATE);
            writer = new OutputStreamWriter(fileout);
        }
         catch (Exception e) {
             e.printStackTrace();
             Log.i("Test", "bug file");
             Toast toast = Toast.makeText(context, "can't open file", Toast.LENGTH_LONG);
             toast.show();
         }
    }
    @Override
    public void run() {
        if(queue.isEmpty()) {
            //retrieve the location
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null){
                Log.i("RecordTask","Location null");
                return;
            }
            Log.i("Test", "Latitude " + location.getLatitude() + " et longitude " + location.getLongitude());
            try {
                writer.write("lt:" + location.getLatitude() + ";lg:" + location.getLongitude() +"\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            try {
                String msg = queue.take();
                Log.i("Queue", msg);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try{
                JSONObject obj =read_record();
                //client.post("checkpoint",null,new CheckpointHandler());
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.cancel();
        }
    }

    /*
        Build the JSON we will use to send to our server forom the log file
        Could be more efficient without the file but it's more convenient, it keeps a log
     */
    public JSONObject read_record() throws IOException, JSONException {
        FileInputStream fileIn= null;
        JSONObject result = new JSONObject();
        JSONArray tab = new JSONArray();
        fileIn = context.openFileInput(file_path);
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
        result.put("value",tab);
        Log.i("JSON", result.toString());
        return result;
    }


}
