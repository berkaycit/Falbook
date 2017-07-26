package com.falbookv4.helloteam.falbook.falcisec;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.falbookv4.helloteam.falbook.activities.AnasayfaActivity;
import com.falbookv4.helloteam.falbook.activities.GelenfallarActivity;
import com.falbookv4.helloteam.falbook.activities.KafeActivity;
import com.falbookv4.helloteam.falbook.activities.SSSActivity;
import com.falbookv4.helloteam.falbook.adapters.FalcilarPagerAdapter;
import com.falbookv4.helloteam.falbook.adapters.FalcisecAdapter;
import com.falbookv4.helloteam.falbook.R;

import me.relex.circleindicator.CircleIndicator;

public class FalcilarActivity extends AppCompatActivity {

    ViewPager viewPager;
    FalcilarPagerAdapter adapter;
    private Button btnFalciyaGonder;
    private CircleIndicator indicator;
    private BottomNavigationView botToolbar;
    private FloatingActionButton fbFalGonder;

    public void init(){

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);

        botToolbar = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        fbFalGonder = (FloatingActionButton) findViewById(R.id.fbFalGonder);

    }

    private void menuleriHazirla() {

        fbFalGonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent anasayfaToKafe = new Intent(FalcilarActivity.this, KafeActivity.class);
                anasayfaToKafe.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
                        Intent intentToAnasayfa = new Intent(FalcilarActivity.this, AnasayfaActivity.class);
                        intentToAnasayfa.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToAnasayfa);
                        finish();
                        break;
                    case R.id.menuGelenfalButon:
                        Intent intentToFallarim = new Intent(FalcilarActivity.this, GelenfallarActivity.class);
                        intentToFallarim.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentToFallarim);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    public void handler(){

        menuleriHazirla();

        FalcisecAdapter falcisecAdapter = new FalcisecAdapter(getSupportFragmentManager());
        viewPager.setAdapter(falcisecAdapter);

        indicator.setViewPager(viewPager);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falcilar);
        init();
        handler();
    }

    /*
    @Subscribe
    public void onGelenfalEvent(GelenfalEvent event){

        String gonderenKisininIsmi = event.getIsim();
        Log.d("Fragmentler", gonderenKisininIsmi);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
    */


}
