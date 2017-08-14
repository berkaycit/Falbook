package com.falbookv4.helloteam.falbook.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.falbookv4.helloteam.falbook.R;
import com.falbookv4.helloteam.falbook.classes.FontCache;

import java.io.File;

public class FalbookhakkindaActivity extends AppCompatActivity {

    private Toolbar toolbarFalbookHakkinda;

    private FloatingActionButton fbFalGonder;
    private BottomNavigationView botToolbar;
    private TextView txtKullanim, txtGizlilik, toolbarBaslik, txtLisans;

    private void init(){

        toolbarBaslik = (TextView) findViewById(R.id.falbookhakkinda_toolbar_baslik);

        toolbarFalbookHakkinda = (Toolbar) findViewById(R.id.toolbarFalbookHakkinda);

        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);
        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        txtKullanim = (TextView) findViewById(R.id.txtKullanimSartlari);
        txtGizlilik = (TextView) findViewById(R.id.txtGizlilikPolitikasi);
        txtLisans = (TextView) findViewById(R.id.txtLisanslar);

    }

    private void fontHandler(){

        Typeface typeFaceBold= FontCache.get("fonts/MyriadProBold.ttf", this);
        Typeface typeFace= FontCache.get("fonts/MyriadPro.ttf", this);

        toolbarBaslik.setTypeface(typeFaceBold);
        txtKullanim.setTypeface(typeFace);
        txtGizlilik.setTypeface(typeFace);
        txtLisans.setTypeface(typeFace);

    }

    private void menuleriHazirla() {

        txtKullanim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FalbookhakkindaActivity.this, GizlilikpolitikasiActivity.class));
            }
        });

        txtGizlilik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FalbookhakkindaActivity.this, GizlilikpolitikasiActivity.class));
            }
        });

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(FalbookhakkindaActivity.this, KafeActivity.class);
                startActivity(anasayfaToKafe);
                finish();
            }
        });

        botToolbar.setSelectedItemId(R.id.menuBosButon);

        botToolbar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.menuAnasafaButon:
                        Intent intentToAnasayfa = new Intent(FalbookhakkindaActivity.this, AnasayfaActivity.class);
                        intentToAnasayfa.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToAnasayfa);
                        finish();
                        break;
                    case R.id.menuGelenfalButon:
                        Intent intentToFallarim = new Intent(FalbookhakkindaActivity.this, GelenfallarActivity.class);
                        intentToFallarim.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToFallarim);
                        finish();
                        break;
                }
                return true;
            }
        });
    }


    private void handler(){

        fontHandler();
        menuleriHazirla();

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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        fbFalGonder.setOnClickListener(null);

    }
}
