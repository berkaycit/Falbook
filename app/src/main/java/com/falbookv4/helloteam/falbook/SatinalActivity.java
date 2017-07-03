package com.falbookv4.helloteam.falbook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class SatinalActivity extends AppCompatActivity {


    private Toolbar toolbarSatinal;

    public void init(){

        toolbarSatinal = (Toolbar) findViewById(R.id.toolbarSatinal);

    }

    public void handler(){

        setSupportActionBar(toolbarSatinal);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_satinal);
        init();
        handler();
    }
}
