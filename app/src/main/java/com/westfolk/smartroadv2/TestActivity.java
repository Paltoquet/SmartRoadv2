package com.westfolk.smartroadv2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.westfolk.smartroad.R;
import com.westfolk.smartroadv2.WsClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by thibault on 24/12/2016.
 */
public class TestActivity extends Activity {


    private Button test_record;
    private Button test_predict;
    private WsClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        client = new WsClient(this);
        test_record = (Button) findViewById(R.id.test_record);
        test_predict = (Button) findViewById(R.id.test_predict);
        test_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject obj = predict();
                    client.post("record",obj,new TimingHandler());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        test_predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject obj = predict();
                    client.post("predict",obj,new TimingHandler());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



        private JSONObject predict() throws JSONException {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");
            JSONObject obj = new JSONObject();
            JSONArray values = new JSONArray();
            JSONObject travel =  new JSONObject();
            JSONObject checkpoint = new JSONObject();
            travel.put("start",dateFormat.format((new Date())));
            travel.put("time",200);
            Checkpoint checkpoint1 = new Checkpoint("42","17",0);
            Checkpoint checkpoint2 = new Checkpoint("42","18",1);
            Date current = new Date();
            Date next = new Date();
            checkpoint1.setDate(new Date());
            next.setTime(current.getTime() + 120000);
            checkpoint2.setDate(current);
            checkpoint.put("lt", checkpoint1.getLatitude());
            checkpoint.put("lg", checkpoint1.getLongitude());
            checkpoint.put("id", checkpoint1.getId());
            checkpoint.put("time", 77);
            values.put(checkpoint);
            checkpoint.put("lt",checkpoint2.getLatitude());
            checkpoint.put("lg", checkpoint2.getLongitude());
            checkpoint.put("id", checkpoint2.getId());
            checkpoint.put("time", 177);
            System.out.println("test putain" +dateFormat.format(checkpoint1.getDate()));
            values.put(checkpoint);
            obj.put("travel",travel);
            obj.put("values",values);
            return obj;
        }
}
