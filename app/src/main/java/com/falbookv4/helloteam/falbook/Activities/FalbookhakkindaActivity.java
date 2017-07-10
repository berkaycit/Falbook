package com.falbookv4.helloteam.falbook.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.falbookv4.helloteam.falbook.R;

public class FalbookhakkindaActivity extends AppCompatActivity {

    private Toolbar toolbarFalbookHakkinda;

    private void init(){

        toolbarFalbookHakkinda = (Toolbar) findViewById(R.id.toolbarFalbookHakkinda);

    }

    private void handler(){

        setSupportActionBar(toolbarFalbookHakkinda);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falbookhakkinda);
        init();
        handler();
    }
}
