package com.falbookv4.helloteam.falbook.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.falbookv4.helloteam.falbook.falcisec.Falci1;
import com.falbookv4.helloteam.falbook.falcisec.Falci2;
import com.falbookv4.helloteam.falbook.falcisec.Falci3;
import com.falbookv4.helloteam.falbook.falcisec.Falci4;
import com.falbookv4.helloteam.falbook.falcisec.Falci5;
import com.falbookv4.helloteam.falbook.falcisec.Falci6;

public class FalcisecAdapter extends FragmentStatePagerAdapter {

    private final int ITEMS = 6; // 6 tane sayfamız olduğunu varsayıyoruz.

    public FalcisecAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position){
        switch(position){

            case 0:
                return new Falci1();
            case 1:
                return new Falci2();
            case 2:
                return new Falci3();
            case 3:
                return new Falci4();
            case 4:
                return new Falci5();
            case 5:
                return new Falci6();

            default:
                return new Falci1();

        }
    }

    @Override
    public int getCount(){
        return ITEMS;
    }
}