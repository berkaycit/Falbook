package com.falbookv4.helloteam.falbook.FalciSec;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.falbookv4.helloteam.falbook.Adapters.FalcilarPagerAdapter;
import com.falbookv4.helloteam.falbook.Adapters.FalcisecAdapter;
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

        /*
        //->eski metot fragment değil adapter kullanıyordum.
        //metodu daha önceden statik tanımladığım için direk çekiyorum
        adapter = new FalcilarPagerAdapter(this, FalciData.getDataList());
        viewPager.setAdapter(adapter);
        */

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
}
