package com.falbookv4.helloteam.falbook.Activities;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.falbookv4.helloteam.falbook.R;

public class FaldetayActivity extends AppCompatActivity {


    private CollapsingToolbarLayout colToolbar;
    private Toolbar faldetayToolbar;

    public void init(){

        colToolbar = (CollapsingToolbarLayout)findViewById(R.id.faldetayColToolbar);
        colToolbar.setTitle("FALLARIM");

        faldetayToolbar = (Toolbar) findViewById(R.id.faldetayToolbar);

    }

    public void handler(){

        //toolbar ı action bar ın özelliklerinden faydalanmasını sağlıyoruz
        setSupportActionBar(faldetayToolbar);
        //parent ı na geri tuşu koymuş oluyoruz, action bar özelliği sayesinde->toolbar a
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faldetay);
        init();
        handler();
    }
}
