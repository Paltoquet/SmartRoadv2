package com.westfolk.smartroadv2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.westfolk.smartroad.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lucas on 05/01/2017.
 */

public class LeaveNowActivity extends Activity {

    private WsClient client;
    private Context context;

    private Button leaveButton;
    private TextView leaveTimeText;
    private TextView leaveHourText;

    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        context = this.getApplicationContext();
        leaveButton = (Button) findViewById(R.id.leaveButton);
        leaveTimeText = (TextView) findViewById(R.id.leaveTimeText);
        leaveHourText = (TextView) findViewById(R.id.leaveHourText);

        client = new WsClient(this);

        jsonObject = new JSONObject();
        JSONObject coord = new JSONObject();
        JSONObject travelInfo = new JSONObject();
        JSONArray values = new JSONArray();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss");

        try {
            travelInfo.put("start", dateFormat.format(new Date()));
            travelInfo.put("time", 0);

            jsonObject.put("travel",travelInfo);

            coord.put("lt", 0);
            coord.put("lg", 0);
            coord.put("id", 0);
            coord.put("time", 0);
            values.put(coord);

            jsonObject.put("values", values);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        client.post("predict", jsonObject, new LeaveHandler(leaveTimeText, leaveHourText));


        leaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                client.post("predict", jsonObject, new LeaveHandler(leaveTimeText, leaveHourText));
            }
        });

    }
}
