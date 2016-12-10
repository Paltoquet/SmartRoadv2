package com.westfolk.smartroadv2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.westfolk.smartroad.R;

import java.util.ArrayList;

/**
 * Created by Lucas on 10/12/2016.
 */

public class StatsActivity extends Activity {

    private ListView mListView;
    private Utils utils;
    private String data [];
    private String[] prenoms = new String[]{"Parcours 1", "Parcours 2 Parcours 2 Parcours 2 Parcours 2 Parcours 2 Parcours 2 Parcours 2", "Parcours 3", "Parcours 4"};
    private ArrayList<String> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        mListView = (ListView) findViewById(R.id.list);

        utils = new Utils();
        String datas = utils.readFile("Timing.txt");
        Log.i("Stats", datas);

        String toSplit = "SPLIT";
        String[] parts = datas.split(toSplit);

        //TODO virer le dernier split
        /*
        for(int i = 0; i < parts.length; i++) {
            Log.i("test", parts[i]);
        }
        */

        ArrayAdapter<String> adapter = new ArrayAdapter<>(StatsActivity.this, android.R.layout.simple_list_item_1, parts);
        mListView.setAdapter(adapter);

    }
}
