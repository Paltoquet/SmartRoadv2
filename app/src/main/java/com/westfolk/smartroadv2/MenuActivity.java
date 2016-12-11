package com.westfolk.smartroadv2;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.westfolk.smartroad.R;


public class MenuActivity extends ActionBarActivity {


    Button start;
    Button stats;
    private Button proximity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        start=(Button)findViewById(R.id.start);
        stats=(Button)findViewById(R.id.stats);
        proximity=(Button)findViewById(R.id.proximity);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondeActivite = new Intent(MenuActivity.this, RecordActivity.class);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.left_animation, R.anim.right_animation);
            }
        });
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondeActivite = new Intent(MenuActivity.this, StatsActivity.class);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.left_animation, R.anim.right_animation);
            }
        });
        proximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent thirdActivite = new Intent(MenuActivity.this, ProximityAlert.class);
                startActivity(thirdActivite);
                overridePendingTransition(R.anim.left_animation, R.anim.right_animation);
            }
        });
    }
}
