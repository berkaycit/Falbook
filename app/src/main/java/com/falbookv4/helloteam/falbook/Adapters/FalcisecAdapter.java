package com.falbookv4.helloteam.falbook.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.falbookv4.helloteam.falbook.FalciSec.Falci1;
import com.falbookv4.helloteam.falbook.FalciSec.Falci2;
import com.falbookv4.helloteam.falbook.FalciSec.Falci3;

public class FalcisecAdapter extends FragmentStatePagerAdapter {

    private final int ITEMS = 3; // 3 tane sayfamız olduğunu varsayıyoruz.

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

            default:
                return new Falci1();

        }
    }

    @Override
    public int getCount(){
        return ITEMS;
    }
}