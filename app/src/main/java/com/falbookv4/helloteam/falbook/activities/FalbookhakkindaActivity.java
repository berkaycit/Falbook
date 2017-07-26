package com.falbookv4.helloteam.falbook.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.falbookv4.helloteam.falbook.R;

public class FalbookhakkindaActivity extends AppCompatActivity {

    private Toolbar toolbarFalbookHakkinda;

    private FloatingActionButton fbFalGonder;
    private BottomNavigationView botToolbar;

    private void init(){

        toolbarFalbookHakkinda = (Toolbar) findViewById(R.id.toolbarFalbookHakkinda);

        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);
        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);

    }

    private void menuleriHazirla() {

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

}
