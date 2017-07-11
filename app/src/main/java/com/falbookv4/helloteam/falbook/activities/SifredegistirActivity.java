package com.falbookv4.helloteam.falbook.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.falbookv4.helloteam.falbook.R;

public class SifredegistirActivity extends AppCompatActivity {

    private Toolbar sifreDegistirToolbar;

    public void init(){

        sifreDegistirToolbar = (Toolbar) findViewById(R.id.toolbarSifreDegistir);

    }


    public void handler(){

        setSupportActionBar(sifreDegistirToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sifredegistir);
        init();
        handler();
    }
}
