package com.falbookv4.helloteam.falbook;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import me.relex.circleindicator.CircleIndicator;

public class FalcilarActivity extends AppCompatActivity {

    ViewPager viewPager;
    FalcilarPagerAdapter adapter;

    public void init(){

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);

        //metodu daha önceden statik tanımladığım için direk çekiyorum
        adapter = new FalcilarPagerAdapter(this, FalciData.getDataList());
        viewPager.setAdapter(adapter);
        indicator.setViewPager(viewPager);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_falcilar);
        init();
    }
}
