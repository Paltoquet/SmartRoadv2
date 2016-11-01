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


    Button play;
    Button score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        play=(Button)findViewById(R.id.start);
        score=(Button)findViewById(R.id.stats);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondeActivite = new Intent(MenuActivity.this, RecordActivity.class);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.left_animation, R.anim.right_animation);
            }
        });
        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent secondeActivite = new Intent(MenuActivity.this, RecordActivity.class);
                startActivity(secondeActivite);
                overridePendingTransition(R.anim.left_animation, R.anim.right_animation);
            }
        });
    }
}
