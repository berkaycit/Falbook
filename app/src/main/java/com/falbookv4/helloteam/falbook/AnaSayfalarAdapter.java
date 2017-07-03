package com.falbookv4.helloteam.falbook;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class AnaSayfalarAdapter extends FragmentPagerAdapter{

    List<Fragment> fragmentListesi = new ArrayList<>();

    public AnaSayfalarAdapter(FragmentManager fm, List<Fragment> fraList){
        super(fm);

        this.fragmentListesi = fraList;
    }

    @Override
    public Fragment getItem(int position){
        return fragmentListesi.get(position);
    }

    @Override
    public int getCount(){
        return fragmentListesi.size();
    }
}
