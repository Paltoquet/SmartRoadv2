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
    private Button test1;
    private Button test2;
    private TextView leaveTimeText;
    private TextView leaveHourText;

    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);

        context = this.getApplicationContext();
        leaveButton = (Button) findViewById(R.id.leaveButton);
        test1 = (Button) findViewById(R.id.test1);
        test2 = (Button) findViewById(R.id.Test2);
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


        // ================ TEST  ================
        // ******* FAST *******
        //2 checkpoints (19 au total en vrai)
        String fastString = "{\"travel\":{\"start\":\"2017\\/01\\/29-17:15:24\",\"time\":0},\"values\":[" +
                "{\"lt\":\"43.61702166666667\",\"lg\":\"7.070624999999999\",\"id\":0,\"time\":1}," +
                "{\"lt\":\"43.61619\",\"lg\":\"7.065709999999999\",\"id\":1,\"time\":1}]}";

        //4 checkpoints (19 au total en vrai)
        String fastString2 = "{\"travel\":{\"start\":\"2017\\/01\\/29-17:15:24\",\"time\":0},\"values\":[" +
                "{\"lt\":\"43.61702166666667\",\"lg\":\"7.070624999999999\",\"id\":0,\"time\":1}," +
                "{\"lt\":\"43.61619\",\"lg\":\"7.065709999999999\",\"id\":1,\"time\":1}," +
                "{\"lt\":\"43.61727333333333\",\"lg\":\"7.059446666666665\",\"id\":2,\"time\":1}]}" +
                "{\"lt\":\"43.613479\",\"lg\":\"7.045980\",\"id\":3,\"time\":1}]}";

        //10 checkpoints (19 au total en vrai)
        String fastString3 = "{\"travel\":{\"start\":\"2017\\/01\\/29-17:15:24\",\"time\":0},\"values\":[" +
                "{\"lt\":\"43.61702166666667\",\"lg\":\"7.070624999999999\",\"id\":0,\"time\":1}," +
                "{\"lt\":\"43.61619\",\"lg\":\"7.065709999999999\",\"id\":1,\"time\":1}," +
                "{\"lt\":\"43.61727333333333\",\"lg\":\"7.059446666666665\",\"id\":2,\"time\":1}," +
                "{\"lt\":\"43.617606666666674\",\"lg\":\"7.050965000000001\",\"id\":3,\"time\":1}," +
                "{\"lt\":\"43.613478333333326\",\"lg\":\"7.045979999999999\",\"id\":4,\"time\":1}," +
                "{\"lt\":\"43.616458333333334\",\"lg\":\"7.037391666666667\",\"id\":5,\"time\":1}," +
                "{\"lt\":\"43.61281333333333\",\"lg\":\"7.027713333333333\",\"id\":6,\"time\":1}," +
                "{\"lt\":\"43.61083833333334\",\"lg\":\"7.02081\",\"id\":7,\"time\":1}," +
                "{\"lt\":\"43.608354999999996\",\"lg\":\"7.0096733333333345\",\"id\":8,\"time\":1}," +
                "{\"lt\":\"43.60330333333333\",\"lg\":\"7.011711666666668\",\"id\":9,\"time\":1}]}";

        //18 checkpoints (19 au total en vrai)
        String fastString4 = "{\"travel\":{\"start\":\"2017\\/01\\/29-17:15:24\",\"time\":0},\"values\":[" +
                "{\"lt\":\"43.61702166666667\",\"lg\":\"7.070624999999999\",\"id\":0,\"time\":1}," +
                "{\"lt\":\"43.61619\",\"lg\":\"7.065709999999999\",\"id\":1,\"time\":1}," +
                "{\"lt\":\"43.61727333333333\",\"lg\":\"7.059446666666665\",\"id\":2,\"time\":1}," +
                "{\"lt\":\"43.617606666666674\",\"lg\":\"7.050965000000001\",\"id\":3,\"time\":1}," +
                "{\"lt\":\"43.613478333333326\",\"lg\":\"7.045979999999999\",\"id\":4,\"time\":1}," +
                "{\"lt\":\"43.616458333333334\",\"lg\":\"7.037391666666667\",\"id\":5,\"time\":1}," +
                "{\"lt\":\"43.61281333333333\",\"lg\":\"7.027713333333333\",\"id\":6,\"time\":1}," +
                "{\"lt\":\"43.61083833333334\",\"lg\":\"7.02081\",\"id\":7,\"time\":1}," +
                "{\"lt\":\"43.608354999999996\",\"lg\":\"7.0096733333333345\",\"id\":8,\"time\":1}," +
                "{\"lt\":\"43.60330333333333\",\"lg\":\"7.011711666666668\",\"id\":9,\"time\":1}," +
                "{\"lt\":\"43.602795\",\"lg\":\"7.003898333333333\",\"id\":10,\"time\":1}," +
                "{\"lt\":\"43.607726666666665\",\"lg\":\"6.982481666666667\",\"id\":11,\"time\":1}," +
                "{\"lt\":\"43.61128166666667\",\"lg\":\"6.967508333333335\",\"id\":12,\"time\":1}," +
                "{\"lt\":\"43.60450833333333\",\"lg\":\"6.960655000000001\",\"id\":13,\"time\":1}," +
                "{\"lt\":\"43.59889166666667\",\"lg\":\"6.9569333333333345\",\"id\":14,\"time\":1}," +
                "{\"lt\":\"43.59756833333333\",\"lg\":\"6.954509999999999\",\"id\":15,\"time\":1}," +
                "{\"lt\":\"43.59244333333333\",\"lg\":\"6.956928333333334\",\"id\":16,\"time\":1}," +
                "{\"lt\":\"43.58778666666667\",\"lg\":\"6.956873333333334\",\"id\":17,\"time\":1}]}";

        //Build JSON
        JSONObject fastJson = null;
        try {
            fastJson = new JSONObject(fastString); //Switch string here to test
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JSONObject finalFastJson = fastJson;

        //Send predict with fast time
        test1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                client.post("predict", finalFastJson, new LeaveHandler(leaveTimeText, leaveHourText));
            }
        });

        // ******* SLOW *******

        //2 checkpoints (19 au total en vrai)
        String slowString = "{\"travel\":{\"start\":\"2017\\/02\\/15-10:47:28\",\"time\":0},\"values\":[" +
                "{\"lt\":\"43.61702166666667\",\"lg\":\"7.070624999999999\",\"id\":0,\"time\":1000}," +
                "{\"lt\":\"43.61619\",\"lg\":\"7.065709999999999\",\"id\":1,\"time\":1400}]}";

        //Build JSON
        JSONObject slowJson = null;
        try {
            slowJson = new JSONObject(slowString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JSONObject finalSlowJson = slowJson;

        //Send predict with slow time
        test2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                client.post("predict", finalSlowJson, new LeaveHandler(leaveTimeText, leaveHourText));
            }
        });


    }
}
