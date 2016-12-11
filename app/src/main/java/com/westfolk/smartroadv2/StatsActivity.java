package com.westfolk.smartroadv2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;
import com.westfolk.smartroad.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lucas on 10/12/2016.
 */

public class StatsActivity extends Activity {

    private Utils utils;

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.list);

        // preparing list data
        try {
            prepareListData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                //Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " Expanded", Toast.LENGTH_SHORT).show();
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                //Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " Collapsed", Toast.LENGTH_SHORT).show();
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                //Toast.makeText(getApplicationContext(), listDataHeader.get(groupPosition) + " : " + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    /*
     * Preparing the list data
     */
    private void prepareListData() throws JSONException {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        List<String> data;

        Date dateStart = null;
        Date dateEnd = null;

        utils = new Utils();
        String datas = utils.readFile("Timing.txt");
        String toSplit = "SPLIT";
        String[] parts = datas.split(toSplit);

        for(int i = 1; i < parts.length; i++) {
            data = new ArrayList<String>();

            listDataHeader.add("Parcours "+ (i));

            JSONObject dataJson = new JSONObject(parts[i-1]);
            JSONArray dataJsonJSONArray = dataJson.getJSONArray("value");

            for(int y = 0; y < dataJsonJSONArray.length(); y++) {
                //data.add(String.valueOf(dataJsonJSONArray.getJSONObject(y)));
                data.add("Checkpoint : "+String.valueOf(dataJsonJSONArray.getJSONObject(y).get("id")));
                data.add("Date : "+String.valueOf(dataJsonJSONArray.getJSONObject(y).get("date")));

                if(y == 0) {
                    dateStart = new Date(String.valueOf(dataJsonJSONArray.getJSONObject(y).get("date")));
                }
                if(y == dataJsonJSONArray.length()-1) {
                    dateEnd = new Date(String.valueOf(dataJsonJSONArray.getJSONObject(y).get("date")));
                }
            }

            long millis = ( dateEnd.getTime() - dateStart.getTime() );
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));

            data.add("Temps total : "+ hms);

            listDataChild.put(listDataHeader.get(i-1), data);
        }
    }
}
