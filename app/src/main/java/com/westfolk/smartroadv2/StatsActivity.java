package com.westfolk.smartroadv2;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.westfolk.smartroad.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lucas on 10/12/2016.
 */

public class StatsActivity extends Activity {

    private WsClient client;
    private Utils utils;
    private Button refresh;
    private Context context;
    private TextView moyen;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        context = this.getApplicationContext();
        refresh = (Button) findViewById(R.id.refresh);
        moyen = (TextView) findViewById(R.id.moyen);

        client = new WsClient(this);

        JSONObject obj = new JSONObject();
        client.post("stats", obj, new StatsHandler());

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });
    }

    public void load() {
        try {
            prepareListData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.list);

        // preparing list data
        listAdapter = new ExpandableListAdapter(context, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    /*
     * Preparing the list data
     */
    protected void prepareListData() throws JSONException {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        List<String> data;

        utils = new Utils();
        String datas = utils.readFile("Stats.txt");

        JSONObject dataJson = new JSONObject(datas);
        JSONArray meanByDayArray = new JSONArray();
        JSONArray minByDayArray = new JSONArray();
        int Moyenne = dataJson.getInt("mean");

        meanByDayArray = dataJson.getJSONArray("meanByDayArray");
        minByDayArray = dataJson.getJSONArray("minByDayArray");

        moyen.setText("Temps moyen : "+utils.getDateFromSecond(Moyenne));

        for (int i = 0; i < meanByDayArray.length(); i++) {
            listDataHeader.add( String.valueOf(meanByDayArray.getJSONObject(i).get("Day")));

            data = new ArrayList<String>();
            String mean = String.valueOf(meanByDayArray.getJSONObject(i).get("Value"));
            String min = String.valueOf(minByDayArray.getJSONObject(i).get("Value"));

            if(!mean.equals("error")) {
                data.add("Average time : " + utils.getDateFromSecond(Long.parseLong(mean)));
            } else {
                data.add("Average time : " + mean);
            }
            if(!min.equals("error")) {
                data.add("Minimum time : " + utils.getDateFromSecond(Long.parseLong(min)));
            } else {
                data.add("Minimum time : " + min);
            }

            listDataChild.put(listDataHeader.get(i), data);
        }
    }


}
