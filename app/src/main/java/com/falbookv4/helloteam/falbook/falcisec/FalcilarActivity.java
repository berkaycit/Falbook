package com.falbookv4.helloteam.falbook.falcisec;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.falbookv4.helloteam.falbook.adapters.FalcilarPagerAdapter;
import com.falbookv4.helloteam.falbook.adapters.FalcisecAdapter;
import com.falbookv4.helloteam.falbook.R;

import me.relex.circleindicator.CircleIndicator;

public class FalcilarActivity extends AppCompatActivity {

    ViewPager viewPager;
    FalcilarPagerAdapter adapter;
    private Button btnFalciyaGonder;
    private CircleIndicator indicator;

    public void init(){

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        indicator = (CircleIndicator) findViewById(R.id.indicator);




    }

    public void handler(){

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
